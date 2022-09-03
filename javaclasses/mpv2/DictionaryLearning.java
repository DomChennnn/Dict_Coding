// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DictionaryLearning.java

package mpv2;

// Referenced classes of package mpv2:
//            SimpleMatrix, DiagonalMatrix, SymmetricMatrix, MatchingPursuit, 
//            AllMatrices

public class DictionaryLearning {

  public DictionaryLearning(AllMatrices allmatrices, boolean flag, int i) {
    lambdaMet = '1';
    lambdaLow = 1.0D;
    lambdaHigh = 1.0D;
    lambdaPar = 20000D;
    mpMet = 2;
    mpS = 2;
    mpRelErr = 9.9999999999999995E-007D;
    mpAbsErr = 9.9999999999999995E-007D;
    mpComb = 50;
    paramI1 = 128;
    minCdiag = 1E+018D;
    maxCdiag = 0.0D;
    valueCmmr = 1000000D;
    limitCmax = 0.0D;
    limitCmmr = 1000000D;
    verbose = false;
    veryVerbose = false;
    loggActive = true;
    sumxx = 0.0D;
    sumww = 0.0D;
    sumrr = 0.0D;
    nzc = 0;
    N = allmatrices.getN();
    K = allmatrices.getK();
    noTV = K;
    D = new SimpleMatrix(allmatrices);
    G = new DiagonalMatrix(K, 1.0D);
    C = new SymmetricMatrix(K, K);
    x = new double[N];
    w = new double[K];
    r = new double[N];
    u = new double[K];
    v = new double[K];
    setVerbose(i);
    for (int j = 0; j < K; j++) {
      double d = D.innerProduct(j, j);
      if (d > 0.0D) {
        d = 1.0D / d;
        if (d > maxCdiag) {maxCdiag = d;}
        if (d < minCdiag) {minCdiag = d;}
        C.set(j, j, d);
        G.set(j, Math.sqrt(d));
      } else {
        System.out.println(
            (new StringBuilder()).append("Warning: 2-norm of dictionary vector ").append(j)
                .append(" is less or equal to zero."));
        C.set(j, j, 1.0D);
        G.set(j, 1.0D);
      }
      valueCmmr = maxCdiag / minCdiag;
      limitCmax = 4D * maxCdiag;
    }

    D.timeseqScaleColumns(G);
    if (flag) {
      DD = SymmetricMatrix.innerProductMatrix(D);
      mp = new MatchingPursuit(D, DD);
      if (verbose) {
        System.out.println(
            (new StringBuilder()).append("A DictionaryLearning object of size ").append(N)
                .append("-by-").append(K).append(" is made and DD is used."));
      }
    } else {
      DD = null;
      mp = new MatchingPursuit(D);
      if (verbose) {
        System.out.println(
            (new StringBuilder()).append("A DictionaryLearning object of size ").append(N)
                .append("-by-").append(K).append(" is made without using DD."));
      }
    }
    mp.setNormalized();
  }

  public DictionaryLearning(AllMatrices allmatrices, boolean flag) {
    this(allmatrices, flag, 0);
  }

  public SimpleMatrix getDictionary() {
    return D;
  }

  public SymmetricMatrix getInnerProductMatrix() {
    return DD;
  }

  public SymmetricMatrix getTheCMatrix() {
    return C;
  }

  public double[] getWeights() {
    return w;
  }

  public double[] getResidual() {
    return r;
  }

  public double[] getx() {
    return x;
  }

  public double[] getw() {
    return w;
  }

  public double[] getr() {
    return r;
  }

  public double[] getu() {
    return u;
  }

  public double[] getv() {
    return v;
  }

  public int getNoTV() {
    return noTV;
  }

  public int getMPMet() {
    return mpMet;
  }

  public int getParamI1() {
    return paramI1;
  }

  public double getLimitCmmr() {
    return limitCmmr;
  }

  public double getValueCmmr() {
    return valueCmmr;
  }

  public double getLimitCmax() {
    return limitCmax;
  }

  public double getMinCdiag() {
    return minCdiag;
  }

  public double getMaxCdiag() {
    return maxCdiag;
  }

  public double getSumxx() {
    return sumxx;
  }

  public double getSumww() {
    return sumww;
  }

  public double getSumrr() {
    return sumrr;
  }

  public double[] getSumAllxxTab() {
    return sumAllxxTab;
  }

