/*************************************************************************
 *  Compilation:  javac QuickSort.java
 *  Execution:    java QuickSort N
 *
 *  Generate N random real numbers between 0 and 1 and quicksort them.
 *
 *  On average, this quicksort algorithm runs in time proportional to
 *  N log N, independent of the input distribution. The algorithm
 *  guards against the worst-case by randomly shuffling the elements
 *  before sorting. In addition, it uses Sedgewick's partitioning
 *  method which stops on equal keys. This protects against cases
 *  that make many textbook implementations, even randomized ones,
 *  go quadratic (e.g., all keys are the same).
 *
 * Copyright  2006, Robert Sedgewick and Kevin Wayne.
 * Last updated: Fri Mar 2 06:52:24 EST 2007
 *
 * 30 October 2007, Copied from
 *   http://www.cs.princeton.edu/introcs/42sort/QuickSort.java.html
 * Karl Skretting added functions that take two arrays as input,
 * sorting key in first array, and swap the same way in second array.
 *************************************************************************/

package mpv2;

public class QuickSort {
  private static long comparisons = 0;
  private static long exchanges = 0;

  /***********************************************************************
   *  Quicksort code from Sedgewick 7.1, 7.2.
   ***********************************************************************/
  public static void quicksort(double[] a) {
    shuffle(a);                        // to guard against worst-case
    quicksort(a, 0, a.length - 1);
  }

  public static void quicksort(double[] a, int left, int right) {
    if (right <= left) {return;}
    int i = partition(a, left, right);
    quicksort(a, left, i - 1);
    quicksort(a, i + 1, right);
  }

  private static int partition(double[] a, int left, int right) {
    int i = left - 1;
    int j = right;
    while (true) {
      while (less(a[++i], a[right]))      // find item on left to swap
        ;                               // a[right] acts as sentinel
      while (less(a[right], a[--j]))      // find item on right to swap
      {
        if (j == left) {
          break;           // don't go out-of-bounds
        }
      }
      if (i >= j) {
        break;                  // check if pointers cross
      }
      exch(a, i, j);                      // swap two elements into place
    }
    exch(a, i, right);                      // swap with partition element
    return i;
  }

  // is x < y ?
  private static boolean less(double x, double y) {
    comparisons++;
    return (x < y);
  }

  // exchange a[i] and a[j]
  private static void exch(double[] a, int i, int j) {
    exchanges++;
    double swap = a[i];
    a[i] = a[j];
    a[j] = swap;
  }

  // shuffle the array a
  private static void shuffle(double[] a) {
    int N = a.length;
    for (int i = 0; i < N; i++) {
      int r = i + (int)(Math.random() * (N - i));   // between i and N-1
      exch(a, i, r);
    }
  }

  // Added Oct. 30, 2007, two arrays   double and double
  public static void quicksort(double[] a, double[] b) {
    if (a.length == b.length) {
      shuffle(a, b);                        // to guard against worst-case
      quicksort(a, b, 0, a.length - 1);
    } else {  // just ignore the b array
      shuffle(a);                        // to guard against worst-case
      quicksort(a, 0, a.length - 1);
    }
  }

  public static void quicksort(double[] a, double[] b, int left, int right) {
    if (right <= left) {return;}
    int i = partition(a, b, left, right);
    quicksort(a, b, left, i - 1);
    quicksort(a, b, i + 1, right);
  }

  private static int partition(double[] a, double[] b, int left, int right) {
    int i = left - 1;
    int j = right;
    while (true) {
      while (less(a[++i], a[right]))      // find item on left to swap
        ;                               // a[right] acts as sentinel
      while (less(a[right], a[--j]))      // find item on right to swap
      {
        if (j == left) {
          break;           // don't go out-of-bounds
        }
      }
      if (i >= j) {
        break;                  // check if pointers cross
      }
      exch(a, b, i, j);                   // swap two elements into place
    }
    exch(a, b, i, right);                   // swap with partition element
    return i;
  }

  // exchange a[i] and a[j], b[i] and b[j]
  private static void exch(double[] a, double[] b, int i, int j) {
    exchanges++;
    double swap = a[i];
    a[i] = a[j];
    a[j] = swap;
    exchanges++;
    swap = b[i];
    b[i] = b[j];
    b[j] = swap;
  }

  // shuffle the array a, and array b
  private static void shuffle(double[] a, double[] b) {
    int N = a.length;
    for (int i = 0; i < N; i++) {
      int r = i + (int)(Math.random() * (N - i));   // between i and N-1
      exch(a, b, i, r);
    }
  }

  // Added Oct. 30, 2007, two arrays   int and double
  public static void quicksort(int[] a, double[] b) {
    if (a.length == b.length) {
      shuffle(b, a);                        // to guard against worst-case
      quicksort(a, b, 0, a.length - 1);
    } else {  // just ignore the b array
      shuffle(a);                        // to guard against worst-case
      quicksort(a, 0, a.length - 1);
    }
  }

  public static void quicksort(int[] a, double[] b, int left, int right) {
    if (right <= left) {return;}
    int i = partition(a, b, left, right);
    quicksort(a, b, left, i - 1);
    quicksort(a, b, i + 1, right);
  }

  private static int partition(int[] a, double[] b, int left, int right) {
    int i = left - 1;
    int j = right;
    while (true) {
      while (less(a[++i], a[right]))      // find item on left to swap
        ;                               // a[right] acts as sentinel
      while (less(a[right], a[--j]))      // find item on right to swap
      {
        if (j == left) {
          break;           // don't go out-of-bounds
        }
      }
      if (i >= j) {
        break;                  // check if pointers cross
      }
      exch(b, a, i, j);                   // swap two elements into place
    }
    exch(b, a, i, right);                   // swap with partition element
    return i;
  }

  // Added Oct. 30, 2007, two arrays   double and int
  public static void quicksort(double[] a, int[] b) {
    if (a.length == b.length) {
      shuffle(a, b);                        // to guard against worst-case
      quicksort(a, b, 0, a.length - 1);
    } else {  // just ignore the b array
      shuffle(a);                        // to guard against worst-case
      quicksort(a, 0, a.length - 1);
    }
  }

  public static void quicksort(double[] a, int[] b, int left, int right) {
    if (right <= left) {return;}
    int i = partition(a, b, left, right);
    quicksort(a, b, left, i - 1);
    quicksort(a, b, i + 1, right);
  }

  private static int partition(double[] a, int[] b, int left, int right) {
    int i = left - 1;
    int j = right;
    while (true) {
      while (less(a[++i], a[right]))      // find item on left to swap
        ;                               // a[right] acts as sentinel
      while (less(a[right], a[--j]))      // find item on right to swap
      {
        if (j == left) {
          break;           // don't go out-of-bounds
        }
      }
      if (i >= j) {
        break;                  // check if pointers cross
      }
      exch(a, b, i, j);                   // swap two elements into place
    }
    exch(a, b, i, right);                   // swap with partition element
    return i;
  }

  // exchange a[i] and a[j], b[i] and b[j]
  private static void exch(double[] a, int[] b, int i, int j) {
    exchanges++;
    double swap = a[i];
    a[i] = a[j];
    a[j] = swap;
    exchanges++;
    int swap_i = b[i];
    b[i] = b[j];
    b[j] = swap_i;
  }

  // shuffle the array a, and array b
  private static void shuffle(double[] a, int[] b) {
    int N = a.length;
    for (int i = 0; i < N; i++) {
      int r = i + (int)(Math.random() * (N - i));   // between i and N-1
      exch(a, b, i, r);
    }
  }

  // Added Oct. 30, 2007, one array   int
  public static void quicksort(int[] a) {
    shuffle(a);                        // to guard against worst-case
    quicksort(a, 0, a.length - 1);
  }

  public static void quicksort(int[] a, int left, int right) {
    if (right <= left) {return;}
    int i = partition(a, left, right);
    quicksort(a, left, i - 1);
    quicksort(a, i + 1, right);
  }

  private static int partition(int[] a, int left, int right) {
    int i = left - 1;
    int j = right;
    while (true) {
      while (less(a[++i], a[right]))      // find item on left to swap
        ;                               // a[right] acts as sentinel
      while (less(a[right], a[--j]))      // find item on right to swap
      {
        if (j == left) {
          break;           // don't go out-of-bounds
        }
      }
      if (i >= j) {
        break;                  // check if pointers cross
      }
      exch(a, i, j);                      // swap two elements into place
    }
    exch(a, i, right);                      // swap with partition element
    return i;
  }

