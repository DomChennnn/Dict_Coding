/*
 * class:           SparseMatrix
 *
 * Description:		This class is a sparse matrix implementation of abstract
 *                  class AllMatrices.
 *
 * Copyright (c) 2007-2008.  Karl Skretting.  All rights reserved.
 * University of Stavanger, Institutt for data- og elektroteknikk
 * Mail:  karl.skretting@uis.no   Homepage:  http://www.ux.his.no/~karlsk/
 *
 * HISTORY:  dd.mm.yyyy
 * Ver. 1.0  12.06.2007  KS: class made and testet (as MPSparseMatrix)
 * Ver. 2.0  04.11.2008  KS: class is now an extension of AllMatrices
 * */

package mpv2;

import java.lang.*;
import java.util.Random;
// import mpv2.util.*;

/**
 * This class is a sparse matrix implementation.
 *
 * <P>
 * The class SparseMatrices contains stores the matrix elements in a way
 * well suitable to sparse matrices, i.e. most of the entries are zero.
 * For each non-zero element a structure with 5 fields are stored: 
 * the row index, the coluimn index, the value, a pointer to next in same row
 * and a pointer to next in same column. Thus the sparsness factor, i.e.
 * number of non-zero elements divided by total number of elements, should
 * be lower that 0.2 to get any effect, and lower than 0.1 to get significant
 * improvement related to the Matrix-class. Read access, especially to
 * a row or column is effective, while write access, especially to elements
 * which are zero, are slower. <BR>
 * There is a quite large number of methods available for matrices in general
 * and in this class specially, see the AllMatrices class for a general overview.
 * Here is a brief overview for added methods:</P>
 *
 * <P><UL>
 * <LI> Constructors:<BR>
 *     v
 * </UL></P>
 *
 * @version 2.0  November 2008
 * @author  Karl Skretting (karl.skretting@uis.no)
 * @author  http://www.ux.uis.no/~karlsk/
 *
 * */