  public double[] getSumAllwwTab() {
    return sumAllwwTab;
  }

  public double[] getSumAllrrTab() {
    return sumAllrrTab;
  }

  public double[] getSnrTab() {
    return snrTab;
  }

  public double[] getSumxxTab() {
    return sumxxTab;
  }

  public double[] getSumwwTab() {
    return sumwwTab;
  }

  public double[] getSumrrTab() {
    return sumrrTab;
  }

  public int[] getIndexW() {
    int[] ai = new int[nzc];
    for (int i = 0; i < nzc; i++) {ai[i] = indexW[i];}

    return ai;
  }

  public double[] getValueW() {
    double[] ad = new double[nzc];
    for (int i = 0; i < nzc; i++) {ad[i] = valueW[i];}

    return ad;
  }

  private double increasingFun(double d, char c, double d1, double d2) {
    switch (c) {
      case 49: // '1'
        return 1.0D;

      case 76: // 'L'
        if (d < 0.0D) {return d1;}
        if (d <= 1.0D) {return d1 + (d2 - d1) * d;} else {return d2;}

      case 81: // 'Q'
        if (d < 0.0D) {return d1;}
        if (d <= 1.0D) {
          d = 1.0D - d;
          return d2 - (d2 - d1) * d * d;
        } else {
          return d2;
        }

      case 67: // 'C'
        if (d < 0.0D) {return d1;}
        if (d <= 1.0D) {
          d = 1.0D - d;
          return d2 - (d2 - d1) * d * d * d;
        } else {
          return d2;
        }

      case 72: // 'H'
        if (d < 0.0D) {return d1;} else {return d2 - (d2 - d1) / (d + 1.0D);}

      case 69: // 'E'
        if (d < 0.0D) {return d1;} else {return d2 - (d2 - d1) * Math.pow(0.5D, d);}

      case 83: // 'S'
        if (d < 0.0D) {return d1;}
        if (d <= 1.0D) {return d1 + (d2 - d1) * d * d;} else {return d2;}

      case 84: // 'T'
        if (d < 0.0D) {return d1;}
        if (d <= 1.0D) {return d1 + (d2 - d1) * d * d * d;} else {return d2;}
    }
    System.out.println("Illegal Met should never happen.");
    return d2;
  }

  public double getLambda() {
    return increasingFun((double)noTV / lambdaPar, lambdaMet, lambdaLow, lambdaHigh);
  }

  public double getLambda(double d) {
    return increasingFun(d, lambdaMet, lambdaLow, lambdaHigh);
  }

  public void setLambda(char c, double d, double d1, double d2) {
    switch (c) {
      case 49: // '1'
        lambdaMet = '1';
        lambdaLow = 1.0D;
        lambdaHigh = 1.0D;
        lambdaPar = 1.0D;
        if (verbose) {System.out.println("lambda is set to the constant 1.0.");}
        return;

      case 76: // 'L'
      case 108: // 'l'
        lambdaMet = 'L';
        break;

      case 81: // 'Q'
      case 113: // 'q'
        lambdaMet = 'Q';
        break;

      case 67: // 'C'
      case 99: // 'c'
        lambdaMet = 'C';
        break;

      case 83: // 'S'
      case 115: // 's'
        lambdaMet = 'S';
        break;

      case 84: // 'T'
      case 116: // 't'
        lambdaMet = 'T';
        break;

      case 72: // 'H'
      case 104: // 'h'
        lambdaMet = 'H';
        break;

      case 69: // 'E'
      case 101: // 'e'
        lambdaMet = 'E';
        break;

      default:
        throw new IllegalArgumentException(
            "The char for the method to update lambda is not a legal value.");
    }
    if (d < 0.5D || d > 1.0D) {
      throw new IllegalArgumentException(
          "lamLow not within legal (resonable) range: 0.5 <= lamLow <= 1.0.");
    }
    lambdaLow = d;
    if (d1 < lambdaLow || d1 > 1.0D) {
      throw new IllegalArgumentException(
          "lamHigh not within legal (resonable) range: lambdaLow <= lamHigh <= 1.0.");
    }
    lambdaHigh = d1;
    if (d2 < (double)K) {
      throw new IllegalArgumentException(
          "lamPar is too small, the value is normally several thousands.");
    }
    lambdaPar = d2;
    if (verbose) {
      System.out.println(
          (new StringBuilder()).append("The update method for lambda is set to: ").append(lambdaMet)
              .append(" from ").append(lambdaLow).append(" to ").append(lambdaHigh)
              .append(" and parameter ").append(lambdaPar).append("."));
    }
  }

