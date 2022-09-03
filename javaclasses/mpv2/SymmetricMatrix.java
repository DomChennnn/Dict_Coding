package mpv2;

public class SymmetricMatrix extends AllMatrices {

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
   * Construct an n-by-n matrix of zeros. Note that there exist a constructor that takes a 2D array
   * argument and that a single double number as input is interpreted as an array. Thus to call this
   * one from Matlab use: jA = SymmetricMatrix(int32(5));
   *
   * @param n Number of rows.
   */
  public SymmetricMatrix(int n) {
    N = n;
    K = n;
    Aarray = new double[N * (N + 1) / 2];
  }

  /**
   * Construct an n-by-n matrix of zeros.
   *
   * @param n Number of rows.
   * @param k Number of colums is also n, so k is ignored!
   */
  public SymmetricMatrix(int n, int k) {
    N = n;
    K = n;
    Aarray = new double[N * (N + 1) / 2];
  }

  /**
   * Construct an m-by-n constant matrix.
   *
   * @param n Number of rows.
   * @param k Number of colums is also n, so k is ignored!
   * @param s Fill the matrix with this scalar value.
   */
  public SymmetricMatrix(int n, int k, double s) {
    N = n;
    K = n;
    Aarray = new double[N * (N + 1) / 2];
    for (int i = 0; i < (N * (N + 1) / 2); i++) {Aarray[i] = s;}
  }

  /**
   * Construct a matrix from a 2-D array, only lower-left-values are used.
   *
   * @param A Two-dimensional array of doubles.
   * @throws IllegalArgumentException All rows must have the same length
   */
  public SymmetricMatrix(double[][] A) {
    N = A.length;
    K = N;
    for (int n = 0; n < N; n++) {
      if (A[n].length != N) {
        throw new IllegalArgumentException(
            "All rows must have the same length as the number of rows.");
      }
    }
    Aarray = new double[N * (N + 1) / 2];
    int j = 0;
    for (int k = 0; k < K; k++) {
      for (int n = k; n < N; n++, j++) {
        Aarray[j] = A[n][k];
      }
    }
  }

  /**
   * Construct a new matrix from another matrix (of any kind), only lower-left-values are used. If
   * argument B is an object of class SymmetricMatrix this is the same as (deep) copy (or clone).
   * Note that argument B may be another kind of matrix, ex. an object of class SparseMatrix or
   * BandMatrix or any subclass of AllMatrices
   *
   * @param B A matrix of any kind, class is a subclass of AllMatrices
   * @throws IllegalArgumentException Number of rows and Columns must be the same
   * @see #copy
   */
  public SymmetricMatrix(AllMatrices B) {
    if (B.getN() != B.getK()) {
      throw new IllegalArgumentException(
          "Number of rows and columns must be the same.");
    }
    N = B.getN();
    K = N;
    Aarray = new double[N * (N + 1) / 2];
    double[] col = new double[N];
    int j = 0;
    for (int k = 0; k < K; k++) {
      B.getColumn(k, col);
      for (int n = k; n < N; n++, j++) {
        Aarray[j] = col[n];
      }
    }
  }

  /**
   * Get a single element.
   *
   * @param n Row index.
   * @param k Column index.
   * @return A(n, k)
   * @throws ArrayIndexOutOfBoundsException
   */
  public double get(int n, int k) {
    int j = 0;
    if (n < k) {
      j = n * N + k - (n * (n + 1) / 2);
    } else {
      j = k * N + n - (k * (k + 1) / 2);
    }
    return Aarray[j];
  }

  /**
   * Set one or two elements in the symmetric matrix A(n,k)=A(k,n)=s.
   *
   * @param n Row index.
   * @param k Column index.
   * @param s A(n,k).
   * @throws ArrayIndexOutOfBoundsException
   */
  public void set(int n, int k, double s) {
    int j = 0;
    if (n < k) {
      j = n * N + k - (n * (n + 1) / 2);
    } else {
      j = k * N + n - (k * (k + 1) / 2);
    }
    Aarray[j] = s;
  }

  /**
   * Copy a column of the matrix into argument d Legal range of the integer argument is <code>0 <= k
   * < K</code>. If argument is out of range a length <code>N</code> array of zeros should be
   * returned. <br> The corresponding Matlab expression would be: <code>A(:,k+1)</code>.
   *
   * @param k number of the column in the matrix
   * @param d the given column of the matrix
   * @throws IllegalArgumentException
   */
  public void getColumn(int k, double[] d) {
    if (d.length != N) {
      throw new IllegalArgumentException(
          "getColumn: argument d is not expected length N.");
    }
    int j = k;
    for (int n = 0; n < k; n++) {
      d[n] = Aarray[j];
      j = j + N - 1 - n;
    }
    for (int n = k; n < N; n++) {d[n] = Aarray[j++];}
  }

  /**
   * Copy an array (d) into a column, and a row, of the matrix
   *
   * @param k number of the column, and row, in the matrix
   * @param d the given column, and row, of the matrix
   */
  public void setColumn(int k, double[] d) {
    if (d.length != N) {
      throw new IllegalArgumentException(
          "setColumn: argument d is not expected length N.");
    }
    int j = k;
    for (int n = 0; n < k; n++) {
      Aarray[j] = d[n];
      j = j + N - 1 - n;
    }
    for (int n = k; n < N; n++) {Aarray[j++] = d[n];}
  }

  /**
   * Set inner-products of matrix B into this, i.e. A = B'*B,
   *
   * @param B A matrix of any kind, class is a subclass of AllMatrices
   * @throws IllegalArgumentException
   */
  public void eqInnerProductMatrix(AllMatrices B) {
    if (B.getK() != K) {
      throw new IllegalArgumentException("Matrix dimensions must agree.");
    }
    int j = 0;
    for (int k = 0; k < K; k++) {
      for (int n = k; n < N; n++, j++) {
        Aarray[j] = B.innerProduct(n, k);
      }
    }
  }
}

