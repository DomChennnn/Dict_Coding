/*
 * class:           SparseVectorMatrix
 *
 * Description:		A matrix where each column is a SparseVector
 *
 * Copyright (c) 2008.  Karl Skretting.  All rights reserved.
 * University of Stavanger, Institutt for data- og elektroteknikk
 * Mail:  karl.skretting@uis.no   Homepage:  http://www.ux.his.no/~karlsk/
 *
 * HISTORY:  dd.mm.yyyy
 * Ver. 1.0  18.06.2007  KS: class made
 * */

package mpv2;

// import mpv2.util.*;

/**
 * This class is a matrix implementation of the AllMatrices superclass, where each column is a
 * SparseVector.
 *
 * @author Karl Skretting (karl.skretting@uis.no)
 * @author http://www.ux.uis.no/~karlsk/
 * @version 1.0  November 2008
 */
public class SparseVectorMatrix extends AllMatrices
    implements Cloneable, java.io.Serializable {

  /**
   * Stores the pointers to each column in an array
   */
  private final SparseVector[] col;
       
/* ------------------------
   Constructors
 * ------------------------ */

  /**
   * Construct a <code>N&times;K</code> matrix with only zero values.
   *
   * @param iN number of rows, i.e. length of column vectors of the matrix.
   * @param iK number of columns
   */
  public SparseVectorMatrix(int iN, int iK) {
    N = iN;
    K = iK;
    col = new SparseVector[K];
    for (int k = 0; k < K; k++) {
      col[k] = new SparseVector(N);
    }
    System.out.println("SparseVectorMatrix created with " +
        " N = " + N + ", K = " + K + " and only zero values.");
  }

  /**
   * Construct a matrix from a one-dimensional packed array
   *
   * @param vals One-dimensional array of doubles, packed by columns (ala Fortran).
   * @param m    Number of rows, i.e. length of columns.
   * @throws IllegalArgumentException Array length must be a multiple of m.
   */
  public SparseVectorMatrix(double[] vals, int m) {
    N = m;
    K = (m != 0 ? vals.length / m : 0);
    if (N * K != vals.length) {
      throw new IllegalArgumentException("Array length must be a multiple of m.");
    }
    col = new SparseVector[K];
    setAll(vals);
    System.out.println("SparseVectorMatrix created with " +
        " N = " + N + ", K = " + K + " and supplied values.");
  }

  /**
   * Construct a matrix from a 2-D array, all values are copied.
   *
   * @param A Two-dimensional array of doubles.
   * @throws IllegalArgumentException All rows must have the same length
   */
  public SparseVectorMatrix(double[][] A) {
    N = A.length;
    K = A[0].length;
    for (int n = 0; n < N; n++) {
      if (A[n].length != K) {
        throw new IllegalArgumentException("All rows must have the same length.");
      }
    }
    col = new SparseVector[K];
    double[] colk = new double[N];
    for (int k = 0; k < K; k++) {
      for (int n = 0; n < N; n++) {
        colk[n] = A[n][k];
      }
      col[k] = new SparseVector(colk);
    }
  }

  /**
   * Construct a new matrix from another matrix (of any kind)
   *
   * @param B a matrix of any kind, class is a subclass of AllMatrices
   */
  public SparseVectorMatrix(AllMatrices B) {
    N = B.getN();
    K = B.getK();
    col = new SparseVector[K];
    setAll(B.getAll());
    System.out.println("SparseVectorMatrix created with " +
        " N = " + N + ", K = " + K + " and supplied values.");
  }
    
 /* ------------------------
   Private Methods
  * ------------------------ */
    
   
 /* ------------------------
   Public Methods
  * ------------------------ */

  /**
   * Return an entry of the matrix. Note that the row, i.e. position in the column, is given first,
   * the second argument is the column number. We should have: <code>0 <= n < N</code> and <code>0
   * <= k < K</code>. If any argument is outside legal range 0.0 is returned without error or
   * warning.
   *
   * @param n row number for the returned entry value.
   * @param k column number for the returned entry value.
   */
  public double get(int n, int k) {
    // rekkeflge for disse testene er viktig (egentlig en if else struktur)
    if ((k < 0) || (k >= K) || (n < 0) || (n >= N)) {return 0.0;}
    return col[k].get(n);
  }

  /**
   * Set an entry (a value) in the matrix. Note that the row, i.e. position in the column, is given
   * first, the second argument is the column number. We should have: <code>0 <= n < N</code> and
   * <code>0 <= k < K</code>. If any argument is outside legal range nothing is done.
   *
   * @param n   row number for the entry value to be changed.
   * @param k   column number for the entry value to be changed.
   * @param val the value to be put into the given entry of the dictionary.
   */
  public void set(int n, int k, double val) {
    // rekkeflge for disse testene er viktig (egentlig en if else struktur)
    if ((k < 0) || (k >= K) || (n < 0) || (n >= N)) {return;}
    col[k].set(n, val);
  }

  /**
   * Get a column in the matrix as an array
   *
   * @param k column number for the column to be returned
   * @return c   the column as an array of length N
   */
  public double[] getColumn(int k) {
    double[] d = col[k].get();
    return d;
  }

  /**
   * Get a copy of a column in the matrix as a SparseVector
   *
   * @param k column number for the column to be returned
   * @return copy of the column
   */
  public SparseVector getSparseColumn(int k) {
    if ((k < 0) || (k >= K)) {return null;}
    return col[k].copy();
  }

  /**
   * Replace a column in the matrix with the given column vector. The column should be of length
   * <code>N</code>. We should have: <code>0 <= k < K</code>. If any argument is outside legal
   * dimension or range nothing is done.
   *
   * @param k column number for the column to be changed.
   * @param c the column as an array of length N
   */
  public void setColumn(int k, double[] c) {
    if ((k < 0) || (k >= K) || (c.length != N)) {return;}
    col[k] = new SparseVector(c);
  }

  /**
   * Replace a column in the matrix with a copy of the given column. The column should be of length
   * <code>N</code>. We should have: <code>0 <= k < K</code>. If any argument is outside legal
   * dimension or range nothing is done.
   *
   * @param k column number for the column to be changed.
   * @param c the column as a SparseVector
   */
  public void setColumn(int k, SparseVector c) {
    if ((k < 0) || (k >= K) || (c.getLength() != N)) {return;}
    col[k] = c.copy();
  }

  /**
   * Add a column of the matrix multiplied by a factor to a given vector. Legal range of the integer
   * argument is <code>0 <= k < K</code>. <br> The corresponding Matlab expression would be: <code>x
   * = x+factor*D(:,k+1)</code>.
   *
   * @param k      number of the column in dictionary, i.e. matrix <code>D</code>.
   * @param factor a factor to multiply the column vector by.
   * @param x      an array of length <code>N</code>.
   */
  public void addColumn(int k, double factor, double[] x) {
    // if (x.length != N)  throw new IllegalArgumentException(
    //     "addColumn: argument x is not expected length N.");
    if ((k < 0) || (k >= K)) {return;}
    col[k].addToArray(factor, x);
  }

  /**
   * Set all entries of to matrix to the supplied new values If argument is wrong length an
   * IllegalArgumentException is thrown. <br>
   *
   * @param vals One-dimensional array of doubles, packed by columns (ala Fortran).
   */
  public void setAll(double[] vals) {
    if (N * K != vals.length) {
      throw new IllegalArgumentException
          ("Array length must be as total size of matrix (N*K).");
    }
    double[] colk = new double[N];
    for (int k = 0; k < K; k++) {
      for (int n = 0; n < N; n++) {
        colk[n] = vals[N * k + n];
      }
      col[k] = new SparseVector(colk);
    }
  }

  /**
   * Returns the inner product of two matrix column vectors. Legal range is <code>0 <= k1 < K</code>
   * and <code>0 <= k2 < K</code>. <br> If <code>k1</code> or <code>k2</code> is out of range, 0.0
   * is returned.
   *
   * @param k1 number for the first column.
   * @param k2 number for the second column.
   */
  public double innerProduct(int k1, int k2) {
    if ((k2 < 0) || (k1 >= K) || (k1 < 0) || (k2 >= K)) {return 0.0;}
    return col[k1].innerProduct(col[k2]);
  }

  /**
   * Multiplies the matrix by an array. The dimensions are: <code>A</code> is
   * <code>N&times;K</code>, <code>x</code> is <code>N&times;1</code>,
   * and <code>y</code> is <code>K&times;1</code>.<br> The corresponding mulitplication in Matlab
   * would be: <code>x = A*y</code>.
   *
   * @param y the input array
   * @param x the results as an array of length <code>N</code>.
   */
  public void times(double[] y, double[] x) {
    if (y.length != K) {
      throw new IllegalArgumentException(
          "times: argument y is not expected length K.");
    }
    if (x.length != N) {
      throw new IllegalArgumentException(
          "times: argument x is not expected length N.");
    }
    for (int n = 0; n < N; n++) {x[n] = 0.0;}
    for (int k = 0; k < K; k++) {
      if (y[k] != 0.0) {addColumn(k, y[k], x);}
    }
  }

  /**
   * Multiplies the transposed matrix by an array. The dimensions are: <code>A</code> is
   * <code>N&times;K</code>, <code>x</code> is <code>N&times;1</code>,
   * and <code>y</code> is <code>K&times;1</code>.<br> The corresponding mulitplication in Matlab
   * would be: <code>y = A'*x</code>.
   *
   * @param x the input array
   * @param y the results as an array of length <code>K</code>.
   */
  public void transposeTimes(double[] x, double[] y) {
    if (x.length != N) {
      throw new IllegalArgumentException(
          "transposeTimes: argument x is not expected length N.");
    }
    if (y.length != K) {
      throw new IllegalArgumentException(
          "transposeTimes: argument y is not expected length K.");
    }
    for (int k = 0; k < K; k++) {
      y[k] = col[k].innerProduct(x);
    }
    return;
  }
}