  public void setBMP() {
    mpMet = 1;
  }

  public void setOMP() {
    mpMet = 2;
  }

  public void setORMP() {
    mpMet = 3;
  }

  public void setPS() {
    mpMet = 4;
  }

  public void setBMP(int i) {
    setMParg(i, 9.9999999999999995E-007D, 9.9999999999999995E-007D);
    mpMet = 1;
  }

  public void setOMP(int i, double d, double d1) {
    setMParg(i, d, d1);
    mpMet = 2;
  }

  public void setORMP(int i, double d, double d1) {
    setMParg(i, d, d1);
    mpMet = 3;
  }

  public void setPS(int i, double d, double d1, int j) {
    setMParg(i, d, d1);
    mpComb = j;
    mpMet = 4;
  }

  private void setMParg(int i, double d, double d1) {
    mpS = i;
    if (mpS < 1) {
      System.out.println("Number of non-zeros is smaller than one. Set it to 1.");
      mpS = 1;
    }
    if (mpS >= N) {
      System.out.println("Number of non-zeros is too large. Set it to 1.");
      mpS = 1;
    }
    mpRelErr = d;
    mpAbsErr = d1;
  }

  public void setLoggOn() {
    loggActive = true;
  }

  public void setLoggOff() {
    loggActive = false;
  }

  public void setParamI1(int i) {
    paramI1 = i;
  }

  public void setLimitCmmr(double d) {
    limitCmmr = d;
  }

  public void setLimitCmax(double d) {
    limitCmax = d;
  }

  public void setVerbose(int i) {
    if (i < 0) {
      throw new IllegalArgumentException(
          "Verbose level should be given as a non-zero positive integer.");
    }
    if (i == 0) {
      verbose = false;
      veryVerbose = false;
    } else if (i == 1) {
      verbose = true;
      veryVerbose = false;
    } else {
      verbose = true;
      veryVerbose = true;
    }
  }

  private void verbosePrintln(int i) {
    System.out.println(
        (new StringBuilder()).append("Iteration ").append(i + 1).append(" : ").append("noTV = ")
            .append(noTV).append(", lambdaPar = ").append(lambdaPar).append(", lambda = ")
            .append(getLambda((double)noTV / lambdaPar)).append(", SNR = ").append(snrTab[i]));
    if (veryVerbose) {
      doNormalizationSlow();
      System.out.println((new StringBuilder()).append("Iteration ").append(i + 1).append(" : ")
          .append("normF of C is ").append(C.normF()).append(" and trace of C is ")
          .append(C.trace()).append("."));
    }
  }

  private void doNormalizationFast() {
    double d = 0.0D;
    for (int i = 0; i < K; i++) {
      double d1 = D.innerProduct(i, i);
      if (d1 > 0.0D) {
        G.set(i, 1.0D / Math.sqrt(d1));
        d += Math.abs(1.0D - d1);
      } else {
        G.set(i, 1.0D);
      }
    }

    if (d > 0.10000000000000001D) {
      D.timeseqScaleColumns(G);
      mp.setNormalized();
      C.eqScaleRowsAndColumns(G, C);
      if (DD != null) {DD.eqScaleRowsAndColumns(G, DD);}
    }
  }

  private void doNormalizationSlow() {
    for (int i = 0; i < K; i++) {
      double d = D.innerProduct(i, i);
      if (d > 0.0D) {G.set(i, 1.0D / Math.sqrt(d));} else {G.set(i, 1.0D);}
    }

    D.timeseqScaleColumns(G);
    mp.setNormalized();
    C.eqScaleRowsAndColumns(G, C);
    if (DD != null) {DD.eqTProduct(D, D);}
  }

