package mpv2;        

   /** LUP Decomposition, P*A = L*U
    *  A is N-by-K, P is N-by-N, 
    *  M is Math.min(N,K), then L is N-by-M and U is M-by-K
   */

public class LUPDecomposition implements java.io.Serializable {

/* ------------------------
   Class variables
 * ------------------------ */

   /** Array for internal storage of decomposition and permutations
   @serial internal array storage.
   @serial permutations.
   */
   private double[] LU;     // verdier i matrisa ordnet kolonnevis
   private int[] perm;

   /** Row and column dimensions.
   @serial column dimension.
   @serial row dimension.
   @serial pivot sign.
   */
   private int N, K, pivsign;
   
/* ------------------------
   Constructor
 * ------------------------ */

   /** LUP Decomposition, P*A = L*U
    *  Constructor returns a structure to access L, U and P.
   @param  A   Rectangular matrix of any AllMatrices type
   */
   public LUPDecomposition(AllMatrices A) {
      LU = A.getAll();        // A(n,k) = LU[k*N+n]
      N = A.getN();
      K = A.getK();
      doLU();
   }

   /** LUP Decomposition, P*A = L*U
    *  Constructor returns a structure to access L, U and P.
   @param  N   Number of rows in the matrix
   @param  K   Number of columns in the matrix
   @param  vals   The (N*K) matrix elements ordered by columns in an array
   @exception  IllegalArgumentException
   */
   public LUPDecomposition (int N, int K, double[] vals) {
      if (vals.length != (N*K))  throw new IllegalArgumentException(
            "LUPDecomposition: argument vals is not expected length N*K.");
      this.N = N;
      this.K = K;
      LU = new double[N*K];
      for (int i=0; i<(N*K); i++){
          LU[i] = vals[i];
      }
      doLU();
   }
   
   /** LUP Decomposition, P*A = L*U.
    *  Constructor returns a structure to access L, U and P.
    *  The dimension of the matrix is assumed to be square and
    *  given by the length of the vals array.
   @param  vals   The (N*N) matrix elements ordered by columns in an array
   @exception  IllegalArgumentException
   */
   public LUPDecomposition (double[] vals) {
       N = (int) Math.floor( Math.sqrt( (double) vals.length ) );
       K = vals.length / N;
       while (vals.length != (N*K)) {
           // A warning would be appropriate here
           N = N-1;
           K = vals.length / N;
       }
       if (N == 1)  throw new IllegalArgumentException(
            "LUPDecomposition: can not find a reasonable size of the matrix.");
       LU = new double[N*K];
       for (int i=0; i<(N*K); i++){
           LU[i] = vals[i];
       }
       doLU();
   }
   
/* ------------------------
   Private Method. Do the actual LU decomposition
 * ------------------------ */
   
  private void doLU() {
      // Algorithm as in Trefethen and Bau: Numerical Linear algebra, algorithm 21.1
      perm = new int[N];
      for (int n = 0; n < N; n++) {
         perm[n] = n;
      }
      pivsign = 1;

      // Outer loop, for each column
      for (int k = 0; k < Math.min(N,K); k++) {

          // find pivot (largest element i column and unprocessed rows)
          double maks = 0.0;
          int nmax = -1;
          for (int n=k; n<N; n++) {
              double temp = Math.abs( LU[k*N+perm[n]] );
              if (temp > maks) {
                  maks = temp;
                  nmax = n;
              }
          }
          //  if some elements is non-zero, a pivot value is found
          //  and below is LU[ k*N+perm[nmax] ] != 0.0
          if (nmax >= 0) {
              if (nmax != k) { // change two rows, by just swapping in perm
                  int ntemp = perm[k];
                  perm[k] = perm[nmax];
                  perm[nmax] = ntemp;
                  pivsign = -pivsign;
              }
              // process the rows
              for (int n=(k+1); n<N; n++){   // row n
                  double factor = LU[ k*N+perm[n] ] / LU[ k*N+perm[k] ];
                  LU[ k*N+perm[n] ] = factor;
                  // the elements to the right of the diagonal is updatede in this row
                  for (int k1 = (k+1); k1<K; k1++) {
                      LU[ k1*N+perm[n] ] -= factor*LU[ k1*N+perm[k] ];
                  }
              }
          }
      }
   }   

