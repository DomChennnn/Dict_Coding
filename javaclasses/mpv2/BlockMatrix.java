/*
 * class:  BlockMatrix
 *
 * Description:		This is a BlockMatrix consisting of four parts
 *
 * HISTORY:  dd.mm.yyyy
 * Ver. 1.0  17.11.2008  KS: class made
 *
 * */

package mpv2;

import java.lang.*;
// import mpv2.util.*;

/**
 * This is a block matrix consisting of four submatrices, Matlab-notation: [ A, B; C, D].
 * <P>
 * @author  Karl Skretting, University of Stavanger (UiS), (karl.skretting@uis.no)
 * @version November 2008
 */

public class BlockMatrix extends AllMatrices
implements Cloneable, java.io.Serializable {
    
/* ------------------------
   Class variables
 * ------------------------ */
    
    /** Only pointers to the submatrices are stored
   @serial internal array storage.
     */
    AllMatrices A, B, C, D;
    int top;  // rows 0,1,..., top-1 are in A and B
    int left; // columns 0,1,..., left-1 are in A and C
    
/* ------------------------
   Constructors
 * ------------------------ */
    
    /** Construct a Block matrix with four submatrices, [A, B; C, D].
     * The dimensions for the submatrices must agree.
     * Note that some of the matrices may be null, legal use is like: <BR>
     * BlockMatrix(A,B,C,D); BlockMatrix(A,B,null,null);
     * BlockMatrix(A,null,C,null); BlockMatrix(A,null,null,D);
     * BlockMatrix(A,B,C,null);BlockMatrix(A,B,null,D);
   @param A    A matrix of any kind, or null.
   @param B    A matrix of any kind, or null.
   @param C    A matrix of any kind, or null.
   @param D    A matrix of any kind, or null.
     */
    public BlockMatrix(AllMatrices A, AllMatrices B, AllMatrices C, AllMatrices D) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
        if ((A==null) & (C==null)) {
            left = 0;
        } else if (A==null) {
            left = C.getK();
        } else if (C==null) {
            left = A.getK();
        } else{
            left = Math.max(A.getK(),C.getK());
        }
        if ((A==null) & (B==null)) {
            top = 0;
        } else if (A==null) {
            top = B.getN();
        } else if (B==null) {
            top = A.getN();
        } else{
            top = Math.max(A.getN(),B.getN());
        }
        if ((B==null) & (D==null)) {
            K = left;
        } else if (B==null) {
            K = left + D.getK();
        } else if (D==null) {
            K = left + B.getK();
        } else{
            K = left + Math.max(B.getK(),D.getK());
        }
        if ((C==null) & (D==null)) {
            N = top;
        } else if (C==null) {
            N = top + D.getN();
        } else if (D==null) {
            N = top + C.getN();
        } else{
            N = top + Math.max(C.getN(),D.getN());
        }
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
    public double get (int n, int k) {
        double val = 0.0;
        if (n < top){
            if (k < left){
                if (A != null) val = A.getValue(n,k);
            } else {  // k >= left
                if (B != null) val = B.getValue(n,k-left);
            }
        } else { // n >= top
            if (k < left){
                if (C != null) val = C.getValue(n-top,k);
            } else {  // k >= left
                if (D != null) val = D.getValue(n-top,k-left);
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
        if (n < top){
            if (k < left){
                if (A != null) A.setValue(n,k,s);
            } else {  // k >= left
                if (B != null) B.setValue(n,k-left,s);
            }
        } else { // n >= top
            if (k < left){
                if (C != null) C.setValue(n-top,k,s);
            } else {  // k >= left
                if (D != null) D.setValue(n-top,k-left,s);
            }
        }
    }
    
    /** Copy a column of the matrix into argument y
     * Legal range of the integer argument is <code>0 <= k < K</code>.
     * If argument is out of range a length <code>N</code> array of zeros should be returned. <br>
     * The corresponding Matlab expression would be: <code>A(:,k+1)</code>.
     *
     * @param k number of the column in the matrix
     * @param y the given column of the matrix
     * */
    public void getColumn(int k, double[] y){
        if (y.length != N)  throw new IllegalArgumentException(
        "getColumn: argument y is not expected length N.");
        for (int n=0; n<N; n++) y[n] = 0.0;
        if (k < left){  // from A and C
            if (A != null) {
                double[] col = A.getColumn(k);
                for (int n=0; n<A.getN(); n++) y[n] = col[n];
            }
            if (C != null) {
                double[] col = C.getColumn(k);
                for (int n=0; n<C.getN(); n++) y[top+n] = col[n];
            }
        } else {        // from B and D
            if (B != null) {
                double[] col = B.getColumn(k-left);
                for (int n=0; n<B.getN(); n++) y[n] = col[n];
            }
            if (D != null) {
                double[] col = D.getColumn(k-left);
                for (int n=0; n<D.getN(); n++) y[top+n] = col[n];
            }
        }
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
        for (int k=0; k<K; k++) x[k] = 0.0;
        if (n < top){  // from A and B
            if (A != null) {
                double[] row = A.getRow(n);
                for (int k=0; k<A.getK(); k++) x[k] = row[k];
            }
            if (B != null) {
                double[] row = B.getRow(n);
                for (int k=0; k<B.getK(); k++) x[left+k] = row[k];
            }
        } else {        // from C and D
            if (C != null) {
                double[] row = C.getRow(n);
                for (int k=0; k<C.getK(); k++) x[k] = row[k];
            }
            if (D != null) {
                double[] row = D.getRow(n);
                for (int k=0; k<D.getK(); k++) x[left+k] = row[k];
            }
        }
    }
    
}

