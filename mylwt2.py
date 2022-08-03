import numpy as np

def mylwt2(X, ls, nivaa):
    '''

    mylwt2          My variant of lwt2, use as lwt2 with Lifting
    ----------------------------------------------------------------------
    :param A     the image (matrix of doubles), size MxN, both M and N should have
    2^level (typically 8, 16 or 32) as a factor. If not myimadjust.m
    may be used to extend the image.
    :param ls    cell array or a name as in liftwave
                the name may also be 'j97' to get the wavelet as used in JPEG-
                2000, M.D. Adams and R.Ward: Wavelet Transforms in the JPEG-2000
                Standard.
    level number of levels for the wavelet, typically 3, 4 or 5
    :return Y     the coefficients, same size as X
    ----------------------------------------------------------------------
    '''
    nivaa = nivaa.astype(int)
    M,N = X.shape
    K = np.power(2,int(nivaa))
    Y = X

    # check arguments
    if type(X) != np.ndarray:
        print('mylwt2: The supplied image A is not numeric.')
        return
    if not ((np.mod(M, K) == 0) and (np.mod(N, K) == 0)):
        print(['mylwt2: Image size has not ', str(K), ' as factor.'])
        return

    if type(ls) == str:
        i = ls.find('79')
        if i == 1:
            ls = list(ls)
            ls[i:i+2] = '97'
            ls = ''.join(ls)
        i = ls.find('7.9')
        if i == 1:
            ls = list(ls)
            ls[i:i + 3] = '9.7'
            ls = ''.join(ls)

        if ls == 'j97' or ls == 'db97' or ls == 'm97':
            p1 = -1.586134342059924
            u1 = -0.052980118572961
            p2 = 0.882911075530934
            u2 = 0.443506852043971
            if ls == 'm97':
                sc = 1.1496 # more equal energy in subbands
            else:
                sc = 1.230174104914001 # as in JPEG - 2000

            ls = [['d',[p1,p1],1],
                  ['p',[u1,u1],0],
                  ['d',[p2,p2],1],
                  ['p',[u2,u2],0],
                  [sc,1/sc,[]],]

    sc1 = ls[4][0]
    sc2 = ls[4][1]

    #do transform inplace in Y
    for k in range(1, nivaa + 1):
        if (np.mod(M, np.power(2,k)) == 0): # the columns
            all = np.array(range(1, N + 1, np.power(2,k-1)))   # all columns at this level
            lpI = np.array(range(1, M + 1, np.power(2,k)))      # low-pass rows (elements in columns)
            hpI = lpI + (np.power(2,k-1))
            for i in range(len(ls)-1):
                if (ls[i][0] == 'd'):  # dual, update hp
                    for j in range(1, 1 + len(ls[i][1])):
                        offset = 1 -j + ls[i][2]
                        if (offset == 0):
                            I = lpI
                        elif offset > 0:
                            I = np.concatenate((lpI[offset:],lpI[-offset:]), axis=0)
                        elif offset < 0:
                            I = np.concatenate((lpI[0:-offset],lpI[0:offset]), axis=0)

                        Y[hpI.reshape(-1,1)-1,all.reshape(1,-1)-1] =  Y[hpI.reshape(-1,1)-1,all.reshape(1,-1)-1] +  Y[I.reshape(-1,1)-1,all.reshape(1,-1)-1] * ls[i][1][j-1]

                if (ls[i][0] == 'p'):  # dual, update hp
                    for j in range(1, 1 + len(ls[i][1])):
                        offset = 1 - j + ls[i][2]
                        if (offset == 0):
                            I = hpI
                        elif offset > 0:
                            I = np.concatenate((hpI[offset:], hpI[-offset:]), axis=0)
                        elif offset < 0:
                            I = np.concatenate((hpI[0:-offset], hpI[0:offset]), axis=0)

                        Y[lpI.reshape(-1,1) - 1, all.reshape(1,-1) - 1] = Y[lpI.reshape(-1,1) - 1, all.reshape(1,-1) - 1] + Y[I.reshape(-1,1) - 1, all.reshape(1,-1) - 1] * ls[i][1][j - 1]

            Y[lpI.reshape(-1,1)-1, all.reshape(1,-1)-1] = Y[lpI.reshape(-1,1)-1, all.reshape(1,-1)-1]*sc1  # scaling low-pass
            Y[hpI.reshape(-1,1)-1, all.reshape(1,-1)-1] = Y[hpI.reshape(-1,1)-1, all.reshape(1,-1)-1]*sc2  # scaling high-pass

        if (np.mod(N, np.power(2, k)) == 0):  # the rows
            all = np.array(range(1, M + 1, np.power(2, k - 1)))  # all rows at this level
            lpI = np.array(range(1, N + 1, np.power(2, k)))  # low-pass cols (elements in columns)
            hpI = lpI + (np.power(2, k - 1))
            for i in range(len(ls) - 1):
                if (ls[i][0] == 'd'):  # dual, update hp
                    for j in range(1, 1 + len(ls[i][1])):
                        offset = 1 - j + ls[i][2]
                        if (offset == 0):
                            I = lpI
                        elif offset > 0:
                            I = np.concatenate((lpI[offset:], lpI[-offset:]), axis=0)
                        elif offset < 0:
                            I = np.concatenate((lpI[0:-offset], lpI[0:offset]), axis=0)

                        Y[all.reshape(-1,1) - 1, hpI.reshape(1,-1) - 1] = Y[all.reshape(-1,1) - 1, hpI.reshape(1,-1) - 1] + Y[all.reshape(-1,1) - 1, I.reshape(1,-1) - 1] * ls[i][1][j - 1]

                if (ls[i][0] == 'p'):  # dual, update hp
                    for j in range(1, 1 + len(ls[i][1])):
                        offset = 1 - j + ls[i][2]
                        if (offset == 0):
                            I = hpI
                        elif offset > 0:
                            I = np.concatenate((hpI[offset:], hpI[-offset:]), axis=0)
                        elif offset < 0:
                            I = np.concatenate((hpI[0:-offset], hpI[0:offset]), axis=0)

                        Y[all.reshape(-1,1) - 1, lpI.reshape(1,-1) - 1] = Y[all.reshape(-1,1) - 1, lpI.reshape(1,-1) - 1] + Y[all.reshape(-1,1) - 1, I.reshape(1,-1) - 1] * ls[i][1][j - 1]

            Y[all.reshape(-1,1) - 1, lpI.reshape(1,-1) - 1] = Y[all.reshape(-1,1) - 1, lpI.reshape(1,-1) - 1] * sc1  # scaling low-pass
            Y[all.reshape(-1,1) - 1, hpI.reshape(1,-1) - 1] = Y[all.reshape(-1,1) - 1, hpI.reshape(1,-1) - 1] * sc2  # scaling low-pass

    return Y