public class SparseMatrix extends AllMatrices
implements Cloneable, java.io.Serializable {
    
    // for å lagre data brukes her flere array, de er alltid like lange!
    // de neste fem indekseres med i
    private int[] nonZeroRow;        // i D(n,k) er dette n (row), -1 for ledig
    private int[] nonZeroColumn;     // i D(n,k) er dette k (column), -1 for ledig
    private double[] nonZeroValue;   // verdien
    private int[] nextInSameRow;     // peker til neste i samme rad
    private int[] nextInSameColumn;  // peker til neste i samme kolonne
    
    private int iNextFree;           // peker til første ubrukte / ledige
    private int[] firstInRow;        // peker til første i hver rad (eller neste ledige)
    private int[] firstInColumn;     // peker til første i hver kolonne
    
    private int nonZeroCount;        // går gjennom alle med: for (int i=0; i<nonZeroCount; i++)
    private int capacity;            // == nonZeroValue.length
    // antall ledige er capacity-nonZeroCount
    private int increment = 100;     // det en øker med når array utvides
    
/* ------------------------
   Constructors
 * ------------------------ */
    
    /** Construct an m-by-n sparse matrix of zeros.
     * @param m    Number of rows.
     * @param n    Number of colums.
     */
    public SparseMatrix (int m, int n) {
        N = m;
        K = n;
        capacity = 0;           // starter med null-pekere
        iNextFree = -1;         // og det er ikke noen ledige
        nonZeroCount = 0;
        // System.out.println("SparseMatrix, " + N + "-by-" + K +
        // ", created with only zeros.");
    }
    
    /** Construct a matrix from a 2-D array.
   @param A    Two-dimensional array of doubles.
   @exception  IllegalArgumentException All rows must have the same length
     */
    public SparseMatrix(double[][] A) {
        N = A.length;
        K = A[0].length;
        for (int n = 0; n < N; n++) {
            if (A[n].length != K) {
                throw new IllegalArgumentException
                ("All rows must have the same length.");
            }
        }
        capacity = 0;           // starter med null-pekere
        iNextFree = -1;         // og det er ikke noen ledige
        nonZeroCount = 0;
        for (int n = 0; n < N; n++) {
            for (int k = 0; k < K; k++) {
                if (A[n][k] != 0.0) nonZeroCount++;
            }
        }
        incrementCapacity(nonZeroCount);    // endrer også iNextFree og capacity
        firstInRow = new int[N];
        for (int n=0; n<N; n++) firstInRow[n] = -1;
        firstInColumn = new int[K];
        for (int k=0; k<K; k++) firstInColumn[k] = -1;
        //
        int[] lastInRow = new int[N];
        for (int n=0; n<N; n++) lastInRow[n] = -1;
        int i = 0;    // indeks for nonZeroValue (og tilhørende)
        for (int k=0; k<K; k++){
            for (int n=0; n<N; n++){
                if (A[n][k] != 0.0) {
                    nonZeroRow[i] = n;
                    nonZeroColumn[i] = k;
                    nonZeroValue[i] = A[n][k];
                    nextInSameRow[i] = -1;
                    nextInSameColumn[i] = -1;
                    // oppdaterer kolonnepeker
                    if (firstInColumn[k] < 0) {
                        firstInColumn[k] = i;
                    } else {
                        nextInSameColumn[i-1] = i;  // forrige peker til denne
                    }
                    // oppdaterer linjepeker
                    if (firstInRow[n] < 0) {
                        firstInRow[n] = i;
                    } else {
                        nextInSameRow[lastInRow[n]] = i;   // forrige peker til denne
                    }
                    lastInRow[n] = i;
                    i++;
                }
            }
        }
        // noen unødvendige (?) sjekk
        if (i != nonZeroCount) {   // nå skal en ha fått med alle
            System.out.println("SparseMatrix constructor has logical error: " +
            " i = " + i + ", while nonZeroCount = " + nonZeroCount );
        }
        if (capacity != nonZeroCount) {
            System.out.println("SparseMatrix constructor has logical error: " +
            " capacity = " + capacity + ", while nonZeroCount = " + nonZeroCount );
        }
        //
        iNextFree = -1;     // det er nå ikke noen indeks til neste ledige
        // System.out.println("SparseMatrix, " + N + "-by-" + K +
        // ", created with supplied values.");
    }
    
    /** Construct a matrix from a one-dimensional packed array
   @param vals One-dimensional array of doubles, packed by columns (ala Fortran).
   @param m    Number of rows, i.e. length of columns.
   @exception  IllegalArgumentException Array length must be a multiple of m.
     */
    public SparseMatrix(double vals[], int m) {
        N = m;
        K = (m != 0 ? vals.length/m : 0);
        if (N*K != vals.length) {
            throw new IllegalArgumentException("Array length must be a multiple of m.");
        }
        //
        // if (vals.length != (N*K)) throw new IllegalArgumentException(
        //     "Given size of matrix, " + N + "-by-" + K +
        //     ", does not correspond to the size of vals, vals.length = " + vals.length );
        //
        capacity = 0;           // starter med null-pekere
        iNextFree = -1;         // og det er ikke noen ledige
        setAll(vals);
        iNextFree = -1;     // det er nå ikke noen indeks til neste ledige
    }
    
    /** Construct a new matrix from another matrix (of any kind)
     * @param B    a matrix of any kind, class is a subclass of AllMatrices
     */
    public SparseMatrix(AllMatrices B) {
        N = B.getN();
        K = B.getK();
        //   Bør kanskje heller gå om pakka kolonne!
        capacity = 0;           // starter med null-pekere
        iNextFree = -1;         // og det er ikke noen ledige
        nonZeroCount = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < K; j++) {
                if (B.get(i,j) != 0.0) nonZeroCount++;
            }
        }
        //
        incrementCapacity(nonZeroCount);    // endrer også iNextFree og capacity
        firstInRow = new int[N];
        for (int n=0; n<N; n++) firstInRow[n] = -1;
        firstInColumn = new int[K];
        for (int k=0; k<K; k++) firstInColumn[k] = -1;
        //
        int[] lastInRow = new int[N];
        for (int n=0; n<N; n++) lastInRow[n] = -1;
        int i = 0;    // indeks for nonZeroValue (og tilhørende)
        for (int k=0; k<K; k++){
            for (int n=0; n<N; n++){
                if (B.get(n,k) != 0.0) {
                    nonZeroRow[i] = n;
                    nonZeroColumn[i] = k;
                    nonZeroValue[i] = B.get(n,k);
                    nextInSameRow[i] = -1;
                    nextInSameColumn[i] = -1;
                    // oppdaterer kolonnepeker
                    if (firstInColumn[k] < 0) {
                        firstInColumn[k] = i;
                    } else {
                        nextInSameColumn[i-1] = i;  // forrige peker til denne
                    }
                    // oppdaterer linjepeker
                    if (firstInRow[n] < 0) {
                        firstInRow[n] = i;
                    } else {
                        nextInSameRow[lastInRow[n]] = i;   // forrige peker til denne
                    }
                    lastInRow[n] = i;
                    i++;
                }
            }
        }
        iNextFree = -1;     // det er nå ikke noen indeks til neste ledige
    }
    
 /* ------------------------
   Private Methods
  * ------------------------ */
    
    private void incrementCapacity(int incCapacity){
        if (incCapacity <= 0) return;
        int i;
        int[] tempi;
        double[] tempd;
        int newCapacity = capacity + incCapacity;
        // System.out.println("incrementCapacity: Øker med " +
        //         + incCapacity + " fra " + capacity + " til " + newCapacity );
        //
        tempi = nonZeroRow;
        nonZeroRow = new int[newCapacity];
        for (i=0; i<capacity; i++) nonZeroRow[i] = tempi[i];
        for (i=capacity; i<newCapacity; i++) nonZeroRow[i] = -1;
        //
        tempi = nonZeroColumn;
        nonZeroColumn = new int[newCapacity];
        for (i=0; i<capacity; i++) nonZeroColumn[i] = tempi[i];
        for (i=capacity; i<newCapacity; i++) nonZeroColumn[i] = -1;
        //
        tempd = nonZeroValue;
        nonZeroValue = new double[newCapacity];
        for (i=0; i<capacity; i++) nonZeroValue[i] = tempd[i];
        for (i=capacity; i<newCapacity; i++) nonZeroValue[i] = 0.0;
        //
        tempi = nextInSameRow;
        nextInSameRow = new int[newCapacity];
        for (i=0; i<capacity; i++) nextInSameRow[i] = tempi[i];
        for (i=capacity; i<newCapacity; i++) nextInSameRow[i] = i+1;
        // System.out.println("incrementCapacity: i =  " +
        //     i + ", newCapacity = " + newCapacity +
        //     ", nextInSameRow.length = " + nextInSameRow.length );
        nextInSameRow[newCapacity-1] = -1;  // feil her?
        //
        if (iNextFree < 0) {        // det var ingen ledige før utvidelsen
            iNextFree = capacity;   // første av de nye er nå neste ledig
        } else {
            int iLastFree = iNextFree;      // skal finne siste ledige før utvidelsen
            while (nextInSameRow[iLastFree] >= 0) iLastFree = nextInSameRow[iLastFree];
            // kobler siste ledige før utvidelse til de nye ledige
            nextInSameRow[iLastFree] = capacity;
            // mens iNextFree er uforandret
        }
        //
        tempi = nextInSameColumn;
        nextInSameColumn = new int[newCapacity];
        for (i=0; i<capacity; i++) nextInSameColumn[i] = tempi[i];
        for (i=capacity; i<newCapacity; i++) nextInSameColumn[i] = -1;
        //
        capacity = newCapacity;
    }
    
    private int nk2i(int n, int k){
        if ((k<0) || (k>=K) || (n<0) || (n>=N)) return -1;
        int i = -1;
        if (N < K) {    // søker nedetter kolonne k
            i = firstInColumn[k];
            if (i < 0) return -1;
            while ((nonZeroRow[i] < n) && (nextInSameColumn[i] >= 0)) i = nextInSameColumn[i];
        } else {   // søker langs linje n
            i = firstInRow[n];
            if (i < 0) return -1;
            while ((nonZeroColumn[i] < k) && (nextInSameRow[i] >= 0)) i = nextInSameRow[i];
        }
        if ((nonZeroRow[i] == n) && (nonZeroColumn[i] == k)) return i;
        return -1;
    }
    
 /* ------------------------
   Public Static Methods, these work as constructors
 * ------------------------ */
    
   /** Generate identity matrix
   @param m    Number of rows.
   @param n    Number of colums.
   @return     An m-by-n matrix with ones on the diagonal and zeros elsewhere.
   */

    // HIT
