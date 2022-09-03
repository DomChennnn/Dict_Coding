// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MatchingPursuit.java

package mpv2;

// Referenced classes of package mpv2:
//            AllMatrices, SymmetricMatrix

public class MatchingPursuit {

  public MatchingPursuit(AllMatrices allmatrices) {
    maxS1 = 0;
    maxS2 = 0;
    dict = allmatrices;
    N = dict.getN();
    K = dict.getK();
    ipMat = null;
    normalized = checkNormalized();
  }

  public MatchingPursuit(AllMatrices allmatrices, SymmetricMatrix symmetricmatrix) {
    maxS1 = 0;
    maxS2 = 0;
    dict = allmatrices;
    N = dict.getN();
    K = dict.getK();
    ipMat = symmetricmatrix;
    normalized = checkNormalized();
  }

  public boolean checkNormalized() {
    boolean flag = true;
    int i = 0;
    do {
      if (i >= K) {break;}
      if (Math.abs(dict.innerProduct(i, i) - 1.0D) > 9.9999999999999995E-007D) {
        flag = false;
        break;
      }
      i++;
    } while (true);
    normalized = flag;
    return normalized;
  }

  public boolean getNormalized() {
    return normalized;
  }

  public void setNormalized() {
    normalized = true;
  }

  public void clearNormalized() {
    normalized = false;
  }

  private double tableInnerProduct(int i, int j) {
    if (normalized && i == j) {return 1.0D;}
    if (i < 0 || j < 0 || i >= K || j >= K) {return 0.0D;}
    double d1;
    if (ipMat == null) {d1 = dict.innerProduct(i, j);} else {d1 = ipMat.get(i, j);}
    return d1;
  }

  private void initEkstraVariabler(int i, boolean flag) {
    if (i > 100) {
      System.out.println(
          "initEkstraVariabler: S-value is too large. Continue but may get short of memory.");
    }
    if (i > maxS1) {
      r = new double[i][K];
      T = new int[K];
      J = new int[i];
      d = new double[K];
      e = new double[K];
      u = new double[K];
      c = new double[K];
      maxS1 = i;
    }
    if (flag && i > maxS2) {
      ceu = new double[3 * K * i];
      mm = new int[i * K];
      nx = new double[i];
      maxS2 = i;
    }
  }

  public double[] vsSelectBest(double[] ad) {
    double[] ad1 = new double[K];
    if (ad.length != N) {
      throw new IllegalArgumentException((new StringBuilder()).append(
          "vsSelectBest: Input argument vector x is not expected length, N=").append(N).toString());
    } else {
      vsSelectBest(ad, ad1);
      return ad1;
    }
  }

  private void vsSelectBest(double[] ad, double[] ad1) {
    double[] ad2 = new double[K];
    dict.transposeTimes(ad, ad2);
    if (!normalized) {
      for (int i = 0; i < K; i++) {ad2[i] = ad2[i] / Math.sqrt(tableInnerProduct(i, i));}
    }
    double d1 = 0.0D;
    int j = 0;
    for (int k = 0; k < K; k++) {
      ad1[k] = 0.0D;
      if (Math.abs(ad2[k]) > d1) {
        d1 = Math.abs(ad2[k]);
        j = k;
      }
    }

    if (normalized) {ad1[j] = ad2[j];} else {
      ad1[j] = ad2[j] / Math.sqrt(tableInnerProduct(j, j));
    }
  }

  public double[] vsBMP(double[] ad, int i) {
    double[] ad1 = new double[K];
    if (ad.length != N) {
      throw new IllegalArgumentException(
          (new StringBuilder()).append(
                  "vsBMP: Input argument vector x is not expected length N=")
              .append(N).toString());
    }
    if (i == 1) {vsSelectBest(ad, ad1);}
    if (i > 1 && i <= 100) {vsBMP(ad, ad1, i, 2 * i, 1.0000000000000001E-005D, 0);}
    return ad1;
  }

  void vsBMP(double[] ad, double[] ad1, int i) {
    if (i == 1) {vsSelectBest(ad, ad1);} else if (i > 1 && i <= 100) {
      vsBMP(ad, ad1, i, i + 1 + i / 3, 9.9999999999999995E-007D, 0);
    } else {
      for (int j = 0; j < K; j++) {ad1[j] = 0.0D;}
    }
  }

  private void vsBMP(double[] ad, double[] ad1, int i, int j, double d1, int k) {
    double d2 = d1 * d1;
    double[] ad2 = new double[K];
    double[] ad3 = new double[N];
    double d3 = 0.0D;
    for (int l = 0; l < N; l++) {
      ad3[l] = ad[l];
      d3 += ad[l] * ad[l];
    }

    for (int i1 = 0; i1 < K; i1++) {ad1[i1] = 0.0D;}

    double d4 = d3;
    boolean flag = true;
    int j1 = 0;
    int k1 = 0;
    double d5 = 0.0D;
    double d8 = 0.0D;
    int l1 = 0;
    try {
      do {
        if (!flag) {break;}
        dict.transposeTimes(ad3, ad2);
        if (!normalized) {
          for (int i2 = 0; i2 < K; i2++) {
            ad2[i2] = ad2[i2] / Math.sqrt(tableInnerProduct(i2, i2));
          }
        }
        double d6 = 0.0D;
        k1 = 0;
        for (int j2 = 0; j2 < K; j2++) {
          if (Math.abs(ad2[j2]) > d6) {
            d6 = Math.abs(ad2[j2]);
            k1 = j2;
          }
        }

        if (ad1[k1] == 0.0D) {j1++;}
        double d9;
        if (normalized) {d9 = ad2[k1];} else {
          d9 = ad2[k1] / Math.sqrt(tableInnerProduct(k1, k1));
        }
        ad1[k1] += d9;
        dict.addColumn(k1, -d9, ad3);
        if (j1 == i) {flag = false;}
        if (++l1 >= j) {flag = false;}
        d4 -= d9 * d9;
        if (d4 < d2 * d3) {flag = false;}
      } while (true);
      do {
        if (k <= 0) {break;}
        dict.transposeTimes(ad3, ad2);
        if (!normalized) {
          for (int k2 = 0; k2 < K; k2++) {
            ad2[k2] = ad2[k2] / Math.sqrt(tableInnerProduct(k2, k2));
          }
        }
        double d7 = 0.0D;
        k1 = 0;
        for (int l2 = 0; l2 < K; l2++) {
          if (ad1[l2] != 0.0D && Math.abs(ad2[l2]) > d7) {
            d7 = Math.abs(ad2[l2]);
            k1 = l2;
          }
        }

        if (d7 == 0.0D) {break;}
        double d10;
        if (normalized) {d10 = ad2[k1];} else {
          d10 = ad2[k1] / Math.sqrt(tableInnerProduct(k1, k1));
        }
        ad1[k1] += d10;
        dict.addColumn(k1, -d10, ad3);
        k--;
      } while (true);
    } catch (NullPointerException nullpointerexception) {
      System.out.println(
          (new StringBuilder()).append("vsBMP: NullPointerException s=").append(j1).append(", km=")
              .append(k1).append(", count=").append(l1));
      nullpointerexception.printStackTrace();
    }
  }

  public double[] vsOMP(double[] ad, int i) {
    double[] ad1 = new double[K];
    if (ad.length != N) {
      System.out.println(
          (new StringBuilder()).append("vsOMP: Input argument vector x is not expected length N=")
              .append(N));
      return ad1;
    }
    if (i == 1) {vsSelectBest(ad, ad1);}
    if (i > 100) {
      System.out.println(
          (new StringBuilder()).append("vsOMP: Input argument S=").append(i)
              .append(" is to large."));
    }
    if (i > 1 && i <= 100) {vsOMPorORMP(ad, ad1, i, 1.0000000000000001E-005D, true);}
    return ad1;
  }

  public double[] vsOMP(double[] ad, double d1) {
    double[] ad1 = new double[K];
    if (ad.length != N) {
      System.out.println(
          (new StringBuilder()).append("vsOMP: Input argument vector x is not expected length N=")
              .append(N));
      return ad1;
    }
    if (d1 <= 0.0D) {d1 = 9.9999999999999995E-007D;}
    vsOMPorORMP(ad, ad1, 100 >= N ? N : 100, d1, true);
    return ad1;
  }

  public double[] vsOMP(double[] ad, int i, double d1) {
    double[] ad1 = new double[K];
    if (ad.length != N) {
      System.out.println(
          (new StringBuilder()).append("vsOMP: Input argument vector x is not expected length N=")
              .append(N));
      return ad1;
    }
    if (d1 <= 0.0D) {d1 = 9.9999999999999995E-007D;}
    if (i == 1) {vsSelectBest(ad, ad1);}
    if (i > 100) {
      System.out.println((new StringBuilder()).append("Input argument S=").append(i)
          .append(" is to large, S is set to ").append(Math.min(100, N)));
      i = Math.min(100, N);
    }
    if (i > 1) {vsOMPorORMP(ad, ad1, i, d1, true);}
    return ad1;
  }

  public double[] vsORMP(double[] ad, int i) {
    double[] ad1 = new double[K];
    if (ad.length != N) {
      System.out.println(
          (new StringBuilder()).append("vsORMP: Input argument vector x is not expected length N=")
              .append(N));
      return ad1;
    }
    if (i == 1) {vsSelectBest(ad, ad1);}
    if (i > 100) {
      System.out.println((new StringBuilder()).append("Input argument S=").append(i)
          .append(" is to large, S is set to ").append(Math.min(100, N)));
      i = Math.min(100, N);
    }
    if (i > 1 && i <= 100) {vsOMPorORMP(ad, ad1, i, 1.0000000000000001E-005D, false);}
    return ad1;
  }

  public double[] vsORMP(double[] ad, double d1) {
    double[] ad1 = new double[K];
    if (ad.length != N) {
      System.out.println(
          (new StringBuilder()).append("vsORMP: Input argument vector x is not expected length N=")
              .append(N));
      return ad1;
    }
    if (d1 <= 0.0D) {d1 = 9.9999999999999995E-007D;}
    vsOMPorORMP(ad, ad1, 100 >= N ? N : 100, d1, false);
    return ad1;
  }

  public double[] vsORMP(double[] ad, int i, double d1) {
    double[] ad1 = new double[K];
    if (ad.length != N) {
      System.out.println(
          (new StringBuilder()).append("vsORMP: Input argument vector x is not expected length N=")
              .append(N));
      return ad1;
    }
    if (d1 <= 0.0D) {d1 = 9.9999999999999995E-007D;}
    if (i == 1) {vsSelectBest(ad, ad1);}
    if (i > 100) {
      System.out.println((new StringBuilder()).append("Input argument S=").append(i)
          .append(" is to large, S is set to ").append(Math.min(100, N)));
      i = Math.min(100, N);
    }
    if (i > 1) {vsOMPorORMP(ad, ad1, i, d1, false);}
    return ad1;
  }

  void vsOMPorORMP(double[] ad, double[] ad1, int i, double d1, boolean flag) {
    int j = vsOMPorORMP2(ad, ad1, i, d1, flag);
  }

  int vsOMPorORMP2(double[] ad, double[] ad1, int i, double d1, boolean flag) {
    for (int j = 0; j < K; j++) {ad1[j] = 0.0D;}

    double d2 = 0.0D;
    for (int k = 0; k < N; k++) {d2 += ad[k] * ad[k];}

    double d3 = d2 * d1 * d1;
    double d4 = 0.0D;
    double d6 = 0.0D;
    int l = 0;
    initEkstraVariabler(i, false);
    dict.transposeTimes(ad, c);
    for (int i1 = 0; i1 < K; i1++) {
      T[i1] = i1;
      if (normalized) {
        e[i1] = 1.0D;
        u[i1] = 1.0D;
      } else {
        e[i1] = tableInnerProduct(i1, i1);
        u[i1] = Math.sqrt(e[i1]);
        ad1[i1] = u[i1];
        c[i1] = c[i1] / u[i1];
      }
      if (Math.abs(c[i1]) > d4) {
        d4 = Math.abs(c[i1]);
        l = i1;
      }
    }

    int j1 = 0;
    J[0] = l;
    T[l] = -1;
    r[0][l] = u[l];
    for (d2 -= d4 * d4; j1 + 1 < i && d2 > d3; ) {
      for (int k1 = 0; k1 < K; k1++) {
        if (T[k1] < 0) {continue;}
        r[j1][k1] = tableInnerProduct(l, k1);
        for (int i2 = 0; i2 < j1; i2++) {r[j1][k1] -= r[i2][l] * r[i2][k1];}

        if (u[l] != 0.0D) {r[j1][k1] /= u[l];}
        c[k1] = c[k1] * u[k1] - c[l] * r[j1][k1];
        if (flag) {
          d[k1] = Math.abs(c[k1]);
          if (!normalized) {d[k1] = d[k1] / ad1[k1];}
        }
        e[k1] -= r[j1][k1] * r[j1][k1];
        u[k1] = Math.sqrt(e[k1]);
        if (u[k1] != 0.0D) {c[k1] /= u[k1];}
        if (!flag) {d[k1] = Math.abs(c[k1]);}
      }

      l = 0;
      double d5 = 0.0D;
      for (int l1 = 0; l1 < K; l1++) {
        if (T[l1] >= 0 && d[l1] > d5) {
          d5 = d[l1];
          l = l1;
        }
      }

      j1++;
      J[j1] = l;
      r[j1][l] = u[l];
      T[l] = -1;
      d2 -= c[l] * c[l];
      if (d2 + d3 < 0.0D) {
        System.out.println(
            (new StringBuilder()).append("vsOMPorORMP: Values not as expected, here n2x=")
                .append(d2).append(", s=").append(j1).append(", normalized = ").append(normalized)
                .append(", do vsBMP instead."));
        normalized = checkNormalized();
        System.out.println(
            (new StringBuilder()).append("and checkNormalized returned ").append(normalized)
                .append("."));
        vsBMP(ad, ad1, i, 2 * i, d1, 2);
        return -1;
      }
    }

    backSubstitution(ad1, j1);
    return 0;
  }

  private void backSubstitution(double[] ad, int i) {
    for (int j = 0; j < K; j++) {ad[j] = 0.0D;}

    for (int k = i; k >= 0; k--) {
      int l = J[k];
      for (int i1 = i; i1 > k; i1--) {c[l] -= c[J[i1]] * r[k][J[i1]];}

      if (r[k][l] != 0.0D) {c[l] = c[J[k]] / r[k][l];}
      ad[l] = c[l];
    }
  }

  public double[] vsPS(double[] ad, int i, double d1, int j) {
    double[] ad1 = new double[K];
    if (ad.length != N) {
      System.out.println(
          (new StringBuilder()).append("vsPS: Input argument vector x is not expected length N=")
              .append(N));
      return ad1;
    }
    if (d1 <= 0.0D) {d1 = 9.9999999999999995E-007D;}
    if (i == 1) {vsSelectBest(ad, ad1);}
    if (i > 100) {
      System.out.println((new StringBuilder()).append("Input argument S=").append(i)
          .append(" is to large, S is set to ").append(Math.min(100, N)));
      i = Math.min(100, N);
    }
    if (i > 1) {vsPS(ad, ad1, i, d1, j);}
    return ad1;
  }

