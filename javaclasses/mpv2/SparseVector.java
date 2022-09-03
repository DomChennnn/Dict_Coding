/*
 * class:           SparseVector
 *
 * Description:		This class is a sparse vector implementation
 *
 * Copyright (c) 2008.  Karl Skretting.  All rights reserved.
 * University of Stavanger, Institutt for data- og elektroteknikk
 * Mail:  karl.skretting@uis.no   Homepage:  http://www.ux.his.no/~karlsk/
 *
 * HISTORY:  dd.mm.yyyy
 * Ver. 1.0  06.11.2008  KS: class made
 * */

package mpv2;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
// import mpv2.util.*;

/**
 * This class is a sparse vector implementation.
 *
 * <p>
 * The class SparseVector class stores only the non-zero elements of the vector. Read access is
 * fast, but write access (set a new non-zero element) need to change the whole vector.
 *
 * @author Karl Skretting (karl.skretting@uis.no)
 * @author http://www.ux.uis.no/~karlsk/
 * @version 1.0  November 2008
 */
public class SparseVector implements Cloneable, java.io.Serializable {

  private final int N;                // lengde p vektor, inkludert nullere
  private int nonZeroCount;     // antall ikke-null elementer
  private int[] index;          // stigende og lengde nonZeroCount
  private double[] value;       // length is nonZeroCount
    
/* ------------------------
   Constructors
 * ------------------------ */

  /**
   * Construct a length n sparse vector of zeros.
   *
   * @param n length of vector
   */
  public SparseVector(int n) {
    N = n;
    nonZeroCount = 0;       // ingen non-zeros
    // index and value are kept as null pointers
  }

  /**
   * Construct a sparse vector from an array.
   *
   * @param v array of doubles.
   */
  public SparseVector(double[] v) {
    N = v.length;
    //  teller antall ikke-null elementer
    nonZeroCount = 0;       // ingen non-zeros
    for (int n = 0; n < N; n++) {
      if (v[n] != 0.0) {nonZeroCount++;}
    }
    // make the arrays
    index = new int[nonZeroCount];
    value = new double[nonZeroCount];
    int i = 0;    // indeks for nonZeroValue (og tilhrende)
    for (int n = 0; n < N; n++) {
      if (v[n] != 0.0) {
        index[i] = n;
        value[i] = v[n];
        i++;
      }
    }
  }

  /**
   * Construct a length n sparse vector with non-zero indices and values supplied. If some indices
   * are equal, then one of the corresponding values are used
   *
   * @param n    length of vector
   * @param ind  indices for the non-zero values
   * @param vals the non-zero values
   */
  public SparseVector(int n, int[] ind, double[] vals) {
    N = n;
    double[] vin = vals;
    int[] iin = ind;
    if (ind.length < vals.length) {
      vin = new double[ind.length];
      for (int i = 0; i < vin.length; i++) {vin[i] = vals[i];}
    }
    if (ind.length > vals.length) {
      iin = new int[vals.length];
      for (int i = 0; i < iin.length; i++) {iin[i] = ind[i];}
    }
    QuickSort.quicksort(iin, vin);   // sort ascending
    int siste = -1;
    nonZeroCount = 0;
    for (int i = 0; i < vin.length; i++) {
      if ((iin[i] > siste) && (iin[i] < N) && (vin[i] != 0.0)) {
        siste = iin[i];
        nonZeroCount++;
      }
    }
    // make the arrays
    index = new int[nonZeroCount];
    value = new double[nonZeroCount];
    siste = -1;
    int j = 0;
    for (int i = 0; i < vin.length; i++) {
      if ((iin[i] > siste) && (iin[i] < N) && (vin[i] != 0.0)) {
        siste = iin[i];
        index[j] = siste;
        value[j] = vin[i];
        j++;
      }
    }
  }
    
 /* ------------------------
   Private Methods
  * ------------------------ */

  private int find(int n) {
    if ((n < 0) || (n >= N)) {
      return -1;  // out of range
    }
    if (nonZeroCount < 8) { // sequential search
      for (int i = 0; i < nonZeroCount; i++) {
        if (index[i] == n) {return i;}
      }
    } else { // binary search
      int i0 = 0;
      int i1 = nonZeroCount;
      while (i0 < i1) {
        // this always keep i<i1 (since i0<i1)
        int i = (i0 + i1) / 2;  // midten, (15+16)/2 = 15
        if (index[i] == n) {
          return i;
        } else {
          if (n > index[i]) {
            i0 = i + 1;
          } else {
            i1 = i;
          }
        }
      }
      // i0 == i1 == (i+1), and index[i] < n < index[i1]
    }
    return -1;   // not found
  }

