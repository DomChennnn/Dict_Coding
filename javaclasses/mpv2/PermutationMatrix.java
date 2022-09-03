/*
 * class:  PermutationMatrix
 *
 * Description:		This is a permutation matrix
 *
 * HISTORY:  dd.mm.yyyy
 * Ver. 1.0  12.11.2008  KS: class made
 *
 * */

package mpv2;

import java.lang.*;
// import mpv2.util.*;

/**
   This is a permutation matrix class made by implementing AllMatrices.
 <P>
  @author  Karl Skretting, University of Stavanger (UiS), (karl.skretting@uis.no)
  @version November 2008
*/

public class PermutationMatrix extends AllMatrices 
    implements Cloneable, java.io.Serializable {

/* ------------------------
   Class variables
 * ------------------------ */

   /** Array for internal storage of elements.
    * Gives position for the ones in each row, A(n,k)==1  <=>  pos[n]==k. 
   @serial internal array storage.
   */
   private int[] pos;

/* ------------------------
   Constructors
 * ------------------------ */

   /** Construct an n-by-n permutation matrix with ones on the diagonal, i.e. identity matrix.
   @param n    Number of rows and columns
   */
   public PermutationMatrix(int n) {
      N = n; 
      K = N;    
      pos = new int[N];
      for (int i=0; i<N; i++) pos[i] = i;
   }

   /** Construct an N-by-N permutation matrix from a vector p, A(n,k)==1  <=>  p[n]==k. 
    *  p must be a permutation of the integers from 0 to (N-1).
   @param p   the vector that gives p. 
   @exception  IllegalArgumentException
   */
   public PermutationMatrix(int[] p) {
      N = p.length; 
      // test
      pos = new int[N];
      for (int i=0; i<N; i++) {
          if ((p[i] >= 0) && (p[i]<N)) pos[p[i]] = 1;
      }
      int sum = 0;
      for (int i=0; i<N; i++) sum += pos[i];
      if (sum != N) throw new IllegalArgumentException(
            "PermutationMatrix: argument p is not a permutation of the first integers.");
      // ok
      K = N;    
      for (int i=0; i<N; i++) pos[i] = p[i];
   }

   /** Construct an n-by-n permutation matrix which will make the given
    * array when the matrix is applied to the sorted sequence, ord = A*ascending(ord).
    * Note that A'*ord will give an ordered sequence, i.e. ascending(ord).
   @param ord    the sequence that the permutation matrix will put the ordered sequence into
   */
   public PermutationMatrix(double[] ord) {
      N = ord.length; 
      K = N;    
      pos = new int[N];
      for (int i=0; i<N; i++) pos[i] = i;
      QuickSort.quicksort(ord, pos);
   }

 /* ------------------------
   Static Public Methods, which return different permutation matrices
 * ------------------------ */
 
    /** Generate randon permutation matrix
   @param n    Number of rows and colums.
   @return     An n-by-n permutation matrix
   */
   public static PermutationMatrix random(int n) {
      PermutationMatrix P = new PermutationMatrix(n);
      double[] d = new double[n];
      for (int i=0; i<n; i++) d[i] = Math.random();
      QuickSort.quicksort(d, P.getPos());
      return P;
   }


   /** Generate identity matrix
   @param n    Number of rows and colums.
   @return     An n-by-n identity matrix
   */
   public static PermutationMatrix identity(int n) {
      return new PermutationMatrix(n);
   }
   
   /** Generate reverse matrix
   @param n    Number of rows and colums.
   @return     An n-by-n reverse matrix
   */
   public static PermutationMatrix reverse(int n) {
      PermutationMatrix P = new PermutationMatrix(n);
   	  int[] p =  P.getPos();
   	  int j = n-1;
      for (int i=0; i<n; i++, j--) p[i] = j;
      return P;
   }

   /** Generate perfect shuffle (stride) permutation matrix.
    * This permutation always start with the first element, i.e. P(0,0)=1.
    * The next element is found by going s positions forward (mod n), and if
    * that element is already used go forward until next unused ek\lement is
    * found.
   @param n    Number of rows and colums.
   @param s    The stride, how many elements to go forward on each, normally a factor of n.
   @return     An n-by-n perfect shuffle (stride) permutation matrix
   */
   public static PermutationMatrix perfectShuffle(int n, int s) {
      PermutationMatrix P = new PermutationMatrix(n);
      int[] p =  P.getPos();
      int[] used = new int[n];
      // 
      int j = 0;
      for (int i=0; i<n; i++) {
          p[i] = j;
          used[j] = 1;
          j = (j+s)%n;
          while (used[j] == 1){ 
              j = (j+1)%n;
          }
      }
      return P;
   }

 /* ------------------------
   Public Methods, which use this object (A)
 * ------------------------ */

   /** Make a deep copy of a matrix
   */
   public PermutationMatrix copy() {
   	  PermutationMatrix P = new PermutationMatrix(N);
   	  int[] p =  P.getPos();
   	  for (int i=0; i<N; i++) p[i] = pos[i];
      return P;
   }

   /** Clone the Matrix object.
   */
   public Object clone() {
      return this.copy();
   }

   /** Get a single element, A(n,k).
   @param n    Row index.
   @param k    Column index.
   @return     A(n,k)
   @exception  ArrayIndexOutOfBoundsException
   */
   public double get(int n, int k) {
       if (pos[n] == k) return 1.0;
       return 0.0;
   }

   /** Access the internal array pos, we have A(n,k)==1  <=>  pos[n]==k.
   @return     Pointer to the one-dimensional array of column numbers.
   */
   public int[] getPos() {
      return pos;
   }

   /** Set a single element, A(n,k)=s, this is not allowed som method just return.
   @param n    Row index.
   @param k    Column index.
   @param s    A(n,k).
   @exception  ArrayIndexOutOfBoundsException
   */
   public void set(int n, int k, double s) {
   	   return;
   }
    
    /** Frobenius norm
   @return    sqrt of sum of squares of all elements.
     */
    public double normF() {
        return Math.sqrt(N);
    }

    /** Matrix determinant
   @return     determinant
     */
    public double det() {
        return 1.0;
    }

    /** Matrix rank
   @return     effective numerical rank, obtained from SVD.
     */
    public int rank() {
        return N;
    }

    /** Matrix condition (2 norm)
   @return     ratio of largest to smallest singular value.
     */
    public double cond() {
        return 1.0;
    }
    
    /** Two norm
   @return    maximum singular value.
     */
    public double norm2() {
        return 1.0;
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
        // N==K og A er permutasjonsmatrise
        if (y.length != K)  throw new IllegalArgumentException(
            "times: argument y is not expected length K.");
        if (x.length != N)  throw new IllegalArgumentException(
            "times: argument x is not expected length N.");
        for (int n=0; n<N; n++) x[n] = y[pos[n]];
        return;
    }

    /** Multiplies the transposed matrix by an array, y = A'*x.
     * Note that the transposed of a permutation matrix is its inverse.
     *
     * @param x  the input array
     * @param y  the results as an array of length <code>K</code>.
     * */
    public void transposeTimes(double[] x, double[] y){
        // N==K og A er permutasjonsmatrise
        if (y.length != K)  throw new IllegalArgumentException(
            "times: argument y is not expected length K.");
        if (x.length != N)  throw new IllegalArgumentException(
            "times: argument x is not expected length N.");
        for (int n=0; n<N; n++)  y[pos[n]] = x[n];
    }
     
}

