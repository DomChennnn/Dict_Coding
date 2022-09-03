/*     */ package mpv2;
/*     */ 
/*     */ public class MatchingPursuit {
/*     */   private static final int MAXS = 100;
/*     */   
/*     */   private int N;
/*     */   
/*     */   private int K;
/*     */   
/*     */   private AllMatrices dict;
/*     */   
/*     */   private SymmetricMatrix ipMat;
/*     */   
/*     */   private boolean normalized;
/*     */   
/*  78 */   private int maxS1 = 0;
/*     */   
/*  79 */   private int maxS2 = 0;
/*     */   
/*     */   private double[][] r;
/*     */   
/*     */   private int[] T;
/*     */   
/*     */   private int[] J;
/*     */   
/*     */   private double[] d;
/*     */   
/*     */   private double[] e;
/*     */   
/*     */   private double[] u;
/*     */   
/*     */   private double[] c;
/*     */   
/*     */   private double[] ceu;
/*     */   
/*     */   private int[] mm;
/*     */   
/*     */   private double[] nx;
/*     */   
/*     */   public MatchingPursuit(AllMatrices paramAllMatrices) {
/* 101 */     this.dict = paramAllMatrices;
/* 102 */     this.N = this.dict.getN();
/* 103 */     this.K = this.dict.getK();
/* 104 */     this.ipMat = null;
/* 106 */     this.normalized = checkNormalized();
/*     */   }
/*     */   
/*     */   public MatchingPursuit(AllMatrices paramAllMatrices, SymmetricMatrix paramSymmetricMatrix) {
/* 117 */     this.dict = paramAllMatrices;
/* 118 */     this.N = this.dict.getN();
/* 119 */     this.K = this.dict.getK();
/* 120 */     this.ipMat = paramSymmetricMatrix;
/* 122 */     this.normalized = checkNormalized();
/*     */   }
/*     */   
/*     */   public boolean checkNormalized() {
/* 135 */     boolean bool = true;
/* 136 */     for (byte b = 0; b < this.K; b++) {
/* 139 */       if (Math.abs(this.dict.innerProduct(b, b) - 1.0D) > 1.0E-6D) {
/* 140 */         bool = false;
/*     */         break;
/*     */       } 
/*     */     } 
/* 144 */     this.normalized = bool;
/* 145 */     return this.normalized;
/*     */   }
/*     */   
/*     */   public boolean getNormalized() {
/* 152 */     return this.normalized;
/*     */   }
/*     */   
/*     */   public void setNormalized() {
/* 158 */     this.normalized = true;
/*     */   }
/*     */   
/*     */   public void clearNormalized() {
/* 164 */     this.normalized = false;
/*     */   }
/*     */   
/*     */   private double tableInnerProduct(int paramInt1, int paramInt2) {
/*     */     double d;
/* 177 */     if (this.normalized && paramInt1 == paramInt2)
/* 177 */       return 1.0D; 
/* 178 */     if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 >= this.K || paramInt2 >= this.K)
/* 178 */       return 0.0D; 
/* 179 */     if (this.ipMat == null) {
/* 180 */       d = this.dict.innerProduct(paramInt1, paramInt2);
/*     */     } else {
/* 182 */       d = this.ipMat.get(paramInt1, paramInt2);
/*     */     } 
/* 184 */     return d;
/*     */   }
/*     */   
/*     */   private void initEkstraVariabler(int paramInt, boolean paramBoolean) {
/* 196 */     if (paramInt > 100)
/* 197 */       System.out.println("initEkstraVariabler: S-value is too large. Continue but may get short of memory."); 
/* 200 */     if (paramInt > this.maxS1) {
/* 201 */       this.r = new double[paramInt][this.K];
/* 202 */       this.T = new int[this.K];
/* 203 */       this.J = new int[paramInt];
/* 204 */       this.d = new double[this.K];
/* 205 */       this.e = new double[this.K];
/* 206 */       this.u = new double[this.K];
/* 207 */       this.c = new double[this.K];
/* 208 */       this.maxS1 = paramInt;
/*     */     } 
/* 210 */     if (paramBoolean && paramInt > this.maxS2) {
/* 211 */       this.ceu = new double[3 * this.K * paramInt];
/* 212 */       this.mm = new int[paramInt * this.K];
/* 213 */       this.nx = new double[paramInt];
/* 214 */       this.maxS2 = paramInt;
/*     */     } 
/*     */   }
/*     */   
/*     */   public double[] vsSelectBest(double[] paramArrayOfdouble) {
/* 232 */     double[] arrayOfDouble = new double[this.K];
/* 233 */     if (paramArrayOfdouble.length != this.N)
/* 234 */       throw new IllegalArgumentException("vsSelectBest: Input argument vector x is not expected length, N=" + this.N); 
/* 237 */     vsSelectBest(paramArrayOfdouble, arrayOfDouble);
/* 239 */     return arrayOfDouble;
/*     */   }
/*     */   
/*     */   private void vsSelectBest(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) {
/* 255 */     double[] arrayOfDouble = new double[this.K];
/* 256 */     this.dict.transposeTimes(paramArrayOfdouble1, arrayOfDouble);
/* 257 */     if (!this.normalized)
/* 258 */       for (byte b = 0; b < this.K; b++)
/* 259 */         arrayOfDouble[b] = arrayOfDouble[b] / Math.sqrt(tableInnerProduct(b, b));  
/* 262 */     double d = 0.0D;
/* 263 */     byte b1 = 0;
/* 264 */     for (byte b2 = 0; b2 < this.K; b2++) {
/* 265 */       paramArrayOfdouble2[b2] = 0.0D;
/* 266 */       if (Math.abs(arrayOfDouble[b2]) > d) {
/* 266 */         d = Math.abs(arrayOfDouble[b2]);
/* 266 */         b1 = b2;
/*     */       } 
/*     */     } 
/* 268 */     if (this.normalized) {
/* 269 */       paramArrayOfdouble2[b1] = arrayOfDouble[b1];
/*     */     } else {
/* 271 */       paramArrayOfdouble2[b1] = arrayOfDouble[b1] / Math.sqrt(tableInnerProduct(b1, b1));
/*     */     } 
/*     */   }
/*     */   
/*     */   public double[] vsBMP(double[] paramArrayOfdouble, int paramInt) {
/* 288 */     double[] arrayOfDouble = new double[this.K];
/* 289 */     if (paramArrayOfdouble.length != this.N)
/* 290 */       throw new IllegalArgumentException("vsBMP: Input argument vector x is not expected length N=" + this.N); 
/* 293 */     if (paramInt == 1)
/* 293 */       vsSelectBest(paramArrayOfdouble, arrayOfDouble); 
/* 294 */     if (paramInt > 1 && paramInt <= 100)
/* 294 */       vsBMP(paramArrayOfdouble, arrayOfDouble, paramInt, 2 * paramInt, 1.0E-5D, 0); 
/* 295 */     return arrayOfDouble;
/*     */   }
/*     */   
/*     */   void vsBMP(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, int paramInt) {
/* 306 */     if (paramInt == 1) {
/* 307 */       vsSelectBest(paramArrayOfdouble1, paramArrayOfdouble2);
/* 308 */     } else if (paramInt > 1 && paramInt <= 100) {
/* 309 */       vsBMP(paramArrayOfdouble1, paramArrayOfdouble2, paramInt, paramInt + 1 + paramInt / 3, 1.0E-6D, 0);
/*     */     } else {
/* 311 */       for (byte b = 0; b < this.K; ) {
/* 311 */         paramArrayOfdouble2[b] = 0.0D;
/* 311 */         b++;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void vsBMP(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, int paramInt1, int paramInt2, double paramDouble, int paramInt3) {
/* 337 */     double d1 = paramDouble * paramDouble;
/* 338 */     double[] arrayOfDouble1 = new double[this.K];
/* 339 */     double[] arrayOfDouble2 = new double[this.N];
/* 340 */     double d2 = 0.0D;
/*     */     byte b1;
/* 341 */     for (b1 = 0; b1 < this.N; b1++) {
/* 342 */       arrayOfDouble2[b1] = paramArrayOfdouble1[b1];
/* 343 */       d2 += paramArrayOfdouble1[b1] * paramArrayOfdouble1[b1];
/*     */     } 
/* 345 */     for (b1 = 0; b1 < this.K; ) {
/* 345 */       paramArrayOfdouble2[b1] = 0.0D;
/* 345 */       b1++;
/*     */     } 
/* 346 */     double d3 = d2;
/* 347 */     boolean bool = true;
/* 348 */     byte b2 = 0;
/* 349 */     byte b3 = 0;
/* 350 */     double d4 = 0.0D;
/* 351 */     double d5 = 0.0D;
/* 352 */     byte b4 = 0;
/*     */     try {
/* 354 */       while (bool) {
/* 355 */         this.dict.transposeTimes(arrayOfDouble2, arrayOfDouble1);
/* 356 */         if (!this.normalized)
/* 357 */           for (byte b5 = 0; b5 < this.K; b5++)
/* 358 */             arrayOfDouble1[b5] = arrayOfDouble1[b5] / Math.sqrt(tableInnerProduct(b5, b5));  
/* 359 */         d4 = 0.0D;
/* 360 */         b3 = 0;
/* 361 */         for (byte b = 0; b < this.K; b++) {
/* 362 */           if (Math.abs(arrayOfDouble1[b]) > d4) {
/* 363 */             d4 = Math.abs(arrayOfDouble1[b]);
/* 364 */             b3 = b;
/*     */           } 
/*     */         } 
/* 367 */         if (paramArrayOfdouble2[b3] == 0.0D)
/* 367 */           b2++; 
/* 368 */         if (this.normalized) {
/* 369 */           d5 = arrayOfDouble1[b3];
/*     */         } else {
/* 371 */           d5 = arrayOfDouble1[b3] / Math.sqrt(tableInnerProduct(b3, b3));
/*     */         } 
/* 373 */         paramArrayOfdouble2[b3] = paramArrayOfdouble2[b3] + d5;
/* 374 */         this.dict.addColumn(b3, -d5, arrayOfDouble2);
/* 375 */         if (b2 == paramInt1)
/* 375 */           bool = false; 
/* 376 */         b4++;
/* 377 */         if (b4 >= paramInt2)
/* 377 */           bool = false; 
/* 378 */         d3 -= d5 * d5;
/* 379 */         if (d3 < d1 * d2)
/* 379 */           bool = false; 
/*     */       } 
/* 381 */       while (paramInt3 > 0) {
/* 382 */         this.dict.transposeTimes(arrayOfDouble2, arrayOfDouble1);
/* 383 */         if (!this.normalized)
/* 384 */           for (byte b5 = 0; b5 < this.K; b5++)
/* 385 */             arrayOfDouble1[b5] = arrayOfDouble1[b5] / Math.sqrt(tableInnerProduct(b5, b5));  
/* 386 */         d4 = 0.0D;
/* 387 */         b3 = 0;
/* 388 */         for (byte b = 0; b < this.K; b++) {
/* 389 */           if (paramArrayOfdouble2[b] != 0.0D && Math.abs(arrayOfDouble1[b]) > d4) {
/* 390 */             d4 = Math.abs(arrayOfDouble1[b]);
/* 391 */             b3 = b;
/*     */           } 
/*     */         } 
/* 394 */         if (d4 == 0.0D)
/*     */           break; 
/* 395 */         if (this.normalized) {
/* 396 */           d5 = arrayOfDouble1[b3];
/*     */         } else {
/* 398 */           d5 = arrayOfDouble1[b3] / Math.sqrt(tableInnerProduct(b3, b3));
/*     */         } 
/* 400 */         paramArrayOfdouble2[b3] = paramArrayOfdouble2[b3] + d5;
/* 401 */         this.dict.addColumn(b3, -d5, arrayOfDouble2);
/* 402 */         paramInt3--;
/*     */       } 
/* 404 */     } catch (NullPointerException nullPointerException) {
/* 405 */       System.out.println("vsBMP: NullPointerException s=" + b2 + ", km=" + b3 + ", count=" + b4);
/* 407 */       nullPointerException.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public double[] vsOMP(double[] paramArrayOfdouble, int paramInt) {
/* 432 */     double[] arrayOfDouble = new double[this.K];
/* 433 */     if (paramArrayOfdouble.length != this.N) {
/* 434 */       System.out.println("vsOMP: Input argument vector x is not expected length N=" + this.N);
/* 435 */       return arrayOfDouble;
/*     */     } 
/* 437 */     if (paramInt == 1)
/* 437 */       vsSelectBest(paramArrayOfdouble, arrayOfDouble); 
/* 438 */     if (paramInt > 100)
/* 439 */       System.out.println("vsOMP: Input argument S=" + paramInt + " is to large."); 
/* 440 */     if (paramInt > 1 && paramInt <= 100)
/* 440 */       vsOMPorORMP(paramArrayOfdouble, arrayOfDouble, paramInt, 1.0E-5D, true); 
/* 441 */     return arrayOfDouble;
/*     */   }
/*     */   
/*     */   public double[] vsOMP(double[] paramArrayOfdouble, double paramDouble) {
/* 459 */     double[] arrayOfDouble = new double[this.K];
/* 460 */     if (paramArrayOfdouble.length != this.N) {
/* 461 */       System.out.println("vsOMP: Input argument vector x is not expected length N=" + this.N);
/* 462 */       return arrayOfDouble;
/*     */     } 
/* 464 */     if (paramDouble <= 0.0D)
/* 464 */       paramDouble = 1.0E-6D; 
/* 465 */     vsOMPorORMP(paramArrayOfdouble, arrayOfDouble, (100 < this.N) ? 100 : this.N, paramDouble, true);
/* 466 */     return arrayOfDouble;
/*     */   }
/*     */   
/*     */   public double[] vsOMP(double[] paramArrayOfdouble, int paramInt, double paramDouble) {
/* 485 */     double[] arrayOfDouble = new double[this.K];
/* 486 */     if (paramArrayOfdouble.length != this.N) {
/* 487 */       System.out.println("vsOMP: Input argument vector x is not expected length N=" + this.N);
/* 488 */       return arrayOfDouble;
/*     */     } 
/* 490 */     if (paramDouble <= 0.0D)
/* 490 */       paramDouble = 1.0E-6D; 
/* 491 */     if (paramInt == 1)
/* 491 */       vsSelectBest(paramArrayOfdouble, arrayOfDouble); 
/* 492 */     if (paramInt > 100) {
/* 493 */       System.out.println("Input argument S=" + paramInt + " is to large, S is set to " + Math.min(100, this.N));
/* 495 */       paramInt = Math.min(100, this.N);
/*     */     } 
/* 497 */     if (paramInt > 1)
/* 497 */       vsOMPorORMP(paramArrayOfdouble, arrayOfDouble, paramInt, paramDouble, true); 
/* 498 */     return arrayOfDouble;
/*     */   }
/*     */   
/*     */   public double[] vsORMP(double[] paramArrayOfdouble, int paramInt) {
/* 516 */     double[] arrayOfDouble = new double[this.K];
/* 517 */     if (paramArrayOfdouble.length != this.N) {
/* 518 */       System.out.println("vsORMP: Input argument vector x is not expected length N=" + this.N);
/* 519 */       return arrayOfDouble;
/*     */     } 
/* 521 */     if (paramInt == 1)
/* 521 */       vsSelectBest(paramArrayOfdouble, arrayOfDouble); 
/* 522 */     if (paramInt > 100) {
/* 523 */       System.out.println("Input argument S=" + paramInt + " is to large, S is set to " + Math.min(100, this.N));
/* 525 */       paramInt = Math.min(100, this.N);
/*     */     } 
/* 527 */     if (paramInt > 1 && paramInt <= 100)
/* 527 */       vsOMPorORMP(paramArrayOfdouble, arrayOfDouble, paramInt, 1.0E-5D, false); 
/* 528 */     return arrayOfDouble;
/*     */   }
/*     */   
/*     */   public double[] vsORMP(double[] paramArrayOfdouble, double paramDouble) {
/* 546 */     double[] arrayOfDouble = new double[this.K];
/* 547 */     if (paramArrayOfdouble.length != this.N) {
/* 548 */       System.out.println("vsORMP: Input argument vector x is not expected length N=" + this.N);
/* 549 */       return arrayOfDouble;
/*     */     } 
/* 551 */     if (paramDouble <= 0.0D)
/* 551 */       paramDouble = 1.0E-6D; 
/* 552 */     vsOMPorORMP(paramArrayOfdouble, arrayOfDouble, (100 < this.N) ? 100 : this.N, paramDouble, false);
/* 553 */     return arrayOfDouble;
/*     */   }
/*     */   
/*     */   public double[] vsORMP(double[] paramArrayOfdouble, int paramInt, double paramDouble) {
/* 572 */     double[] arrayOfDouble = new double[this.K];
/* 573 */     if (paramArrayOfdouble.length != this.N) {
/* 574 */       System.out.println("vsORMP: Input argument vector x is not expected length N=" + this.N);
/* 575 */       return arrayOfDouble;
/*     */     } 
/* 577 */     if (paramDouble <= 0.0D)
/* 577 */       paramDouble = 1.0E-6D; 
/* 578 */     if (paramInt == 1)
/* 578 */       vsSelectBest(paramArrayOfdouble, arrayOfDouble); 
/* 579 */     if (paramInt > 100) {
/* 580 */       System.out.println("Input argument S=" + paramInt + " is to large, S is set to " + Math.min(100, this.N));
/* 582 */       paramInt = Math.min(100, this.N);
/*     */     } 
/* 584 */     if (paramInt > 1)
/* 584 */       vsOMPorORMP(paramArrayOfdouble, arrayOfDouble, paramInt, paramDouble, false); 
/* 585 */     return arrayOfDouble;
/*     */   }
/*     */   
/*     */   void vsOMPorORMP(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, int paramInt, double paramDouble, boolean paramBoolean) {
/* 606 */     int i = vsOMPorORMP2(paramArrayOfdouble1, paramArrayOfdouble2, paramInt, paramDouble, paramBoolean);
/*     */   }
/*     */   
/*     */   int vsOMPorORMP2(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, int paramInt, double paramDouble, boolean paramBoolean) {
/* 611 */     for (byte b1 = 0; b1 < this.K; ) {
/* 611 */       paramArrayOfdouble2[b1] = 0.0D;
/* 611 */       b1++;
/*     */     } 
/* 612 */     double d1 = 0.0D;
/* 613 */     for (byte b2 = 0; b2 < this.N; ) {
/* 613 */       d1 += paramArrayOfdouble1[b2] * paramArrayOfdouble1[b2];
/* 613 */       b2++;
/*     */     } 
/* 614 */     double d2 = d1 * paramDouble * paramDouble;
/* 615 */     double d3 = 0.0D;
/* 616 */     double d4 = 0.0D;
/* 617 */     byte b3 = 0;
/* 618 */     initEkstraVariabler(paramInt, false);
/* 619 */     this.dict.transposeTimes(paramArrayOfdouble1, this.c);
/*     */     byte b4;
/* 621 */     for (b4 = 0; b4 < this.K; b4++) {
/* 622 */       this.T[b4] = b4;
/* 623 */       if (this.normalized) {
/* 624 */         this.e[b4] = 1.0D;
/* 625 */         this.u[b4] = 1.0D;
/*     */       } else {
/* 627 */         this.e[b4] = tableInnerProduct(b4, b4);
/* 628 */         this.u[b4] = Math.sqrt(this.e[b4]);
/* 629 */         paramArrayOfdouble2[b4] = this.u[b4];
/* 630 */         this.c[b4] = this.c[b4] / this.u[b4];
/*     */       } 
/* 632 */       if (Math.abs(this.c[b4]) > d3) {
/* 633 */         d3 = Math.abs(this.c[b4]);
/* 634 */         b3 = b4;
/*     */       } 
/*     */     } 
/* 637 */     b4 = 0;
/* 638 */     this.J[0] = b3;
/* 639 */     this.T[b3] = -1;
/* 640 */     this.r[0][b3] = this.u[b3];
/* 641 */     d1 -= d3 * d3;
/* 642 */     while (b4 + 1 < paramInt && d1 > d2) {
/*     */       byte b;
/* 643 */       for (b = 0; b < this.K; ) {
/* 643 */         if (this.T[b] >= 0) {
/* 644 */           this.r[b4][b] = tableInnerProduct(b3, b);
/* 645 */           for (byte b5 = 0; b5 < b4; ) {
/* 645 */             this.r[b4][b] = this.r[b4][b] - this.r[b5][b3] * this.r[b5][b];
/* 645 */             b5++;
/*     */           } 
/* 646 */           if (this.u[b3] != 0.0D)
/* 646 */             this.r[b4][b] = this.r[b4][b] / this.u[b3]; 
/* 647 */           this.c[b] = this.c[b] * this.u[b] - this.c[b3] * this.r[b4][b];
/* 648 */           if (paramBoolean) {
/* 649 */             this.d[b] = Math.abs(this.c[b]);
/* 650 */             if (!this.normalized)
/* 650 */               this.d[b] = this.d[b] / paramArrayOfdouble2[b]; 
/*     */           } 
/* 652 */           this.e[b] = this.e[b] - this.r[b4][b] * this.r[b4][b];
/* 653 */           this.u[b] = Math.sqrt(this.e[b]);
/* 654 */           if (this.u[b] != 0.0D)
/* 654 */             this.c[b] = this.c[b] / this.u[b]; 
/* 655 */           if (!paramBoolean)
/* 655 */             this.d[b] = Math.abs(this.c[b]); 
/*     */         } 
/*     */         b++;
/*     */       } 
/* 658 */       b3 = 0;
/* 659 */       d3 = 0.0D;
/* 660 */       for (b = 0; b < this.K; ) {
/* 660 */         if (this.T[b] >= 0 && 
/* 661 */           this.d[b] > d3) {
/* 662 */           d3 = this.d[b];
/* 663 */           b3 = b;
/*     */         } 
/*     */         b++;
/*     */       } 
/* 666 */       b4++;
/* 667 */       this.J[b4] = b3;
/* 668 */       this.r[b4][b3] = this.u[b3];
/* 669 */       this.T[b3] = -1;
/* 670 */       d1 -= this.c[b3] * this.c[b3];
/* 671 */       if (d1 + d2 < 0.0D) {
/* 672 */         System.out.println("vsOMPorORMP: Values not as expected, here n2x=" + d1 + ", s=" + b4 + ", normalized = " + this.normalized + ", do vsBMP instead.");
/* 674 */         this.normalized = checkNormalized();
/* 675 */         System.out.println("and checkNormalized returned " + this.normalized + ".");
/* 676 */         vsBMP(paramArrayOfdouble1, paramArrayOfdouble2, paramInt, 2 * paramInt, paramDouble, 2);
/* 677 */         return -1;
/*     */       } 
/*     */     } 
/* 680 */     backSubstitution(paramArrayOfdouble2, b4);
/* 681 */     return 0;
/*     */   }
/*     */   
/*     */   private void backSubstitution(double[] paramArrayOfdouble, int paramInt) {
/*     */     int i;
/* 686 */     for (i = 0; i < this.K; ) {
/* 686 */       paramArrayOfdouble[i] = 0.0D;
/* 686 */       i++;
/*     */     } 
/* 687 */     for (i = paramInt; i >= 0; i--) {
/* 688 */       int j = this.J[i];
/* 689 */       for (int k = paramInt; k > i; ) {
/* 689 */         this.c[j] = this.c[j] - this.c[this.J[k]] * this.r[i][this.J[k]];
/* 689 */         k--;
/*     */       } 
/* 690 */       if (this.r[i][j] != 0.0D)
/* 690 */         this.c[j] = this.c[this.J[i]] / this.r[i][j]; 
/* 691 */       paramArrayOfdouble[j] = this.c[j];
/*     */     } 
/*     */   }
/*     */   
/*     */   public double[] vsPS(double[] paramArrayOfdouble, int paramInt1, double paramDouble, int paramInt2) {
/* 709 */     double[] arrayOfDouble = new double[this.K];
/* 710 */     if (paramArrayOfdouble.length != this.N) {
/* 711 */       System.out.println("vsPS: Input argument vector x is not expected length N=" + this.N);
/* 712 */       return arrayOfDouble;
/*     */     } 
/* 714 */     if (paramDouble <= 0.0D)
/* 714 */       paramDouble = 1.0E-6D; 
/* 715 */     if (paramInt1 == 1)
/* 715 */       vsSelectBest(paramArrayOfdouble, arrayOfDouble); 
/* 716 */     if (paramInt1 > 100) {
/* 717 */       System.out.println("Input argument S=" + paramInt1 + " is to large, S is set to " + Math.min(100, this.N));
/* 719 */       paramInt1 = Math.min(100, this.N);
/*     */     } 
/* 721 */     if (paramInt1 > 1)
/* 721 */       vsPS(paramArrayOfdouble, arrayOfDouble, paramInt1, paramDouble, paramInt2); 
/* 722 */     return arrayOfDouble;
/*     */   }
/*     */   
/*     */   void vsPS(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2, int paramInt1, double paramDouble, int paramInt2) {
/* 728 */     initEkstraVariabler(paramInt1, true);
/* 729 */     double d1 = 0.0D;
/* 730 */     for (byte b1 = 0; b1 < this.N; ) {
/* 730 */       d1 += paramArrayOfdouble1[b1] * paramArrayOfdouble1[b1];
/* 730 */       b1++;
/*     */     } 
/* 731 */     double d2 = d1 * paramDouble * paramDouble;
/* 732 */     double d3 = d1;
/* 733 */     int i = 0;
/* 735 */     this.dict.transposeTimes(paramArrayOfdouble1, this.c);
/*     */     int j;
/* 737 */     for (j = 0; j < this.K; j++) {
/* 738 */       this.T[j] = j;
/* 739 */       if (this.normalized) {
/* 740 */         this.e[j] = 1.0D;
/* 741 */         this.u[j] = 1.0D;
/*     */       } else {
/* 744 */         this.e[j] = tableInnerProduct(j, j);
/* 745 */         this.u[j] = Math.sqrt(this.e[j]);
/* 746 */         this.c[j] = this.c[j] / this.u[j];
/*     */       } 
/*     */     } 
/* 749 */     for (j = 0; j < paramInt1; ) {
/* 749 */       this.J[j] = -1;
/* 749 */       j++;
/*     */     } 
/* 750 */     j = -1;
/* 751 */     boolean bool = true;
/* 752 */     int k = -1;
/* 753 */     byte b2 = 0;
/*     */     while (true) {
/* 757 */       j++;
/* 758 */       if (j < 0 || j >= paramInt1)
/* 759 */         System.out.println("vsPS: s=" + j + ", beginning MAIN LOOP."); 
/* 761 */       if (bool) {
/*     */         byte b;
/* 762 */         for (b = 0; b < this.K; ) {
/* 762 */           this.d[b] = (this.T[b] >= 0) ? Math.abs(this.c[b]) : 0.0D;
/* 762 */           b++;
/*     */         } 
/* 763 */         if (k >= 0)
/* 763 */           this.d[k] = 0.0D; 
/* 764 */         i = distributeFunction(paramInt1, j, paramInt2, this.mm);
/*     */         int m;
/* 766 */         for (b = 0, m = j * 3 * this.K; b < this.K; b++) {
/* 767 */           this.ceu[m++] = this.c[b];
/* 768 */           this.ceu[m++] = this.e[b];
/* 769 */           this.ceu[m++] = this.u[b];
/*     */         } 
/* 771 */         this.nx[j] = d1;
/*     */       } else {
/*     */         byte b;
/*     */         int m;
/* 774 */         for (b = 0, m = j * 3 * this.K; b < this.K; b++) {
/* 775 */           this.c[b] = this.ceu[m++];
/* 776 */           this.e[b] = this.ceu[m++];
/* 777 */           this.u[b] = this.ceu[m++];
/*     */         } 
/* 779 */         d1 = this.nx[j];
/* 780 */         bool = true;
/*     */       } 
/* 783 */       this.J[j] = i;
/* 784 */       this.T[i] = -1;
/* 785 */       this.r[j][i] = this.u[i];
/* 786 */       d1 -= this.c[i] * this.c[i];
/* 788 */       if (d1 < d2) {
/* 790 */         backSubstitution(paramArrayOfdouble2, j);
/* 791 */         j++;
/* 792 */         d3 = d1;
/*     */         break;
/*     */       } 
/* 796 */       if (j + 1 < paramInt1) {
/* 798 */         for (byte b = 0; b < this.K; ) {
/* 798 */           if (this.T[b] >= 0) {
/* 799 */             this.r[j][b] = tableInnerProduct(i, b);
/* 800 */             for (byte b3 = 0; b3 < j; ) {
/* 800 */               this.r[j][b] = this.r[j][b] - this.r[b3][i] * this.r[b3][b];
/* 800 */               b3++;
/*     */             } 
/* 801 */             if (this.u[i] != 0.0D)
/* 801 */               this.r[j][b] = this.r[j][b] / this.u[i]; 
/* 802 */             this.c[b] = this.c[b] * this.u[b] - this.c[i] * this.r[j][b];
/* 803 */             this.e[b] = this.e[b] - this.r[j][b] * this.r[j][b];
/* 804 */             this.u[b] = Math.sqrt(this.e[b]);
/* 805 */             if (this.u[b] != 0.0D)
/* 805 */               this.c[b] = this.c[b] / this.u[b]; 
/*     */           } 
/*     */           b++;
/*     */         } 
/*     */         continue;
/*     */       } 
/* 809 */       b2++;
/* 810 */       if (d1 < d3) {
/* 811 */         backSubstitution(paramArrayOfdouble2, j);
/* 812 */         d3 = d1;
/*     */       } 
/* 815 */       this.T[i] = i;
/* 816 */       this.J[j] = -1;
/* 817 */       j--;
/* 818 */       while (j >= 0) {
/* 819 */         int m = j * this.K;
/* 820 */         k = this.J[j];
/* 821 */         this.mm[m + k] = 0;
/* 822 */         this.T[k] = k;
/* 823 */         this.J[j] = -1;
/* 824 */         int n = 0;
/* 825 */         for (byte b = 0; b < this.K; b++) {
/* 826 */           if (this.mm[m + b] > n) {
/* 827 */             n = this.mm[m + b];
/* 828 */             i = b;
/*     */           } 
/*     */         } 
/* 831 */         if (n > 0)
/*     */           break; 
/* 832 */         j--;
/*     */       } 
/* 834 */       if (j < 0 || b2 > paramInt2) {
/* 835 */         j = paramInt1;
/*     */         break;
/*     */       } 
/* 839 */       bool = false;
/* 840 */       j--;
/*     */     } 
/* 844 */     if (b2 >= (int)(paramInt2 * 0.95D) || d1 > d2);
/*     */   }
/*     */   
/*     */   private static int findMin(double[] paramArrayOfdouble) {
/* 854 */     byte b1 = 0;
/* 855 */     double d = Double.MAX_VALUE;
/* 856 */     for (byte b2 = 0; b2 < paramArrayOfdouble.length; ) {
/* 856 */       if (paramArrayOfdouble[b2] > 0.0D && 
/* 857 */         paramArrayOfdouble[b2] < d) {
/* 857 */         b1 = b2;
/* 857 */         d = paramArrayOfdouble[b2];
/*     */       } 
/*     */       b2++;
/*     */     } 
/* 859 */     return b1;
/*     */   }
/*     */   
/*     */   private static int findMax(double[] paramArrayOfdouble) {
/* 864 */     byte b1 = 0;
/* 865 */     for (byte b2 = 1; b2 < paramArrayOfdouble.length; b2++) {
/* 866 */       if (paramArrayOfdouble[b2] > paramArrayOfdouble[b1])
/* 866 */         b1 = b2; 
/*     */     } 
/* 868 */     return b1;
/*     */   }
/*     */   
/*     */   private int distributeFunction(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfint) {
/* 879 */     double d = 0.65D;
/* 880 */     if (paramInt3 > this.K)
/* 880 */       d = 0.5D; 
/* 881 */     if (paramInt3 > paramInt1 * this.K)
/* 881 */       d = 0.4D; 
/* 882 */     if (paramInt3 > 2 * paramInt1 * this.K)
/* 882 */       d = 0.2D; 
/* 883 */     if (paramInt3 > 6 * paramInt1 * this.K)
/* 883 */       d = 0.1D; 
/* 884 */     int i = 0;
/* 885 */     if (paramInt2 == 0)
/* 885 */       i = paramInt3; 
/* 886 */     if (paramInt2 == paramInt1 - 1)
/* 886 */       i = 1; 
/* 887 */     if (paramInt2 > 0 && paramInt2 < paramInt1 - 1)
/* 887 */       i = paramArrayOfint[(paramInt2 - 1) * this.K + this.J[paramInt2 - 1]]; 
/* 889 */     int j = 0;
/* 890 */     int k = paramInt2 * this.K;
/*     */     int m;
/* 891 */     for (m = 0; m < this.K; ) {
/* 891 */       paramArrayOfint[k + m] = 0;
/* 891 */       m++;
/*     */     } 
/* 894 */     m = findMax(this.d);
/* 895 */     if (this.d[m] <= 0.0D)
/* 896 */       System.out.println("Error: distributeFunction  km=" + m + ", d[km]=" + this.d[m]); 
/* 898 */     if (i <= 1) {
/* 899 */       paramArrayOfint[k + m] = 1;
/* 901 */     } else if (paramInt2 + 2 == paramInt1) {
/* 902 */       paramArrayOfint[k + m] = 1;
/* 903 */       this.d[m] = 0.0D;
/* 904 */       i--;
/* 905 */       while (i > 0) {
/* 906 */         j = findMax(this.d);
/* 907 */         if (this.d[j] <= 0.0D) {
/* 908 */           System.out.println("Info: No more positive values in array d.");
/*     */           break;
/*     */         } 
/* 911 */         paramArrayOfint[k + j] = 1;
/* 912 */         this.d[j] = 0.0D;
/* 913 */         i--;
/*     */       } 
/*     */     } else {
/* 919 */       double d1 = 0.0D;
/* 920 */       double d2 = 0.75D / (1.0D - d);
/* 921 */       double d3 = d2 / this.d[m];
/* 922 */       double d4 = d * this.d[m];
/* 923 */       for (byte b = 0; b < this.K; b++) {
/* 924 */         if (this.d[b] > 0.0D)
/* 925 */           if (this.d[b] < d4) {
/* 926 */             this.d[b] = 0.0D;
/*     */           } else {
/* 928 */             this.d[b] = this.d[b] * d3 + 1.0D - d2;
/* 929 */             d1 += this.d[b];
/*     */           }  
/*     */       } 
/* 934 */       if (d1 > 0.0D) {
/* 935 */         double d5 = i / d1;
/* 936 */         int n = 0;
/*     */         byte b1;
/* 937 */         for (b1 = 0; b1 < this.K; b1++) {
/* 938 */           if (this.d[b1] > 0.0D) {
/* 939 */             paramArrayOfint[k + b1] = (int)Math.round(this.d[b1] * d5);
/* 940 */             n += paramArrayOfint[k + b1];
/*     */           } 
/*     */         } 
/* 946 */         while (n < i) {
/* 947 */           j = findMax(this.d);
/* 948 */           paramArrayOfint[k + j] = paramArrayOfint[k + j] + 1;
/* 949 */           this.d[j] = this.d[j] - 1.0D;
/* 950 */           n++;
/*     */         } 
/* 952 */         while (n > i) {
/* 953 */           j = 0;
/* 954 */           for (b1 = 0; b1 < this.K; b1++) {
/* 955 */             if (this.d[b1] > 0.0D && (this.d[j] == 0.0D || this.d[b1] < this.d[j]))
/* 955 */               j = b1; 
/*     */           } 
/* 957 */           if (this.d[j] <= 0.0D)
/*     */             break; 
/* 958 */           if (paramArrayOfint[k + j] > 0) {
/* 959 */             paramArrayOfint[k + j] = paramArrayOfint[k + j] - 1;
/* 960 */             n--;
/*     */           } 
/* 962 */           this.d[j] = this.d[j] + 1.0D;
/*     */         } 
/* 964 */         if (paramArrayOfint[k + m] <= 0) {
/* 965 */           System.out.println("Error: distributeFunction logical program error.");
/* 966 */           paramArrayOfint[k + m] = 1;
/* 967 */           n = 0;
/* 968 */           for (b1 = 0; b1 < this.K; ) {
/* 968 */             n += paramArrayOfint[k + b1];
/* 968 */             b1++;
/*     */           } 
/*     */         } 
/* 970 */         if (n != i)
/* 971 */           System.out.println("Warning: sumMm = " + n + " != " + i + " = nCombLeft"); 
/*     */       } else {
/* 974 */         System.out.println("Warning: sum of d array not as expected, sumD = " + d1);
/* 975 */         paramArrayOfint[k + m] = 1;
/*     */       } 
/*     */     } 
/* 979 */     return m;
/*     */   }
/*     */ }


/* Location:              /Users/liuzenglu/codebase/Dict_Coding/javaclasses/!/mpv2/MatchingPursuit.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */