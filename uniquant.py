import numpy as np



def uniquant(X, dele, thr, ymax = None):
    '''
    
    uniquant    Uniform scalar quantizer (or inverse quantizer) with threshold
    Note: Use three arguments for inverse quantizing and
       four arguments for quantizing.

    Y = uniquant(X, del, thr, ymax);      quantizer
    X = uniquant(Y, del, thr);            inverse quantizer
    ----------------------------------------------
    arguments:
    :param X    - the values to be quantized (or result after inverse
          quantizer), a vector or matrix with real values.
    :param del  - delta i quantizer, size/width of all cells except zero-cell
    :param thr  - threshold value, width of zero cell is from -thr to +thr
    :param ymax - largest value for y, only used when quantizing
    :return Y    - the indexes for the quantizer cells, the bins are indexed as
      ..., -3, -2, -1, 0, 1, 2, 3, ...  where 0 is for the zero bin
    ----------------------------------------------
    '''
    S = np.sign(X)
    X = abs(X)

    if ymax != None:
        Y = np.floor((X - thr)/dele) + 1
        if thr > dele:
            Y[Y<0] = 0
        ymax = np.floor(ymax)
        Y[Y>ymax] = ymax
    elif ymax == None:
        Y = np.zeros_like(X)
        I = np.nonzero(X)
        Y[I] = X[I] * dele + (thr - dele/2)
    else:
        print('uniquant error')
    Y = Y * S
    return Y
