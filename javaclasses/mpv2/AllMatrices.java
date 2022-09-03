/*
 * abstract class:  AllMatrices
 *
 * Description:		This abstract class is the superclass for matrices
 *                  and dictionaries in this package.
 *
 * Kommentarer for brukerne (javadoc) skal være på engelsk, mens
 * det som er for eget bruk gjerne er norsk, og kommentert som dette,
 * (med stjerne i starten av hver linje, eller // i slutten)
 *
 * Copyright (c) 2008.  Karl Skretting.  All rights reserved.
 * University of Stavanger, Institutt for data- og elektroteknikk
 * Mail:  karl.skretting@uis.no   Homepage:  http://www.ux.his.no/~karlsk/
 *
 * HISTORY:  dd.mm.yyyy
 * Ver. 2.0  30.10.2008  KS: class defined and tested
 * Ver. 2.1  13.11.2008  KS: class redefined and reorganized
 *
 * */

package mpv2;

import java.lang.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.text.FieldPosition;
import java.io.PrintWriter;
// import java.io.BufferedReader;
// import java.io.StreamTokenizer;
// import mpv2.util.*;  // hypot is included in java.lang.Math (since ver. 1.5)

/**
 * mpv2 = Matrix package version 2
 * <P>
 * The abstract class AllMatrices contains common stuff for several kinds of matrices.
 *
 * <P>
 * There is a quite large number of methods available for matrices in general
 * and in this class specially. An overview may be helpful: </P>
 *
 * <P><UL>
 * <LI> Notation. <BR>
 *   The matrix, A (this), is N-by-K. 
 *   y (a column) and x (a row) are arrays (column vectors) with lengths N and K respectively. 
 * <LI> Class variables and constructors. <BR>
 *   The matrix dimension is included here (N-by-K) but not the actual values,
 *   how these should be stored is left to the implementing class.
 *   Constructors must be defined in the implementing class.
 *   Also deep copy as in <CODE>copy()</CODE> and <CODE>clone()</CODE> are not in 
 *   this abstract class and may be defined in the implementing class. </LI>
 * <LI> Abstract methods <BR>
 *   These are <CODE>get(i,j)</CODE> and <CODE>set(i,j,s)</CODE>,
 *   they must be defined in the implementing class as effective as possible, 
 *   without control of the indices. 
 *   Actually these are the only methods an implementing class need to define.  
 *   All other methods here access data by using these two methods. <BR>
 *   The implementing class may make more effective implementations of others methods, especially :
 *   <CODE>getColumn(k,y)</CODE>, <CODE>setColumn(k,y)</CODE>, and <CODE>addColumn(k,s,y)</CODE>,
 *   and <CODE>times(x,y)</CODE> which set y=Ax, and <CODE>transposeTimes(y,x)</CODE> which set x=A'y. <BR>
 *   The general methods here work best if these methods are effectively implemented. </LI>
 * <LI> Access methods, <B>get...</B> <BR>
 *   These methods return data stored in the object either as a single number or
 *   as an one-dimensional array of values. If several values of the matrix are returned
 *   they are always packed by columns. Row-packed versions may be defined in the implementing class.
 *   Also, the implementing class may define a get-method (or methods) that returns a pointer 
 *   to the structure that actually stores the matrix values.
 *   The methods included here are: <BR>
 *   <CODE>getN()</CODE> or the equivalent method <CODE>getRowDimension()</CODE>. <BR>
 *   <CODE>getK()</CODE> or the equivalent method <CODE>getColumnDimension()</CODE>. <BR>
 *   <CODE>getValue(i,j)</CODE> check that arguments are in valid range and if not just return zero. <BR>
 *   <CODE>getColumn(k)</CODE> return an array containing the column,
 *   and <CODE>getColumn(k,x)</CODE> put the column into array <CODE>x</CODE>. <BR>
 *   <CODE>getRow(n)</CODE> return an array containing the row,
 *   and <CODE>getRow(n,x)</CODE> put the row into array <CODE>x</CODE>. <BR>
 *   <CODE>getAll()</CODE> return an array containing all matrix elements (by columns), 
 *   and <CODE>setAll(vals)</CODE> put all matrix elements into array <CODE>vals</CODE>. <BR>
 *   <CODE>getSubMatrix(...)</CODE> return an array containing elements (by columns)
 *   from a part of the matrix as given by the arguments. </LI>
 * <LI> Access methods, <B>set...</B> <BR>
 *   These methods set a given element (or elements) of the matrix to values supplied
 *   in the argument list, either as a single number or as an one-dimensional array of values. 
 *   The methods included here are: <BR>
 *   <CODE>setValue(i,j,s)</CODE> check that arguments are in valid range and if not just return. <BR>
 *   <CODE>setColumn(k,y)</CODE> put values from array <CODE>y</CODE> into the matrix. <BR>
 *   <CODE>setRow(n,x)</CODE> put values from array <CODE>x</CODE> into the matrix. <BR>
 *   <CODE>setAll(vals)</CODE> put values from array <CODE>vals</CODE> into the matrix. <BR>
 *   <CODE>setSubMatrix(...,vals)</CODE> put values from array <CODE>vals</CODE> into 
 *   a part of the matrix as given by the arguments. </LI>
 * <LI> Methods where the matrix operates on a vector. <BR>
 *   The methods included here are: <BR>
 *   <CODE>addColumn(k,s,y)</CODE> add a column to y, i.e. <CODE>y = y + s*A(:,k)</CODE>. <BR>
 *   <CODE>times(x)</CODE> return <CODE>A*x</CODE> and
 *   <CODE>times(x,y)</CODE> set <CODE>y = A*x</CODE> (return void). <BR>
 *   <CODE>transposeTimes(y)</CODE> return <CODE>A'*y</CODE> and
 *   <CODE>transposeTimes(y,x)</CODE> set <CODE>x = A'*y</CODE> (return void). <BR>
 *   <CODE>solve(y)</CODE> solve equation Ax=y, return <CODE>pinv(A)*y</CODE> and
 *   <CODE>solve(y,x)</CODE> set <CODE>x = pinv(A)*y</CODE> (return void). <BR> </LI>
 * <LI> Update methods, <B>eq...</B> <BR>
 *   General and flexible methods that allow to update a matrix of any kind (any class)
 *   based on other matrices which may also be of any kind (any classes).
 *   These methods update this matrix, denoted <CODE>A</CODE>.
 *   The <CODE>eq</CODE>-prefix is meant to mimic '=', an (assign) equal sign.
 *   All methods are void, and do not change any of the arguments, it only update this (A).
 *   The arguments can be <CODE>B</CODE> and <CODE>C</CODE> which are matrices of any kind, 
 *   <CODE>D</CODE> is a diagonal matrix and <CODE>P</CODE> is a permutation matrix, 
 *   and <CODE>s</CODE> is a double. 
 *   Note that this (A) may be used as an argument instead of another matrix, 
 *   for example instead of B. Then this argument is of course changed.
 *   The methods included here are: <BR>
<PRE>
     A = I,         eqIdentity(), identity, ones on main diagonal zero elsewhere. 
     A = 0,         eqZeros(), all values to zero. 
     A = 1,         eqOnes(), all values to 1. 
     A = s,         eqConstant(s), all values to s. 
     A = rand,      eqRandom(), all values to random values between 0 and 1. 
     A = B,         eqCopy(B), copy values, note A and B may be matrices of two different classes.
     A = B(..),     eqCopy(B,...), copy selected rows and columns of B. 
     A = -B,        eqNegate(B). 
     A = B*s,       eqProduct(B,s). 
     A = B+C,       eqSum(B,C). 
     A = B-C,       eqDifference(B,C). 
     A = B.*C,      eqEProduct(B,C). Elementwise multipliccation.
     A = B./C,      eqEQuotient(B,C). Elementwise (right) division. Left division by changing arguments. 
     A = B',        eqTranspose(B). 
     A = inv(B),    eqInverse(B), if inverse not exist, the Moore-Penrose pseudoinverse. 
     A = B*C,       eqProduct(B,C). 
     A = B'*C,      eqTProduct(B,C). 
     A = inv(B)*C,  eqIProduct(B,C). Perhaps also variants: 
                    eqNTProduct(B,C), eqNIProduct(B,C), eqTTProduct(B,C), eqIIProduct(B,C). 
     A = P*B,       eqPermuteRows(P,B). 
     A = B*P,       eqPermuteColumns(B,P).
     A = D*B,       eqScaleRows(D,B). 
     A = B*D,       eqScaleColumns(B,D). 
</PRE>
 *   A class that implements AllMatrices may define methods that returns a matrix (of the 
 *   implementing class) instead of a void, <CODE>eqName(...) -> Name(...)</CODE>.
 *   This implementation is quite simple, example with class SimpleMatrix is <BR>
<PRE>
        public static SimpleMatrix sum(AllMatrices B, AllMatrices C){
            SimpleMatrix A = new SimpleMatrix(B.getN(), B.getK());
            A.eqSum(B,C);  // use the general method
            return A;
        }
</PRE></LI>
 * <LI> Update methods, <B>pluseq...</B> <BR>
 *   Like <B>eq...</B> above, but here the prefix is meant to mimic '+='.
 *   The methods included here are: <BR>
<PRE>
     A = s1*A + s2*(u*v'), pluseqOuterProduct(s1,s2,u,v). 
</PRE></LI>
 * <LI> Methods that return matrix properties. <BR>
 *   These are: <CODE>norm1()</CODE>, <CODE>normInf()</CODE>,
 *   <CODE>normF()</CODE>, <CODE>trace()</CODE>,
 *   <CODE>det()</CODE>, <CODE>rank()</CODE>,
 *   <CODE>cond()</CODE> and <CODE>norm2()</CODE>. <BR>
 *   There may also be boolean methods defined like <CODE>isOrthogonal()</CODE>,
 *   <CODE>isSquare()</CODE>, <CODE>isNormal()</CODE>, <CODE>isInvertible()</CODE>
 *   and <CODE>isSymmetric()</CODE>. </LI>
 * <LI> Methods that return matrix decompositions. <BR>
 *   Decompositions first copy the matrix, which may be of any kind, into a JamaMatrix object,
 *   and then use this object for decompositions. Each method returns an object of
 *   the corresponding class. The decompositions are almost exactly as in the Jama package,
 *   they are:  <BR> 
 *   <UL>
 *     <LI>Cholesky Decomposition of symmetric, positive definite matrices, <CODE>chol()</CODE>.
 *     <LI>LU Decomposition of rectangular matrices, <CODE>lu()</CODE>.
 *     <LI>QR Decomposition of rectangular matrices, <CODE>qr()</CODE>.
 *     <LI>Singular Value Decomposition of rectangular matrices, <CODE>svd()</CODE>.
 *     <LI>Eigenvalue Decomposition of both symmetric and nonsymmetric square matrices, <CODE>eig()</CODE>.
 *   </UL> </LI>
 * <LI> I/O methods, i.e. print. <BR>
 *   Print the matrix to standard out or a stream using <CODE>print(...)</CODE>. </LI>
 * <LI> Others methods. <BR>
 *   There may be methods that do not fit into any of the groups above.
 *   For example: <BR>
 *   <CODE>innerProduct(k1,k2)</CODE> The inner product of two column vectors. </LI>
 * </UL></P>
 *
 * <P>
 * @author  Karl Skretting, University of Stavanger (UiS), (karl.skretting@uis.no)
 * @version November 2008
 */

