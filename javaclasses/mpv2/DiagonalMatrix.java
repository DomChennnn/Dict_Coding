/*
 * class:  DiagonalMatrix
 *
 * Description:		This is a diagonal matrix
 *
 * HISTORY:  dd.mm.yyyy
 * Ver. 1.0  11.11.2008  KS: class made
 *
 * */

package mpv2;

import java.lang.*;
// import mpv2.util.*;

/**
   This is a diagonal matrix class made by implementing AllMatrices.
 <P>
  @author  Karl Skretting, University of Stavanger (UiS), (karl.skretting@uis.no)
  @version November 2008
*/

public class DiagonalMatrix extends AllMatrices 
    implements Cloneable, java.io.Serializable {

/* ------------------------
   Class variables
 * ------------------------ */

   /** Array for internal storage of elements.
   @serial internal array storage.
   */
   private double[] diag;

/* ------------------------
   Constructors
 * ------------------------ */

   /** Construct an n-by-n diagonal matrix of zeros. 
   @param n    Number of rows and columns
   */
   public DiagonalMatrix(int n) {
      N = n; 
      K = n;    
      diag = new double[N];
   }

   /** Construct an n-by-n diagonal matrix with constant value in each element
   @param n    Number of rows and columns
   @param s    Fill the matrix with this scalar value.
   */

   public DiagonalMatrix(int n, double s) {
      N = n; 
      K = n;    
      diag = new double[N];
      for (int i = 0; i < N; i++) diag[i] = s;
   }

   /** Construct a diagonal matrix from a 2-D array, only values in the diagonal are copied.
   @param A    Two-dimensional array of doubles.
   @exception  IllegalArgumentException All rows must have the same length
   */
   public DiagonalMatrix(double[][] A) {
      N = Math.min(A.length, A[0].length);
      K = N;
      for (int n = 0; n < N; n++) {
         if (A[n].length < N) {
            throw new IllegalArgumentException("A row is too short.");
         }
      }
      diag = new double[N];
      for (int n = 0; n < N; n++) diag[n] = A[n][n];
   }

   /** Construct a diagonal matrix from a one-dimensional array
   @param vals One-dimensional array
   */
   public DiagonalMatrix(double vals[]) {
      N = vals.length;
      K = N;
      diag = new double[N];
      for (int n = 0; n < N; n++) diag[n] = vals[n];
   }

   /** Construct a diagonal matrix from another matrix (of any kind) 
    *  by extracting the elements on the diagonal.
   @param B    a matrix of any kind, class is a subclass of AllMatrices
   */
   public DiagonalMatrix(AllMatrices B) {
      N = Math.min(B.getN(), B.getK());
      K = N;
      diag = new double[N];
      for (int n = 0; n < N; n++) diag[n] = B.get(n,n);
   }

 /* ------------------------
   Public Methods, which use this object (A)
 * ------------------------ */

   /** Make a deep copy of a matrix
   */
   public DiagonalMatrix copy() {
      return new DiagonalMatrix(this);
   }

   /** Clone the Matrix object.
   */
   public Object clone() {
      return new DiagonalMatrix(this);
   }

   /** Get a single element, A(n,n).
   @param n    Row and column index.
   @return     A(n,n)
   @exception  ArrayIndexOutOfBoundsException
   */
   public double get(int n) {
       return diag[n];
   }
   
   /** Get a single element, A(n,k).
   @param n    Row index.
   @param k    Column index.
   @return     A(n,k)
   @exception  ArrayIndexOutOfBoundsException
   */
   public double get(int n, int k) {
       if (n != k) return 0.0;
       if ((n<0) | (n>=N)) return 0.0;
       return diag[n];
   }


   /** Set a single element, A(n,n)=s.
   @param n    Row and column index.
   @param s    A(n,n).
   @exception  ArrayIndexOutOfBoundsException
   */
   public void set(int n, double s) {
       // if ((n<0) | (n>=N)) return;
       diag[n] = s;
   }

   /** Set a single element, A(n,k)=s, but only if (n==k).
   @param n    Row index.
   @param k    Column index.
   @param s    A(n,k).
   @exception  ArrayIndexOutOfBoundsException
   */
   public void set(int n, int k, double s) {
       if (n != k) return;
       if ((n<0) | (n>=N)) return;
       diag[n] = s;
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
        if (x.length != N)  throw new IllegalArgumentException(
            "addColumn: argument x is not expected length N.");
        x[k] += factor*diag[k];
    }
    
    /** Set all entries of to matrix to the supplied new values
     * If argument is wrong length an IllegalArgumentException is thrown. <br>
     *
     * @param vals One-dimensional array of doubles, packed by columns (ala Fortran).
     * */
    public void setAll(double[] vals){
        if (N != vals.length) {
            throw new IllegalArgumentException
                ("Array length must be length of the diagonal (N).");
        }
        for (int n = 0; n < N; n++) diag[n] = vals[n];
    }

    // de fleste metoder kan gjøres betydelig enklere når en har ei diagonal matrise
    
    /** Frobenius norm
   @return    sqrt of sum of squares of all elements.
     */
    public double normF() {
        double f = 0.0;
        for (int n = 0; n < N; n++) {
            f = Math.hypot(f,diag[n]);
        }
        return f;
    }
    /** Matrix determinant
   @return     determinant
     */
    public double det() {
        double f = 1.0;
        for (int n = 0; n < N; n++) f *= diag[n];
        return f;
    }

    /** Matrix rank
   @return     effective numerical rank, obtained from SVD.
     */
    public int rank() {
        int f = 1;
        for (int n = 0; n < N; n++) {
            if (diag[n] != 0.0) f++;
        }
        return f;
    }

    /** Matrix condition (2 norm)
   @return     ratio of largest to smallest singular value.
     */
    public double cond() {
        double ma = Math.abs(diag[0]);
        double mi = Math.abs(diag[0]);
        for (int n = 1; n < N; n++) {
            mi = Math.min(mi, Math.abs(diag[n]));
            ma = Math.max(ma, Math.abs(diag[n]));
        }
        if (mi == 0.0) return 0.0;
        return mi/ma;
    }
    
    /** Two norm
   @return    maximum singular value.
     */
    public double norm2 () {
        double ma = Math.abs(diag[0]);
        for (int n = 1; n < N; n++) {
            ma = Math.max(ma, Math.abs(diag[n]));
        }
        return ma;
    }

    /** Returns the inner product of two matrix column vectors,
     * which for a diagonal matrix is zero.
     *
     * @param k1 number for the first dictionary element.
     * @param k2 number for the second dictionary element.
     * */
    public double innerProduct(int k1, int k2){
        return 0.0;
    }

    /** Multiplies the matrix by an array, x = A*y.
     *
     * @param y  the input array
     * @param x  the results as an array of length <code>N</code>.
     * */
    public void times(double[] y, double[] x){
        // N==K og A er diagonal
        if (y.length != K)  throw new IllegalArgumentException(
            "times: argument y is not expected length K.");
        if (x.length != N)  throw new IllegalArgumentException(
            "times: argument x is not expected length N.");
        for (int n=0; n<N; n++) x[n] = diag[n]*y[n];
        return;
    }

    /** Multiplies the transposed matrix by an array, y = A'*x.
     * Since a diagonal matrix is equal to its transposed y = A*x
     *
     * @param x  the input array
     * @param y  the results as an array of length <code>K</code>.
     * */
    public void transposeTimes(double[] x, double[] y){
        times(x,y);
    }
  
   
}

