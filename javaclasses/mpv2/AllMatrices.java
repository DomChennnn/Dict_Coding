/*
 * abstract class:  AllMatrices
 *
 * Description:		This abstract class is the superclass for matrices
 *                  and dictionaries in this package.
 *
 * Kommentarer for brukerne (javadoc) skal vre p engelsk, mens
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

/**
 * mpv2 = Matrix package version 2
 * <p>
 * The abstract class AllMatrices contains common stuff for several kinds of matrices.
 *
 * <p>
 * There is a quite large number of methods available for matrices in general and in this class
 * specially. An overview may be helpful: </P>
 *
 * <P><UL>
 * <LI> Notation. <BR>
 * The matrix, A (this), is N-by-K. y (a column) and x (a row) are arrays (column vectors) with
 * lengths N and K respectively.
 * <LI> Class variables and constructors. <BR>
 * The matrix dimension is included here (N-by-K) but not the actual values, how these should be
 * stored is left to the implementing class. Constructors must be defined in the implementing class.
 * Also deep copy as in <CODE>copy()</CODE> and <CODE>clone()</CODE> are not in this abstract class
 * and may be defined in the implementing class. </LI>
 * <LI> Abstract methods <BR>
 * These are <CODE>get(i,j)</CODE> and <CODE>set(i,j,s)</CODE>, they must be defined in the
 * implementing class as effective as possible, without control of the indices. Actually these are
 * the only methods an implementing class need to define. All other methods here access data by
 * using these two methods. <BR> The implementing class may make more effective implementations of
 * others methods, especially :
 * <CODE>getColumn(k,y)</CODE>, <CODE>setColumn(k,y)</CODE>, and <CODE>addColumn(k,s,y)</CODE>,
 * and <CODE>times(x,y)</CODE> which set y=Ax, and <CODE>transposeTimes(y,x)</CODE> which set
 * x=A'y.
 * <BR> The general methods here work best if these methods are effectively implemented. </LI>
 * <LI> Access methods, <B>get...</B> <BR>
 * These methods return data stored in the object either as a single number or as an one-dimensional
 * array of values. If several values of the matrix are returned they are always packed by columns.
 * Row-packed versions may be defined in the implementing class. Also, the implementing class may
 * define a get-method (or methods) that returns a pointer to the structure that actually stores the
 * matrix values. The methods included here are: <BR>
 * <CODE>getN()</CODE> or the equivalent method <CODE>getRowDimension()</CODE>. <BR>
 * <CODE>getK()</CODE> or the equivalent method <CODE>getColumnDimension()</CODE>. <BR>
 * <CODE>getValue(i,j)</CODE> check that arguments are in valid range and if not just return zero.
 * <BR>
 * <CODE>getColumn(k)</CODE> return an array containing the column,
 * and <CODE>getColumn(k,x)</CODE> put the column into array <CODE>x</CODE>. <BR>
 * <CODE>getRow(n)</CODE> return an array containing the row,
 * and <CODE>getRow(n,x)</CODE> put the row into array <CODE>x</CODE>. <BR>
 * <CODE>getAll()</CODE> return an array containing all matrix elements (by columns),
 * and <CODE>setAll(vals)</CODE> put all matrix elements into array <CODE>vals</CODE>. <BR>
 * <CODE>getSubMatrix(...)</CODE> return an array containing elements (by columns)
 * from a part of the matrix as given by the arguments. </LI>
 * <LI> Access methods, <B>set...</B> <BR>
 * These methods set a given element (or elements) of the matrix to values supplied in the argument
 * list, either as a single number or as an one-dimensional array of values. The methods included
 * here are: <BR>
 * <CODE>setValue(i,j,s)</CODE> check that arguments are in valid range and if not just return.
 * <BR>
 * <CODE>setColumn(k,y)</CODE> put values from array <CODE>y</CODE> into the matrix. <BR>
 * <CODE>setRow(n,x)</CODE> put values from array <CODE>x</CODE> into the matrix. <BR>
 * <CODE>setAll(vals)</CODE> put values from array <CODE>vals</CODE> into the matrix. <BR>
 * <CODE>setSubMatrix(...,vals)</CODE> put values from array <CODE>vals</CODE> into
 * a part of the matrix as given by the arguments. </LI>
 * <LI> Methods where the matrix operates on a vector. <BR>
 * The methods included here are: <BR>
 * <CODE>addColumn(k,s,y)</CODE> add a column to y, i.e. <CODE>y = y + s*A(:,k)</CODE>. <BR>
 * <CODE>times(x)</CODE> return <CODE>A*x</CODE> and
 * <CODE>times(x,y)</CODE> set <CODE>y = A*x</CODE> (return void). <BR>
 * <CODE>transposeTimes(y)</CODE> return <CODE>A'*y</CODE> and
 * <CODE>transposeTimes(y,x)</CODE> set <CODE>x = A'*y</CODE> (return void). <BR>
 * <CODE>solve(y)</CODE> solve equation Ax=y, return <CODE>pinv(A)*y</CODE> and
 * <CODE>solve(y,x)</CODE> set <CODE>x = pinv(A)*y</CODE> (return void). <BR> </LI>
 * <LI> Update methods, <B>eq...</B> <BR>
 * General and flexible methods that allow to update a matrix of any kind (any class) based on other
 * matrices which may also be of any kind (any classes). These methods update this matrix, denoted
 * <CODE>A</CODE>. The <CODE>eq</CODE>-prefix is meant to mimic '=', an (assign) equal sign. All
 * methods are void, and do not change any of the arguments, it only update this (A). The arguments
 * can be <CODE>B</CODE> and <CODE>C</CODE> which are matrices of any kind,
 * <CODE>D</CODE> is a diagonal matrix and <CODE>P</CODE> is a permutation matrix,
 * and <CODE>s</CODE> is a double. Note that this (A) may be used as an argument instead of another
 * matrix, for example instead of B. Then this argument is of course changed. The methods included
 * here are: <BR>
 * <PRE>
 * A = I,         eqIdentity(), identity, ones on main diagonal zero elsewhere. A = 0, eqZeros(),
 * all values to zero. A = 1,         eqOnes(), all values to 1. A = s, eqConstant(s), all values to
 * s. A = rand,      eqRandom(), all values to random values between 0 and 1. A = B, eqCopy(B), copy
 * values, note A and B may be matrices of two different classes. A = B(..), eqCopy(B,...), copy
 * selected rows and columns of B. A = -B, eqNegate(B). A = B*s, eqProduct(B,s). A = B+C,
 * eqSum(B,C). A = B-C, eqDifference(B,C). A = B.*C, eqEProduct(B,C). Elementwise multipliccation. A
 * = B./C, eqEQuotient(B,C). Elementwise (right) division. Left division by changing arguments. A =
 * B', eqTranspose(B). A = inv(B), eqInverse(B), if inverse not exist, the Moore-Penrose
 * pseudoinverse. A = B*C, eqProduct(B,C). A = B'*C,      eqTProduct(B,C). A = inv(B)*C,
 * eqIProduct(B,C). Perhaps also variants: eqNTProduct(B,C), eqNIProduct(B,C), eqTTProduct(B,C),
 * eqIIProduct(B,C). A = P*B, eqPermuteRows(P,B). A = B*P,       eqPermuteColumns(B,P). A = D*B,
 * eqScaleRows(D,B). A = B*D,       eqScaleColumns(B,D).
 * </PRE>
 * A class that implements AllMatrices may define methods that returns a matrix (of the implementing
 * class) instead of a void, <CODE>eqName(...) -> Name(...)</CODE>. This implementation is quite
 * simple, example with class SimpleMatrix is <BR>
 * <PRE>
 * public static SimpleMatrix sum(AllMatrices B, AllMatrices C){ SimpleMatrix A = new
 * SimpleMatrix(B.getN(), B.getK()); A.eqSum(B,C);  // use the general method return A; }
 * </PRE></LI>
 * <LI> Update methods, <B>pluseq...</B> <BR>
 * Like <B>eq...</B> above, but here the prefix is meant to mimic '+='. The methods included here
 * are: <BR>
 * <PRE>
 * A = s1*A + s2*(u*v'), pluseqOuterProduct(s1,s2,u,v).
 * </PRE></LI>
 * <LI> Methods that return matrix properties. <BR>
 * These are: <CODE>norm1()</CODE>, <CODE>normInf()</CODE>,
 * <CODE>normF()</CODE>, <CODE>trace()</CODE>,
 * <CODE>det()</CODE>, <CODE>rank()</CODE>,
 * <CODE>cond()</CODE> and <CODE>norm2()</CODE>. <BR>
 * There may also be boolean methods defined like <CODE>isOrthogonal()</CODE>,
 * <CODE>isSquare()</CODE>, <CODE>isNormal()</CODE>, <CODE>isInvertible()</CODE>
 * and <CODE>isSymmetric()</CODE>. </LI>
 * <LI> Methods that return matrix decompositions. <BR>
 * Decompositions first copy the matrix, which may be of any kind, into a JamaMatrix object, and
 * then use this object for decompositions. Each method returns an object of the corresponding
 * class. The decompositions are almost exactly as in the Jama package, they are:  <BR>
 * <UL>
 * <LI>Cholesky Decomposition of symmetric, positive definite matrices, <CODE>chol()</CODE>.
 * <LI>LU Decomposition of rectangular matrices, <CODE>lu()</CODE>.
 * <LI>QR Decomposition of rectangular matrices, <CODE>qr()</CODE>.
 * <LI>Singular Value Decomposition of rectangular matrices, <CODE>svd()</CODE>.
 * <LI>Eigenvalue Decomposition of both symmetric and nonsymmetric square matrices,
 * <CODE>eig()</CODE>.
 * </UL> </LI>
 * <LI> I/O methods, i.e. print. <BR>
 * Print the matrix to standard out or a stream using <CODE>print(...)</CODE>. </LI>
 * <LI> Others methods. <BR>
 * There may be methods that do not fit into any of the groups above. For example: <BR>
 * <CODE>innerProduct(k1,k2)</CODE> The inner product of two column vectors. </LI>
 * </UL></P>
 *
 * <p>
 *
 * @author Karl Skretting, University of Stavanger (UiS), (karl.skretting@uis.no)
 * @version November 2008
 */

