/*
 * class:           BandMatrix
 *
 * Description:		This class is a band diagonal matrix implementation of AllMatrices 
 *
 * Copyright (c) 2007-2008.  Karl Skretting.  All rights reserved.
 * University of Stavanger, Institutt for data- og elektroteknikk
 * Mail:  karl.skretting@uis.no   Homepage:  http://www.ux.his.no/~karlsk/
 * 
 * HISTORY:  dd.mm.yyyy
 * Ver. 1.0  18.06.2007  KS: function (mp.MPBandMatrix) made and testet
 *           22.06.2007  KS: utvidet mer 
 * Ver. 2.0  18.06.2007  KS: Moved to mpv2, and extends AllMatrices
 * */

package mpv2;

import java.lang.*;
import java.util.Random;
// import mpv2.util.*;

/**
 * This class is a band diagonal matrix implementation of the AllMatrices superclass.
 * The N-by-K matrix <code>A</code> is represented as an array of column vectors,
 * and for each stored array (column vector) it is also stored how many zeros 
 * that preceed this array in the column vector of the matrix. 
 * Thus the lengths of the stored column vectors are usualle less than <code>N</code>.
 * 
 * @version 2.0  November 2008
 * @author  Karl Skretting (karl.skretting@uis.no)
 * @author  http://www.ux.uis.no/~karlsk/
 *
 * */
