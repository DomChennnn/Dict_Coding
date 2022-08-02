import numpy as np

def uniquant(X, dele, thr, ymax = None):
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