  public void rlsdla(double[] ad, int i) {
    if (ad.length < N) {throw new IllegalArgumentException("The supplied data is too short.");}
    int j = ad.length / N;
    double d = 0.0D;
    double d1 = 0.0D;
    double d2 = 0.0D;
    if (loggActive) {
      sumAllxxTab = new double[i];
      sumAllwwTab = new double[i];
      sumAllrrTab = new double[i];
      snrTab = new double[i];
      sumxxTab = new double[j];
      sumwwTab = new double[j];
      sumrrTab = new double[j];
    }
    indexW = new int[j * mpS];
    valueW = new double[j * mpS];
    nzc = 0;
    for (int k = 0; k < i; k++) {
      if (loggActive) {
        d = 0.0D;
        d1 = 0.0D;
        d2 = 0.0D;
      }
      for (int i1 = 0; i1 < j; i1++) {
        for (int k1 = 0; k1 < N; k1++) {x[k1] = ad[i1 * N + k1];}

        if (Double.isNaN(D.get(0, 0))) {
          if (verbose) {
            System.out.println((new StringBuilder()).append("Error in rlsdla, noTV = ").append(noTV)
                .append(", dictionary is NaN, return! "));
          }
          return;
        }
        rlsdla1(x);
        if (!loggActive) {continue;}
        d += sumxx;
        d1 += sumww;
        d2 += sumrr;
        if (k != i - 1) {continue;}
        sumxxTab[i1] = sumxx;
        sumwwTab[i1] = sumww;
        sumrrTab[i1] = sumrr;
        for (int l1 = 0; l1 < K; l1++) {
          if (w[l1] != 0.0D) {
            indexW[nzc] = i1 * K + l1;
            valueW[nzc] = w[l1];
            nzc++;
          }
        }
      }

      if (!loggActive) {continue;}
      sumAllxxTab[k] = d;
      sumAllwwTab[k] = d1;
      sumAllrrTab[k] = d2;
      snrTab[k] = 10D * Math.log10(d / d2);
      if (verbose) {verbosePrintln(k);}
    }

    doNormalizationSlow();
    if (loggActive) {
      for (int l = 0; l < nzc; l++) {
        int j1 = indexW[l] % K;
        valueW[l] /= G.get(j1);
      }
    }
  }

  public void rlsdla1(double[] ad) {
    noTV++;
    sumxx = 0.0D;
    for (int i = 0; i < N; i++) {sumxx += ad[i] * ad[i];}

    int j = 0;
    switch (mpMet) {
      case 1: // '\001'
        mp.vsBMP(ad, w, mpS);
        break;

      case 2: // '\002'
        j = mp.vsOMPorORMP2(ad, w, mpS, Math.max(mpAbsErr / Math.sqrt(sumxx), mpRelErr), true);
        break;

      case 3: // '\003'
        j = mp.vsOMPorORMP2(ad, w, mpS, Math.max(mpAbsErr / Math.sqrt(sumxx), mpRelErr), false);
        break;

      case 4: // '\004'
        mp.vsPS(ad, w, mpS, Math.max(mpAbsErr / Math.sqrt(sumxx), mpRelErr), mpComb);
        break;

      default:
        System.out.println("Illegal mpMet should never happen.");
        break;
    }
    if (j < 0) {
      if (veryVerbose) {
        System.out.println((new StringBuilder()).append("Error in OMP/ORMP, noTV = ").append(noTV)
            .append(", ignore this training vector. "));
      }
      return;
    }
    if (loggActive) {
      sumww = 0.0D;
      for (int k = 0; k < K; k++) {if (w[k] != 0.0D) {sumww += w[k] * w[k];}}
    }
    sumrr = 0.0D;
    D.times(w, r);
    for (int l = 0; l < N; l++) {
      r[l] = ad[l] - r[l];
      sumrr += r[l] * r[l];
    }

    boolean flag = false;
    if (lambdaMet != '1') {
      double d = getLambda((double)noTV / lambdaPar);
      if (d < 0.99399999999999999D) {
        if (checkC()) {
          C.eqProduct(C, 1.0D / d);
          flag = valueCmmr > limitCmmr;
        }
      } else if (d < 0.99929999999999997D) {
        if (noTV % 16 == 0 && checkC()) {
          C.eqProduct(C, 17D - 16D * d);
          flag = valueCmmr > limitCmmr;
        }
      } else if (d < 0.99990999999999997D) {
        if (noTV % 128 == 0 && checkC()) {
          C.eqProduct(C, 129D - 128D * d);
          flag = valueCmmr > limitCmmr;
        }
      } else if (d < 1.0D && noTV % 1024 == 0 && checkC()) {
        C.eqProduct(C, 1025D - 1024D * d);
        flag = valueCmmr > limitCmmr;
      }
    }
    C.times(w, u);
    double d1 = 1.0D;
    for (int i1 = 0; i1 < K; i1++) {if (w[i1] != 0.0D) {d1 += w[i1] * u[i1];}}

    d1 = 1.0D / d1;
    if (DD != null) {D.transposeTimes(r, v);}
    D.pluseqOuterProduct(1.0D, d1, r, u);
    mp.clearNormalized();
    if (DD != null) {
      DD.pluseqOuterProduct(1.0D, d1, u, v);
      DD.pluseqOuterProduct(1.0D, d1 * d1 * sumrr, u);
    }
    C.pluseqOuterProduct(1.0D, -d1, u);
    if (flag) {
      double d2 = C.get(0, 0);
      maxCdiag = d2;
      minCdiag = d2;
      int j1 = 0;
      int k1 = 0;
      for (int l1 = 1; l1 < K; l1++) {
        double d3 = C.get(l1, l1);
        if (d3 > maxCdiag) {
          maxCdiag = d3;
          j1 = l1;
        }
        if (d3 < minCdiag) {
          minCdiag = d3;
          k1 = l1;
        }
      }

      D.getColumn(j1, ad);
      D.getColumn(k1, r);
      for (int i2 = 0; i2 < N; i2++) {ad[i2] = 0.90000000000000002D * ad[i2] + 0.11D * r[i2];}

      D.setColumn(j1, ad);
      doNormalizationSlow();
    } else if (noTV % 32768 == 0) {doNormalizationSlow();} else if (noTV % paramI1 == 0) {
      doNormalizationFast();
    }
  }

