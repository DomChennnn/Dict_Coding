/*     */ package mpv2;
/*     */ 
/*     */ public class DictionaryLearning {
/*     */   private int N;
/*     */   
/*     */   private int K;
/*     */   
/*     */   private SimpleMatrix D;
/*     */   
/*     */   private SymmetricMatrix DD;
/*     */   
/*     */   private SymmetricMatrix C;
/*     */   
/*     */   private DiagonalMatrix G;
/*     */   
/*  38 */   private char lambdaMet = '1';
/*     */   
/*  40 */   private double lambdaLow = 1.0D;
/*     */   
/*  41 */   private double lambdaHigh = 1.0D;
/*     */   
/*  42 */   private double lambdaPar = 20000.0D;
/*     */   
/*     */   public static final int BMP = 1;
/*     */   
/*     */   public static final int OMP = 2;
/*     */   
/*     */   public static final int ORMP = 3;
/*     */   
/*     */   public static final int PS = 4;
/*     */   
/*     */   private MatchingPursuit mp;
/*     */   
/*  50 */   private int mpMet = 2;
/*     */   
/*  51 */   private int mpS = 2;
/*     */   
/*  52 */   private double mpRelErr = 1.0E-6D;
/*     */   
/*  53 */   private double mpAbsErr = 1.0E-6D;
/*     */   
/*  54 */   private int mpComb = 50;
/*     */   
/*  56 */   private int paramI1 = 128;
/*     */   
/*  57 */   private double minCdiag = 1.0E18D;
/*     */   
/*  58 */   private double maxCdiag = 0.0D;
/*     */   
/*  59 */   private double valueCmmr = 1000000.0D;
/*     */   
/*  60 */   private double limitCmax = 0.0D;
/*     */   
/*  61 */   private double limitCmmr = 1000000.0D;
/*     */   
/*     */   private int noTV;
/*     */   
/*     */   private boolean verbose = false;
/*     */   
/*     */   private boolean veryVerbose = false;
/*     */   
/*     */   private double[] x;
/*     */   
/*     */   private double[] w;
/*     */   
/*     */   private double[] r;
/*     */   
/*     */   private double[] u;
/*     */   
/*     */   private double[] v;
/*     */   
/*     */   private boolean loggActive = true;
/*     */   
/*  75 */   private double sumxx = 0.0D;
/*     */   
/*  76 */   private double sumww = 0.0D;
/*     */   
/*  77 */   private double sumrr = 0.0D;
/*     */   
/*     */   private double[] sumAllxxTab;
/*     */   
/*     */   private double[] sumAllwwTab;
/*     */   
/*     */   private double[] sumAllrrTab;
/*     */   
/*     */   private double[] snrTab;
/*     */   
/*     */   private double[] sumxxTab;
/*     */   
/*     */   private double[] sumwwTab;
/*     */   
/*     */   private double[] sumrrTab;
/*     */   
/*  85 */   private int nzc = 0;
/*     */   
/*     */   private int[] indexW;
/*     */   
/*     */   private double[] valueW;
/*     */   
/*     */   public DictionaryLearning(AllMatrices paramAllMatrices, boolean paramBoolean, int paramInt) {
/* 112 */     this.N = paramAllMatrices.getN();
/* 113 */     this.K = paramAllMatrices.getK();
/* 114 */     this.noTV = this.K;
/* 115 */     this.D = new SimpleMatrix(paramAllMatrices);
/* 116 */     this.G = new DiagonalMatrix(this.K, 1.0D);
/* 117 */     this.C = new SymmetricMatrix(this.K, this.K);
/* 118 */     this.x = new double[this.N];
/* 119 */     this.w = new double[this.K];
/* 120 */     this.r = new double[this.N];
/* 121 */     this.u = new double[this.K];
/* 122 */     this.v = new double[this.K];
/* 123 */     setVerbose(paramInt);
/* 125 */     for (byte b = 0; b < this.K; b++) {
/* 126 */       double d = this.D.innerProduct(b, b);
/* 127 */       if (d > 0.0D) {
/* 128 */         d = 1.0D / d;
/* 129 */         if (d > this.maxCdiag)
/* 129 */           this.maxCdiag = d; 
/* 130 */         if (d < this.minCdiag)
/* 130 */           this.minCdiag = d; 
/* 131 */         this.C.set(b, b, d);
/* 132 */         this.G.set(b, Math.sqrt(d));
/*     */       } else {
/* 134 */         System.out.println("Warning: 2-norm of dictionary vector " + b + " is less or equal to zero.");
/* 136 */         this.C.set(b, b, 1.0D);
/* 137 */         this.G.set(b, 1.0D);
/*     */       } 
/* 139 */       this.valueCmmr = this.maxCdiag / this.minCdiag;
/* 140 */       this.limitCmax = 4.0D * this.maxCdiag;
/*     */     } 
/* 142 */     this.D.timeseqScaleColumns(this.G);
/* 144 */     if (paramBoolean) {
/* 145 */       this.DD = SymmetricMatrix.innerProductMatrix(this.D);
/* 146 */       this.mp = new MatchingPursuit(this.D, this.DD);
/* 147 */       if (this.verbose)
/* 147 */         System.out.println("A DictionaryLearning object of size " + this.N + "-by-" + this.K + " is made and DD is used."); 
/*     */     } else {
/* 150 */       this.DD = null;
/* 151 */       this.mp = new MatchingPursuit(this.D);
/* 152 */       if (this.verbose)
/* 152 */         System.out.println("A DictionaryLearning object of size " + this.N + "-by-" + this.K + " is made without using DD."); 
/*     */     } 
/* 155 */     this.mp.setNormalized();
/*     */   }
/*     */   
/*     */   public DictionaryLearning(AllMatrices paramAllMatrices, boolean paramBoolean) {
/* 161 */     this(paramAllMatrices, paramBoolean, 0);
/*     */   }
/*     */   
/*     */   public SimpleMatrix getDictionary() {
/* 168 */     return this.D;
/*     */   }
/*     */   
/*     */   public SymmetricMatrix getInnerProductMatrix() {
/* 169 */     return this.DD;
/*     */   }
/*     */   
/*     */   public SymmetricMatrix getTheCMatrix() {
/* 170 */     return this.C;
/*     */   }
/*     */   
/*     */   public double[] getWeights() {
/* 171 */     return this.w;
/*     */   }
/*     */   
/*     */   public double[] getResidual() {
/* 172 */     return this.r;
/*     */   }
/*     */   
/*     */   public double[] getx() {
/* 173 */     return this.x;
/*     */   }
/*     */   
/*     */   public double[] getw() {
/* 174 */     return this.w;
/*     */   }
/*     */   
/*     */   public double[] getr() {
/* 175 */     return this.r;
/*     */   }
/*     */   
/*     */   public double[] getu() {
/* 176 */     return this.u;
/*     */   }
/*     */   
/*     */   public double[] getv() {
/* 177 */     return this.v;
/*     */   }
/*     */   
/*     */   public int getNoTV() {
/* 178 */     return this.noTV;
/*     */   }
/*     */   
/*     */   public int getMPMet() {
/* 179 */     return this.mpMet;
/*     */   }
/*     */   
/*     */   public int getParamI1() {
/* 181 */     return this.paramI1;
/*     */   }
/*     */   
/*     */   public double getLimitCmmr() {
/* 182 */     return this.limitCmmr;
/*     */   }
/*     */   
/*     */   public double getValueCmmr() {
/* 183 */     return this.valueCmmr;
/*     */   }
/*     */   
/*     */   public double getLimitCmax() {
/* 184 */     return this.limitCmax;
/*     */   }
/*     */   
/*     */   public double getMinCdiag() {
/* 185 */     return this.minCdiag;
/*     */   }
/*     */   
/*     */   public double getMaxCdiag() {
/* 186 */     return this.maxCdiag;
/*     */   }
/*     */   
/*     */   public double getSumxx() {
/* 188 */     return this.sumxx;
/*     */   }
/*     */   
/*     */   public double getSumww() {
/* 189 */     return this.sumww;
/*     */   }
/*     */   
/*     */   public double getSumrr() {
/* 190 */     return this.sumrr;
/*     */   }
/*     */   
/*     */   public double[] getSumAllxxTab() {
/* 191 */     return this.sumAllxxTab;
/*     */   }
/*     */   
/*     */   public double[] getSumAllwwTab() {
/* 192 */     return this.sumAllwwTab;
/*     */   }
/*     */   
/*     */   public double[] getSumAllrrTab() {
/* 193 */     return this.sumAllrrTab;
/*     */   }
/*     */   
/*     */   public double[] getSnrTab() {
/* 194 */     return this.snrTab;
/*     */   }
/*     */   
/*     */   public double[] getSumxxTab() {
/* 195 */     return this.sumxxTab;
/*     */   }
/*     */   
/*     */   public double[] getSumwwTab() {
/* 196 */     return this.sumwwTab;
/*     */   }
/*     */   
/*     */   public double[] getSumrrTab() {
/* 197 */     return this.sumrrTab;
/*     */   }
/*     */   
/*     */   public int[] getIndexW() {
/* 199 */     int[] arrayOfInt = new int[this.nzc];
/* 200 */     for (byte b = 0; b < this.nzc; ) {
/* 200 */       arrayOfInt[b] = this.indexW[b];
/* 200 */       b++;
/*     */     } 
/* 201 */     return arrayOfInt;
/*     */   }
/*     */   
/*     */   public double[] getValueW() {
/* 204 */     double[] arrayOfDouble = new double[this.nzc];
/* 205 */     for (byte b = 0; b < this.nzc; ) {
/* 205 */       arrayOfDouble[b] = this.valueW[b];
/* 205 */       b++;
/*     */     } 
/* 206 */     return arrayOfDouble;
/*     */   }
/*     */   
/*     */   private double increasingFun(double paramDouble1, char paramChar, double paramDouble2, double paramDouble3) {
/* 216 */     switch (paramChar) {
/*     */       case '1':
/* 218 */         return 1.0D;
/*     */       case 'L':
/* 220 */         if (paramDouble1 < 0.0D)
/* 221 */           return paramDouble2; 
/* 222 */         if (paramDouble1 <= 1.0D)
/* 223 */           return paramDouble2 + (paramDouble3 - paramDouble2) * paramDouble1; 
/* 225 */         return paramDouble3;
/*     */       case 'Q':
/* 228 */         if (paramDouble1 < 0.0D)
/* 229 */           return paramDouble2; 
/* 230 */         if (paramDouble1 <= 1.0D) {
/* 231 */           paramDouble1 = 1.0D - paramDouble1;
/* 232 */           return paramDouble3 - (paramDouble3 - paramDouble2) * paramDouble1 * paramDouble1;
/*     */         } 
/* 234 */         return paramDouble3;
/*     */       case 'C':
/* 237 */         if (paramDouble1 < 0.0D)
/* 238 */           return paramDouble2; 
/* 239 */         if (paramDouble1 <= 1.0D) {
/* 240 */           paramDouble1 = 1.0D - paramDouble1;
/* 241 */           return paramDouble3 - (paramDouble3 - paramDouble2) * paramDouble1 * paramDouble1 * paramDouble1;
/*     */         } 
/* 243 */         return paramDouble3;
/*     */       case 'H':
/* 246 */         if (paramDouble1 < 0.0D)
/* 247 */           return paramDouble2; 
/* 249 */         return paramDouble3 - (paramDouble3 - paramDouble2) / (paramDouble1 + 1.0D);
/*     */       case 'E':
/* 252 */         if (paramDouble1 < 0.0D)
/* 253 */           return paramDouble2; 
/* 255 */         return paramDouble3 - (paramDouble3 - paramDouble2) * Math.pow(0.5D, paramDouble1);
/*     */       case 'S':
/* 258 */         if (paramDouble1 < 0.0D)
/* 259 */           return paramDouble2; 
/* 260 */         if (paramDouble1 <= 1.0D)
/* 261 */           return paramDouble2 + (paramDouble3 - paramDouble2) * paramDouble1 * paramDouble1; 
/* 263 */         return paramDouble3;
/*     */       case 'T':
/* 266 */         if (paramDouble1 < 0.0D)
/* 267 */           return paramDouble2; 
/* 268 */         if (paramDouble1 <= 1.0D)
/* 269 */           return paramDouble2 + (paramDouble3 - paramDouble2) * paramDouble1 * paramDouble1 * paramDouble1; 
/* 271 */         return paramDouble3;
/*     */     } 
/* 274 */     System.out.println("Illegal Met should never happen.");
/* 277 */     return paramDouble3;
/*     */   }
/*     */   
/*     */   public double getLambda() {
/* 286 */     return increasingFun(this.noTV / this.lambdaPar, this.lambdaMet, this.lambdaLow, this.lambdaHigh);
/*     */   }
/*     */   
/*     */   public double getLambda(double paramDouble) {
/* 294 */     return increasingFun(paramDouble, this.lambdaMet, this.lambdaLow, this.lambdaHigh);
/*     */   }
/*     */   
/*     */   public void setLambda(char paramChar, double paramDouble1, double paramDouble2, double paramDouble3) {
/* 314 */     switch (paramChar) {
/*     */       case '1':
/* 316 */         this.lambdaMet = '1';
/* 317 */         this.lambdaLow = 1.0D;
/* 318 */         this.lambdaHigh = 1.0D;
/* 319 */         this.lambdaPar = 1.0D;
/* 320 */         if (this.verbose)
/* 320 */           System.out.println("lambda is set to the constant 1.0."); 
/*     */         return;
/*     */       case 'L':
/*     */       case 'l':
/* 324 */         this.lambdaMet = 'L';
/*     */         break;
/*     */       case 'Q':
/*     */       case 'q':
/* 328 */         this.lambdaMet = 'Q';
/*     */         break;
/*     */       case 'C':
/*     */       case 'c':
/* 332 */         this.lambdaMet = 'C';
/*     */         break;
/*     */       case 'S':
/*     */       case 's':
/* 336 */         this.lambdaMet = 'S';
/*     */         break;
/*     */       case 'T':
/*     */       case 't':
/* 340 */         this.lambdaMet = 'T';
/*     */         break;
/*     */       case 'H':
/*     */       case 'h':
/* 344 */         this.lambdaMet = 'H';
/*     */         break;
/*     */       case 'E':
/*     */       case 'e':
/* 348 */         this.lambdaMet = 'E';
/*     */         break;
/*     */       default:
/* 351 */         throw new IllegalArgumentException("The char for the method to update lambda is not a legal value.");
/*     */     } 
/* 355 */     if (paramDouble1 < 0.5D || paramDouble1 > 1.0D)
/* 355 */       throw new IllegalArgumentException("lamLow not within legal (resonable) range: 0.5 <= lamLow <= 1.0."); 
/* 357 */     this.lambdaLow = paramDouble1;
/* 358 */     if (paramDouble2 < this.lambdaLow || paramDouble2 > 1.0D)
/* 358 */       throw new IllegalArgumentException("lamHigh not within legal (resonable) range: lambdaLow <= lamHigh <= 1.0."); 
/* 360 */     this.lambdaHigh = paramDouble2;
/* 361 */     if (paramDouble3 < this.K)
/* 361 */       throw new IllegalArgumentException("lamPar is too small, the value is normally several thousands."); 
/* 363 */     this.lambdaPar = paramDouble3;
/* 365 */     if (this.verbose)
/* 366 */       System.out.println("The update method for lambda is set to: " + this.lambdaMet + " from " + this.lambdaLow + " to " + this.lambdaHigh + " and parameter " + this.lambdaPar + "."); 
/*     */   }
/*     */   
/*     */   public void setBMP() {
/* 375 */     this.mpMet = 1;
/*     */   }
/*     */   
/*     */   public void setOMP() {
/* 376 */     this.mpMet = 2;
/*     */   }
/*     */   
/*     */   public void setORMP() {
/* 377 */     this.mpMet = 3;
/*     */   }
/*     */   
/*     */   public void setPS() {
/* 378 */     this.mpMet = 4;
/*     */   }
/*     */   
/*     */   public void setBMP(int paramInt) {
/* 385 */     setMParg(paramInt, 1.0E-6D, 1.0E-6D);
/* 386 */     this.mpMet = 1;
/*     */   }
/*     */   
/*     */   public void setOMP(int paramInt, double paramDouble1, double paramDouble2) {
/* 396 */     setMParg(paramInt, paramDouble1, paramDouble2);
/* 397 */     this.mpMet = 2;
/*     */   }
/*     */   
/*     */   public void setORMP(int paramInt, double paramDouble1, double paramDouble2) {
/* 407 */     setMParg(paramInt, paramDouble1, paramDouble2);
/* 408 */     this.mpMet = 3;
/*     */   }
/*     */   
/*     */   public void setPS(int paramInt1, double paramDouble1, double paramDouble2, int paramInt2) {
/* 419 */     setMParg(paramInt1, paramDouble1, paramDouble2);
/* 420 */     this.mpComb = paramInt2;
/* 421 */     this.mpMet = 4;
/*     */   }
/*     */   
/*     */   private void setMParg(int paramInt, double paramDouble1, double paramDouble2) {
/* 425 */     this.mpS = paramInt;
/* 427 */     if (this.mpS < 1) {
/* 428 */       System.out.println("Number of non-zeros is smaller than one. Set it to 1.");
/* 429 */       this.mpS = 1;
/*     */     } 
/* 431 */     if (this.mpS >= this.N) {
/* 432 */       System.out.println("Number of non-zeros is too large. Set it to 1.");
/* 433 */       this.mpS = 1;
/*     */     } 
/* 435 */     this.mpRelErr = paramDouble1;
/* 436 */     this.mpAbsErr = paramDouble2;
/*     */   }
/*     */   
/*     */   public void setLoggOn() {
/* 440 */     this.loggActive = true;
/*     */   }
/*     */   
/*     */   public void setLoggOff() {
/* 441 */     this.loggActive = false;
/*     */   }
/*     */   
/*     */   public void setParamI1(int paramInt) {
/* 442 */     this.paramI1 = paramInt;
/*     */   }
/*     */   
/*     */   public void setLimitCmmr(double paramDouble) {
/* 443 */     this.limitCmmr = paramDouble;
/*     */   }
/*     */   
/*     */   public void setLimitCmax(double paramDouble) {
/* 444 */     this.limitCmax = paramDouble;
/*     */   }
/*     */   
/*     */   public void setVerbose(int paramInt) {
/* 452 */     if (paramInt < 0)
/* 452 */       throw new IllegalArgumentException("Verbose level should be given as a non-zero positive integer."); 
/* 454 */     if (paramInt == 0) {
/* 455 */       this.verbose = false;
/* 456 */       this.veryVerbose = false;
/* 457 */     } else if (paramInt == 1) {
/* 458 */       this.verbose = true;
/* 459 */       this.veryVerbose = false;
/*     */     } else {
/* 461 */       this.verbose = true;
/* 462 */       this.veryVerbose = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void verbosePrintln(int paramInt) {
/* 471 */     System.out.println("Iteration " + (paramInt + 1) + " : " + "noTV = " + this.noTV + ", lambdaPar = " + this.lambdaPar + ", lambda = " + getLambda(this.noTV / this.lambdaPar) + ", SNR = " + this.snrTab[paramInt]);
/* 478 */     if (this.veryVerbose) {
/* 479 */       doNormalizationSlow();
/* 480 */       System.out.println("Iteration " + (paramInt + 1) + " : " + "normF of C is " + this.C.normF() + " and trace of C is " + this.C.trace() + ".");
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doNormalizationFast() {
/* 489 */     double d = 0.0D;
/* 490 */     for (byte b = 0; b < this.K; b++) {
/* 491 */       double d1 = this.D.innerProduct(b, b);
/* 492 */       if (d1 > 0.0D) {
/* 493 */         this.G.set(b, 1.0D / Math.sqrt(d1));
/* 494 */         d += Math.abs(1.0D - d1);
/*     */       } else {
/* 496 */         this.G.set(b, 1.0D);
/*     */       } 
/*     */     } 
/* 499 */     if (d > 0.1D) {
/* 500 */       this.D.timeseqScaleColumns(this.G);
/* 501 */       this.mp.setNormalized();
/* 502 */       this.C.eqScaleRowsAndColumns(this.G, this.C);
/* 503 */       if (this.DD != null)
/* 503 */         this.DD.eqScaleRowsAndColumns(this.G, this.DD); 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doNormalizationSlow() {
/* 509 */     for (byte b = 0; b < this.K; b++) {
/* 510 */       double d = this.D.innerProduct(b, b);
/* 511 */       if (d > 0.0D) {
/* 512 */         this.G.set(b, 1.0D / Math.sqrt(d));
/*     */       } else {
/* 514 */         this.G.set(b, 1.0D);
/*     */       } 
/*     */     } 
/* 517 */     this.D.timeseqScaleColumns(this.G);
/* 518 */     this.mp.setNormalized();
/* 519 */     this.C.eqScaleRowsAndColumns(this.G, this.C);
/* 520 */     if (this.DD != null)
/* 520 */       this.DD.eqTProduct(this.D, this.D); 
/*     */   }
/*     */   
/*     */   public void rlsdla(double[] paramArrayOfdouble, int paramInt) {
/* 535 */     if (paramArrayOfdouble.length < this.N)
/* 535 */       throw new IllegalArgumentException("The supplied data is too short."); 
/* 537 */     int i = paramArrayOfdouble.length / this.N;
/* 540 */     double d1 = 0.0D;
/* 541 */     double d2 = 0.0D;
/* 542 */     double d3 = 0.0D;
/* 543 */     if (this.loggActive) {
/* 545 */       this.sumAllxxTab = new double[paramInt];
/* 546 */       this.sumAllwwTab = new double[paramInt];
/* 547 */       this.sumAllrrTab = new double[paramInt];
/* 548 */       this.snrTab = new double[paramInt];
/* 550 */       this.sumxxTab = new double[i];
/* 551 */       this.sumwwTab = new double[i];
/* 552 */       this.sumrrTab = new double[i];
/*     */     } 
/* 555 */     this.indexW = new int[i * this.mpS];
/* 556 */     this.valueW = new double[i * this.mpS];
/* 557 */     this.nzc = 0;
/*     */     byte b;
/* 559 */     for (b = 0; b < paramInt; b++) {
/* 560 */       if (this.loggActive) {
/* 561 */         d1 = 0.0D;
/* 562 */         d2 = 0.0D;
/* 563 */         d3 = 0.0D;
/*     */       } 
/* 565 */       for (byte b1 = 0; b1 < i; b1++) {
/*     */         byte b2;
/* 566 */         for (b2 = 0; b2 < this.N; b2++)
/* 567 */           this.x[b2] = paramArrayOfdouble[b1 * this.N + b2]; 
/* 569 */         if (Double.isNaN(this.D.get(0, 0))) {
/* 570 */           if (this.verbose)
/* 570 */             System.out.println("Error in rlsdla, noTV = " + this.noTV + ", dictionary is NaN, return! "); 
/*     */           return;
/*     */         } 
/* 574 */         rlsdla1(this.x);
/* 575 */         if (this.loggActive) {
/* 576 */           d1 += this.sumxx;
/* 577 */           d2 += this.sumww;
/* 578 */           d3 += this.sumrr;
/* 579 */           if (b == paramInt - 1) {
/* 580 */             this.sumxxTab[b1] = this.sumxx;
/* 581 */             this.sumwwTab[b1] = this.sumww;
/* 582 */             this.sumrrTab[b1] = this.sumrr;
/* 583 */             for (b2 = 0; b2 < this.K; ) {
/* 583 */               if (this.w[b2] != 0.0D) {
/* 584 */                 this.indexW[this.nzc] = b1 * this.K + b2;
/* 585 */                 this.valueW[this.nzc] = this.w[b2];
/* 586 */                 this.nzc++;
/*     */               } 
/*     */               b2++;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 591 */       if (this.loggActive) {
/* 592 */         this.sumAllxxTab[b] = d1;
/* 593 */         this.sumAllwwTab[b] = d2;
/* 594 */         this.sumAllrrTab[b] = d3;
/* 595 */         this.snrTab[b] = 10.0D * Math.log10(d1 / d3);
/* 596 */         if (this.verbose)
/* 596 */           verbosePrintln(b); 
/*     */       } 
/*     */     } 
/* 599 */     doNormalizationSlow();
/* 600 */     if (this.loggActive)
/* 601 */       for (b = 0; b < this.nzc; b++) {
/* 602 */         int j = this.indexW[b] % this.K;
/* 603 */         this.valueW[b] = this.valueW[b] / this.G.get(j);
/*     */       }  
/*     */   }
/*     */   
/*     */   public void rlsdla1(double[] paramArrayOfdouble) {
/* 620 */     this.noTV++;
/* 622 */     this.sumxx = 0.0D;
/*     */     int i;
/* 623 */     for (i = 0; i < this.N; ) {
/* 623 */       this.sumxx += paramArrayOfdouble[i] * paramArrayOfdouble[i];
/* 623 */       i++;
/*     */     } 
/* 625 */     i = 0;
/* 626 */     switch (this.mpMet) {
/*     */       case 1:
/* 628 */         this.mp.vsBMP(paramArrayOfdouble, this.w, this.mpS);
/*     */         break;
/*     */       case 2:
/* 631 */         i = this.mp.vsOMPorORMP2(paramArrayOfdouble, this.w, this.mpS, Math.max(this.mpAbsErr / Math.sqrt(this.sumxx), this.mpRelErr), true);
/*     */         break;
/*     */       case 3:
/* 634 */         i = this.mp.vsOMPorORMP2(paramArrayOfdouble, this.w, this.mpS, Math.max(this.mpAbsErr / Math.sqrt(this.sumxx), this.mpRelErr), false);
/*     */         break;
/*     */       case 4:
/* 637 */         this.mp.vsPS(paramArrayOfdouble, this.w, this.mpS, Math.max(this.mpAbsErr / Math.sqrt(this.sumxx), this.mpRelErr), this.mpComb);
/*     */         break;
/*     */       default:
/* 640 */         System.out.println("Illegal mpMet should never happen.");
/*     */         break;
/*     */     } 
/* 643 */     if (i < 0) {
/* 644 */       if (this.veryVerbose)
/* 644 */         System.out.println("Error in OMP/ORMP, noTV = " + this.noTV + ", ignore this training vector. "); 
/*     */       return;
/*     */     } 
/* 650 */     if (this.loggActive) {
/* 651 */       this.sumww = 0.0D;
/* 652 */       for (byte b = 0; b < this.K; ) {
/* 652 */         if (this.w[b] != 0.0D)
/* 652 */           this.sumww += this.w[b] * this.w[b]; 
/* 652 */         b++;
/*     */       } 
/*     */     } 
/* 655 */     this.sumrr = 0.0D;
/* 656 */     this.D.times(this.w, this.r);
/*     */     byte b1;
/* 657 */     for (b1 = 0; b1 < this.N; b1++) {
/* 658 */       this.r[b1] = paramArrayOfdouble[b1] - this.r[b1];
/* 659 */       this.sumrr += this.r[b1] * this.r[b1];
/*     */     } 
/* 662 */     b1 = 0;
/* 663 */     if (this.lambdaMet != '1') {
/* 666 */       double d1 = getLambda(this.noTV / this.lambdaPar);
/* 668 */       if (d1 < 0.994D) {
/* 669 */         if (checkC()) {
/* 670 */           this.C.eqProduct(this.C, 1.0D / d1);
/* 671 */           b1 = (this.valueCmmr > this.limitCmmr) ? 1 : 0;
/*     */         } 
/* 673 */       } else if (d1 < 0.9993D) {
/* 674 */         if (this.noTV % 16 == 0 && 
/* 675 */           checkC()) {
/* 676 */           this.C.eqProduct(this.C, 17.0D - 16.0D * d1);
/* 677 */           b1 = (this.valueCmmr > this.limitCmmr) ? 1 : 0;
/*     */         } 
/* 680 */       } else if (d1 < 0.99991D) {
/* 681 */         if (this.noTV % 128 == 0 && 
/* 682 */           checkC()) {
/* 683 */           this.C.eqProduct(this.C, 129.0D - 128.0D * d1);
/* 684 */           b1 = (this.valueCmmr > this.limitCmmr) ? 1 : 0;
/*     */         } 
/* 687 */       } else if (d1 < 1.0D && 
/* 688 */         this.noTV % 1024 == 0 && 
/* 689 */         checkC()) {
/* 690 */         this.C.eqProduct(this.C, 1025.0D - 1024.0D * d1);
/* 691 */         b1 = (this.valueCmmr > this.limitCmmr) ? 1 : 0;
/*     */       } 
/*     */     } 
/* 697 */     this.C.times(this.w, this.u);
/* 699 */     double d = 1.0D;
/* 700 */     for (byte b2 = 0; b2 < this.K; b2++) {
/* 701 */       if (this.w[b2] != 0.0D)
/* 701 */         d += this.w[b2] * this.u[b2]; 
/*     */     } 
/* 703 */     d = 1.0D / d;
/* 705 */     if (this.DD != null)
/* 706 */       this.D.transposeTimes(this.r, this.v); 
/* 708 */     this.D.pluseqOuterProduct(1.0D, d, this.r, this.u);
/* 709 */     this.mp.clearNormalized();
/* 711 */     if (this.DD != null) {
/* 712 */       this.DD.pluseqOuterProduct(1.0D, d, this.u, this.v);
/* 713 */       this.DD.pluseqOuterProduct(1.0D, d * d * this.sumrr, this.u);
/*     */     } 
/* 720 */     this.C.pluseqOuterProduct(1.0D, -d, this.u);
/* 723 */     if (b1 != 0) {
/* 724 */       double d1 = this.C.get(0, 0);
/* 725 */       this.maxCdiag = d1;
/* 726 */       this.minCdiag = d1;
/* 727 */       byte b3 = 0;
/* 728 */       byte b4 = 0;
/*     */       byte b5;
/* 729 */       for (b5 = 1; b5 < this.K; b5++) {
/* 730 */         d1 = this.C.get(b5, b5);
/* 731 */         if (d1 > this.maxCdiag) {
/* 731 */           this.maxCdiag = d1;
/* 731 */           b3 = b5;
/*     */         } 
/* 732 */         if (d1 < this.minCdiag) {
/* 732 */           this.minCdiag = d1;
/* 732 */           b4 = b5;
/*     */         } 
/*     */       } 
/* 737 */       this.D.getColumn(b3, paramArrayOfdouble);
/* 738 */       this.D.getColumn(b4, this.r);
/* 739 */       for (b5 = 0; b5 < this.N; ) {
/* 739 */         paramArrayOfdouble[b5] = 0.9D * paramArrayOfdouble[b5] + 0.11D * this.r[b5];
/* 739 */         b5++;
/*     */       } 
/* 740 */       this.D.setColumn(b3, paramArrayOfdouble);
/* 742 */       doNormalizationSlow();
/* 743 */     } else if (this.noTV % 32768 == 0) {
/* 744 */       doNormalizationSlow();
/* 745 */     } else if (this.noTV % this.paramI1 == 0) {
/* 746 */       doNormalizationFast();
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean checkC() {
/* 755 */     double d = this.C.get(0, 0);
/* 756 */     this.maxCdiag = d;
/* 757 */     this.minCdiag = d;
/*     */     byte b;
/* 758 */     for (b = 1; b < this.K; b++) {
/* 759 */       d = this.C.get(b, b);
/* 760 */       if (d > this.maxCdiag)
/* 760 */         this.maxCdiag = d; 
/* 761 */       if (d < this.minCdiag)
/* 761 */         this.minCdiag = d; 
/*     */     } 
/* 764 */     if (this.minCdiag <= 0.0D) {
/* 765 */       this.minCdiag = this.maxCdiag / this.valueCmmr;
/* 766 */       for (b = 0; b < this.K; b++) {
/* 767 */         if (this.C.get(b, b) <= 0.0D)
/* 767 */           this.C.set(b, b, this.minCdiag); 
/* 768 */         if (this.veryVerbose)
/* 768 */           System.out.println("checkC: let element  " + b + " in diagonal of C matrix be " + this.minCdiag); 
/*     */       } 
/*     */     } 
/* 773 */     this.valueCmmr = this.maxCdiag / this.minCdiag;
/* 774 */     return (this.maxCdiag < this.limitCmax);
/*     */   }
/*     */   
/*     */   public void ilsdla(double[] paramArrayOfdouble, int paramInt) {
/* 789 */     setLoggOn();
/* 790 */     if (paramArrayOfdouble.length < this.N)
/* 790 */       throw new IllegalArgumentException("The supplied data is too short."); 
/* 792 */     int i = paramArrayOfdouble.length / this.N;
/* 794 */     SimpleMatrix simpleMatrix = new SimpleMatrix(this.N, this.K);
/* 795 */     SymmetricMatrix symmetricMatrix = new SymmetricMatrix(this.K, this.K);
/* 799 */     this.sumAllxxTab = new double[paramInt];
/* 800 */     this.sumAllwwTab = new double[paramInt];
/* 801 */     this.sumAllrrTab = new double[paramInt];
/* 802 */     this.snrTab = new double[paramInt];
/* 804 */     this.sumxxTab = new double[i];
/* 805 */     this.sumwwTab = new double[i];
/* 806 */     this.sumrrTab = new double[i];
/* 808 */     this.indexW = new int[i * this.mpS];
/* 809 */     this.valueW = new double[i * this.mpS];
/* 810 */     double d = 0.0D;
/* 812 */     for (byte b = 0; b < paramInt; b++) {
/* 813 */       double d1 = 0.0D;
/* 814 */       double d2 = 0.0D;
/* 815 */       int j = 0;
/* 816 */       this.nzc = 0;
/* 817 */       simpleMatrix.eqZeros();
/* 818 */       symmetricMatrix.eqZeros();
/*     */       byte b1;
/* 820 */       for (b1 = 0; b1 < i; b1++) {
/* 823 */         if (b == 0) {
/* 824 */           this.sumxx = 0.0D;
/* 825 */           for (byte b4 = 0; b4 < this.N; b4++) {
/* 826 */             this.x[b4] = paramArrayOfdouble[b1 * this.N + b4];
/* 827 */             this.sumxx += this.x[b4] * this.x[b4];
/*     */           } 
/* 829 */           this.sumxxTab[b1] = this.sumxx;
/* 830 */           d += this.sumxx;
/*     */         } else {
/* 832 */           for (byte b4 = 0; b4 < this.N; ) {
/* 832 */             this.x[b4] = paramArrayOfdouble[b1 * this.N + b4];
/* 832 */             b4++;
/*     */           } 
/* 833 */           this.sumxx = this.sumxxTab[b1];
/*     */         } 
/* 837 */         switch (this.mpMet) {
/*     */           case 1:
/* 839 */             this.mp.vsBMP(this.x, this.w, this.mpS);
/*     */             break;
/*     */           case 2:
/* 842 */             this.mp.vsOMPorORMP(this.x, this.w, this.mpS, Math.max(this.mpAbsErr / Math.sqrt(this.sumxx), this.mpRelErr), true);
/*     */             break;
/*     */           case 3:
/* 845 */             this.mp.vsOMPorORMP(this.x, this.w, this.mpS, Math.max(this.mpAbsErr / Math.sqrt(this.sumxx), this.mpRelErr), false);
/*     */             break;
/*     */           case 4:
/* 848 */             this.mp.vsPS(this.x, this.w, this.mpS, Math.max(this.mpAbsErr / Math.sqrt(this.sumxx), this.mpRelErr), this.mpComb);
/*     */             break;
/*     */           default:
/* 851 */             System.out.println("Illegal mpMet should never happen.");
/*     */             break;
/*     */         } 
/* 854 */         simpleMatrix.pluseqOuterProduct(1.0D, 1.0D, this.x, this.w);
/* 857 */         int[] arrayOfInt = new int[this.mpS];
/* 858 */         byte b2 = 0;
/*     */         byte b3;
/* 859 */         for (b3 = 0; b3 < this.K; ) {
/* 859 */           if (this.w[b3] != 0.0D)
/* 859 */             arrayOfInt[b2++] = b3; 
/* 859 */           b3++;
/*     */         } 
/* 860 */         j += b2;
/* 861 */         for (b3 = 0; b3 < b2; b3++) {
/* 862 */           int k = arrayOfInt[b3];
/* 863 */           for (byte b4 = 0; b4 <= b3; b4++) {
/* 864 */             int m = arrayOfInt[b4];
/* 865 */             double d3 = symmetricMatrix.get(k, m) + this.w[k] * this.w[m];
/* 866 */             symmetricMatrix.set(k, m, d3);
/*     */           } 
/*     */         } 
/* 873 */         if (this.loggActive) {
/* 875 */           this.sumww = 0.0D;
/* 876 */           for (b3 = 0; b3 < this.K; ) {
/* 876 */             if (this.w[b3] != 0.0D)
/* 876 */               this.sumww += this.w[b3] * this.w[b3]; 
/* 876 */             b3++;
/*     */           } 
/* 877 */           d1 += this.sumww;
/* 878 */           this.sumrr = 0.0D;
/* 879 */           this.D.times(this.w, this.r);
/* 880 */           for (b3 = 0; b3 < this.N; b3++) {
/* 881 */             this.r[b3] = this.x[b3] - this.r[b3];
/* 882 */             this.sumrr += this.r[b3] * this.r[b3];
/*     */           } 
/* 884 */           d2 += this.sumrr;
/* 885 */           this.sumwwTab[b1] = this.sumww;
/* 886 */           this.sumrrTab[b1] = this.sumrr;
/* 887 */           for (b3 = 0; b3 < this.K; ) {
/* 887 */             if (this.w[b3] != 0.0D) {
/* 888 */               this.indexW[this.nzc] = b1 * this.K + b3;
/* 889 */               this.valueW[this.nzc] = this.w[b3];
/* 890 */               this.nzc++;
/*     */             } 
/*     */             b3++;
/*     */           } 
/*     */         } 
/*     */       } 
/* 894 */       this.noTV += i;
/* 896 */       this.C.eqInverse(symmetricMatrix);
/* 897 */       this.D.eqProduct(simpleMatrix, this.C);
/* 898 */       doNormalizationSlow();
/* 900 */       if (b == paramInt - 1)
/* 901 */         for (b1 = 0; b1 < this.nzc; b1++) {
/* 902 */           int k = this.indexW[b1] % this.K;
/* 903 */           this.valueW[b1] = this.valueW[b1] / this.G.get(k);
/*     */         }  
/* 906 */       if (this.loggActive) {
/* 907 */         this.sumAllxxTab[b] = d;
/* 908 */         this.sumAllwwTab[b] = d1;
/* 916 */         this.sumAllrrTab[b] = d2;
/* 917 */         this.snrTab[b] = 10.0D * Math.log10(d / d2);
/* 918 */         if (this.verbose) {
/* 919 */           System.out.println("Iteration " + (b + 1) + " : noTV = " + this.noTV + ", non-zeros in W = " + j + ", SNR = " + this.snrTab[b]);
/* 921 */           if (this.veryVerbose) {
/* 922 */             System.out.println("Iteration " + (b + 1) + " : " + "normF of C is " + this.C.normF() + " and trace of C is " + this.C.trace() + ".");
/* 925 */             System.out.println("and  sumSignalSquared = " + d + "  sumWeightSquared = " + d1 + "  sumErrorSquared = " + d2);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /Users/liuzenglu/codebase/Dict_Coding/javaclasses/!/mpv2/DictionaryLearning.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */