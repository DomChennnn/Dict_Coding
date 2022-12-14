package mpv2;

public class SimpleMatrix extends AllMatrices {
/* ------------------------
   Class variables
 * ------------------------ */

  /**
   * Array for internal storage of elements, column-stacked array.
   *
   * @serial internal array storage.
   */
  private final double[] Aarray;

/* ------------------------
   Constructors
 * ------------------------ */

  /**
   * Construct an m-by-n matrix of zeros.
   *
   * @param n Number of rows.
   * @param k Number of colums.
   */

  public SimpleMatrix(int n, int k) {
    N = n;
    K = k;
    Aarray = new double[N * K];
  }

  /**
   * Construct an m-by-n constant matrix.
   *
   * @param n Number of rows.
   * @param k Number of colums.
   * @param s Fill the matrix with this scalar value.
   */

  public SimpleMatrix(int n, int k, double s) {
    N = n;
    K = k;
    Aarray = new double[N * K];
    for (int i = 0; i < (N * K); i++) {Aarray[i] = s;}
  }

  /**
   * Construct a matrix from a 2-D array, all values are copied.
   *
   * @param A Two-dimensional array of doubles.
   * @throws IllegalArgumentException All rows must have the same length
   */
  public SimpleMatrix(double[][] A) {
    N = A.length;
    K = A[0].length;
    for (int n = 0; n < N; n++) {
      if (A[n].length != K) {
        throw new IllegalArgumentException("All rows must have the same length.");
      }
    }
    Aarray = new double[N * K];
    int i = 0;
    for (int k = 0; k < K; k++) {
      for (int n = 0; n < N; n++, i++) {
        Aarray[i] = A[n][k];
      }
    }
  }

  /**
   * Construct a matrix from a one-dimensional packed array
   *
   * @param vals One-dimensional array of doubles, packed by columns (ala Fortran).
   * @param n    Number of rows, i.e. length of columns.
   * @throws IllegalArgumentException Array length must be a multiple of m.
   */

  public SimpleMatrix(double[] vals, int n) {
    N = n;
    K = (n != 0 ? vals.length / n : 0);
    if (N * K != vals.length) {
      throw new IllegalArgumentException("Array length must be a multiple of n.");
    }
    Aarray = new double[N * K];
    for (int i = 0; i < (N * K); i++) {Aarray[i] = vals[i];}
  }

  /**
   * Construct a new matrix from another matrix (of any kind) if argument B is an object of class
   * SimpleMatrix this is the same as (deep) copy (or clone). Note that argument B may be another
   * kind of matrix, ex. an object of class SparseMatrix or BandMatrix or any subclass of
   * AllMatrices
   *
   * @param B a matrix of any kind, class is a subclass of AllMatrices
   * @see #copy
   */
  public SimpleMatrix(AllMatrices B) {
    N = B.getN();
    K = B.getK();
    Aarray = new double[N * K];
    double[] col = new double[N];
    int i = 0;
    for (int k = 0; k < K; k++) {
      B.getColumn(k, col);
      for (int n = 0; n < N; n++, i++) {Aarray[i] = col[n];}
    }
  }

 /* ------------------------
   Public Methods, which use this object (A)
 * ------------------------ */

  /**
   * Get a single element.
   *
   * @param n Row index.
   * @param k Column index.
   * @return A(n, k)
   * @throws ArrayIndexOutOfBoundsException
   */
  public double get(int n, int k) {
    return Aarray[N * k + n];
  }

  /**
   * Set a single element.
   *
   * @param n Row index.
   * @param k Column index.
   * @param s A(n,k).
   * @throws ArrayIndexOutOfBoundsException
   */
  public void set(int n, int k, double s) {
    Aarray[N * k + n] = s;
  }

  /**
   * Copy a column of the matrix into argument d Legal range of the integer argument is <code>0 <= k
   * < K</code>. If argument is out of range a length <code>N</code> array of zeros should be
   * returned. <br> The corresponding Matlab expression would be: <code>A(:,k+1)</code>.
   *
   * @param k number of the column in the matrix
   * @param d the given column of the matrix
   */
  public void getColumn(int k, double[] d) {
    if (d.length != N) {
      throw new IllegalArgumentException(
          "getColumn: argument d is not expected length N.");
    }
    int i = N * k;
    for (int n = 0; n < N; n++, i++) {d[n] = Aarray[i];}
  }

  /**
   * Copy an array (d) into a column of the matrix Legal range of the integer argument is <code>0 <=
   * k < K</code>. If argument is out of range an IllegalArgumentException is thrown. <br> The
   * corresponding Matlab expression would be: <code>A(:,k+1) = d</code>.
   *
   * @param k number of the column in the matrix
   * @param d the given column of the matrix
   */
  public void setColumn(int k, double[] d) {
    if (d.length != N) {
      throw new IllegalArgumentException(
          "setColumn: argument d is not expected length N.");
    }
    int i = N * k;
    for (int n = 0; n < N; n++, i++) {Aarray[i] = d[n];}
  }

  /**
   * Add a column of the matrix multiplied by a factor to x, x = x + A[][k] Legal range of the
   * integer argument is <code>0 <= k < K</code>. <br> The corresponding Matlab expression would
   * be:
   * <code>x = x+f*A(:,k+1)</code>. Note that this function can not be used from Matlab, use
   * getColumn(k) to get A(:,k+1) and calculate the new x in Matlab.
   *
   * @param k      number of the column the matrix <code>A</code>.
   * @param factor a factor to multiply the column vector by.
   * @param x      an array of length <code>N</code>.
   */
  public void addColumn(int k, double factor, double[] x) {
    if (x.length != N) {
      throw new IllegalArgumentException(
          "addColumn: argument x is not expected length N.");
    }
    int i = N * k;
    for (int n = 0; n < N; n++, i++) {x[n] += factor * Aarray[i];}
  }
}

