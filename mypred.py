import numpy as np


def mypred(inn, nofS=None, verbose=None):
    """
    mypred          Prediction in matrix (image) into cell array, or inverse.

    The sequences will be suited for entropy coding, see estimateBits.m
    If first argument is a cell array reshape is from cell array to matrix
    else first argument is matrix and reshape is from matrix to cell array.

    Used on images it works similar to CALIC but slower, a little bit larger
    neighborhood but simpler rules. 5 predictions may be used: p1 (left), p2
    (above), p3 (left above), p4 (right above) and (3*p1+2*p4)/5. Which to
    use is the one that was best for the four known neighbors. The sequences
    returned is divided based on estimated standard deviation.

    use:
    xC = mypred(X);
    Xr = mypred(xC);       decoding only need xC
    ------------------------------------------------------------------------
    :param xC     cell array of integer sequences
    :param X      MxN matrix of integers, (an image)
    :param 'nofS' is number of sequences to split X into. Since first sequence in
         xC is side information, the number of sequences in xC will be
         one more, (nofS+1). Default: nofS = 3.
    :param 'v' or 'verbose' to indicate verboseness, default 0
    ------------------------------------------------------------------------

    """
    M = 0
    N = 0
    w = (np.array([3, 3, 2, 2])).T

    if nofS is None:
        nofS = 3
    else:
        nofS = max(1, np.floor(abs(nofS)))

    if verbose is None:
        verbose = 0
    else:
        if type(verbose) == bool:
            verbose = 1
        else:
            verbose = verbose

    vLimRange = 4000

    if type(inn) == np.ndarray:
        # start encoding
        # X = np.float64(inn)  # make sure we can calculate on X
        X = np.array(inn)
        del inn
        if ((M != 0) and (M != X.shape[0])) or ((N != 0) and (N != X.shape[1])):
            print(
                "Actual size of A overrule input given by " "size" " or " "M" " option"
            )

        M, N = X.shape
        if np.max(X) - np.min(X) == 0:
            xrange = 0.1
        else:
            xrange = np.power(2, np.ceil(np.log2(np.max(X) - np.min(X))))
        if verbose:
            print(
                [
                    "mypred: from ",
                    str(M),
                    "x",
                    str(N),
                    " matrix/image (xrange = ",
                    str(xrange),
                    ", min = ",
                    str(min(X)),
                    ", max = ",
                    str(max(X)),
                    ") to ",
                    str(nofS),
                    " sequences in cell array.",
                ]
            )
        #
        if ((M < 6) or (N < 6)) or ((M * N) < 100):
            # may be extended in future versions
            if verbose:
                print("mypred: just use DPCM here.")

            X[:, 1::2] = np.flipud(X[:, 1::2])
            ut = [
                [M, np.log2(xrange), 1],
                (
                    np.insert(
                        np.diff(X.reshape((-1, 1), order="F"), axis=0),
                        0,
                        X[0][0],
                        axis=0,
                    )
                ).tolist(),
            ]
            return ut

        Y = np.zeros((M, N), np.int16)
        V = np.zeros((M, N), np.float32)
        i = 0  # first row
        j = 0
        v = xrange / 8
        V[i, j] = v
        Y[i, j] = 0
        j = 1
        v = xrange / 16
        V[i, j] = v
        Y[i, j] = X[i, j - 1]
        j = 2
        v = (xrange / 16 + abs(X[i, 1] - X[i, 0])) / 2
        V[i, j] = v
        Y[i, j] = X[i, j - 1]
        j = 3
        v = (xrange / 16 + sum(abs(X[i, 1:3] - X[i, 0:2]))) / 3
        V[i, j] = v
        Y[i, j] = X[i, j - 1]
        for j in range(4, N):
            v = sum(abs(X[i, (j - 3) : j] - X[i, (j - 4) : (j - 1)])) / 3
            V[i, j] = v
            Y[i, j] = X[i, j - 1]

        i = 1  # second row
        j = 0
        v = xrange / 16
        V[i, j] = v
        Y[i, j] = X[i - 1, j]
        j = 1
        # ***** Block, for i=2,j=2, 2 lines identical in encode/decode ********
        x1 = X[i, j - 1]
        x2 = X[i - 1, j]
        x3 = X[i - 1, j - 1]  # *
        d1 = abs(x2 - x3)
        d2 = abs(x1 - x3)
        v = (d1 + d2) / 2  # *
        # *********************************************************************
        V[i, j] = v
        if d1 <= d2:
            Y[i, j] = x1
        else:
            Y[i, j] = x2
        for j in range(2, N):
            # ***** Block, for i=2,j>2, 5 lines identical in encode/decode ****
            x1 = X[i, j - 1]
            x2 = X[i - 1, j]
            x3 = X[i - 1, j - 1]  # *
            x5 = X[i, j - 2]
            x6 = X[i - 1, j - 2]  # *
            d1 = (abs(x2 - x3) + abs(x5 - x1)) / 2  # *
            d2 = (3 * abs(x1 - x3) + 2 * abs(x6 - x5)) / 5  # *
            v = (d1 + d2) / 2  # *
            # *****************************************************************
            V[i, j] = v
            if d1 <= d2:
                Y[i, j] = x1
            else:
                Y[i, j] = x2

        for i in range(2, M):
            j = 0
            if i == 2:
                v = (xrange / 16 + abs(X[1, j] - X[0, j])) / 2
                V[i, j] = v
                Y[i, j] = X[i - 1, j]

            if i == 3:
                v = (xrange / 16 + sum(abs(X[1:3, [j]] - X[0:2, [j]]))) / 3
                V[i, j] = v
                Y[i, j] = X[i - 1, j]

            if i == 4:
                v = sum(abs(X[(i - 1) : (i - 4), j] - X[(i - 2) : 0, j])) / 3
                V[i, j] = v
                Y[i, j] = X[i - 1, j]

            if i > 4:
                v = sum(abs(X[(i - 1) : (i - 4), j] - X[(i - 2) : (i - 5), j])) / 3
                V[i, j] = v
                Y[i, j] = X[i - 1, j]

            j = 1
            # ***** Block, for i>2,j=2, 5 lines identical in encode/decode ****
            x1 = X[i, j - 1]
            x2 = X[i - 1, j]
            x3 = X[i - 1, j - 1]  # *
            x8 = X[i - 2, j - 1]
            x9 = X[i - 2, j]  # *
            d1 = (3 * abs(x2 - x3) + 2 * abs(x8 - x9)) / 5  # *
            d2 = (abs(x1 - x3) + abs(x2 - x9)) / 2  # *
            v = (d1 + d2) / 2  # *
            # *****************************************************************
            V[i, j] = v
            if d1 <= d2:
                Y[i, j] = x1
            else:
                Y[i, j] = x2
            #
            for j in range(2, N):
                if i == 7 and j == 20:
                    a = 10
                # *** Block, for i>2,j>2, lines identical in encode/decode ****
                x7 = X[i - 2, j - 2]
                x8 = X[i - 2, j - 1]
                x9 = X[i - 2, j]  # *
                x6 = X[i - 1, j - 2]
                x3 = X[i - 1, j - 1]
                x2 = X[i - 1, j]  # *
                x5 = X[i, j - 2]
                x1 = X[i, j - 1]  # X(i,j)                      # *
                if j == N - 1:
                    x4 = x3
                    x10 = x8  # *
                else:
                    x4 = X[i - 1, j + 1]
                    x10 = X[i - 2, j + 1]  # *
                if (j + 1) >= N - 1:
                    x11 = x2
                else:
                    x11 = X[i - 2, j + 2]  # *
                d = abs(
                    np.ones((5, 1)) * [x1, x2, x3, x4]
                    - [  # *
                        [x5, x3, x6, x2],
                        [x3, x9, x8, x10],  # *
                        [x6, x8, x7, x9],
                        [x2, x10, x9, x11],  # *
                        [
                            (3 * x5 + 2 * x2) / 5,
                            (3 * x3 + 2 * x10) / 5,  # *
                            (3 * x6 + 2 * x9) / 5,
                            (3 * x2 + 2 * x11) / 5,
                        ],
                    ]
                )  # *
                lib_search = np.round(np.dot(d, w), 8)
                temp, pNo = np.min(lib_search), np.argmin(lib_search)  # *

                # if (lib_search[0] ==lib_search[4]) and lib_search[0] == temp:
                #     pNo = 4
                # # if len(np.unique(lib_search)) != len(lib_search):
                # #    print(lib_search)
                # #    print('('+str(i)+','+str(j)+')')

                v = np.round(np.mean(d[pNo, :]), 8)  # *
                # *************************************************************
                V[i, j] = v
                if pNo == 0:
                    Y[i, j] = x1
                if pNo == 1:
                    Y[i, j] = x2
                if pNo == 2:
                    Y[i, j] = x3
                if pNo == 3:
                    Y[i, j] = x4
                if pNo == 4:
                    Y[i, j] = np.floor((3 * x1 + 2 * x2) / 5 + 0.45)

        xC = []
        for i in range((nofS + 1).astype(int)):
            xC.append([])
        # limits to store
        t = np.sort(V.reshape((-1, 1)), axis=0)
        vLim = np.ceil(
            t[
                (
                    np.floor(np.array(range(1, (nofS).astype(int))) * ((M * N) / nofS))
                ).astype(int)
                - 1
            ]
            * vLimRange
            / xrange
        ).T
        vLim = vLim.astype(int)
        xC[0] = [M, np.int(np.log2(xrange)), np.int(nofS), vLim]
        vLim_copy = vLim.copy()

        # # limits to use
        vLim = xC[0][3] * xrange / vLimRange
        vLim = np.append(vLim, np.inf)
        Sekv = np.zeros((M, N))
        V = np.float16(np.round(V, 8))
        for i in range(M):
            for j in range(N):
                Sekv[i, j] = np.where(V[i, j] <= vLim)[0][0]

        # # make Y prediction error (and transpose it to get elements orderd by row)
        Y = X - Y
        # # sequence also transposed
        for i in range(1, (nofS + 1).astype(int)):
            xC[i] = Y[Sekv == (i - 1)]
            # xC{i} = makePositive( Y(Sekv==(i-1)) )
            if verbose:
                print(
                    [
                        "Seq. ",
                        str(i),
                        " has ",
                        str(len(xC[i])),
                        " elements.",
                        "  min = ",
                        str(min(xC[i])),
                        "  max = ",
                        str(max(xC[i])),
                        "  mean = ",
                        str(np.mean(xC[i])),
                        "  std = ",
                        str(np.std(xC[i])),
                    ]
                )

        xC[0].pop()
        for i in range(len(vLim_copy[0])):
            xC[0].append((vLim_copy[0][i]).astype(int))

        ut = xC
    elif type(inn) == list:
        #     start decoding
        xC = inn
        del inn
        # get side info
        M = xC[0][0]
        xrange = np.power(2, xC[0][1])
        nofS = int(xC[0][2])
        if (nofS + 1) != len(xC):
            raise IndexError(
                "mypred: Error decoding xC, nofS+1 is not equal to number of sequences in xC"
            )

        nofElements = 0
        for i in range(1, nofS + 1):
            nofElements = nofElements + len(xC[i])

        N = int(np.floor(nofElements / M))
        if (N * M) != nofElements:
            raise IndexError("mypred: ERROR decoding xC, (N*M) ~= nofElements in xC.")

        if verbose:
            print(
                [
                    "mypred: decoding from ",
                    str(nofS),
                    " sequences in xC into ",
                    str(M),
                    "x",
                    str(N),
                    " matrix/image (range = ",
                    str(range),
                    ")",
                ]
            )

        if ((M < 6) or (N < 6)) or ((M * N) < 100):
            # may be extended in future versions
            if verbose:
                print("mypred: just use DPCM here.")
            X = np.array(xC[1])
            for i in range(1, M * N):
                X[i] = X[i] + X[i - 1]
            X = X.reshape((M, N), order="F")
            X[:, 1::2] = np.flipud(X[:, 1::2])
            ut = X
            return ut

        # The 'normal' decoding
        vLim_temp = [
            np.array(xC[0][3:]).reshape(1, int((len(xC[0]) - 3))) * xrange / vLimRange
        ]
        vLim = []
        for i in range(len(vLim_temp[0][0])):
            vLim.append(vLim_temp[0][0, i])
        vLim.append(np.inf)

        X = np.zeros((M, N))  # restored matrix/image (to be filled by rows!)
        xCi = np.zeros((nofS + 1, 1))
        # for i=2:(nofS+1)       xC{i} = unmakePositive( xC{i} )     end
        #
        i = 0  # first row
        j = 0
        v = xrange / 8
        Sekv = np.where(v <= vLim)[0][0] + 1
        xCi[Sekv] = xCi[Sekv] + 1
        X[i, j] = xC[Sekv][int(xCi[Sekv]) - 1]
        j = 1
        v = xrange / 16
        Sekv = np.where(v <= vLim)[0][0] + 1
        xCi[Sekv] = xCi[Sekv] + 1
        X[i, j] = X[i, j - 1] + xC[Sekv][int(xCi[Sekv]) - 1]
        j = 2
        v = (xrange / 16 + abs(X[i, 1] - X[i, 0])) / 2
        Sekv = np.where(v <= vLim)[0][0] + 1
        xCi[Sekv] = xCi[Sekv] + 1
        X[i, j] = X[i, j - 1] + xC[Sekv][int(xCi[Sekv]) - 1]
        j = 3
        v = (xrange / 16 + sum(abs(X[i, 1:3] - X[i, 0:2]))) / 3
        Sekv = np.where(v <= vLim)[0][0] + 1
        xCi[Sekv] = xCi[Sekv] + 1
        X[i, j] = X[i, j - 1] + xC[Sekv][int(xCi[Sekv]) - 1]
        for j in range(4, N):
            v = sum(abs(X[i, (j - 3) : (j)] - X[i, (j - 4) : (j - 1)])) / 3
            Sekv = np.where(v <= vLim)[0][0] + 1
            xCi[Sekv] = xCi[Sekv] + 1
            X[i, j] = X[i, j - 1] + xC[Sekv][int(xCi[Sekv]) - 1]
        #

        i = 1
        j = 0
        v = xrange / 16
        Sekv = np.where(v <= vLim)[0][0] + 1
        xCi[Sekv] = xCi[Sekv] + 1
        X[i, j] = X[i - 1, j] + xC[Sekv][int(xCi[Sekv]) - 1]
        j = 1
        # ***** Block, for i=2,j=2, 2 lines identical in encode/decode ********
        x1 = X[i, j - 1]
        x2 = X[i - 1, j]
        x3 = X[i - 1, j - 1]  # *
        d1 = abs(x2 - x3)
        d2 = abs(x1 - x3)
        v = (d1 + d2) / 2  # *
        # *********************************************************************
        Sekv = np.where(v <= vLim)[0][0] + 1
        xCi[Sekv] = xCi[Sekv] + 1
        p = xC[Sekv][int(xCi[Sekv]) - 1]
        if d1 <= d2:
            X[i, j] = x1 + p
        else:
            X[i, j] = x2 + p
        for j in range(2, N):
            # ***** Block, for i=2,j>2, 5 lines identical in encode/decode ****
            x1 = X[i, j - 1]
            x2 = X[i - 1, j]
            x3 = X[i - 1, j - 1]  # *
            x5 = X[i, j - 2]
            x6 = X[i - 1, j - 2]  # *
            d1 = (abs(x2 - x3) + abs(x5 - x1)) / 2  # *
            d2 = (3 * abs(x1 - x3) + 2 * abs(x6 - x5)) / 5  # *
            v = (d1 + d2) / 2  # *
            # *****************************************************************
            Sekv = np.where(v <= vLim)[0][0] + 1
            xCi[Sekv] = xCi[Sekv] + 1
            p = xC[Sekv][int(xCi[Sekv]) - 1]
            if d1 <= d2:
                X[i, j] = x1 + p
            else:
                X[i, j] = x2 + p
            #
        vLim = np.array(vLim)
        for i in range(2, M):
            j = 0
            if i == 2:
                v = (xrange / 16 + abs(X[1, j] - X[0, j])) / 2
                Sekv = np.where(v <= vLim)[0][0] + 1
                xCi[Sekv] = xCi[Sekv] + 1
                X[i, j] = X[i - 1, j] + xC[Sekv][int(xCi[Sekv]) - 1]

            if i == 3:
                v = (xrange / 16 + sum(abs(X[1:3, j] - X[0:2, j]))) / 3
                Sekv = np.where(v <= vLim)[0][0] + 1
                xCi[Sekv] = xCi[Sekv] + 1
                X[i, j] = X[i - 1, j] + xC[Sekv][int(xCi[Sekv]) - 1]

            if i == 4:
                v = sum(abs(X[(i - 1) : (i - 4), j] - X[(i - 2) : 0, j])) / 3
                Sekv = np.where(v <= vLim)[0][0] + 1
                xCi[Sekv] = xCi[Sekv] + 1
                X[i, j] = X[i - 1, j] + xC[Sekv][int(xCi[Sekv]) - 1]

            if i > 4:
                v = sum(abs(X[(i - 1) : (i - 4), j] - X[(i - 2) : (i - 5), j])) / 3
                Sekv = np.where(v <= vLim)[0][0] + 1
                xCi[Sekv] = xCi[Sekv] + 1
                X[i, j] = X[i - 1, j] + xC[Sekv][int(xCi[Sekv]) - 1]

            j = 1
            # ***** Block, for i>2,j=2, 5 lines identical in encode/decode ****
            x1 = X[i, j - 1]
            x2 = X[i - 1, j]
            x3 = X[i - 1, j - 1]  # *
            x8 = X[i - 2, j - 1]
            x9 = X[i - 2, j]  # *
            d1 = (3 * abs(x2 - x3) + 2 * abs(x8 - x9)) / 5  # *
            d2 = (abs(x1 - x3) + abs(x2 - x9)) / 2  # *
            v = (d1 + d2) / 2  # *
            # *****************************************************************
            Sekv = np.where(v <= vLim)[0][0] + 1
            xCi[Sekv] = xCi[Sekv] + 1
            p = xC[Sekv][int(xCi[Sekv] - 1)]
            if d1 <= d2:
                X[i, j] = x1 + p
            else:
                X[i, j] = x2 + p

            for j in range(2, N):  # column 3 and more
                if i == 7 and j == 19:
                    a = 10
                # *** Block, for i>2,j>2, lines identical in encode/decode ****
                x7 = X[i - 2, j - 2]
                x8 = X[i - 2, j - 1]
                x9 = X[i - 2, j]  # *
                x6 = X[i - 1, j - 2]
                x3 = X[i - 1, j - 1]
                x2 = X[i - 1, j]  # *
                x5 = X[i, j - 2]
                x1 = X[i, j - 1]  # X(i,j)                      # *
                if j == N - 1:
                    x4 = x3
                    x10 = x8  # *
                else:
                    x4 = X[i - 1, j + 1]
                    x10 = X[i - 2, j + 1]  # *
                if (j + 1) >= N - 1:
                    x11 = x2
                else:
                    x11 = X[i - 2, j + 2]  # *

                d = abs(
                    np.ones((5, 1)) * [x1, x2, x3, x4]
                    - [  # *
                        [x5, x3, x6, x2],
                        [x3, x9, x8, x10],  # *
                        [x6, x8, x7, x9],
                        [x2, x10, x9, x11],  # *
                        [
                            (3 * x5 + 2 * x2) / 5,
                            (3 * x3 + 2 * x10) / 5,  # *
                            (3 * x6 + 2 * x9) / 5,
                            (3 * x2 + 2 * x11) / 5,
                        ],
                    ]
                )  # *
                lib_search = np.round(np.dot(d, w), 8)
                temp, pNo = np.min(lib_search), np.argmin(lib_search)  # *
                v = np.round(np.mean(d[pNo, :]), 8)  # *
                # *************************************************************
                Sekv = np.where(v <= vLim)[0][0] + 1
                xCi[Sekv] = xCi[Sekv] + 1

                # print(Sekv)
                # print(int(xCi[Sekv]) - 1)
                # print(str(i)+','+str(j))
                p = xC[Sekv][int(xCi[Sekv]) - 1]

                if pNo == 0:
                    X[i, j] = x1 + p
                if pNo == 1:
                    X[i, j] = x2 + p
                if pNo == 2:
                    X[i, j] = x3 + p
                if pNo == 3:
                    X[i, j] = x4 + p
                if pNo == 4:
                    X[i, j] = np.floor((3 * x1 + 2 * x2) / 5 + 0.45) + p
        ut = X

    else:
        raise IndexError("mypred: illegal data type")

    return ut
