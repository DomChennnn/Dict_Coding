package mpv2;

public abstract class AllMatrices {
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
