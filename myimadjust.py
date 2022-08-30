import numpy as np


def myimadjust(A, met=None, fac=None):
    """

    myimadjust      Trim or extend image, height and width have given factor.

    If image already match the factors, the image size is not changed.
    -------------------------------------------------------------------------
    arguments:
    :param A           an image, (returned as double) and possible larger
               output than input
    :param method      the method to use: 'none' to cut off
               'mirror', 'zeros', 'periodic', 'extend' repeat last
    :param fac         the factor that height and width may be divided by
               it may be a single number or two elements, the first is
               used on the height and the second on the width
    :return adjusted img
    """
    if met != None:
        if not (
            (met == None)
            or (met == "extend")
            or (met == "zeros")
            or (met == "periodic")
            or (met == "mirror")
        ):
            print("Illegal method given", met, "changed to none")
            met = None

    if len(fac) == 1:
        fac = [fac, fac]

    if len(A.shape) == 2:
        M, N = A.shape
        L = 1
    else:
        M, N, L = A.shape

    A = np.array(A, np.double)
    addRows = np.mod((fac[0] - np.mod(M, fac[0])), fac[0])
    addCols = np.mod((fac[1] - np.mod(N, fac[1])), fac[1])

    if (addRows > 0) or (addCols > 0):  # adjust is needed
        if L == 1:  # simpler operations can be used
            if met == None:
                A = A[0 : (M - np.mod(M, fac[0])), 0 : (N - np.mod(N, fac[1]))]
            elif met == "extend":
                if addRows > 0:
                    A = np.concatenate(
                        (A, np.multiply(np.ones((addRows, 1)), A[[-1], :])), axis=0
                    )
                if addCols > 0:
                    A = np.concatenate(
                        (A, np.multiply(A[:, [-1]], np.ones((1, addCols)))), axis=1
                    )
            elif met == "periodic":
                if addRows > 0:
                    A = np.concatenate((A, A[0:addRows, :]), axis=0)
                if addCols > 0:
                    A = np.concatenate((A, A[:, 0:addCols]), axis=1)
            elif met == "mirror":
                if addRows > 0:
                    A = np.concatenate((A, A[(M - addRows) : M, :]), axis=0)
                if addCols > 0:
                    A = np.concatenate((A, A[:, (N - addCols) : N]), axis=1)
            elif met == "zeros":
                if addRows > 0:
                    A = np.concatenate((A, np.zeros((addRows, N))), axis=0)
                if addCols > 0:
                    A = np.concatenate((A, np.zeros((M + addRows, addCols))), axis=1)
        else:
            print("not ready for serveal layers, it will be added later")
    return A