/*   public static SparseMatrix identity (int m, int n) {
        N = m;
        K = n;
   }  */

 /* ------------------------
   Public Methods, which use this object
  * ------------------------ */
    
    /** Get a single element.
     * @param n row number for the returned entry value.
     * @param k column number for the returned entry value.
     * @return     A(n,k)
     * @exception  ArrayIndexOutOfBoundsException
     */
    public double get(int n, int k){
        int i = nk2i(n, k);
        if (i < 0) return 0.0;
        return nonZeroValue[i];
    }
    
    /** Set an entry (a value) in the dictionary.
     * Note that the row, i.e. position in the column, is given first,
     * the second argument is the column number i.e. the number of the dictionary atom.
     * We should have: <code>0 <= n < N</code> and <code>0 <= k < K</code>.
     * If any argument is outside legal range nothing is done.
     *
     * @param n   row number for the entry value to be changed.
     * @param k   column number for the entry value to be changed.
     * @param val the value to be put into the given entry of the dictionary.
     */
    public void set(int n, int k, double val){
        if ((k<0) || (k>=K) || (n<0) || (n>=N)) return;
        int i = nk2i(n, k);
        if (i >= 0){                    // fanns fra før
            nonZeroValue[i] = val;
            if (val == 0.0) {
                // pekere skal hoppe over dette elementet (i), først for linje
                int a = firstInRow[n];      // a : after, indeks for elementet etter
                int b = -1;                 // b : before, elementet foran
                while (a >= 0) {
                    if (nonZeroColumn[a] < k) b = a;
                    if (nonZeroColumn[a] > k) break;
                    a = nextInSameRow[a];   // kan bli -1
                }
                if (b < 0) firstInRow[n] = a;
                else nextInSameRow[b] = a;
                // og så kolonna
                a = firstInColumn[k];       // a : after, elementet etter
                b = -1;                     // b : before, elementet foran
                while (a >= 0) {
                    if (nonZeroRow[a] < n) b = a;
                    if (nonZeroRow[a] > n) break;
                    a = nextInSameColumn[a];   // kan bli -1
                }
                if (b < 0) firstInColumn[k] = a;
                else nextInSameColumn[b] = a;
                // her fjernes elementet (det blir ledig)
                nonZeroRow[i] = -1;
                nonZeroColumn[i] = -1;
                nextInSameRow[i] = iNextFree;       // peker til neste ledig
                iNextFree = i;                      // denne er nå neste (første) av de ledige
                nextInSameColumn[i] = -1;
                nonZeroCount--;
            }
        } else {                        // nytt element må opprettes
            if (iNextFree < 0) incrementCapacity(increment);   // øker kapasitet
            i = iNextFree;
            iNextFree = nextInSameRow[i];
            nonZeroRow[i] = n;
            nonZeroColumn[i] = k;
            nonZeroValue[i] = val;
            nonZeroCount++;
            // og så må pekere korrigeres, tar først linja
            int a = firstInRow[n];      // a : after, indeks for elementet etter
            int b = -1;                 // b : before, elementet foran
            while (a >= 0) {
                if (nonZeroColumn[a] > k) break;
                b = a;
                a = nextInSameRow[a];   // kan bli -1
            }
            nextInSameRow[i] = a;       // neste blir den etter
            if (b < 0) firstInRow[n] = i;
            else nextInSameRow[b] = i;
            // og så kolonna
            a = firstInColumn[k];       // a : after, elementet etter
            b = -1;                     // b : before, elementet foran
            while (a >= 0) {
                if (nonZeroRow[a] > n) break;
                b = a;
                a = nextInSameColumn[a];   // kan bli -1
            }
            nextInSameColumn[i] = a;       // neste blir den etter
            if (b < 0) firstInColumn[k] = i;
            else nextInSameColumn[b] = i;
        }
        return;
    }
    
    /**
     * Returns number of non-zeros elements in sparse matrix.
     * */
    public int getNonZeroCount(){ return nonZeroCount;}
    
    /**
     * Returns current size (capacity for non-zero values) in sparse matrix.
     * */
    public int getCapacity(){ return capacity;}
    
    
    // ********** Improved (?) methods which also are in AllMatrices **********
    
    /** Make a one-dimensional column packed copy of the matrix.
     * @return     Matrix elements packed in a one-dimensional array by columns.
     */
    public double[] getColumnPackedCopy () {
        double[] vals = new double[N*K];
        for (int i=0; i<nonZeroCount; i++){
            vals[ nonZeroRow[i] + N*nonZeroColumn[i] ] = nonZeroValue[i];
        }
        return vals;
    }
    
    /** Make a one-dimensional row packed copy of the matrix.
     * @return     Matrix elements packed in a one-dimensional array by rows.
     */
    public double[] getRowPackedCopy () {
        double[] vals = new double[N*K];
        for (int i=0; i<nonZeroCount; i++){
            vals[ K*nonZeroRow[i] + nonZeroColumn[i] ] = nonZeroValue[i];
        }
        return vals;
    }
    
    /** Set all entries of to matrix to the supplied new values
     * If argument is wrong length an IllegalArgumentException is thrown. <br>
     *
     * @param vals One-dimensional array of doubles, packed by columns (ala Fortran).
     * */
    public void setAll(double[] vals){
        if (N*K != vals.length) {
            throw new IllegalArgumentException
                ("Array length must as total size of matrix (N*K).");
        }
        nonZeroCount = 0;
        for (int j=0; j<(N*K); j++) if (vals[j] != 0.0) nonZeroCount++;
        // set capacity
        capacity = 0;           // starter med null-pekere
        iNextFree = -1;         // og det er ikke noen ledige
        incrementCapacity(nonZeroCount);    // endrer også iNextFree og capacity
        firstInRow = new int[N];
        for (int n=0; n<N; n++) firstInRow[n] = -1;
        firstInColumn = new int[K];
        for (int k=0; k<K; k++) firstInColumn[k] = -1;
        //
        int[] lastInRow = new int[N];
        for (int n=0; n<N; n++) lastInRow[n] = -1;
        int j = 0;    // indeks som går gjennom alle de N*K elementen i val
        int i = 0;    // indeks for nonZeroValue (og tilhørende)
        for (int k=0; k<K; k++){
            for (int n=0; n<N; n++, j++){
                if (vals[j] != 0.0) {
                    nonZeroRow[i] = n;
                    nonZeroColumn[i] = k;
                    nonZeroValue[i] = vals[j];
                    nextInSameRow[i] = -1;
                    nextInSameColumn[i] = -1;
                    // oppdaterer kolonnepeker
                    if (firstInColumn[k] < 0) {
                        firstInColumn[k] = i;
                    } else {
                        nextInSameColumn[i-1] = i;  // forrige peker til denne
                    }
                    // oppdaterer linjepeker
                    if (firstInRow[n] < 0) {
                        firstInRow[n] = i;
                    } else {
                        nextInSameRow[lastInRow[n]] = i;   // forrige peker til denne
                    }
                    lastInRow[n] = i;
                    i++;
                }
            }
        }
        iNextFree = -1;     // det er nå ikke noen indeks til neste ledige
    }

    
    // noen algoritmer kan være mer effektive enn de som arves
    
    // men innerProduct er ikke så effektiv som forventet, sammenlignet med MPSimpleMatrix
    // som testet i testmp03.m fra Matlab. Men mye (mesteparten) av tida antas da å
    // være i forbindelse med kall til Java.
    /**
     * Returns the inner product of two dictionary elements, i.e. matrix column vectors.
     * Legal range is <code>0 <= k1 < K</code> and <code>0 <= k2 < K</code>.
     * If <code>k1</code> or <code>k2</code> are out of range, 0.0 should be returned.
     *
     * @param k1 number for the first dictionary element.
     * @param k2 number for the second dictionary element.
     * */
    public double innerProduct(int k1, int k2){
        if ((k1<0) || (k1>=K) || (k2<0) || (k2>=K)) return 0.0;
        double ip = 0.0;
        int i1 = firstInColumn[k1];
        int i2 = firstInColumn[k2];
        while ((i1 >= 0) && (i2 >= 0)) {
            // if (nonZeroRow[i1] < 0) break;             // egentlig feil hvis dette skjer!
            // if (nonZeroRow[i2] < 0) break;             // egentlig feil hvis dette skjer!
            if (nonZeroRow[i1] == nonZeroRow[i2])
                ip += nonZeroValue[i1]*nonZeroValue[i2];
            if (nonZeroRow[i1] <= nonZeroRow[i2]) i1 = nextInSameColumn[i1];
            else i2 = nextInSameColumn[i2];
        }
        return ip;
    }
    