  // exchange a[i] and a[j]
  private static void exch(int[] a, int i, int j) {
    exchanges++;
    int swap = a[i];
    a[i] = a[j];
    a[j] = swap;
  }

  // shuffle the array a
  private static void shuffle(int[] a) {
    int N = a.length;
    for (int i = 0; i < N; i++) {
      int r = i + (int)(Math.random() * (N - i));   // between i and N-1
      exch(a, i, r);
    }
  }

  // is x < y ?
  private static boolean less(int x, int y) {
    comparisons++;
    return (x < y);
  }

  // Added Oct. 30, 2007, two arrays   int and int
  public static void quicksort(int[] a, int[] b) {
    if (a.length == b.length) {
      shuffle(a, b);                        // to guard against worst-case
      quicksort(a, b, 0, a.length - 1);
    } else {  // just ignore the b array
      shuffle(a);                        // to guard against worst-case
      quicksort(a, 0, a.length - 1);
    }
  }

  public static void quicksort(int[] a, int[] b, int left, int right) {
    if (right <= left) {return;}
    int i = partition(a, b, left, right);
    quicksort(a, b, left, i - 1);
    quicksort(a, b, i + 1, right);
  }

  private static int partition(int[] a, int[] b, int left, int right) {
    int i = left - 1;
    int j = right;
    while (true) {
      while (less(a[++i], a[right]))      // find item on left to swap
        ;                               // a[right] acts as sentinel
      while (less(a[right], a[--j]))      // find item on right to swap
      {
        if (j == left) {
          break;           // don't go out-of-bounds
        }
      }
      if (i >= j) {
        break;                  // check if pointers cross
      }
      exch(a, b, i, j);                   // swap two elements into place
    }
    exch(a, b, i, right);                   // swap with partition element
    return i;
  }

  // exchange a[i] and a[j], b[i] and b[j]
  private static void exch(int[] a, int[] b, int i, int j) {
    exchanges++;
    int swap = a[i];
    a[i] = a[j];
    a[j] = swap;
    exchanges++;
    swap = b[i];
    b[i] = b[j];
    b[j] = swap;
  }

  // shuffle the array a, and array b
  private static void shuffle(int[] a, int[] b) {
    int N = a.length;
    for (int i = 0; i < N; i++) {
      int r = i + (int)(Math.random() * (N - i));   // between i and N-1
      exch(a, b, i, r);
    }
  }

  // test client
  public static void test(int N) {
    // generate N random real numbers between 0 and 1
    long start = System.currentTimeMillis();
    double[] a = new double[N];
    int[] b = new int[N];
    for (int i = 0; i < N; i++) {
      a[i] = Math.random();
      b[i] = i;
    }
    if (N < 10) {
      for (int i = 0; i < N; i++) {System.out.println(" a[i] = " + a[i] + ",  b[i] = " + b[i]);}
    }

    long stop = System.currentTimeMillis();
    double elapsed = (stop - start) / 1000.0;
    System.out.println("Generating input:  " + elapsed + " seconds");

    // sort them
    start = System.currentTimeMillis();
    quicksort(a, b);
    stop = System.currentTimeMillis();
    elapsed = (stop - start) / 1000.0;
    System.out.println("Quicksort:   " + elapsed + " seconds");

    // print statistics
    System.out.println("Comparisons: " + comparisons);
    System.out.println("Exchanges:   " + exchanges);
    if (N < 10) {
      for (int i = 0; i < N; i++) {System.out.println(" a[i] = " + a[i] + ",  b[i] = " + b[i]);}
    }

    // sort them back again
    start = System.currentTimeMillis();
    quicksort(b, a);
    stop = System.currentTimeMillis();
    elapsed = (stop - start) / 1000.0;
    System.out.println("Quicksort (reverse):   " + elapsed + " seconds");
    if (N < 10) {
      for (int i = 0; i < N; i++) {System.out.println(" a[i] = " + a[i] + ",  b[i] = " + b[i]);}
    }

    return;
  }

  public static void main(String[] args) {
    int N = Integer.parseInt(args[0]);
    test(N);
  }
}