  private boolean checkC() {
    double d = C.get(0, 0);
    maxCdiag = d;
    minCdiag = d;
    for (int i = 1; i < K; i++) {
      double d1 = C.get(i, i);
      if (d1 > maxCdiag) {maxCdiag = d1;}
      if (d1 < minCdiag) {minCdiag = d1;}
    }

    if (minCdiag <= 0.0D) {
      minCdiag = maxCdiag / valueCmmr;
      for (int j = 0; j < K; j++) {
        if (C.get(j, j) <= 0.0D) {C.set(j, j, minCdiag);}
        if (veryVerbose) {
          System.out.println((new StringBuilder()).append("checkC: let element  ").append(j)
              .append(" in diagonal of C matrix be ").append(minCdiag));
        }
      }
    }
    valueCmmr = maxCdiag / minCdiag;
    return maxCdiag < limitCmax;
  }

  public void ilsdla(double[] ad, int i) {
    setLoggOn();
    if (ad.length < N) {throw new IllegalArgumentException("The supplied data is too short.");}
    int j = ad.length / N;
    SimpleMatrix simplematrix = new SimpleMatrix(N, K);
    SymmetricMatrix symmetricmatrix = new SymmetricMatrix(K, K);
    sumAllxxTab = new double[i];
    sumAllwwTab = new double[i];
    sumAllrrTab = new double[i];
    snrTab = new double[i];
    sumxxTab = new double[j];
    sumwwTab = new double[j];
    sumrrTab = new double[j];
    indexW = new int[j * mpS];
    valueW = new double[j * mpS];
    double d = 0.0D;
    for (int k = 0; k < i; k++) {
      double d1 = 0.0D;
      double d2 = 0.0D;
      int l = 0;
      nzc = 0;
      simplematrix.eqZeros();
      symmetricmatrix.eqZeros();
      for (int i1 = 0; i1 < j; i1++) {
        if (k == 0) {
          sumxx = 0.0D;
          for (int k1 = 0; k1 < N; k1++) {
            x[k1] = ad[i1 * N + k1];
            sumxx += x[k1] * x[k1];
          }

          sumxxTab[i1] = sumxx;
          d += sumxx;
        } else {
          for (int l1 = 0; l1 < N; l1++) {x[l1] = ad[i1 * N + l1];}

          sumxx = sumxxTab[i1];
        }
        switch (mpMet) {
          case 1: // '\001'
            mp.vsBMP(x, w, mpS);
            break;

          case 2: // '\002'
            mp.vsOMPorORMP(x, w, mpS, Math.max(mpAbsErr / Math.sqrt(sumxx), mpRelErr), true);
            break;

          case 3: // '\003'
            mp.vsOMPorORMP(x, w, mpS, Math.max(mpAbsErr / Math.sqrt(sumxx), mpRelErr), false);
            break;

          case 4: // '\004'
            mp.vsPS(x, w, mpS, Math.max(mpAbsErr / Math.sqrt(sumxx), mpRelErr), mpComb);
            break;

          default:
            System.out.println("Illegal mpMet should never happen.");
            break;
        }
        simplematrix.pluseqOuterProduct(1.0D, 1.0D, x, w);
        int[] ai = new int[mpS];
        int j2 = 0;
        for (int k2 = 0; k2 < K; k2++) {if (w[k2] != 0.0D) {ai[j2++] = k2;}}

        l += j2;
        for (int l2 = 0; l2 < j2; l2++) {
          int l3 = ai[l2];
          for (int i4 = 0; i4 <= l2; i4++) {
            int j4 = ai[i4];
            double d3 = symmetricmatrix.get(l3, j4) + w[l3] * w[j4];
            symmetricmatrix.set(l3, j4, d3);
          }
        }

        if (!loggActive) {continue;}
        sumww = 0.0D;
        for (int i3 = 0; i3 < K; i3++) {if (w[i3] != 0.0D) {sumww += w[i3] * w[i3];}}

        d1 += sumww;
        sumrr = 0.0D;
        D.times(w, r);
        for (int j3 = 0; j3 < N; j3++) {
          r[j3] = x[j3] - r[j3];
          sumrr += r[j3] * r[j3];
        }

        d2 += sumrr;
        sumwwTab[i1] = sumww;
        sumrrTab[i1] = sumrr;
        for (int k3 = 0; k3 < K; k3++) {
          if (w[k3] != 0.0D) {
            indexW[nzc] = i1 * K + k3;
            valueW[nzc] = w[k3];
            nzc++;
          }
        }
      }

      noTV += j;
      C.eqInverse(symmetricmatrix);
      D.eqProduct(simplematrix, C);
      doNormalizationSlow();
      if (k == i - 1) {
        for (int j1 = 0; j1 < nzc; j1++) {
          int i2 = indexW[j1] % K;
          valueW[j1] /= G.get(i2);
        }
      }
      if (!loggActive) {continue;}
      sumAllxxTab[k] = d;
      sumAllwwTab[k] = d1;
      sumAllrrTab[k] = d2;
      snrTab[k] = 10D * Math.log10(d / d2);
      if (!verbose) {continue;}
      System.out.println(
          (new StringBuilder()).append("Iteration ").append(k + 1).append(" : noTV = ").append(noTV)
              .append(", non-zeros in W = ").append(l).append(", SNR = ").append(snrTab[k]));
      if (veryVerbose) {
        System.out.println((new StringBuilder()).append("Iteration ").append(k + 1).append(" : ")
            .append("normF of C is ").append(C.normF()).append(" and trace of C is ")
            .append(C.trace()).append("."));
        System.out.println((new StringBuilder()).append("and  sumSignalSquared = ").append(d)
            .append("  sumWeightSquared = ").append(d1).append("  sumErrorSquared = ").append(d2));
      }
    }
  }