  void vsPS(double[] ad, double[] ad1, int i, double d1, int j) {
    initEkstraVariabler(i, true);
    double d2 = 0.0D;
    for (int k = 0; k < N; k++) {d2 += ad[k] * ad[k];}

    double d3 = d2 * d1 * d1;
    double d4 = d2;
    int l = 0;
    dict.transposeTimes(ad, c);
    for (int i1 = 0; i1 < K; i1++) {
      T[i1] = i1;
      if (normalized) {
        e[i1] = 1.0D;
        u[i1] = 1.0D;
      } else {
        e[i1] = tableInnerProduct(i1, i1);
        u[i1] = Math.sqrt(e[i1]);
        c[i1] = c[i1] / u[i1];
      }
    }

    for (int j1 = 0; j1 < i; j1++) {J[j1] = -1;}

    int k1 = -1;
    boolean flag = true;
    int l1 = -1;
    int i2 = 0;
    do {
      if (++k1 < 0 || k1 >= i) {
        System.out.println(
            (new StringBuilder()).append("vsPS: s=").append(k1).append(", beginning MAIN LOOP."));
      }
      if (flag) {
        for (int j2 = 0; j2 < K; j2++) {d[j2] = T[j2] < 0 ? 0.0D : Math.abs(c[j2]);}

        if (l1 >= 0) {d[l1] = 0.0D;}
        l = distributeFunction(i, k1, j, mm);
        int k2 = 0;
        int k3 = k1 * 3 * K;
        for (; k2 < K; k2++) {
          ceu[k3++] = c[k2];
          ceu[k3++] = e[k2];
          ceu[k3++] = u[k2];
        }

        nx[k1] = d2;
      } else {
        int l2 = 0;
        int l3 = k1 * 3 * K;
        for (; l2 < K; l2++) {
          c[l2] = ceu[l3++];
          e[l2] = ceu[l3++];
          u[l2] = ceu[l3++];
        }

        d2 = nx[k1];
        flag = true;
      }
      J[k1] = l;
      T[l] = -1;
      r[k1][l] = u[l];
      d2 -= c[l] * c[l];
      if (d2 < d3) {
        backSubstitution(ad1, k1);
        k1++;
        d4 = d2;
        break;
      }
      if (k1 + 1 < i) {
        int i3 = 0;
        while (i3 < K) {
          if (T[i3] >= 0) {
            r[k1][i3] = tableInnerProduct(l, i3);
            for (int i4 = 0; i4 < k1; i4++) {r[k1][i3] -= r[i4][l] * r[i4][i3];}

            if (u[l] != 0.0D) {r[k1][i3] = r[k1][i3] / u[l];}
            c[i3] = c[i3] * u[i3] - c[l] * r[k1][i3];
            e[i3] -= r[k1][i3] * r[k1][i3];
            u[i3] = Math.sqrt(e[i3]);
            if (u[i3] != 0.0D) {c[i3] = c[i3] / u[i3];}
          }
          i3++;
        }
        continue;
      }
      i2++;
      if (d2 < d4) {
        backSubstitution(ad1, k1);
        d4 = d2;
      }
      T[l] = l;
      J[k1] = -1;
      k1--;
      do {
        if (k1 < 0) {break;}
        int j3 = k1 * K;
        l1 = J[k1];
        mm[j3 + l1] = 0;
        T[l1] = l1;
        J[k1] = -1;
        int j4 = 0;
        for (int k4 = 0; k4 < K; k4++) {
          if (mm[j3 + k4] > j4) {
            j4 = mm[j3 + k4];
            l = k4;
          }
        }

        if (j4 > 0) {break;}
        k1--;
      } while (true);
      if (k1 < 0 || i2 > j) {
        k1 = i;
        break;
      }
      flag = false;
      k1--;
    } while (true);
    if (i2 < (int)((double)j * 0.94999999999999996D)) {
      if (d2 <= d3)
        ;
    }
  }

  private static int findMin(double[] ad) {
    int i = 0;
    double d1 = 1.7976931348623157E+308D;
    for (int j = 0; j < ad.length; j++) {
      if (ad[j] > 0.0D && ad[j] < d1) {
        i = j;
        d1 = ad[j];
      }
    }

    return i;
  }

  private static int findMax(double[] ad) {
    int i = 0;
    for (int j = 1; j < ad.length; j++) {if (ad[j] > ad[i]) {i = j;}}

    return i;
  }