//  ?????
//  TODO: en burde nok unngått bruk av spesielle klasser her (JamaMatrix) og heller
//  satset på void metoder som setter resultatet i en AllMatrices object
//  Et eksempel er laget for metoden plus.
//  Kanskje heller resultat til A (this), A = B + C, A = A + B, o.s.v
/*  Det er da følgende metoder som kan være aktuelle, alle er av type void!
 *  A er dette AllMatrices objektet (this) og det får nye verdier i operasjonen (NxK)
 *  B og C er andre AllMatrices objekt og gis som argument til metoden, disse endres
 *  ikke (uten hvis et av de er this). s er en skalar.
 *  Elementvise metoder:
 *  A = B, A = -B, A = s*B, A = B+C, A=B-C, A = B+s*C, A = B.*C, A = B./C, A = B.\C
 *  Matrise metoder:
 *  A = B', A = inv(B), A = pinv(B) (Moore-Penrose), A = linv(B) (A*B=I), A = rinv(B) (B*A=I),
 *  A = B*C, A = B'*C, A = B*C', A = B'*C, A = B'*C'
 *  A = pinv(B)*C (solve B*A=C), A = B*pinv(C) (solve A*C=B, i.e. C'*A'=B')
 */

public abstract class AllMatrices
implements Cloneable, java.io.Serializable {
    
/* ------------------------
   Class variables
 * ------------------------ */
    
    /** Row and column dimensions.
   @serial row dimension.
   @serial column dimension.
     */
    protected int N, K;            // matrise er her NxK, i Jama var det mxn
    
/* ------------------------
   Constructors, these are for the implementing class
 * ------------------------ */
 
/* ------------------------
   Abstract methods, must be defined in the implementing class
 * ------------------------ */
 
    /** Get a single element.
   @param i    Row index.
   @param j    Column index.
   @return     A(i,j)
   @exception  ArrayIndexOutOfBoundsException
     */
    public abstract double get(int i, int j);

    /** Set a single element.
   @param i    Row index.
   @param j    Column index.
   @param s    the value to be set into A(i,j).
   @exception  ArrayIndexOutOfBoundsException
     */
    public abstract void set(int i, int j, double s);
 
/* ------------------------
   Access methods, get...
 * ------------------------ */
    
    /** Get number of rows in the matrix, i.e. length of each column vector.
   @return     N, the number of rows.
     */
    public int getN() { 
    	return N;
    }
    
    /** Get number of columns in the matrix, i.e. length of each row vector.
   @return     K, the number of columns.
     */
    public int getK() { 
    	return K;
    }
    
    // and same name as in Jama.Matrix
    /** Get row dimension.
   @return     N, the number of rows.
     */
    public int getRowDimension() {
        return N;
    }
    
    /** Get column dimension.
   @return     K, the number of columns.
     */
    public int getColumnDimension() {
        return K;
    }
       
    /** Get a single element, i.e. the value of an entry of the matrix.
     * If arguments are outside legal range a zero is returned without error or warning.
     *
     * @param n   Row index for the returned entry value.
     * @param k   Column index for the returned entry value.
     * @return    The matrix element, A(n,k).
     * */
    public double getValue(int n, int k) {
        if ( (n>=0) & (n<N) & (k>=0) & (k<K) ) {
            return get(n,k);
        } else {
            return 0.0;
        }
    }
    
    /** Get a column of the matrix.
     * If argument is out of range a length <code>N</code> array of zeros should be returned.
     *
    @param k number of the column in the matrix
    @return the column
     */
    public double[] getColumn(int k) {
        double[] y = new double[N];
        getColumn(k, y);
        return y;
    }
    
    /** Get a column of the matrix into argument y.
     * Legal range of the integer argument is <code>0 <= k < K</code>.
     *
    @param k number of the column in the matrix
    @param y is set to the given column of the matrix
    @exception  IllegalArgumentException
     */
    public void getColumn(int k, double[] y) {
        if (y.length != N)  throw new IllegalArgumentException(
            "getColumn: argument y is not expected length N.");
        if ( (k>=0) & (k<K) ) {
        	for (int n = 0; n<N; n++) y[n] = get(n,k);
        } else {
        	for (int n = 0; n<N; n++) y[n] = 0.0;
        }
    }
        
    /** Get a row of the matrix.
     * Legal range of the integer argument is <code>0 <= n < N</code>.
     * If argument is out of range a length <code>K</code> array of zeros should be returned.
     *
    @param n number of the row in the matrix A.
    @return the row
     * */
    public double[] getRow(int n){
        double[] x = new double[K];
        getRow(n, x);
        return x;
    }
    
    /** Get a row of the matrix into argument x.
     * Legal range of the integer argument is <code>0 <= n < N</code>.
     *
    @param n number of the row in the matrix.
    @param x is set to the given row of matrix.
    @exception  IllegalArgumentException
     */
    public void getRow(int n, double[] x){
        if (x.length != K)  throw new IllegalArgumentException(
            "getRow: argument x is not expected length K.");
        if ( (n>=0) & (n<N) ) {
        	for (int k = 0; k<K; k++) x[k] = get(n,k);
        } else {
        	for (int k = 0; k<K; k++) x[k] = 0.0;
        }
    }
    
    /** Get all entries of the matrix, as a one-dimensional column-packed array.
    @return all entries in a one-dimensional array
     */
    public double[] getAll(){
        double[] vals = new double[N*K];
        getAll(vals);
        return vals;
    }
    
    /** Get all entries of the matrix into argument vals in a column-packed way.
     *
    @param vals an one-dimensional array where the values are copied into
    @exception  IllegalArgumentException
     */
    public void getAll(double[] vals){
        if (vals.length != (N*K)) throw new IllegalArgumentException(
            "getAll: argument vals is not expected length (N*K).");
      	//
      	double[] y = new double[N];
      	int i = 0;
        for (int k = 0; k<K; k++) {
	        getColumn(k, y);
        	for (int n = 0; n<N; n++, i++){
        		vals[i] = y[n];
        	}
        }
    }
    
    /** Get a one-dimensional column packed copy of a submatrix.
   @param i0   Initial row index
   @param i1   Final row index
   @param j0   Initial column index
   @param j1   Final column index
   @return     Submatrix elements packed in a one-dimensional array by columns.
     */
    public double[] getSubMatrix(int i0, int i1, int j0, int j1) {
        double[] vals = new double[(i1-i0+1)*(j1-j0+1)];
        int vi = 0;
        for (int j = j0; j <= j1; j++) {
            for (int i = i0; i <= i1; i++) {
                vals[vi++] = getValue(i,j);  // returns 0.0 if out of range
            }
        }
        return vals;
    }
    
    /** Get a one-dimensional column packed copy of a submatrix.
   @param r    Array of row indices.
   @param c    Array of column indices.
   @return     Submatrix elements packed in a one-dimensional array by columns.
     */
    public double[] getSubMatrix(int[] r, int[] c) {
        double[] vals = new double[r.length * c.length];
        int vi = 0;
        for (int j = 0; j < c.length; j++) {
            for (int i = 0; i < r.length; i++) {
                vals[vi++] = getValue(r[i],c[j]);  // returns 0.0 if out of range
            }
        }
        return vals;
    }
    
    /** Get a one-dimensional column packed copy of a submatrix.
   @param i0   Initial row index
   @param i1   Final row index
   @param c    Array of column indices.
   @return     Submatrix elements packed in a one-dimensional array by columns.
     */
    public double[] getSubMatrix(int i0, int i1, int[] c) {
        double[] vals = new double[(i1-i0+1) * c.length];
        int vi = 0;
        for (int j = 0; j < c.length; j++) {
            for (int i = i0; i <= i1; i++) {
                vals[vi++] = getValue(i,c[j]);  // returns 0.0 if out of range
            }
        }
        return vals;
    }
    
    /** Get a one-dimensional column packed copy of a submatrix.
   @param r    Array of row indices.
   @param j0   Initial column index
   @param j1   Final column index
   @return     Submatrix elements packed in a one-dimensional array by columns.
     */
    public double[] getSubMatrix (int[] r, int j0, int j1) {
        double[] vals = new double[r.length * (j1-j0+1)];
        int vi = 0;
        for (int j = j0; j <= j1; j++) {
            for (int i = 0; i < r.length; i++) {
                vals[vi++] = getValue(r[i],j);  // returns 0.0 if out of range
            }
        }
        return vals;
    }
    
/* ------------------------
   Access methods, set...
 * ------------------------ */
    
    /** Set a single element, i.e. an entry (a value) in the matrix.
     * We should have: <code>0 <= n < N</code> and <code>0 <= k < K</code>.
     * If any argument is outside legal range nothing is done.
     *
     * @param n   Row index for the entry value to be changed.
     * @param k   Column index for the entry value to be changed.
     * @param s   Value to be put into the given entry of the matrix.
     * */
    public void setValue(int n, int k, double s) {
        if ( (n>=0) & (n<N) & (k>=0) & (k<K) ) {
            set(n,k,s);
        }
    }

    /** Copy an array (y) into a column of the matrix.
     * Legal range of the integer argument is <code>0 <= k < K</code>.
     *
    @param k number of the column in the matrix
    @param y the values that are copied into the given column of the matrix
    @exception  IllegalArgumentException
     * */
    public void setColumn(int k, double[] y){
        if ((k<0) || (k>=K)) return;
        if (y.length != N)  throw new IllegalArgumentException(
            "setColumn: argument y is not expected length N.");
        for (int n = 0; n<N; n++){ 
        	set(n,k,y[n]);
        }
    }
    
    /** Copy an array (x) into a row of the matrix.
     * Legal range of the integer argument is <code>0 <= n < N</code>.
     *
    @param n number of the row in the matrix <code>A</code>.
    @param x the given row of matrix <code>A</code>.
    @exception  IllegalArgumentException
     * */
    public void setRow(int n, double[] x){
        if ((n<0) || (n>=N)) return;
        if (x.length != K)  throw new IllegalArgumentException(
            "setRow: argument x is not expected length K.");
        for (int k = 0; k<K; k++){ 
        	set(n,k,x[k]);
        }
    }
    
    /** Set all entries of the matrix to the supplied new values.
     * If argument is wrong length an IllegalArgumentException is thrown. 
     *
    @param vals One-dimensional array of doubles, packed by columns (ala Fortran).
    @exception  IllegalArgumentException
     * */
    public void setAll(double[] vals){
        if (N*K != vals.length) { throw new IllegalArgumentException
                ("Array length must as total size of matrix (N*K).");
        }
      	double[] y = new double[N];
      	int i = 0;
        for (int k = 0; k<K; k++) {
        	for (int n = 0; n<N; n++, i++){
        		y[n] = vals[i];
        	}
	        setColumn(k, y);
        }
    }
    
    // setSubMatrix()
    
/* ------------------------
   Methods where the matrix operates on an array, i.e. a (column) vector.
 * ------------------------ */
   
    /** Add a column of the matrix multiplied by a factor to y, set y[n] = y[n] + s*A[n][k].
     * Legal range of the integer argument is <code>0 <= k < K</code>. <br>
     *
    @param k number of the column the matrix A.
    @param s a scale factor to multiply the column vector by.
    @param y an array of length N.
    @exception  IllegalArgumentException
     * */
    public void addColumn(int k, double s, double[] y){
        if ((k<0) || (k>=K)) return;
        if (y.length != N)  throw new IllegalArgumentException(
            "addColumn: argument y is not expected length N.");
        double[] col = new double[N];
        getColumn(k, col);
        for (int n = 0; n<N; n++) y[n] += s*col[n];
    }

    /** Multiplies the matrix by an array (vector), return y = A*x.
     *
     * @param x  the input array
     * @return   the results as an array of length N.
     * */
    public double[] times(double[] x){
        double[] y = new double[N];
        times(x, y);
        return y;
    }
    
    /** Multiplies the matrix by a SparseVector x, return y = A*x.
     *
     * @param x  the input sparse vector of class SparseVector
     * @return   the results as an array of length N.
     * */
    public double[] times(SparseVector x){
        double[] y = new double[N];   // for the result
        times(x, y);
        return y;
    }
    
    /** Multiplies the matrix by an array (vector), set y = A*x.
     *
    @param x  the input array
    @param y  the results as an array of length N.
    @exception  IllegalArgumentException
     * */
    public void times(double[] x, double[] y){
        if (x.length != K)  throw new IllegalArgumentException(
            "times: argument x is not expected length K.");
        if (y.length != N)  throw new IllegalArgumentException(
            "times: argument y is not expected length N.");
        for (int n=0; n<N; n++) y[n] = 0.0;
        for (int k=0; k<K; k++){ 
            if (x[k] != 0.0) this.addColumn(k, x[k], y);
        }
    }
    
    // probably faster if x is full and with zeros
    /** Multiplies the matrix by a SparseVector x, set y = A*x.
     *
    @param x  the input sparse vector
    @param y  the results as an array of length N.
    @exception  IllegalArgumentException
     * */
    public void times(SparseVector x, double[] y){
        if (x.getLength() != K)  throw new IllegalArgumentException(
            "times: argument x is not expected length K.");
        if (y.length != N)  throw new IllegalArgumentException(
            "times: argument y is not expected length N.");
        for (int n=0; n<N; n++) y[n] = 0.0;
        for (int k=0; k<K; k++){ 
            double xk = x.get(k);
            if (xk != 0.0) this.addColumn(k, xk, y);
        }
    }

    /** Multiplies the transposed matrix by an array (vector), return x = A'*y.
     *
     * @param y  the input array
     * @return   the results as an array of length K.
     * */
    public double[] transposeTimes(double[] y){
        double[] x = new double[K];
        transposeTimes(y, x);
        return x;
    }
    
    /** Multiplies the transposed matrix by a SparseVector y, return x = A'*y.
     *
    @param y  the input sparse vector
    @return   the results as an array of length K.
     * */
     public double[] transposeTimes(SparseVector y){
         double[] x = new double[K];
         transposeTimes(y, x);
         return x;
     }
    
    /** Multiplies the transposed matrix by an array (vector), set x = A'*y.
     *
    @param y  the input array
    @param x  the results as an array of length K.
    @exception  IllegalArgumentException
     * */
    public void transposeTimes(double[] y, double[] x){
        if (y.length != N)  throw new IllegalArgumentException(
            "transposeTimes: argument y is not expected length N.");
        if (x.length != K)  throw new IllegalArgumentException(
            "transposeTimes: argument x is not expected length K.");
        double[] col = new double[N];
        for (int k=0; k<K; k++) {
            getColumn(k, col);
            x[k] = 0.0; 
            for (int n=0; n<N; n++){ 
            	x[k] += col[n]*y[n];
            }
        }
    }
    
    /** Multiplies the transposed matrix by a SparseVector y, set x = A'*y.
     *
    @param y  the input sparse vector
    @param x  the results as an array of length K.
    @exception  IllegalArgumentException
     * */
    public void transposeTimes(SparseVector y, double[] x){
        if (y.getLength() != N)  throw new IllegalArgumentException(
            "transposeTimes: argument y is not expected length N.");
        if (x.length != K)  throw new IllegalArgumentException(
            "transposeTimes: argument x is not expected length K.");
        double[] col = new double[N];
        for (int k=0; k<K; k++) {
                getColumn(k, col);
                x[k] = y.innerProduct(col);
        }
    }
        
    // solve(y) return x such that Ax=y (in LS-sense), x = pinv(A)*y    

/* ------------------------
   Update methods, eq...
 * ------------------------ */

    /** Set the matrix (this = A) to identity, A = I.
     * The elements along the main diagonal is set to 1, the other elements to 0. 
     *
     * */
    public void eqIdentity(){
      	double[] y = new double[N];  // a column, initially all zeros
        for (int k = 0; k<K; k++) {
        	if (k < N) y[k] = 1.0;
	        setColumn(k, y);
        	if (k < N) y[k] = 0.0;
        }
    }
    
    /** Set the matrix (this = A) to zeros, A = 0.
     * */
    public void eqZeros(){
      	double[] vals = new double[N*K];   // iitially only zeros
        setAll(vals);
    }

    /** Set the matrix (this = A) to ones, A = 1.
     * */
    public void eqOnes(){
      	double[] y = new double[N]; 
        for (int n = 0; n<N; n++) {
	        y[n] = 1.0;
        }
        for (int k = 0; k<K; k++) {
	        setColumn(k, y);
        }
    }

    /** Set the matrix (this = A) to a constant s, A = s.
     *
    @param s    A single value
     * */
    public void eqConstant(double s){
      	double[] y = new double[N]; 
        for (int n = 0; n<N; n++) {
	        y[n] = s;
        }
        for (int k = 0; k<K; k++) {
	        setColumn(k, y);
        }
    }

    /** Set the matrix (this = A) to random elements, A = rand.
     *  all random elements are different and  between 0 and 1.
     * */
    public void eqRandom(){
      	double[] vals = new double[N*K]; 
        for (int i = 0; i<(N*K); i++) {
	        vals[i] = Math.random();
       	}
        setAll(vals);
    }

    /** Set the matrix (this = A) to B, A = B.
     *
    @param B    Matrix of same size as A
    @exception  IllegalArgumentException
     * */
    public void eqCopy(AllMatrices B){
    	checkMatrixDimensions(B);    // B should be same size as this
        setAll( B.getAll() );
    }

    /** Set the matrix (this = A) to a submatrix of B, A = B(i0:i1, j0:j1).
     *  The submatrix of B should be same size as A.
     *
    @param B    Matrix 
    @param i0   Initial row index
    @param i1   Final row index
    @param j0   Initial column index
    @param j1   Final column index
    @exception  IllegalArgumentException
     */
    public void eqCopy(AllMatrices B, int i0, int i1, int j0, int j1) {
        if ((i1-i0+1) != N || (j1-j0+1) != K) {
            throw new IllegalArgumentException("Submatrix dimensions must agree to this.");
        }
        setAll( B.getSubMatrix(i0, i1, j0, j1) );
    }
    
    /** Set the matrix (this = A) to a submatrix of B, A = B(r[], c[]).
     *  The submatrix of B should be same size as A.
     *
    @param B    Matrix 
    @param r    Array of row indices.
    @param c    Array of column indices.
    @exception  IllegalArgumentException
     */
    public void eqCopy(AllMatrices B, int[] r, int[] c) {
        if (r.length != N || c.length != K) {
            throw new IllegalArgumentException("Submatrix dimensions must agree to this.");
        }
        setAll( B.getSubMatrix(r, c) );
    }
    
    /** Set the matrix (this = A) to a submatrix of B, A = B(i0:i1, c[]).
     *  The submatrix of B should be same size as A.
     *
    @param B    Matrix 
    @param i0   Initial row index
    @param i1   Final row index
    @param c    Array of column indices.
    @exception  IllegalArgumentException
     */
    public void eqCopy(AllMatrices B, int i0, int i1, int[] c) {
        if ((i1-i0+1) != N || c.length != K) {
            throw new IllegalArgumentException("Submatrix dimensions must agree to this.");
        }
        setAll( B.getSubMatrix(i0, i1, c) );
    }
    
    /** Set the matrix (this = A) to a submatrix of B, A = B(r[], j0:j1).
     *  The submatrix of B should be same size as A.
     *
    @param B    Matrix 
    @param r    Array of row indices.
    @param j0   Initial column index
    @param j1   Final column index
    @exception  IllegalArgumentException
     */
    public void eqCopy(AllMatrices B, int[] r, int j0, int j1) {
        if (r.length != N || (j1-j0+1) != K) {
            throw new IllegalArgumentException("Submatrix dimensions must agree to this.");
        }
        setAll( B.getSubMatrix(r, j0, j1) );
    }

    /** Set the matrix (this = A) to the negative of B, A = -B.
     *
    @param B    Matrix of same size as A
    @exception  IllegalArgumentException
     * */
    public void eqNegate(AllMatrices B){
    	checkMatrixDimensions(B);    // B should be same size as this
      	double[] vals = B.getAll(); 
        for (int i = 0; i<(N*K); i++) {
	        vals[i] = -vals[i];
       	}
        setAll(vals);
    }

    /** Set the matrix (this = A) to B*s, A = B*s.
     *
    @param B    Matrix of same size as A
    @exception  IllegalArgumentException
     * */
    public void eqProduct(AllMatrices B, double s){
    	checkMatrixDimensions(B);    // B should be same size as this
      	double[] vals = B.getAll(); 
        for (int i = 0; i<(N*K); i++) {
	        vals[i] = s*vals[i];
       	}
        setAll(vals);
    }

    /** Set the matrix (this = A) to sum of B and C, A = B + C.
     *
    @param B    Matrix of same size as A
    @param C    Matrix of same size as A
    @exception  IllegalArgumentException
     * */
    public void eqSum(AllMatrices B, AllMatrices C){
    	checkMatrixDimensions(B);    // B should be same size as this
    	checkMatrixDimensions(C);    // C should be same size as this
      	double[] vals = B.getAll(); 
      	double[] valsC = C.getAll(); 
        for (int i = 0; i<(N*K); i++) {
	        vals[i] += valsC[i];
       	}
        setAll(vals);
    }

    /** Set the matrix (this = A) to difference of B and C, A = B - C.
     *
    @param B    Matrix of same size as A
    @param C    Matrix of same size as A
    @exception  IllegalArgumentException
     * */
    public void eqDifference(AllMatrices B, AllMatrices C){
    	checkMatrixDimensions(B);    // B should be same size as this
    	checkMatrixDimensions(C);    // C should be same size as this
      	double[] vals = B.getAll(); 
      	double[] valsC = C.getAll(); 
        for (int i = 0; i<(N*K); i++) {
	        vals[i] -= valsC[i];
       	}
        setAll(vals);
    }

    /** Set the matrix (this = A) to elementwise multiplication of B and C, A = B.*C.
     *
    @param B    Matrix of same size as A
    @param C    Matrix of same size as A
    @exception  IllegalArgumentException
     * */
    public void eqEProduct(AllMatrices B, AllMatrices C){
    	checkMatrixDimensions(B);    // B should be same size as this
    	checkMatrixDimensions(C);    // C should be same size as this
      	double[] vals = B.getAll(); 
      	double[] valsC = C.getAll(); 
        for (int i = 0; i<(N*K); i++) {
	        vals[i] = vals[i]*valsC[i];
       	}
        setAll(vals);
    }

    /** Set the matrix (this = A) to elementwise division of B and C, A = B./C.
     *  This is right division, left division can be done by switching arguments.
     *
    @param B    Matrix of same size as A
    @param C    Matrix of same size as A
    @exception  IllegalArgumentException
     * */
    public void eqEQuotient(AllMatrices B, AllMatrices C){
    	checkMatrixDimensions(B);    // B should be same size as this
    	checkMatrixDimensions(C);    // C should be same size as this
      	double[] vals = B.getAll(); 
      	double[] valsC = C.getAll(); 
        for (int i = 0; i<(N*K); i++) {
	        vals[i] = vals[i]/valsC[i];
       	}
        setAll(vals);
    }

    /** Set the matrix (this = A) to the transposed of B, A = B'
     *  Matrix dimension must agree.
     *
    @param B    Matrix of 'opposite' size as A
    @exception  IllegalArgumentException
     * */
    public void eqTranspose(AllMatrices B){
        if (B.getK() != N || B.getN() != K) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
      	double[] vals = B.getAll();      // B is K-by-N
      	double[] colA = new double[N]; 
        for (int k = 0; k<K; k++) {
        	for (int n = 0; n<K; n++) {
        		colA[n] = vals[n*K+k];
        	}
        	setColumn(k,colA);
       	}
    }

    /** Set the matrix (this = A) to the inverse of B, A = inv(B)
     *  Matrix dimension must agree, if A is N-by-K then B is K-by-N
     *  If the (normal) inverse does not exist, 
     *  then the Moore-Penrose pseudoivnverse should be returned
     *
    @param B    Matrix of 'opposite' size as A
    @exception  IllegalArgumentException
     * */
    public void eqInverse(AllMatrices B){
        if (B.getK() != N || B.getN() != K) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
        boolean useJama = (N != K);
        if (N == K) {
            LUPDecomposition lup = new LUPDecomposition(B);  // tar lup av B
            if (lup.isNonsingular()) {
                DiagonalMatrix D = new DiagonalMatrix(N,1.0);
                double[] vals = lup.solveArray(D);
                setAll(vals);
                // System.out.println("Matrix inversion done my by own LUP.");
            } else {
                useJama = true;
            }
        }
        if (useJama) {  // dette er nok ikke helt Moore-Penrose pseudoivnverse
            System.out.println("Info: use JamaMatrix in AllMatrices.eqInverse(...)");
            JamaMatrix jamaB = new JamaMatrix(B);
            JamaMatrix C = jamaB.solve(JamaMatrix.identity(N,N));
            double[] vals = C.getAll();      
            setAll(vals);
        }
    }
 
    /** Set the matrix (this = A) to product of B and C, A = B * C.
     *
    @param B    Matrix of size N-by-L
    @param C    Matrix of size L-by-K, size of result is N-by-K
    @exception  IllegalArgumentException
     * */
    public void eqProduct(AllMatrices B, AllMatrices C){
        if (B.getN() != N || B.getK() != C.getN() || C.getK() != K) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
        double[] colA = new double[N];  // a column of A
        double[] colC = new double[B.getK()];  // a column of C
        for (int k = 0; k < K; k++) {
            C.getColumn(k, colC);
            B.times(colC, colA);
            this.setColumn(k, colA);
        }
    }

    /** Set the matrix (this = X) to product of D and B, X = D * B.
     *  D is a diagonal matrix, thus this is scaling of rows of B
     *
    @param D    Matrix of size N-by-N (the (pseudo)inverse is N-by-L)
    @param B    Matrix of size N-by-K, size of result is N-by-K
    @exception  IllegalArgumentException
     * */
    public void eqProduct(DiagonalMatrix D, AllMatrices B){
        if (D.getK() != N || D.getN() != B.getN() || B.getK() != K) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
        for (int n=0; n<N; n++) {
            double temp = D.get(n,n);      
            for (int k=0; k<K; k++) set(n, k, temp*B.get(n, k));
        }
    }
    
    /** Set the matrix (this = A) to product of B transposed and C, A = B' * C.
     *
    @param B    Matrix of size L-by-N (the transposed is N-by-L)
    @param C    Matrix of size L-by-K, size of result is N-by-K
    @exception  IllegalArgumentException
     * */
    public void eqTProduct(AllMatrices B, AllMatrices C){
        if (B.getK() != N || B.getN() != C.getN() || C.getK() != K) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
        double[] colA = new double[N];  // a column of A
        double[] colC = new double[C.getN()];  // a column of C
        for (int k = 0; k < K; k++) {  // for the columns in C
            C.getColumn(k, colC);
            B.transposeTimes(colC, colA);
            this.setColumn(k, colA);
        }
    }

    /** Set the matrix (this = X) to product of A inverse and B, X = inv(A) * B.
     *  It should set this to the least square solution X to the equation: A*X=B.
     *  The JamaMatrix class is used, and the arguments names are as in that
     *  class, hopefully this helps not mixing them.
     *
    @param A    Matrix of size L-by-N (the (pseudo)inverse is N-by-L)
    @param B    Matrix of size L-by-K, size of result is N-by-K
    @exception  IllegalArgumentException
     * */
    public void eqIProduct(AllMatrices A, AllMatrices B){
        if (A.getK() != N || A.getN() != B.getN() || B.getK() != K) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
        JamaMatrix jamaA = new JamaMatrix(A);
        JamaMatrix jamaB = new JamaMatrix(B);
        JamaMatrix jamaX = jamaA.solve(jamaB);
      	double[] vals = jamaX.getAll();      
        setAll(vals);
    }
    
    /** Set the matrix (this = X) to product of D inverse and B, X = inv(D) * B.
     *  D is a diagonal matrix, thus this is scaling of rows of B
     *
    @param D    Matrix of size N-by-N 
    @param B    Matrix of size N-by-K, size of result is N-by-K
    @exception  IllegalArgumentException
     * */
    public void eqIProduct(DiagonalMatrix D, AllMatrices B){
        if (D.getK() != N || D.getN() != B.getN() || B.getK() != K) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
        for (int n=0; n<N; n++) {
            double temp = 1.0/D.get(n,n);      
            for (int k=0; k<K; k++) set(n, k, temp*B.get(n, k));
        }
    }
    
    /** Permute the rows of matrix B and put result in this, A = P * B.
     *
    @param P    Permutation matrix of size N-by-N 
    @param B    Matrix of size N-by-K, size of result (this) is also N-by-K
    @exception  IllegalArgumentException
     * */
    public void eqPermuteRows(PermutationMatrix P, AllMatrices B){
        if (P.getN() != N || B.getN() != N || B.getK() != K) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
        double[] vals = new double[N*K];
        double[] valsB = B.getAll();
        int[] pos = P.getPos();
        for (int n=0; n<N; n++) {
            for (int k=0; k<K; k++) {
                vals[k*N+n] = valsB[k*N+pos[n]];   // A(n,k) = B(pos(n),k)
            }
        }
        this.setAll(vals);
    }

    /** Permute the columns of matrix B and put result in this, A = B * P.
     *
    @param B    Matrix of size N-by-K, size of result (this) is also N-by-K
    @param P    Permutation matrix of size K-by-K 
    @exception  IllegalArgumentException
     * */
    public void eqPermuteColumns(AllMatrices B, PermutationMatrix P){
        if (P.getK() != K || B.getN() != N || B.getK() != K) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
        double[] vals = new double[N*K];
        double[] valsB = B.getAll();
        int[] pos = P.getPos();
        for (int k=0; k<K; k++) {
            int pnk = pos[k]*N;
            int kn = k*N;
            for (int n=0; n<N; n++) {
                vals[pnk++] = valsB[kn++]; // A(n,pos(k)) = B(n,k)
            }
        }
        this.setAll(vals);
    }

    /** Scale the rows of matrix B and put result in this, A = D * B.
     *
    @param D    Diagonal matrix of size N-by-N, class DiagonalMatrix 
    @param B    Matrix of size N-by-K, size of result (this) is also N-by-K
    @exception  IllegalArgumentException
     * */
    public void eqScaleRows(DiagonalMatrix D, AllMatrices B){
        if (D.getN() != N || B.getN() != N || B.getK() != K) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
        double[] vals = B.getAll();
        for (int n=0; n<N; n++) {
            double d = D.get(n,n);
            for (int k=0; k<K; k++) {
                vals[k*N+n] *= d;   
            }
        }
        this.setAll(vals);
    }

    /** Scale the columns of matrix B and put result in this, A = B * D.
     *
    @param B    Matrix of size N-by-K, size of result (this) is also N-by-K
    @param D    Diagonal matrix of size N-by-N, class DiagonalMatrix 
    @exception  IllegalArgumentException
     * */
    public void eqScaleColumns(AllMatrices B, DiagonalMatrix D){
        if (D.getK() != K || B.getN() != N || B.getK() != K) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
        double[] vals = B.getAll();
        for (int k=0; k<K; k++) {
            double d = D.get(k,k);
            for (int n=0; n<N; n++) {
                vals[k*N+n] *= d;   
            }
        }
        this.setAll(vals);
    }


/* ------------------------
   Update methods, pluseq...
 * ------------------------ */

    /*  pluseqOuterProduct(double s1, double s2, double[] u, double[] v) 
     *  Also variants using SparseVector may be like this
     *  The different cases for effective implementation below is like
        if (s1 == 0.0){
            if (s2 == 1.0){
            } else if (s2 == -1.0) {
            } else {  // s2 is a general number
            }
        } else if (s1 == 1.0) {
            if (s2 == 1.0){
            } else if (s2 == -1.0) {
            } else {  // s2 is a general number
            }
        } else {  // s1 is general number
            if (s2 == 1.0){
            } else if (s2 == -1.0) {
            } else {  // s2 is a general number
            }
        }
    */

    /** Add outer product of two vectors to this, A = s1*A + s2*(u*v').
     *  This matrix, A, is of size N-by-K.
     *  If (s1 == 1):  Each element is updated as: A(n,k) += s2*u(n)*v(k)
     *  Else if (s1 == 0):  Each element is updated as: A(n,k) = s2*u(n)*v(k)
     *  Else: Each element is updated as: A(n,k) = s1*A(n,k) + s2*u(n)*v(k)
    @param s1    A single value
    @param s2    A single value
    @param u     A vector with N elements
    @param v     A vector with K elements
    @exception   IllegalArgumentException
     * */
    public void pluseqOuterProduct(double s1, double s2, double[] u, double[] v){
        if ((u.length != N) || (v.length != K)) {
            throw new IllegalArgumentException("Matrix and vector dimensions must agree.");
        }
        double[] col = new double[N];
        // effective implementation if s1==0.0, s1==1.0, s2==1.0; s2==-1.0, and, v[k]==0.0
        if (s1 == 0.0){
            if (s2 == 1.0){
                for (int k=0; k<K; k++) {
                    for (int n=0; n<N; n++) {
                        col[n] = u[n]*v[k];   
                    }
                    setColumn(k,col);
                }
            } else if (s2 == -1.0) {
                for (int k=0; k<K; k++) {
                    for (int n=0; n<N; n++) {
                        col[n] = -(u[n]*v[k]);   
                    }
                    setColumn(k,col);
                }
            } else {  // s2 is a general number
                for (int k=0; k<K; k++) {
                    for (int n=0; n<N; n++) {
                        col[n] = s2*u[n]*v[k];   
                    }
                    setColumn(k,col);
                }
            }
        } else if (s1 == 1.0) {
            if (s2 == 1.0){
                for (int k=0; k<K; k++) {
                    if (v[k] != 0.0) {
                        getColumn(k,col);
                        for (int n=0; n<N; n++) {
                            col[n] += u[n]*v[k];   
                        }
                        setColumn(k,col);
                    }
                }
            } else if (s2 == -1.0) {
                for (int k=0; k<K; k++) {
                    if (v[k] != 0.0) {
                        getColumn(k,col);
                        for (int n=0; n<N; n++) {
                            col[n] -= u[n]*v[k];   
                        }
                        setColumn(k,col);
                    }
                }
            } else {  // s2 is a general number
                for (int k=0; k<K; k++) {
                    if (v[k] != 0.0) {
                        getColumn(k,col);
                        for (int n=0; n<N; n++) {
                            col[n] += s2*u[n]*v[k];   
                        }
                        setColumn(k, col);
                    }
                }
            }
        } else {  // s1 is general number
            if (s2 == 1.0){
                for (int k=0; k<K; k++) {
                    getColumn(k,col);
                    for (int n=0; n<N; n++) {
                        col[n] *= s1;   
                        col[n] += u[n]*v[k];   
                    }
                    setColumn(k,col);
                }
            } else if (s2 == -1.0) {
                for (int k=0; k<K; k++) {
                    getColumn(k,col);
                    for (int n=0; n<N; n++) {
                        col[n] *= s1;   
                        col[n] -= u[n]*v[k];   
                    }
                    setColumn(k,col);
                }
            } else {  // s2 is a general number
                for (int k=0; k<K; k++) {
                    getColumn(k,col);
                    for (int n=0; n<N; n++) {
                        col[n] *= s1;   
                        col[n] += s2*u[n]*v[k];   
                    }
                    setColumn(k,col);
                }
            }
        }
    }
    
/* ------------------------
   Methods that return a matrix property
 * ------------------------ */
    
    /** Sum of all elements in matrix
   @return    sum of all elements
     */
    public double sumAll() {
        double f = 0;
        double[] col = new double[N];
        for (int j = 0; j < K; j++) {
            getColumn(j,col);
            for (int i = 0; i < N; i++) {
                f += col[i];
            }
        }
        return f;
    }
    
    /** One norm
   @return    maximum column sum.
     */
    public double norm1() {
        double f = 0;
        double[] col = new double[N];
        for (int j = 0; j < K; j++) {
            getColumn(j,col);
            double s = 0;
            for (int i = 0; i < N; i++) {
                s += Math.abs(col[i]);
            }
            f = Math.max(f,s);
        }
        return f;
    }
    
    /** Two norm
   @return    maximum singular value.
     */
    public double norm2() {
        JamaMatrix X = new JamaMatrix(this);   // make a deep copy of matrix as a JamaMatrix object
        return (new SingularValueDecomposition(X).norm2());
    }
    
    /** Infinity norm
   @return    maximum row sum.
     */
    public double normInf() {
        double f = 0;
        for (int i = 0; i < N; i++) {
            double s = 0;
            for (int j = 0; j < K; j++) {
                s += Math.abs(get(i,j));
            }
            f = Math.max(f,s);
        }
        return f;
    }
    
    /** Frobenius norm
   @return    sqrt of sum of squares of all elements.
     */
    public double normF() {
        double f = 0.0;
        double[] col = new double[N];
        for (int k = 0; k < K; k++) {
            getColumn(k,col);
            for (int n = 0; n < N; n++) {
                f = Math.hypot(f,col[n]);
            }
        }
        return f;
    }
    
    /** Matrix trace.
   @return     sum of the diagonal elements.
     */
    public double trace() {
        double t = 0;
        for (int i = 0; i < Math.min(N,K); i++) {
            t += get(i,i);
        }
        return t;
    }
    
    /** Matrix determinant
   @return     determinant
     */
    public double det() {
        return new LUPDecomposition(this).det();
    }
    
    /** Matrix rank
   @return     effective numerical rank, obtained from SVD.
     */
    public int rank() {
        JamaMatrix X = new JamaMatrix(this);   // make a deep copy of matrix as a JamaMatrix object
        return new SingularValueDecomposition(X).rank();
    }
    
    /** Matrix condition (2 norm)
   @return     ratio of largest to smallest singular value.
     */
    public double cond() {
        JamaMatrix X = new JamaMatrix(this);   // make a deep copy of matrix as a JamaMatrix object
        return new SingularValueDecomposition(X).cond();
    }
    
    /** The pseudo-zero-norm for a given column
   @param  k  number of the column in the matrix
   @return    number of non-zero values in the column
     */
    public int columnNorm0(int k) {
        int f = 0;
        double[] col = getColumn(k);
        for (int n = 0; n<N; n++) if (col[n] != 0.0) f++;
        return f;
    }
    
    /** One columnNorm for a given column
   @param  k  number of the column in the matrix
   @return    sum of absulute values for a column
     */
    public double columnNorm1(int k) {
        double f = 0;
        double[] col = getColumn(k);
        for (int n = 0; n<N; n++) f += Math.abs(col[n]);
        return f;
    }
    
    /** Two columnNorm for a given column
   @param  k  number of the column in the matrix
   @return    sum of squared values for a column
     */
    public double columnNorm2(int k) {
        double f = 0;
        double[] col = getColumn(k);
        for (int n = 0; n<N; n++) f = Math.hypot(f,col[n]);
        return f;
    }
    
    /** Inf columnNorm for a given column
   @param  k  number of the column in the matrix
   @return    maximum absulute values for a column
     */
    public double columnNormInf(int k) {
        double f = 0;
        double[] col = getColumn(k);
        for (int n = 0; n<N; n++) f = Math.max(f, Math.abs(col[n]) );
        return f;
    }
    
/* ------------------------
   Methods that return a matrix decompositions
 * ------------------------ */

    /** LU Decomposition
   @return     LUDecomposition
   @see LUDecomposition
     */
    public LUDecomposition lu() {
        JamaMatrix X = new JamaMatrix(this);   // make a deep copy of matrix as a JamaMatrix object
        return new LUDecomposition(X);
    }
    
    /** LUP Decomposition
   @return     LUPDecomposition
   @see LUPDecomposition
     */
    public LUPDecomposition lup() {
        return new LUPDecomposition(this);
    }
    
    /** QR Decomposition
   @return     QRDecomposition
   @see QRDecomposition
     */
    public QRDecomposition qr() {
        JamaMatrix X = new JamaMatrix(this);   // make a deep copy of matrix as a JamaMatrix object
        return new QRDecomposition(X);
    }
    
    /** Cholesky Decomposition
   @return     CholeskyDecomposition
   @see CholeskyDecomposition
     */
    public CholeskyDecomposition chol() {
        JamaMatrix X = new JamaMatrix(this);   // make a deep copy of matrix as a JamaMatrix object
        return new CholeskyDecomposition(X);
    }
    
    /** Singular Value Decomposition
   @return     SingularValueDecomposition
   @see SingularValueDecomposition
     */
    public SingularValueDecomposition svd() {
        JamaMatrix X = new JamaMatrix(this);   // make a deep copy of matrix as a JamaMatrix object
        return new SingularValueDecomposition(X);
    }
    
    /** Eigenvalue Decomposition
   @return     EigenvalueDecomposition
   @see EigenvalueDecomposition
     */
    public EigenvalueDecomposition eig() {
        JamaMatrix X = new JamaMatrix(this);   // make a deep copy of matrix as a JamaMatrix object
        return new EigenvalueDecomposition(X);
    }
    
/* ------------------------
   I/O methods, i.e. print
 * ------------------------ */

    /** Print the matrix to stdout.   Line the elements up in columns
     * with a Fortran-like 'Fw.d' style format.
   @param w    Column width.
   @param d    Number of digits after the decimal.
     */
    public void print(int w, int d) {
        print(new PrintWriter(System.out,true),w,d); }
    
    /** Print the matrix to the output stream.   Line the elements up in
     * columns with a Fortran-like 'Fw.d' style format.
   @param output Output stream.
   @param w      Column width.
   @param d      Number of digits after the decimal.
     */
    public void print(PrintWriter output, int w, int d) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        print(output,format,w+2);
    }
    
    /** Print the matrix to stdout.  Line the elements up in columns.
     * Use the format object, and right justify within columns of width
     * characters.
     * Note that is the matrix is to be read back in, you probably will want
     * to use a NumberFormat that is set to US Locale.
   @param format A  Formatting object for individual elements.
   @param width     Field width for each column.
   @see java.text.DecimalFormat#setDecimalFormatSymbols
     */
    public void print(NumberFormat format, int width) {
        print(new PrintWriter(System.out,true),format,width); }
    
    // DecimalFormat is a little disappointing coming from Fortran or C's printf.
    // Since it doesn't pad on the left, the elements will come out different
    // widths.  Consequently, we'll pass the desired column width in as an
    // argument and do the extra padding ourselves.
    
    /** Print the matrix to the output stream.  Line the elements up in columns.
     * Use the format object, and right justify within columns of width
     * characters.
     * Note that is the matrix is to be read back in, you probably will want
     * to use a NumberFormat that is set to US Locale.
   @param output the output stream.
   @param format A formatting object to format the matrix elements
   @param width  Column width.
   @see java.text.DecimalFormat#setDecimalFormatSymbols
     */
    public void print(PrintWriter output, NumberFormat format, int width) {
        output.println();  // start on new line.
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < K; j++) {
                String s = format.format(get(i,j)); // format the number
                int padding = Math.max(1,width-s.length()); // At _least_ 1 space
                for (int k = 0; k < padding; k++) output.print(' ');
                output.print(s);
                if ((i%10) == 9) output.print(" ... \n ... ");  // new line
            }
            output.print("\n");  // new line
        }
        output.println();   // end with blank line.
    }
    
    
/* ------------------------
   Other methods
 * ------------------------ */
    
    /** Returns the inner product of two matrix column vectors.
     * Legal range is <code>0 <= k1 < K</code> and <code>0 <= k2 < K</code>.
     * If <code>k1</code> or <code>k2</code> are out of range, 0.0 should be returned.
     *
     * @param k1 number for the first dictionary element.
     * @param k2 number for the second dictionary element.
     * */
    public double innerProduct(int k1, int k2){
        double ip = 0.0;
        double[] col1 = getColumn(k1);
        double[] col2 = getColumn(k2);
        for (int n = 0; n<N; n++) ip += col1[n]*col2[n];
        return ip;
    }
    
/* ------------------------
   (Package) Methods
 * ------------------------ */
    
    /** Check if size(A) == size(B) **/
    
    void checkMatrixDimensions (AllMatrices B) {
        if (B.getN() != this.getN() || B.getK() != this.getK()) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
    }
    
}