   // a method that just do the job of solving Ax=b, i.e. LUx=Pb
   private void solveArray(double[] b, double[] x) {
       // set x to P*b
      for (int n=0; n<N; n++) x[n] = b[perm[n]];
      // Solve L*y = P*b, i.e. set x to inv(L)*P*b, forward substitution
      for (int k = 0; k<(K-1); k++) {
         for (int n = k+1; n<N; n++) {
             x[n] -= LU[k*N+perm[n]] * x[k];   // LU[..] is L(n,k)
         }
      }
      // Solve U*x = y, , i.e. set x to inv(U)*inv(L)*P*b, backward substitution
      for (int k = N-1; k >= 0; k--) {
          x[k] /= LU[k*N+perm[k]]; // LU[..] is U(k,k)
          for (int n = 0; n<k; n++) {
              x[n] -= LU[k*N+perm[n]] * x[k];  // LU[..] is U(n,k)
          }
      }
   }

/* ------------------------
   Public Methods
 * ------------------------ */

    /** Get number of rows in the original matrix A, i.e. length of each column vector.
    *  LUP-decomposition is P*A = L*U, where A is N-by-K, P is N-by-N, 
    *  M is Math.min(N,K), then L is N-by-M and U is M-by-K
   @return     N, the number of rows in A
     */
    public int getN() { 
    	return N;
    }
    
    /** Get number of columns in the original matrix A, i.e. length of each row vector.
    *  LUP-decomposition is P*A = L*U, where A is N-by-K, P is N-by-N, 
    *  M is Math.min(N,K), then L is N-by-M and U is M-by-K
   @return     K, the number of columns in A
     */
    public int getK() { 
    	return K;
    }
    
    /** Get number of columns in L and rows in U.
    *  LUP-decomposition is P*A = L*U, where A is N-by-K, P is N-by-N, 
    *  M is Math.min(N,K), then L is N-by-M and U is M-by-K
   @return     M, the number of columns in L and rows in U
     */
    public int getM() { 
    	return Math.min(N,K);
    }
    
    /** Is the matrix nonsingular?
   @return     true if U, and hence A, is nonsingular.
     */
    public boolean isNonsingular() {
        if (N != K) return false;
        for (int k = 0; k < K; k++) {
            if ( LU[k*N+perm[k]] == 0) return false;
        }
        return true;
    }

   /** Return lower triangular factor as a BandMatrix object.
    *  LUP-decomposition is P*A = L*U, where A is N-by-K, P is N-by-N, 
    *  M is Math.min(N,K), then L is N-by-M and U is M-by-K
   @return     LMatrix
   */
   public BandMatrix getLMatrix() {
       return new BandMatrix(N, Math.min(N,K), this.getLArray());
   }
   
   /** Return lower triangular factor as a column-ordered array of length N*M.
    *  LUP-decomposition is P*A = L*U, where A is N-by-K, P is N-by-N, 
    *  M is Math.min(N,K), then L is N-by-M and U is M-by-K
   @return     Larray
   */
   public double[] getLArray() {
       int M = Math.min(N,K);
       double[] Larray = new double[N*M];
       int i = 0;   // index in Larray
       for (int k=0; k<M; k++){
           for (int n=0; n<N; n++, i++) {
               if (n < k) {
                   Larray[i] = 0.0;
               } else if (n==k) {
                   Larray[i] = 1.0;
               } else {
                   Larray[i] = LU[k*N+perm[n]];  // L(n,k)
               }
           }
       }
       return Larray;
   }

   /** Return upper triangular factor as a BandMatrix object.
    *  LUP-decomposition is P*A = L*U, where A is N-by-K, P is N-by-N, 
    *  M is Math.min(N,K), then L is N-by-M and U is M-by-K
   @return     UMatrix
   */
   public BandMatrix getUMatrix() {
       return new BandMatrix(Math.min(N,K), K, this.getUArray());
   }
   
   /** Return upper triangular factor as a column-ordered array of length M*K.
    *  LUP-decomposition is P*A = L*U, where A is N-by-K, P is N-by-N, 
    *  M is Math.min(N,K), then L is N-by-M and U is M-by-K
   @return     Uarray
   */
   public double[] getUArray() {
       int M = Math.min(N,K);
       double[] Uarray = new double[M*K];
       int i = 0;   // index in Uarray
       for (int k=0; k<K; k++){
           for (int n=0; n<M; n++, i++) {
               if (n <= k) {
                   Uarray[i] = LU[k*N+perm[n]];   // U(n,k)
               } else {
                   Uarray[i] = 0.0;
               }
           }
       }
       return Uarray;
   }

   /** Return pivot permutation vector, P(n,k)=1 when p[n]=k.
    *  LUP-decomposition is P*A = L*U, where A is N-by-K, P is N-by-N, 
    *  M is Math.min(N,K), then L is N-by-M and U is M-by-K
   @return     p
   */
   public int[] getPivot() {
      int[] p = new int[N];
      for (int n = 0; n < N; n++) {
         p[n] = perm[n];
      }
      return p;
   }