  private final int N;
  private final int K;
  private final SimpleMatrix D;
  private final SymmetricMatrix DD;
  private final SymmetricMatrix C;
  private final DiagonalMatrix G;
  private char lambdaMet;
  private double lambdaLow;
  private double lambdaHigh;
  private double lambdaPar;
  public static final int BMP = 1;
  public static final int OMP = 2;
  public static final int ORMP = 3;
  public static final int PS = 4;
  private final MatchingPursuit mp;
  private int mpMet;
  private int mpS;
  private double mpRelErr;
  private double mpAbsErr;
  private int mpComb;
  private int paramI1;
  private double minCdiag;
  private double maxCdiag;
  private double valueCmmr;
  private double limitCmax;
  private double limitCmmr;
  private int noTV;
  private boolean verbose;
  private boolean veryVerbose;
  private final double[] x;
  private final double[] w;
  private final double[] r;
  private final double[] u;
  private final double[] v;
  private boolean loggActive;
  private double sumxx;
  private double sumww;
  private double sumrr;
  private double[] sumAllxxTab;
  private double[] sumAllwwTab;
  private double[] sumAllrrTab;
  private double[] snrTab;
  private double[] sumxxTab;
  private double[] sumwwTab;
  private double[] sumrrTab;
  private int nzc;
  private int[] indexW;
  private double[] valueW;
}
