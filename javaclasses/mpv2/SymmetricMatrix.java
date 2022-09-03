/*
 * class:  SymmetricMatrix
 *
 * Description:		This is symmetric matrix implementation of AllMatrices
 *
 * HISTORY:  dd.mm.yyyy
 * Ver. 1.0  27.11.2008  KS: class made
 * Ver. 2.0  10.02.2011  KS: Store only N*(N+1)/2 elements for a NxN matrix
 *
 * */

package mpv2;

import java.lang.*;
// import mpv2.util.*;

/**
   This is a symmetric matrix class made by implementing AllMatrices.
   Entries are stored columnwise in a one-dimensional array, for each
   column only elements on or below the main diagonal are stored.
 <P>
  @author  Karl Skretting, University of Stavanger (UiS), (karl.skretting@uis.no)
  @version February 2011
*/

public class SymmetricMatrix extends AllMatrices 
    implements Cloneable, java.io.Serializable {

/* ------------------------
   Class variables
 * ------------------------ */

   /** Array for internal storage of elements, column-stacked array.
   @serial internal array storage.
   */
   private double[] Aarray;

/* ------------------------
   Constructors
 * ------------------------ */

   /** Construct an n-by-n matrix of zeros. 
    *  Note that there exist a constructor that takes a 2D array argument
    *  and that a single double number as input is interpreted as an array.
    *  Thus to call this one from Matlab use:  
    *  jA = SymmetricMatrix(int32(5));
   @param n    Number of rows.
   */
   public SymmetricMatrix(int n) {
      N = n; 
      K = n;    
      Aarray = new double[N*(N+1)/2];
   }

   /** Construct an n-by-n matrix of zeros. 
   @param n    Number of rows.
   @param k    Number of colums is also n, so k is ignored!
   */
   public SymmetricMatrix(int n, int k) {
      N = n; 
      K = n;    
      Aarray = new double[N*(N+1)/2];
   }

   /** Construct an m-by-n constant matrix.
   @param n    Number of rows.
   @param k    Number of colums is also n, so k is ignored!
   @param s    Fill the matrix with this scalar value.
   */
   public SymmetricMatrix(int n, int k, double s) {
      N = n; 
      K = n;    
      Aarray = new double[N*(N+1)/2];
      for (int i = 0; i < (N*(N+1)/2); i++) Aarray[i] = s;
   }

   /** Construct a matrix from a 2-D array, only lower-left-values are used.
   @param A    Two-dimensional array of doubles.
   @exception  IllegalArgumentException All rows must have the same length
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
      Aarray = new double[N*(N+1)/2];
      int j = 0;
      for (int k = 0; k < K; k++) {
          for (int n = k; n < N; n++, j++) {
              Aarray[j] = A[n][k];
          }
      }
   }

   /** Construct a new matrix from another matrix (of any kind), 
    *  only lower-left-values are used.
    *  If argument B is an object of class SymmetricMatrix this is the
    *  same as (deep) copy (or clone). Note that argument B may
    *  be another kind of matrix, ex. an object of class SparseMatrix
    *  or BandMatrix or any subclass of AllMatrices
   @param B    A matrix of any kind, class is a subclass of AllMatrices
   @exception  IllegalArgumentException Number of rows and Columns must be the same
   @see        #copy
   */
   public SymmetricMatrix(AllMatrices B) {
     if (B.getN() != B.getK()) {
            throw new IllegalArgumentException(
            "Number of rows and columns must be the same.");
         }
      N = B.getN();
      K = N;
      Aarray = new double[N*(N+1)/2];
      double[] col = new double[N];
      int j = 0;
      for (int k = 0; k < K; k++) {
          B.getColumn(k, col);
          for (int n = k; n < N; n++, j++) {
              Aarray[j] = col[n];
          }
      }
   }

 /* ------------------------
   Static Methods
 * ------------------------ */
   
   /** Generate identity matrix
   @param n    Size is n-by-n
   */
   public static SymmetricMatrix identity(int n) {
      SymmetricMatrix X = new SymmetricMatrix(n);      // all zero matrix
      double[] A = X.getArray();
      int j = 0;
      for (int k = 0; k < n; k++) {
          A[j] = 1.0;
          j = j+n-k;
      }
      return X;
   }

    /** Generate inner-product matrix from input matrix B, i.e. A = B'*B,
   @param B    A matrix of any kind, class is a subclass of AllMatrices
     */
    public static SymmetricMatrix innerProductMatrix(AllMatrices B) {
        int k = B.getK();
        SymmetricMatrix X = new SymmetricMatrix(k);      // all zero matrix
        X.eqInnerProductMatrix(B);
        return X;
    }


 /* ------------------------
   Private Methods 
 * ------------------------ */
    private double[] getArray(){
        return Aarray;
    }

 /* ------------------------
   Public Methods, which use this object (A)
 * ------------------------ */

   /** Make a deep copy of a matrix
   */
   public SymmetricMatrix copy() {
      return new SymmetricMatrix(this);
   }

   /** Clone the Matrix object.
   */
   public Object clone() {
      return new SymmetricMatrix(this);
   }

   /** Get a single element.
   @param n    Row index.
   @param k    Column index.
   @return     A(n,k)
   @exception  ArrayIndexOutOfBoundsException
   */
   public double get(int n, int k) {
       int j = 0;
       if (n < k) {
           j = n*N+k-(n*(n+1)/2);
       } else {
           j = k*N+n-(k*(k+1)/2);
       }
       return Aarray[j];
   }


   /** Set one or two elements in the symmetric matrix A(n,k)=A(k,n)=s.
   @param n    Row index.
   @param k    Column index.
   @param s    A(n,k).
   @exception  ArrayIndexOutOfBoundsException
   */
   public void set(int n, int k, double s) {
       int j = 0;
       if (n < k) {
           j = n*N+k-(n*(n+1)/2);
       } else {
           j = k*N+n-(k*(k+1)/2);
       }
       Aarray[j] = s;
   }

    /** Copy a column of the matrix into argument d
     * Legal range of the integer argument is <code>0 <= k < K</code>.
     * If argument is out of range a length <code>N</code> array of zeros should be returned. <br>
     * The corresponding Matlab expression would be: <code>A(:,k+1)</code>.
     *
    @param k number of the column in the matrix
    @param d the given column of the matrix
    @exception  IllegalArgumentException
     * */
    public void getColumn(int k, double[] d){
        if (d.length != N)  throw new IllegalArgumentException(
            "getColumn: argument d is not expected length N.");
        int j = k;
        for (int n = 0; n<k; n++) {
            d[n] = Aarray[j];
            j = j+N-1-n;
        }
        for (int n = k; n<N; n++) d[n] = Aarray[j++];
    }

    /** Copy an array (d) into a column, and a row, of the matrix
     *
     * @param k number of the column, and row, in the matrix
     * @param d the given column, and row, of the matrix
     * */
    public void setColumn(int k, double[] d){
        if (d.length != N)  throw new IllegalArgumentException(
            "setColumn: argument d is not expected length N.");
        int j = k;
        for (int n = 0; n<k; n++) {
            Aarray[j] = d[n];
            j = j+N-1-n;
        }
        for (int n = k; n<N; n++) Aarray[j++] = d[n];
    }
    
    /** Copy a row of the matrix into argument d
     *
     * @param n number of the row in the matrix
     * @param d the given column of the matrix
     * */
    public void getRow(int n, double[] d){
        // a row is the same as the corresponding column in a symmetric matrix
        getColumn(n, d);   
    }

    /** Set a row, and a column, of the matrix
     *
     * @param n number of the row in the matrix <code>A</code>.
     * @param r the given row of matrix <code>A</code>.
     * */
    public void setRow(int n, double[] r){
        // a row is the same as the corresponding column in a symmetric matrix
        setColumn(n, r);   
    }

/* ------------------------
   Update methods, eq...
   Most of these generally do not give a symmetric matrix as result.
   For many methods only documentation is changed a little bit, and
   then the same method in AllMatrices is used as is.
   In setAlll(vals) a warning is printed if vals is not a symmetric matrix.
 * ------------------------ */
    
    /** Set all entries of the matrix to the supplied new values.
     *  Note that even if vals contains all the N*N values of the
     *  symmetric matrix, only those in the lower left part is used,
     *  and thus the result is symmetric anyway.
     * If argument is wrong length an IllegalArgumentException is thrown. 
     *
    @param vals One-dimensional array of doubles, packed by columns (ala Fortran).
    @exception  IllegalArgumentException
     * */
    public void setAll(double[] vals){
        if (N*K != vals.length) { throw new IllegalArgumentException
                ("Array length must as total size of matrix (N*K).");
        }
        double notSymmetric = 0.0;
        int j = 0;
        for (int k=0; k<K; k++) {
            for (int n=k; n<N; n++, j++) {
                double temp = vals[k*N+n];
                Aarray[j] = temp;
                if (vals[n*N+k] != temp) notSymmetric += Math.abs(vals[n*N+k] - temp);
            }
        }
        if ( notSymmetric > (1e-10) ) {
            System.out.println("Info: the supplied values was not a symmetric matrix, " 
                               + "deviation is " + notSymmetric );
            System.out.println("Note that only lower left part was used.");
        }
        // System.out.println("setAll method in SymmetricMatrix used.");
    }
    
    /** Set the matrix (this = A) to B*s, A = B*s.
     *  Note that since this matrix (A) is symmetric only lower left
     *  part of B is used.
     *
    @param B    Matrix of same size as A
    @exception  IllegalArgumentException
     * */
    public void eqProduct(AllMatrices B, double s){
    	checkMatrixDimensions(B);    // B should be same size as this
        int j = 0;
        for (int k=0; k<K; k++) {
            for (int n=k; n<N; n++, j++) {
                Aarray[j] = s*B.get(n,k);
            }
       	}
    }
    
    // only help text (documentation) is changed when 'super' is called
    // super calls method in AllMatrices
    
    /** Permute the rows of matrix B and put result in this, A = P * B.
     *  Note that the result usually is not a symmetric matrix, 
     *  but symmetry in A is kept anyway so result will probably be wrong.
     *
    @param P    Permutation matrix of size N-by-N 
    @param B    Matrix of size N-by-K, size of result (this) is also N-by-K
    @exception  IllegalArgumentException
     * */
    public void eqPermuteRows(PermutationMatrix P, AllMatrices B){
        super.eqPermuteRows(P, B);
    }
    
    /** Permute the columns of matrix B and put result in this, A = B * P.
     *  Note that the result usually is not a symmetric matrix, 
     *  but symmetry in A is kept anyway so result will probably be wrong.
     *
    @param B    Matrix of size N-by-K, size of result (this) is also N-by-K
    @param P    Permutation matrix of size K-by-K 
    @exception  IllegalArgumentException
     * */
    public void eqPermuteColumns(AllMatrices B, PermutationMatrix P){
        super.eqPermuteColumns(B, P);
    }
    
    /** Scale the rows and columns of symmtric matrix B and put result in this, A = D * B * D.
     *
    @param B    Symmetric matrix of size N-by-N, size of result (this) is also N-by-N
    @param D    Diagonal matrix of size N-by-N, class DiagonalMatrix 
     * */
    public void eqScaleRowsAndColumns(SymmetricMatrix B, DiagonalMatrix D){
        eqScaleRowsAndColumns(D, B);
    }
    
    /** Scale the rows and columns of symmtric matrix B and put result in this, A = D * B * D.
     *
    @param D    Diagonal matrix of size N-by-N, class DiagonalMatrix 
    @param B    Symmetric matrix of size N-by-N, size of result (this) is also N-by-N
     * */
    public void eqScaleRowsAndColumns(DiagonalMatrix D, SymmetricMatrix B){
        if (D.getN() != N || B.getN() != N || B.getK() != K) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
        double[] vals = B.getArray();  // a copy of all entries in B
        int j = 0;
        for (int k=0; k<K; k++) {
            for (int n=k; n<N; n++, j++) {
                Aarray[j] = D.get(n)*D.get(k)*vals[j];
            }
       	}
    }
    
    /** Scale the rows of matrix B and put result in this, A = D * B.
     *  Note that the result usually is not a symmetric matrix, 
     *  but symmetry in A is kept anyway so result will probably be wrong.
     *
    @param D    Diagonal matrix of size N-by-N, class DiagonalMatrix 
    @param B    Matrix of size N-by-K, size of result (this) is also N-by-K
    @exception  IllegalArgumentException
     * */
    public void eqScaleRows(DiagonalMatrix D, AllMatrices B){
        super.eqScaleRows(D, B);
    }
    
    /** Scale the columns of matrix B and put result in this, A = B * D.
     *  Note that the result usually is not a symmetric matrix, 
     *  but symmetry in A is kept anyway so result will probably be wrong.
     *
    @param B    Matrix of size N-by-K, size of result (this) is also N-by-K
    @param D    Diagonal matrix of size N-by-N, class DiagonalMatrix 
    @exception  IllegalArgumentException
     * */
    public void eqScaleColumns(AllMatrices B, DiagonalMatrix D){
        super.eqScaleColumns(B, D);
    }

    /** Set inner-products of matrix B into this, i.e. A = B'*B,
    @param B    A matrix of any kind, class is a subclass of AllMatrices
    @exception  IllegalArgumentException
     */
    public void eqInnerProductMatrix(AllMatrices B) {
        if (B.getK() != K ) {
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        }
        int j = 0;
        for (int k=0; k<K; k++) {
            for (int n=k; n<N; n++, j++) {
                Aarray[j] = B.innerProduct(n,k);
            }
       	}
    }

    
/* ------------------------
   Update methods, pluseq...
 * ------------------------ */
    // Note that scaling of outer product, i.e. s2, often is more effectively done 
    // by scaling of the vectors u and v by the calling method.

    /** Add outer product of two vectors to this, A = s1*A + s2*(u*v'+v*u').
     *  Note that this method is different for a SymmetricMatrix than
     *  for other matrices, (AllMatrices).
    @param s1   A single value
    @param s2   A single value
    @param u    A vector with N elements
    @param v    A vector with N elements
    @exception  IllegalArgumentException
     * */
    public void pluseqOuterProduct(double s1, double s2, double[] u, double[] v){
        if ((u.length != N) || (v.length != N)) {
            throw new IllegalArgumentException("Matrix and vector dimensions must agree.");
        }
        // effective implementation if s1==0.0, s1==1.0, s2==1.0; s2==-1.0, and, v[k]==0.0
        if (s1 == 0.0){
            if (s2 == 1.0){
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] = (u[n]*v[k]+u[k]*v[n]);
                    }
                }
            } else if (s2 == -1.0) {
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] = -(u[n]*v[k]+u[k]*v[n]);
                    }
                }
            } else {  // s2 is a general number
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] = s2*(u[n]*v[k]+u[k]*v[n]);
                    }
                }
            }
        } else if (s1 == 1.0) {
            if (s2 == 1.0){
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] += (u[n]*v[k]+u[k]*v[n]);
                    }
                }
            } else if (s2 == -1.0) {
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] -= (u[n]*v[k]+u[k]*v[n]);
                    }
                }
            } else {  // s2 is a general number
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] += s2*(u[n]*v[k]+u[k]*v[n]);
                    }
                }
            }
        } else {  // s1 is general number
            if (s2 == 1.0){
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] *= s1;
                        Aarray[j] += (u[n]*v[k]+u[k]*v[n]);
                    }
                }
            } else if (s2 == -1.0) {
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] *= s1;
                        Aarray[j] -= (u[n]*v[k]+u[k]*v[n]);
                    }
                }
            } else {  // s2 is a general number
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] *= s1;
                        Aarray[j] += s2*(u[n]*v[k]+u[k]*v[n]);
                    }
                }
            }
        }
    }

    /** Add outer product of a vector to scaled this, A = s1*A + s2*(u*u').
     *  This matrix, A, is of size N-by-N.
     *  Each element is updated as: A(n,k) = s1*A(n,k) + s2*u(n)*u(k)
     *  Note that this method is like the one with two arguments vectors 
     *  for other matrices, (AllMatrices).
     *
    @param s1   A single value
    @param s2   A single value
    @param u    A vector with N elements
    @exception  IllegalArgumentException
     * */
    public void pluseqOuterProduct(double s1, double s2, double[] u){
        if (u.length != N) {
            throw new IllegalArgumentException("Matrix and vector dimensions must agree.");
        }
        if (s1 == 0.0){
            if (s2 == 1.0){
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] = u[n]*u[k];
                    }
                }
            } else if (s2 == -1.0) {
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] = -u[n]*u[k];
                    }
                }
            } else {  // s2 is a general number
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] = s2*u[n]*u[k];
                    }
                }
            }
        } else if (s1 == 1.0) {
            if (s2 == 1.0){
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] += u[n]*u[k];
                    }
                }
            } else if (s2 == -1.0) {
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] -= u[n]*u[k];
                    }
                }
            } else {  // s2 is a general number
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] += s2*u[n]*u[k];
                    }
                }
            }
        } else {  // s1 is general number
            if (s2 == 1.0){
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] *= s1;
                        Aarray[j] += u[n]*u[k];
                    }
                }
            } else if (s2 == -1.0) {
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] *= s1;
                        Aarray[j] -= u[n]*u[k];
                    }
                }
            } else {  // s2 is a general number
                int j = 0;
                for (int k=0; k<K; k++) {
                    for (int n=k; n<N; n++, j++) {
                        Aarray[j] *= s1;
                        Aarray[j] += s2*u[n]*u[k];
                    }
                }
            }
        }
    }
    
}

