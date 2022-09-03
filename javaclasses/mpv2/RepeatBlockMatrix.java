/*
 * class:  RepeatBlockMatrix
 *
 * Description:		This is a RepeatBlockMatrix consisting of a matrix
 *                  which is repeated many (L) times along the diagonal.
 *
 * HISTORY:  dd.mm.yyyy
 * Ver. 1.0  20.11.2008  KS: class made
 *
 * */

package mpv2;

import java.lang.*;
// import mpv2.util.*;

/**
 * This is a block (e.g. band) diagonal matrix where the block is the same 
 * each time it is repeated. The structure is like
<PRE>
     |---|                              or   |-------|
     | M ||---|                              |   M   | 
     |---|| M ||---|                         |-------|
          |---|| M | .                            |-------|
               |---|   .  |---|                   |   M   |
                          | M ||---|              |-------|
                          |---|| M |                     .
                               |---|                       .
</PRE>
 * The matrix M is repeated L times. Let M be of size N-by-K then for each repetition
 * the matrix is translated K positions horisonotally and dN positions vertically 
 * or dK positions horisonotally and N positions vertically as illustrated above.
 * That is dN==N or dK==K and 1 <= dN <= N and 1 <= dK <= K.
 * The number of repetitions may be flexible, (L==0).
 * The size (when L>0) of this matrix is  (N+(L-1)*dN)-by-(K+(L-1)*dK).
 * <P>
 * @author  Karl Skretting, University of Stavanger (UiS), (karl.skretting@uis.no)
 * @version November 2008
 */

public class RepeatBlockMatrix extends AllMatrices
implements Cloneable, java.io.Serializable {
    
/* ------------------------
   Class variables
 * ------------------------ */
    
    /** Only pointers to the submatrices are stored
   @serial internal array storage.
     */
    AllMatrices M;  
    int dN;   // vertikal translation
    int dK;   // horizontal translation
    int L;    // number of repetitions
    
/* ------------------------
   Constructors
 * ------------------------ */
    
    /** Construct a Block matrix with given matrix M.
   @param M    a matrix of any kind.
   @param vt   vertikal translation.
   @param ht   horizontal translation.
   @param rep  number of reprtitions, =0 if unspecified.
     */
    public RepeatBlockMatrix(AllMatrices M, int vt, int ht, int rep) {
        this.M = M;
        dN = Math.max(vt, 1);
        dK = Math.max(ht, 1);
        if (dN != M.getN()) {
            // a warning would be appropriate
            dK = M.getK();
        }
        L = Math.max(rep, 0);
        N = (N+(L-1)*dN);
        K = (K+(L-1)*dK);
    }
    
 /* ------------------------
   Public Methods, which use this object (A)
  * ------------------------ */
    
    /** Get a single element.
     *  The method is forwarded to getValue(...) in the correct submatrix.
   @param n    Row index.
   @param k    Column index.
   @return     A(n,k)
     */
    public double get(int n, int k) {
        double val = 0.0;
        // if (L==0) assume L is large (enough)
        if (dK == M.getK()) {
            if ((k >= 0) && ( (L==0) || (k < K) )) {
                // column within range
                int kM = k%M.getK();
                if ((n>=0) && ( (L==0) || (n < N) ) ) {
                    // row within range
                    int nM = n - dN*( k/M.getK() );
                    val = M.get(nM, kM);
                }
            }
        } else {
            if ((n>=0) && ( (L==0) || (n < N) ) ) {
                // row within range
                int nM = n%M.getN();
                if ((k >= 0) && ( (L==0) || (k < K) )) {
                    // column within range
                    int kM = k - dK*( n/M.getN() );
                    val = M.get(nM, kM);
                }
            }
        }
        return val;
    }
    
    
    /** Set a single element.
     *  The method is forwarded to setValue(...) in the correct submatrix.
   @param n    Row index.
   @param k    Column index.
   @param s    A(n,k).
     */
    public void set (int n, int k, double s) {
        // if (L==0) assume L is large (enough)
        if (dK == M.getK()) {
            if ((k >= 0) && ( (L==0) || (k < K) )) {
                // column within range
                int kM = k%M.getK();
                if ((n>=0) && ( (L==0) || (n < N) ) ) {
                    // row within range
                    int nM = n - dN*( k/M.getK() );
                    M.set(nM, kM, s);
                }
            }
        } else {
            if ((n>=0) && ( (L==0) || (n < N) ) ) {
                // row within range
                int nM = n%M.getN();
                if ((k >= 0) && ( (L==0) || (k < K) )) {
                    // column within range
                    int kM = k - dK*( n/M.getN() );
                    M.set(nM, kM, s);
                }
            }
        }
    }
    
    // m justere en del metoder for  takle L==0 tilfellet
    // kan de da kalle super.metode(..) for metode i AllMatrices ??
    
}