//  satset p void metoder som setter resultatet i en AllMatrices object
//  Et eksempel er laget for metoden plus.
//  Kanskje heller resultat til A (this), A = B + C, A = A + B, o.s.v
/*  Det er da flgende metoder som kan vre aktuelle, alle er av type void!
 *  A er dette AllMatrices objektet (this) og det fr nye verdier i operasjonen (NxK)
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

  /**
   * Row and column dimensions.
   *
   * @serial row dimension.
   * @serial column dimension.
   */
  protected int N, K;            // matrise er her NxK, i Jama var det mxn
    
/* ------------------------
   Constructors, these are for the implementing class
 * ------------------------ */
 
/* ------------------------
   Abstract methods, must be defined in the implementing class
 * ------------------------ */

  /**
   * Get a single element.
   *
   * @param i Row index.
   * @param j Column index.
   * @return A(i, j)
   * @throws ArrayIndexOutOfBoundsException
   */
  public abstract double get(int i, int j);

  /**
   * Set a single element.
   *
   * @param i Row index.
   * @param j Column index.
   * @param s the value to be set into A(i,j).
   * @throws ArrayIndexOutOfBoundsException
   */
  public abstract void set(int i, int j, double s);
 
/* ------------------------
   Access methods, get...
 * ------------------------ */

  /**
   * Get number of rows in the matrix, i.e. length of each column vector.
   *
   * @return N, the number of rows.
   */
  public int getN() {
    return N;
  }

  /**
   * Get number of columns in the matrix, i.e. length of each row vector.
   *
   * @return K, the number of columns.
   */
  public int getK() {
    return K;
  }

  /**
   * Get a column of the matrix. If argument is out of range a length <code>N</code> array of zeros
   * should be returned.
   *
   * @param k number of the column in the matrix
   * @return the column
   */
  public double[] getColumn(int k) {
    double[] y = new double[N];
    getColumn(k, y);
    return y;
  }

  /**
   * Get a column of the matrix into argument y. Legal range of the integer argument is <code>0 <= k
   * < K</code>.
   *
   * @param k number of the column in the matrix
   * @param y is set to the given column of the matrix
   * @throws IllegalArgumentException
   */
  public void getColumn(int k, double[] y) {
    if (y.length != N) {
      throw new IllegalArgumentException(
          "getColumn: argument y is not expected length N.");
    }
    if ((k >= 0) & (k < K)) {
      for (int n = 0; n < N; n++) {y[n] = get(n, k);}
    } else {
      for (int n = 0; n < N; n++) {y[n] = 0.0;}
    }
  }