  private int binarySearch(int n) {
    if ((n < 0) || (n >= N)) {
      return -1;  // out of range
    }
    // return 0,  n <= index[0]
    // retur i,   index[i-1] < n <= index[i]
    if (n <= index[0]) {return 0;}
    int i0 = 0;
    int i1 = nonZeroCount;
    int i = 0;
    while (i0 < i1) {
      // this always keep i<i1 (since i0<i1)
      i = (i0 + i1) / 2;  // midten, (15+16)/2 = 15
      if (index[i] == n) {
        return i;
      } else {
        if (n > index[i]) {
          i0 = i + 1;
        } else {
          i1 = i;
        }
      }
    }
    // i0 == i1 == (i+1), and index[i] < n < index[i1]
    return i;   // not found
  }

 /* ------------------------
   Public Methods, which use this object
  * ------------------------ */

  /**
   * Make a deep copy of a SparseVector.
   */
  public SparseVector copy() {
    SparseVector X = new SparseVector(N, index, value);
    return X;
  }

  /**
   * Clone the SparseVector object.
   */
  public Object clone() {
    return this.copy();
  }

  /**
   * Get the whole vector as an array
   *
   * @return vector v of length N
   */
  public double[] get() {
    double[] v = new double[N];
    for (int i = 0; i < nonZeroCount; i++) {
      v[index[i]] = value[i];
    }
    return v;
  }

  /**
   * Get a subvector as an array
   *
   * @return vector v of length (i1-i0+1)
   */
  public double[] getSubVector(int i0, int i1) {
    if (i1 < i0) {  // swap
      int temp = i1;
      i1 = i0;
      i0 = temp;
    }
    if (i0 < 0) {i0 = 0;}
    if (i1 >= N) {i1 = N - 1;}
    double[] v = new double[i1 - i0 + 1];
    for (int i = binarySearch(i0); i < nonZeroCount; i++) {
      if (index[i] > i1) {break;}
      v[index[i] - i0] = value[i];
    }
    return v;
  }

  /**
   * Get a vector element.
   *
   * @param n index number
   * @return vector value v[n]
   */
  public double get(int n) {
    int i = find(n);
    if (i < 0) {return 0.0;}
    return value[i];
  }

  /**
   * Access the non-zero values
   *
   * @return pointer to value array
   */
  public double[] getValues() {
    return value;
  }

  /**
   * Access the non-zero indices
   *
   * @return pointer to index array
   */
  public int[] getIndices() {
    return index;
  }

  /**
   * Set a vector element.
   *
   * @param n   index number
   * @param val the value to be put into the given entry of the dictionary.
   * @throws ArrayIndexOutOfBoundsException
   */
  public void set(int n, double val) {
    if ((n < 0) || (n >= N)) {
      throw new ArrayIndexOutOfBoundsException("Index out of range.");
    }
    int foundIndex = find(n);
    if (foundIndex < 0) {  // have to make new array
      int[] newIndex = new int[nonZeroCount + 1];
      double[] newValue = new double[nonZeroCount + 1];
      int j = 0; // index in newValue and newIndex
      for (int i = 0; i < nonZeroCount; i++, j++) {
        if (n < index[i]) {
          newIndex[j] = n;
          newValue[j] = val;
          j++;
        }
        newIndex[j] = index[i];
        newValue[j] = value[i];
      }
      nonZeroCount++;
      index = newIndex;
      value = newValue;
    } else {  // change an existing value
      value[foundIndex] = val;
    }
  }

  /**
   * Returns the lengt of the sparse vector
   */
  public int getN() {return N;}

  /**
   * Returns the lengt of the sparse vector
   */
  public int getLength() {return N;}

  /**
   * Returns number of non-zeros elements in sparse matrix.
   */
  public int getNonZeroCount() {return nonZeroCount;}

  /**
   * Add this sparse vector multiplied by a factor to the given array.
   *
   * @param factor a factor to multiply the column vector by.
   * @param x      an array
   */
  public void addToArray(double factor, double[] x) {
    for (int i = 0; i < nonZeroCount; i++) {
      if (index[i] > x.length) {break;}
      x[index[i]] += factor * value[i];
    }
  }

  // norms

  /**
   * The pseudo-zero-norm, do not count any zeros stored (which may happen)
   *
   * @return number of non-zero values
   */
  public int norm0() {
    int f = 0;
    for (int i = 0; i < nonZeroCount; i++) {
      if (value[i] != 0.0) {f++;}
    }
    return f;
  }

  /**
   * One norm
   *
   * @return sum of absulute values
   */
  public double norm1() {
    double f = 0;
    for (int i = 0; i < nonZeroCount; i++) {
      f += Math.abs(value[i]);
    }
    return f;
  }