   /** Return the permutation matrix, P as a PermutationMatrix object.
    *  LUP-decomposition is P*A = L*U, where A is N-by-K, P is N-by-N, 
    *  M is Math.min(N,K), then L is N-by-M and U is M-by-K
   @return    P, the permutation matrix
   */
   public PermutationMatrix getPmatrix() {
      PermutationMatrix P = new PermutationMatrix(perm);
      return P;
   }

   /** Determinant
   @return     det(A)
   @exception  IllegalArgumentException  Matrix must be square
   */
   public double det() {
      if (N != K) throw new IllegalArgumentException(
            "LUPDecomposition: for determinat the matrix must be square.");
      double d = (double) pivsign;
      for (int n = 0; n < N; n++) {
         d *= LU[n*N+perm[n]];
      }
      return d;
   }

   /** Solve A*x = b
   @param  b   An array with as many elements as rows in A.
   @return     The values in x, x so that L*U*x = P*b
   @exception  IllegalArgumentException Matrix row dimensions must agree.
   @exception  RuntimeException  Matrix is singular.
   */

   public double[] solveArray(double[] b) {
      if (b.length != N) {
         throw new IllegalArgumentException("Length of b array must agree.");
      }
      if (!this.isNonsingular()) {
         throw new RuntimeException("Matrix is singular.");
      }
      double[] x = new double[N];
      solveArray(b, x);
      return x;
   }
   
   /** Solve A*X = B, X is returned as a SimpleMatrix object.
   @param  B   A Matrix with as many rows as A and any number of columns.
   @return     A SimpleMatrix X so that L*U*X = P*B
   @exception  IllegalArgumentException Matrix row dimensions must agree.
   @exception  RuntimeException  Matrix is singular.
   */
   public SimpleMatrix solveMatrix(AllMatrices B) {
      if (B.getRowDimension() != N) {
         throw new IllegalArgumentException("Matrix row dimensions must agree.");
      }
      if (!this.isNonsingular()) {
         throw new RuntimeException("Matrix is singular.");
      }
      int BK = B.getK();
      double[] b = new double[N];
      double[] x = new double[N];
      SimpleMatrix X = new SimpleMatrix(N, BK);
      for (int k=0; k<BK; k++) {
          B.getColumn(k,b);
          solveArray(b, x);
          X.setColumn(k,x);
      }
      return X;
   }
   
   /** Solve A*X = B, X is returned as an array.
    *  An array with the values of X is returned, but B can be any matrix,
    *  i.e. a descendant of AllMatrices.
    *  This is perhaps a little bit faster than the solveMatrix method
    *  which return a SimpleMatrix object. 
   @param  B   A Matrix with as many rows as A and any number of columns.
   @return     The values in X ordered by columns, X so that L*U*X = P*B
   @exception  IllegalArgumentException Matrix row dimensions must agree.
   @exception  RuntimeException  Matrix is singular.
   */
   public double[] solveArray(AllMatrices B) {
      if (B.getRowDimension() != N) {
         throw new IllegalArgumentException("Matrix row dimensions must agree.");
      }
      if (!this.isNonsingular()) {
         throw new RuntimeException("Matrix is singular.");
      }
      int BK = B.getK();
      double[] Xval = new double[N*BK];
      double[] kol = new double[N];
      
       // set X to P*B
      for (int n=0; n<N; n++){
          int pn = perm[n];
          for (int i=0; i<BK; i++) {
              Xval[i*N+n] = B.get(pn,i);
          }
      }
      // Solve L*Y = P*B, i.e. set X to inv(L)*P*B, forward substitution
      for (int k = 0; k<(K-1); k++) {
         for (int n = k+1; n<N; n++) {
             double Lnk = LU[k*N+perm[n]];   // LU[..] is L(n,k)
             for (int i=0; i<BK; i++) {
                 Xval[i*N+n] -= Lnk*Xval[i*N+k];
             }
         }
      }
      // Solve U*X = Y, , i.e. set X to inv(U)*inv(L)*P*B, backward substitution
      for (int k = N-1; k >= 0; k--) {
          double Ukk = LU[k*N+perm[k]];   // LU[..] is U(k,k)
          for (int i=0; i<BK; i++) {
              Xval[i*N+k] /= Ukk;
          }
          for (int n = 0; n<k; n++) {
              double Unk = LU[k*N+perm[n]];   // LU[..] is U(n,k)
              for (int i=0; i<BK; i++) {
                  Xval[i*N+n] -= Unk*Xval[i*N+k];
              }
          }
      }
      return Xval;
   }
}