  private int distributeFunction(int i, int j, int k, int[] ai) {
    double d1 = 0.65000000000000002D;
    if (k > K) {d1 = 0.5D;}
    if (k > i * K) {d1 = 0.40000000000000002D;}
    if (k > 2 * i * K) {d1 = 0.20000000000000001D;}
    if (k > 6 * i * K) {d1 = 0.10000000000000001D;}
    int l = 0;
    if (j == 0) {l = k;}
    if (j == i - 1) {l = 1;}
    if (j > 0 && j < i - 1) {l = ai[(j - 1) * K + J[j - 1]];}
    boolean flag = false;
    int l1 = j * K;
    for (int i2 = 0; i2 < K; i2++) {ai[l1 + i2] = 0;}

    int j2 = findMax(d);
    if (d[j2] <= 0.0D) {
      System.out.println(
          (new StringBuilder()).append("Error: distributeFunction  km=").append(j2)
              .append(", d[km]=").append(d[j2]));
    }
    if (l <= 1) {ai[l1 + j2] = 1;} else if (j + 2 == i) {
      ai[l1 + j2] = 1;
      d[j2] = 0.0D;
      l--;
      do {
        if (l <= 0) {break;}
        int i1 = findMax(d);
        if (d[i1] <= 0.0D) {
          System.out.println("Info: No more positive values in array d.");
          break;
        }
        ai[l1 + i1] = 1;
        d[i1] = 0.0D;
        l--;
      } while (true);
    } else {
      double d2 = 0.0D;
      double d3 = 0.75D / (1.0D - d1);
      double d4 = d3 / d[j2];
      double d5 = d1 * d[j2];
      for (int k2 = 0; k2 < K; k2++) {
        if (d[k2] <= 0.0D) {continue;}
        if (d[k2] < d5) {
          d[k2] = 0.0D;
        } else {
          d[k2] = (d[k2] * d4 + 1.0D) - d3;
          d2 += d[k2];
        }
      }

      if (d2 > 0.0D) {
        double d6 = (double)l / d2;
        int l2 = 0;
        for (int i3 = 0; i3 < K; i3++) {
          if (d[i3] > 0.0D) {
            ai[l1 + i3] = (int)Math.round(d[i3] * d6);
            l2 += ai[l1 + i3];
          }
        }

        for (; l2 < l; l2++) {
          int j1 = findMax(d);
          ai[l1 + j1]++;
          d[j1]--;
        }

        while (l2 > l) {
          int k1 = 0;
          for (int j3 = 0; j3 < K; j3++) {
            if (d[j3] > 0.0D && (d[k1] == 0.0D || d[j3] < d[k1])) {k1 = j3;}
          }

          if (d[k1] <= 0.0D) {break;}
          if (ai[l1 + k1] > 0) {
            ai[l1 + k1]--;
            l2--;
          }
          d[k1]++;
        }
        if (ai[l1 + j2] <= 0) {
          System.out.println("Error: distributeFunction logical program error.");
          ai[l1 + j2] = 1;
          l2 = 0;
          for (int k3 = 0; k3 < K; k3++) {l2 += ai[l1 + k3];}
        }
        if (l2 != l) {
          System.out.println(
              (new StringBuilder()).append("Warning: sumMm = ").append(l2).append(" != ")
                  .append(l)
                  .append(" = nCombLeft"));
        }
      } else {
        System.out.println(
            (new StringBuilder()).append("Warning: sum of d array not as expected, sumD = ")
                .append(d2));
        ai[l1 + j2] = 1;
      }
    }
    return j2;
  }

  private static final int MAXS = 100;
  private final int N;
  private final int K;
  private final AllMatrices dict;
  private final SymmetricMatrix ipMat;
  private boolean normalized;
  private int maxS1;
  private int maxS2;
  private double[][] r;
  private int[] T;
  private int[] J;
  private double[] d;
  private double[] e;
  private double[] u;
  private double[] c;
  private double[] ceu;
  private int[] mm;
  private double[] nx;
}