  /**
   * Two norm
   *
   * @return sum of squared values
   */
  public double norm2() {
    double f = 0;
    for (int i = 0; i < nonZeroCount; i++) {
      f = Math.hypot(f, value[i]);
    }
    return f;
  }

  /**
   * Inf norm
   *
   * @return maximum absulute value
   */
  public double normInf() {
    double f = 0;
    for (int i = 0; i < nonZeroCount; i++) {
      f = Math.max(f, Math.abs(value[i]));
    }
    return f;
  }

  /**
   * innerProduct of this sparse vector to itself
   *
   * @return innerProduct
   */
  public double innerProduct() {
    double f = 0;
    for (int i = 0; i < nonZeroCount; i++) {
      f += value[i] * value[i];
    }
    return f;
  }

  /**
   * innerProduct of this and an array if lengths do not match, the shortest vector is extended with
   * zeros
   *
   * @return innerProduct
   */
  public double innerProduct(double[] v) {
    // take direct access to v
    double f = 0;
    for (int i = 0; i < nonZeroCount; i++) {
      int n = index[i];
      if (n >= v.length) {return f;}
      f += v[n] * value[i];
    }
    return f;
  }

  /**
   * innerProduct of this and another sparse vector if lengths do not match, the shortest vector is
   * extended with zeros
   *
   * @return innerProduct
   */
  public double innerProduct(SparseVector v) {
    // take direct access to v
    double[] vVal = v.getValues();
    int[] vInd = v.getIndices();
    int vNonZeroCount = v.getNonZeroCount();
    double f = 0;
    int j = 0;  // index in v
    for (int i = 0; i < nonZeroCount; i++) {
      int n = index[i];
      while (vInd[j] < n) {
        j++;
        if (j >= vNonZeroCount) {return f;}
      }
      if (vInd[j] == n) {
        f += vVal[j] * value[i];
      }
    }
    return f;
  }

  // print as in AllMatrices

  /**
   * Print the sparse vector to stdout.
   */
  public void print() {
    print(new PrintWriter(System.out, true), 10, 4);
  }

  /**
   * Print the sparse vector to stdout.  Fortran-like 'Fw.d' style format.
   *
   * @param w Column width.
   * @param d Number of digits after the decimal.
   */
  public void print(int w, int d) {
    print(new PrintWriter(System.out, true), w, d);
  }

  /**
   * Print the sparse vector to stdout.  Fortran-like 'Fw.d' style format.
   *
   * @param output Output stream.
   * @param w      Column width.
   * @param d      Number of digits after the decimal.
   */
  public void print(PrintWriter output, int w, int d) {
    DecimalFormat format = new DecimalFormat();
    format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
    format.setMinimumIntegerDigits(1);
    format.setMaximumFractionDigits(d);
    format.setMinimumFractionDigits(d);
    format.setGroupingUsed(false);
    print(output, format, w + 2);
  }

  /**
   * Print the sparse vector to stdout. Use the format object, and right justify within columns of
   * width characters. Note that if the sparse vector is to be read back in, you probably will want
   * to use a NumberFormat that is set to US Locale.
   *
   * @param format A  Formatting object for individual elements.
   * @param width  Field width for each column.
   * @see java.text.DecimalFormat#setDecimalFormatSymbols
   */
  public void print(NumberFormat format, int width) {
    print(new PrintWriter(System.out, true), format, width);
  }

  // DecimalFormat is a little disappointing coming from Fortran or C's printf.
  // Since it doesn't pad on the left, the elements will come out different
  // widths.  Consequently, we'll pass the desired column width in as an
  // argument and do the extra padding ourselves.

  /**
   * Print the sparse vector to stdout. Use the format object, and right justify within columns of
   * width characters. Note that if the sparse vector is to be read back in, you probably will want
   * to use a NumberFormat that is set to US Locale.
   *
   * @param output the output stream.
   * @param format A formatting object to format the matrix elements
   * @param width  Column width.
   * @see java.text.DecimalFormat#setDecimalFormatSymbols
   */
  public void print(PrintWriter output, NumberFormat format, int width) {
    output.println();  // start on new line.
    for (int i = 0; i < nonZeroCount; i++) {
      // String si = format.format(index[i]); // format the index no good
      String sv = format.format(value[i]); // format the number
      // int padding = Math.max(1,width-sv.length()); // At _least_ 1 space
      output.print("  ( ");
      output.print(index[i]);
      output.print(" ) = ");
      // for (int k = 0; k < padding; k++) output.print(' ');
      output.print(sv);
      // if ((i%4) == 3)
      output.print("\n");  // new line
    }
    output.println();   // end with blank line.
  }
}