/* ------------------------
   Access methods, set...
 * ------------------------ */

  /**
   * Copy an array (y) into a column of the matrix. Legal range of the integer argument is <code>0
   * <= k < K</code>.
   *
   * @param k number of the column in the matrix
   * @param y the values that are copied into the given column of the matrix
   * @throws IllegalArgumentException
   */
  public void setColumn(int k, double[] y) {
    if ((k < 0) || (k >= K)) {return;}
    if (y.length != N) {
      throw new IllegalArgumentException(
          "setColumn: argument y is not expected length N.");
    }
    for (int n = 0; n < N; n++) {
      set(n, k, y[n]);
    }
  }

/* ------------------------
   Methods where the matrix operates on an array, i.e. a (column) vector.
 * ------------------------ */

  /**
   * Add a column of the matrix multiplied by a factor to y, set y[n] = y[n] + s*A[n][k]. Legal
   * range of the integer argument is <code>0 <= k < K</code>. <br>
   *
   * @param k number of the column the matrix A.
   * @param s a scale factor to multiply the column vector by.
   * @param y an array of length N.
   * @throws IllegalArgumentException
   */
  public void addColumn(int k, double s, double[] y) {
    if ((k < 0) || (k >= K)) {return;}
    if (y.length != N) {
      throw new IllegalArgumentException(
          "addColumn: argument y is not expected length N.");
    }
    double[] col = new double[N];
    getColumn(k, col);
    for (int n = 0; n < N; n++) {y[n] += s * col[n];}
  }

  /**
   * Multiplies the transposed matrix by an array (vector), set x = A'*y.
   *
   * @param y the input array
   * @param x the results as an array of length K.
   * @throws IllegalArgumentException
   */
  public void transposeTimes(double[] y, double[] x) {
    if (y.length != N) {
      throw new IllegalArgumentException(
          "transposeTimes: argument y is not expected length N.");
    }
    if (x.length != K) {
      throw new IllegalArgumentException(
          "transposeTimes: argument x is not expected length K.");
    }
    double[] col = new double[N];
    for (int k = 0; k < K; k++) {
      getColumn(k, col);
      x[k] = 0.0;
      for (int n = 0; n < N; n++) {
        x[k] += col[n] * y[n];
      }
    }
  }

/* ------------------------
   Other methods
 * ------------------------ */

  /**
   * Returns the inner product of two matrix column vectors. Legal range is <code>0 <= k1 < K</code>
   * and <code>0 <= k2 < K</code>. If <code>k1</code> or <code>k2</code> are out of range, 0.0
   * should be returned.
   *
   * @param k1 number for the first dictionary element.
   * @param k2 number for the second dictionary element.
   */
  public double innerProduct(int k1, int k2) {
    double ip = 0.0;
    double[] col1 = getColumn(k1);
    double[] col2 = getColumn(k2);
    for (int n = 0; n < N; n++) {ip += col1[n] * col2[n];}
    return ip;
  }
}