public class BandMatrix extends AllMatrices
implements Cloneable, java.io.Serializable {
    
    /**
     * Stores the number of zeros in the beginning of each column
     * If this variable is <code>null</code> then a non-sparse storing is used,
     * all column vectors are assumed
     * to start at first position and have length <code>N</code>,
     * i.e. <code>fv[k].length == N</code>.
     * */
    private int[] fn;
    
    /**
     * Stores the values of each dictionary entry.
     * */
    private double[][] fv;
    
/* ------------------------
   Constructors
 * ------------------------ */
    
    /** Construct a <code>N&times;K</code> matrix with only zero values.
     *
     * @param iN number of rows, i.e. length of column vectors of the matrix.
     * @param iK number of columns
     * */
    public BandMatrix(int iN, int iK){
        N = iN;
        K = iK;
        fn = new int[K];
        fv = new double[K][];
        for (int k=0; k<K; k++) fn[k] = N;
        System.out.println("BandMatrix created with " +
            " N = " + N + ", K = " + K + " and only zero values.");
        return;
    }
    
    /** Construct a <code>N&times;K</code> matrix or dictionary with values
     * from given one-dimensional array (which represents the matrix).
     * Length of the array should be <code>N*K</code>.
     *
     * @param iN number of rows, i.e. length of column vectors of the matrix.
     * @param iK number of columns
     * @param vals the values (ordered by column)
     * */
    public BandMatrix(int iN, int iK, double[] vals){
        N = iN;
        K = iK;
        fn = new int[K];
        fv = new double[K][];
        setAll(vals);
        System.out.println("BandMatrix created with " +
            " N = " + N + ", K = " + K + " and supplied values.");
        return;
    }
        
    /** Construct a matrix from a 2-D array.
   @param A    Two-dimensional array of doubles.
   @exception  IllegalArgumentException All rows must have the same length
     */
    public BandMatrix(double[][] A) {
        N = A.length;
        K = A[0].length;
        for (int n = 0; n < N; n++) {
            if (A[n].length != K) {
                throw new IllegalArgumentException
                    ("All rows must have the same length.");
            }
        }
        fn = new int[K];
        fv = new double[K][];
        for (int k=0; k<K; k++){
            int i1=0;      // frste element for vektoren i val
            int i2=N-1;    // og siste
            while ((A[i1][k]==0.0) && (i1<i2)) i1++;
            while ((A[i2][k]==0.0) && (i1<i2)) i2--;
            fn[k] = i1;
            fv[k] = new double[i2-i1+1];
            int i3 = 0;
            while (i1 <= i2) fv[k][i3++] = A[i1++][k];
        }
        System.out.println("BandMatrix created with " +
            " N = " + N + ", K = " + K + " and supplied matrix.");
        return;
    }
    
 /* ------------------------
   Private Methods
  * ------------------------ */
    
    private int getSupportLength(int k){
        return fv[k].length;
    }
    
    private int getSupportStart(int k){
        return fn[k];
    }
    
    private int getSupportEnd(int k){
        return (fn[k]+fv[k].length);
    }
    
 /* ------------------------
   Public Methods
  * ------------------------ */
    
    /** Return an entry of the matrix.
     * Note that the row, i.e. position in the column, is given first,
     * the second argument is the column number.
     * We should have: <code>0 <= n < N</code> and <code>0 <= k < K</code>.
     * If any argument is outside legal range 0.0 is returned without error or warning.
     *
     * @param n row number for the returned entry value.
     * @param k column number for the returned entry value.
     * */
    public double get(int n, int k){
        // rekkeflge for disse testene er viktig (egentlig en if else struktur)
        if ((k<0) || (k>=K) || (n<0) || (n>=N)) return 0.0;
        if ((n<getSupportStart(k)) || (n>=getSupportEnd(k))) return 0.0;
        return fv[k][n-fn[k]];
    }
    
    /** Set an entry (a value) in the matrix.
     * Note that the row, i.e. position in the column, is given first,
     * the second argument is the column number.
     * We should have: <code>0 <= n < N</code> and <code>0 <= k < K</code>.
     * If any argument is outside legal range nothing is done.
     *
     * @param n   row number for the entry value to be changed.
     * @param k   column number for the entry value to be changed.
     * @param val the value to be put into the given entry of the dictionary.
     * */
    public void set(int n, int k, double val){
        // rekkeflge for disse testene er viktig (egentlig en if else struktur)
        if ((k<0) || (k>=K) || (n<0) || (n>=N)) return;
        if ((n<getSupportStart(k)) || (n>=getSupportEnd(k))) {
            // extend support range
            double[] col = getColumn(k);
            col[n] = val;
            setColumn(k, col);
        } else {
            fv[k][n-fn[k]] = val;
        }
        return;
    }
    
    /** Copy a column of the matrix into argument d
     * Legal range of the integer argument is <code>0 <= k < K</code>.
     * If argument is out of range a length <code>N</code> array of zeros should be returned. <br>
     * The corresponding Matlab expression would be: <code>A(:,k+1)</code>.
     *
     * @param k number of the column in the matrix
     * @param d the given column of the matrix
     * */
    public void getColumn(int k, double[] d){
        if (d.length != N)  throw new IllegalArgumentException(
            "getColumn: argument d is not expected length N.");
        int fnk = fn[k];
        for (int n = 0; n<N; n++){
            if ((n<getSupportStart(k)) || (n>=getSupportEnd(k))){ 
                d[n] =0.0;
            } else {
                d[n] = fv[k][n-fnk];
            }
        }
        return;
    }

    /** Replace a column in the dictionary with the given column vector.
     * The column should be of length <code>N</code>.
     * We should have: <code>0 <= k < K</code>.
     * If any argument is outside legal dimension or range nothing is done.
     *
     * @param k   column number for the column to be changed.
     * @param col the value to be put into the given entry of the dictionary.
     * */
    public void setColumn(int k, double[] col){
        if ((k<0) || (k>=K) || (col.length != N)) return;
        int i1 = 0;       // frste element
        int i2 = N-1;     // og siste
        while ((col[i1]==0.0) && (i1<i2)) i1++;
        while ((col[i2]==0.0) && (i1<i2)) i2--;
        fn[k] = i1;
        fv[k] = new double[i2-i1+1];
        int i3 = 0;
        while (i1 <= i2) fv[k][i3++] = col[i1++];
        return;
    }
    
    /** Add a column of the matrix multiplied by a factor to a given vector.
     * Legal range of the integer argument is <code>0 <= k < K</code>. <br>
     * The corresponding Matlab expression would be: <code>x = x+factor*D(:,k+1)</code>.
     *
     * @param k number of the column in dictionary, i.e. matrix <code>D</code>.
     * @param factor a factor to multiply the column vector by.
     * @param x an array of length <code>N</code>.
     * */
    public void addColumn(int k, double factor, double[] x){
        // if (x.length != N)  throw new IllegalArgumentException(
        //     "addColumn: argument x is not expected length N.");
        if ((k<0) || (k>=K) || (x.length != N)) return;
        int n = fn[k];
        for (int i=0; i<fv[k].length; i++, n++) x[n] += factor*fv[k][i];
        return;
    }
    
    
    /** Set all entries of to matrix to the supplied new values
     * If argument is wrong length an IllegalArgumentException is thrown. <br>
     *
     * @param vals One-dimensional array of doubles, packed by columns (ala Fortran).
     * */
    public void setAll(double[] vals){
        if (N*K != vals.length) {
            throw new IllegalArgumentException
                ("Array length must be as total size of matrix (N*K).");
        }
        for (int k=0; k<K; k++){
            int i1=k*N;       // frste element for vektoren i val
            int i2=i1+N-1;    // og siste
            while ((vals[i1]==0.0) && (i1<i2)) i1++;
            while ((vals[i2]==0.0) && (i1<i2)) i2--;
            fn[k] = i1-k*N;
            fv[k] = new double[i2-i1+1];
            int i3 = 0;
            while (i1 <= i2) fv[k][i3++] = vals[i1++];
        }
    }

    /** Returns the inner product of two matrix column vectors.
     * Legal range is <code>0 <= k1 < K</code> and <code>0 <= k2 < K</code>. <br>
     * If <code>k1</code> or <code>k2</code> is out of range, 0.0 is returned.
     *
     * @param k1 number for the first column.
     * @param k2 number for the second column.
     * */
    public double innerProduct(int k1, int k2){
        if (k1<k2){
            int tmp = k1;
            k1 = k2;
            k2 = tmp;
        } // skal n ha  k2 <= k1
        if ((k2<0) || (k1>=K)) return 0.0;
        // vi skal n ha: 0 <= k2 <= k1 < K
        // calculate inner product
        double ip = 0.0;
        for (int n = Math.max(fn[k1], fn[k2]);
                n < Math.min(fn[k1]+fv[k1].length, fn[k2]+fv[k2].length);
                n++ ){
            ip += fv[k1][n-fn[k1]] * fv[k2][n-fn[k2]];
        }
        return ip;
    }
    
    /** Multiplies the matrix by an array.
     * The dimensions are: <code>A</code> is
     * <code>N&times;K</code>, <code>x</code> is <code>N&times;1</code>,
     * and <code>y</code> is <code>K&times;1</code>.<br>
     * The corresponding mulitplication in Matlab would be: <code>x = A*y</code>.
     *
     * @param y  the input array
     * @param x  the results as an array of length <code>N</code>.
     * */
    public void times(double[] y, double[] x){
        if (y.length != K)  throw new IllegalArgumentException(
            "times: argument y is not expected length K.");
        if (x.length != N)  throw new IllegalArgumentException(
            "times: argument x is not expected length N.");
        for (int n=0; n<N; n++) x[n] = 0.0;
        for (int k=0; k<K; k++){
            double yk = y[k];
            if (yk != 0.0){
                int n = fn[k];
                for (int i=0; i<fv[k].length; i++, n++) x[n] += fv[k][i]*yk;
            }
        }
    }
    
    /** Multiplies the transposed matrix by an array.
     * The dimensions are: <code>A</code> is
     * <code>N&times;K</code>, <code>x</code> is <code>N&times;1</code>,
     * and <code>y</code> is <code>K&times;1</code>.<br>
     * The corresponding mulitplication in Matlab would be: <code>y = A'*x</code>.
     *
     * @param x  the input array
     * @param y  the results as an array of length <code>K</code>.
     * */
    public void transposeTimes(double[] x, double[] y){
        if (y.length != K)  throw new IllegalArgumentException(
            "times: argument y is not expected length K.");
        if (x.length != N)  throw new IllegalArgumentException(
            "times: argument x is not expected length N.");
        for (int k=0; k<K; k++){
            y[k] = 0.0;
            int n = fn[k];
            for (int i=0; i<fv[k].length; i++, n++) y[k] += fv[k][i]*x[n];
        }
        return;
    }
    
}