// <LI> Access to columns and rows of the matrix.
    
    // may avoid to set all elememts to zero twice.
    /** Copy a column of the matrix
     * Legal range of the integer argument is <code>0 <= k < K</code>.
     * If argument is out of range a length <code>N</code> array of zeros should be returned. <br>
     * The corresponding Matlab expression would be: <code>A(:,k+1)</code>.
     *
     * @param k number of the column in the matrix
     * */
    public double[] getColumn(int k){
        double[] d = new double[N];
        if ((k<0) || (k>=K)) return d;
        int i = firstInColumn[k];
        while (i>=0) {
            d[nonZeroRow[i]] = nonZeroValue[i];
            i = nextInSameColumn[i];
        }
        return d;
    }
    
    /**
     * Sets argument d to a column of the matrix, i.e. a dictionary element or atom.
     * Legal range of the integer argument is <code>0 <= k < K</code>.
     * If argument is out of range a length <code>N</code> array of zeros should be returned. <br>
     * The corresponding Matlab expression would be: <code>D(:,k+1)</code>.
     *
     * @param k number of the column in dictionary, i.e. matrix <code>D</code>.
     * @param d the given column of matrix <code>D</code>.
     * */
    public void getColumn(int k, double[] d){
        if ((k<0) || (k>=K)) return;
        if (d.length != N)  throw new IllegalArgumentException(
            "getColumn: argument d is not expected length N.");
        for (int n = 0; n<N; n++) d[n] = 0.0;
        int i = firstInColumn[k];
        while (i>=0) {
            d[nonZeroRow[i]] = nonZeroValue[i];
            i = nextInSameColumn[i];
        }
        return;
    }
    
    /** Add a column of the matrix multiplied by a factor to x, x = x + A[][k]
     * Legal range of the integer argument is <code>0 <= k < K</code>. <br>
     * The corresponding Matlab expression would be: <code>x = x+f*A(:,k+1)</code>.
     * Note that this function can not be used from Matlab, use getColumn(k) to
     * get A(:,k+1) and calculate the new x in Matlab.
     *
     * @param k number of the column the matrix <code>A</code>.
     * @param factor a factor to multiply the column vector by.
     * @param x an array of length <code>N</code>.
     * */
    public void addColumn(int k, double factor, double[] x){
        if ((k<0) || (k>=K)) return;
        if (x.length != N)  throw new IllegalArgumentException(
            "addColumn: argument x is not expected length N.");
        int i = firstInColumn[k];
        while (i>=0) {
            x[nonZeroRow[i]] += factor*nonZeroValue[i];
            i = nextInSameColumn[i];
        }
        return;
    }

    // may avoid to set all elememts to zero twice.
    /** Copy a row of the matrix
     * Legal range of the integer argument is <code>0 <= n < N</code>.
     * If argument is out of range a length <code>K</code> array of zeros should be returned. <br>
     * The corresponding Matlab expression would be: <code>A(n+1,:)</code>.
     *
     * @param n number of the row in the matrix <code>A</code>.
     * */
    public double[] getRow(int n){
        double[] r = new double[K];
        if ((n<0) || (n>=N)) return r;
        int i = firstInRow[n];
        while (i>=0) {
            r[nonZeroColumn[i]] = nonZeroValue[i];
            i = nextInSameRow[i];
        }
        return r;
    }
    
    /** Copy a row of the matrix
     * Legal range of the integer argument is <code>0 <= n < N</code>.
     * If argument is out of range a length <code>K</code> array of zeros should be returned. <br>
     * The corresponding Matlab expression would be: <code>r = A(n+1,:)</code>.
     *
     * @param n number of the row in the matrix <code>A</code>.
     * @param r the given row of matrix <code>A</code>.
     * */
    public void getRow(int n, double[] r){
        if ((n<0) || (n>=N)) return;
        if (r.length != K)  throw new IllegalArgumentException(
            "getRow: argument r is not expected length K.");
        for (int k = 0; k<K; k++) r[k] = 0.0;
        int i = firstInRow[n];
        while (i>=0) {
            r[nonZeroColumn[i]] = nonZeroValue[i];
            i = nextInSameRow[i];
        }
        return;
    }
    
    /** Frobenius norm
   @return    sqrt of sum of squares of all elements.
     */
    public double normF () {
        double f = 0.0;
        for (int i=0; i<nonZeroCount; i++){
            f = Math.hypot(f,nonZeroValue[i]);
        }
        return f;
    }
    /**
     * Multiplies the transposed dictionary <code>D'</code> by array <code>x</code>.
     * <code>D</code> is the matrix representing the dictionary, each column is a dictionary
     * element. An array <code>y</code> is returned. <br>
     * The dimensions are: <code>D</code> is
     * <code>N&times;K</code>, <code>x</code> is <code>N&times;1</code>,
     * and <code>y</code> is <code>K&times;1</code>. <br>
     * The corresponding mulitplication in Matlab would be: <code>y = D'*x</code>.
     *
     * @param x  the signal (column vector) that is multiplied by the transposed dictionary.
     * @param y  the results as an array of length <code>K</code>.
     * */
    public void transposeTimes(double[] x, double[] y){
        if (x.length != N)  throw new IllegalArgumentException(
        "transposeTimes: argument x is not expected length N.");
        if (y.length != K)  throw new IllegalArgumentException(
        "transposeTimes: argument y is not expected length K.");
        for (int k=0; k<K; k++) y[k] = 0.0;
        for (int i=0; i<nonZeroCount; i++)
            if (nonZeroValue[i] != 0.0)
                y[nonZeroColumn[i]] += x[nonZeroRow[i]]*nonZeroValue[i];
        return;
    }
    
    /**
     * Multiplies the dictionary <code>D</code> by array <code>y</code>.
     * <code>D</code> is the matrix representing the dictionary,
     * each column is a dictionary element.
     * An array <code>x</code> is returned. <br>
     * The dimensions are: <code>D</code> is
     * <code>N&times;K</code>, <code>x</code> is <code>N&times;1</code>,
     * and <code>y</code> is <code>K&times;1</code>. <br>
     * The corresponding mulitplication in Matlab would be: <code>x = D*y</code>.
     *
     * @param y  the coefficient vector that is multiplied by the dictionary.
     * @param x  the results as an array of length <code>N</code>.
     * */
    public void times(double[] y, double[] x){
        if (y.length != K)  throw new IllegalArgumentException(
        "times: argument y is not expected length K.");
        if (x.length != N)  throw new IllegalArgumentException(
        "times: argument x is not expected length N.");
        for (int n=0; n<N; n++) x[n] = 0.0;
        for (int i=0; i<nonZeroCount; i++)
            if (nonZeroValue[i] != 0.0)
                x[nonZeroRow[i]] += y[nonZeroColumn[i]]*nonZeroValue[i];
        return;
    }
    
    /** Multiplies the matrix by an another matrix, return C = A * B
     * @param B    another matrix
     * @return     Matrix product, A * B, as a JamaMatrix object
     * @exception  IllegalArgumentException Matrix inner dimensions must agree.
     */
    public JamaMatrix times(AllMatrices B) {
        if (B.getRowDimension() != K) {
            throw new IllegalArgumentException("Matrix inner dimensions must agree.");
        }
        int L = B.getColumnDimension();
        JamaMatrix C = new JamaMatrix(N, L);
        double[][] X = C.getArray();
        for (int i=0; i<nonZeroCount; i++){
            int n = nonZeroRow[i];
            int k = nonZeroColumn[i];
            double t = nonZeroValue[i];
            for (int l = 0; l < L; l++) {
                X[n][l] += t*B.get(k,l);
            }
        }
        return C;
    }
    
    /** Multiplies the transposed matrix by an another matrix, return B = A' * C
     * @param C    another matrix
     * @return     Matrix product, A' * C, as a JamaMatrix object
     * @exception  IllegalArgumentException Matrix inner dimensions must agree.
     */
    public JamaMatrix transposeTimes(AllMatrices C) {
        if (C.getRowDimension() != N) {
            throw new IllegalArgumentException("Matrix inner dimensions must agree.");
        }
        int L = C.getColumnDimension();
        JamaMatrix B = new JamaMatrix(K, L);
        double[][] X = B.getArray();
        for (int i=0; i<nonZeroCount; i++){
            int n = nonZeroRow[i];
            int k = nonZeroColumn[i];
            double t = nonZeroValue[i];
            for (int l = 0; l < L; l++) {
                X[k][l] += t*C.get(n,l);
            }
        }
        return B;
    }
    
}
