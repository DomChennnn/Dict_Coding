import numpy as np


def uniquant(X, delta, thr, y_max=None):
    """

    uniquant    Uniform scalar quantizer (or inverse quantizer) with threshold
    Note: Use three arguments for inverse quantizing and
       four arguments for quantizing.

    Y = uniquant(X, del, thr, ymax);      quantizer
    X = uniquant(Y, del, thr);            inverse quantizer
    ----------------------------------------------
    arguments:
    :param X    - the values to be quantized (or result after inverse
          quantizer), a vector or matrix with real values.
    :param delta  - delta i quantizer, size/width of all cells except zero-cell
    :param thr  - threshold value, width of zero cell is from -thr to +thr
    :param y_max - largest value for y, only used when quantizing
    :return Y    - the indexes for the quantizer cells, the bins are indexed as
      ..., -3, -2, -1, 0, 1, 2, 3, ...  where 0 is for the zero bin
    ----------------------------------------------
    """
    S = np.sign(X)
    X = abs(X)

    if y_max is not None:
        Y = np.floor((X - thr) / delta) + 1
        if thr > delta:
            Y[Y < 0] = 0
        y_max = np.floor(y_max)
        Y[Y > y_max] = y_max

    if y_max is None:
        Y = np.zeros_like(X)
        I = np.nonzero(X)
        Y[I] = X[I] * delta + (thr - delta / 2)

    res = np.array(Y * S, np.int)
    return res
