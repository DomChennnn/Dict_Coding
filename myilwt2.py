import numpy as np


def myilwt2(Y, ls, nivaa):
    """
     myilwt2         My variant of ilwt2(..), use as ilwt2 with Lifting
     Here mirror extension is done
    ----------------------------------------------------------------------
     :param Y     the coefficients, size MxN, both M and N should have
           2^level (typically 8, 16 or 32) as a factor.
     :param ls    cell array or a name as in liftwave (Matlab Wavelet Toolbox)
           the name may also be 'j97' to get the wavelet as used in JPEG-
           2000, M.D. Adams and R.Ward: Wavelet Transforms in the JPEG-2000
           Standard. (the obsolete name 'db97' is the same)
           Note that 'jp2' is not the same as '9.7' in liftwave.
           Also, the name 'm97' (better name could be 'e97' or 'ee97') let
           the energi levels of low-pass and high-pass be more equal to
           each other, (JPEG favours low-pass as it gives better visual
           reconstruction when compressing)
     level number of levels for the wavelet, typically 3, 4 or 5
     :return A     the reconstructed image (matrix of doubles), same size as Y.
    ----------------------------------------------------------------------
    """

    nivaa = nivaa.astype(int)
    M, N = Y.shape
    K = np.power(2, int(nivaa))

    # check arguments
    if type(Y) != np.ndarray:
        print("myilwt2: The supplied image Y is not numeric.")
        A = Y
        return
    if not ((np.mod(M, K) == 0) and (np.mod(N, K) == 0)):
        print(["myilwt2: Image size has not ", str(K), " as factor."])
        A = Y
        return

    if type(ls) == str:
        i = ls.find("79")
        if i == 1:
            ls = list(ls)
            ls[i : i + 2] = "97"
            ls = "".join(ls)
        i = ls.find("7.9")
        if i == 1:
            ls = list(ls)
            ls[i : i + 3] = "9.7"
            ls = "".join(ls)

        if ls == "j97" or ls == "db97" or ls == "m97":
            p1 = -1.586134342059924
            u1 = -0.052980118572961
            p2 = 0.882911075530934
            u2 = 0.443506852043971
            if ls == "m97":
                sc = 1.1496  # more equal energy in subbands
            else:
                sc = 1.230174104914001  # as in JPEG - 2000

            ls = [
                ["d", [p1, p1], 1],
                ["p", [u1, u1], 0],
                ["d", [p2, p2], 1],
                ["p", [u2, u2], 0],
                [sc, 1 / sc, []],
            ]

    sc1 = ls[4][0]
    sc2 = ls[4][1]

    # do transform inplace in Y
    for k in range(nivaa, 0, -1):

        if np.mod(N, np.power(2, k)) == 0:  # the rows
            all = np.array(range(1, M + 1, np.power(2, k - 1)))  # all rows at this level
            lpI = np.array(range(1, N + 1, np.power(2, k)))  # low-pass cols (elements in columns)
            hpI = lpI + (np.power(2, k - 1))
            Y[all.reshape(-1, 1) - 1, lpI.reshape(1, -1) - 1] = (
                Y[all.reshape(-1, 1) - 1, lpI.reshape(1, -1) - 1] / sc1
            )  # scaling low-pass
            Y[all.reshape(-1, 1) - 1, hpI.reshape(1, -1) - 1] = (
                Y[all.reshape(-1, 1) - 1, hpI.reshape(1, -1) - 1] / sc2
            )  # scaling low-pass
            for i in range(len(ls) - 1, -1, -1):
                if ls[i][0] == "d":  # dual, update hp
                    for j in range(len(ls[i][1]), 0, -1):
                        offset = 1 - j + ls[i][2]
                        if offset == 0:
                            I = lpI
                        elif offset > 0:
                            I = np.concatenate((lpI[offset:], lpI[-offset:]), axis=0)
                        elif offset < 0:
                            I = np.concatenate((lpI[0:-offset], lpI[0:offset]), axis=0)

                        Y[all.reshape(-1, 1) - 1, hpI.reshape(1, -1) - 1] = (
                            Y[all.reshape(-1, 1) - 1, hpI.reshape(1, -1) - 1]
                            - Y[all.reshape(-1, 1) - 1, I.reshape(1, -1) - 1] * ls[i][1][j - 1]
                        )

                if ls[i][0] == "p":  # dual, update hp
                    for j in range(len(ls[i][1]), 0, -1):
                        offset = 1 - j + ls[i][2]
                        if offset == 0:
                            I = hpI
                        elif offset > 0:
                            I = np.concatenate((hpI[offset:], hpI[-offset:]), axis=0)
                        elif offset < 0:
                            I = np.concatenate((hpI[0:-offset], hpI[0:offset]), axis=0)

                        Y[all.reshape(-1, 1) - 1, lpI.reshape(1, -1) - 1] = (
                            Y[all.reshape(-1, 1) - 1, lpI.reshape(1, -1) - 1]
                            - Y[all.reshape(-1, 1) - 1, I.reshape(1, -1) - 1] * ls[i][1][j - 1]
                        )

        if np.mod(M, np.power(2, k)) == 0:  # the columns
            all = np.array(range(1, N + 1, np.power(2, k - 1)))  # all columns at this level
            lpI = np.array(range(1, M + 1, np.power(2, k)))  # low-pass rows (elements in columns)
            hpI = lpI + (np.power(2, k - 1))
            Y[lpI.reshape(-1, 1) - 1, all.reshape(1, -1) - 1] = (
                Y[lpI.reshape(-1, 1) - 1, all.reshape(1, -1) - 1] / sc1
            )  # scaling low-pass
            Y[hpI.reshape(-1, 1) - 1, all.reshape(1, -1) - 1] = (
                Y[hpI.reshape(-1, 1) - 1, all.reshape(1, -1) - 1] / sc2
            )  # scaling high-pass
            for i in range(len(ls) - 1, -1, -1):
                if ls[i][0] == "d":  # dual, update hp
                    for j in range(len(ls[i][1]), 0, -1):
                        offset = 1 - j + ls[i][2]
                        if offset == 0:
                            I = lpI
                        elif offset > 0:
                            I = np.concatenate((lpI[offset:], lpI[-offset:]), axis=0)
                        elif offset < 0:
                            I = np.concatenate((lpI[0:-offset], lpI[0:offset]), axis=0)

                        Y[hpI.reshape(-1, 1) - 1, all.reshape(1, -1) - 1] = (
                            Y[hpI.reshape(-1, 1) - 1, all.reshape(1, -1) - 1]
                            - Y[I.reshape(-1, 1) - 1, all.reshape(1, -1) - 1] * ls[i][1][j - 1]
                        )

                if ls[i][0] == "p":  # dual, update hp
                    for j in range(len(ls[i][1]), 0, -1):
                        offset = 1 - j + ls[i][2]
                        if offset == 0:
                            I = hpI
                        elif offset > 0:
                            I = np.concatenate((hpI[offset:], hpI[-offset:]), axis=0)
                        elif offset < 0:
                            I = np.concatenate((hpI[0:-offset], hpI[0:offset]), axis=0)

                        Y[lpI.reshape(-1, 1) - 1, all.reshape(1, -1) - 1] = (
                            Y[lpI.reshape(-1, 1) - 1, all.reshape(1, -1) - 1]
                            - Y[I.reshape(-1, 1) - 1, all.reshape(1, -1) - 1] * ls[i][1][j - 1]
                        )

    return Y
