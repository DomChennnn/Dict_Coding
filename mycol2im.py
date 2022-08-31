import numpy as np
import primefac

from myilwt2 import myilwt2


def mycol2im(
    X, imsize=None, transform=None, size=None, neighborhood=None, increment=None
):
    """
    mycol2im     Rearrange matrix columns into blocks.
    The 'inverse' is mycol2im.m
    ----------------------------------------------------------------------
    :param X     a matrix
    There may be an additional number of input arguments, a struct, a cell
        or as pairs: argName, argVal, ...
        These are mostly as in myim2col. Note imsize
    :param imsize to give size of buildt image, [M,N]. Note this is not
        necessarily equal to original size of image. With L = size(X,2) we
        should have: L = ((M-Mn+Mi)/Mi)*((N-Nn+Ni)/Ni)
    :param transform  as in myim2col
    :param size as in myim2col
    :param neighborhood as in myim2col, [Mn,Nn] = size(nei);
    :param increment as in myim2col, [Mi, Ni]
    :return img
    ----------------------------------------------------------------------
    """

    # default options
    M = 0
    N = 0  # size of reconstructed image
    tr = None  # transform
    Ms = 0
    Ns = 0  # size(of transform)
    nei = []  # neighborhood, input or given by tr
    Mn = 0
    Nn = 0  # size of nei(neighborhood)
    Mi = 0
    Ni = 0  # increment

    # get the options
    if imsize != None:
        if len(imsize) == 1:
            M = max(1, np.floor(imsize).astype(int))
            N = M
        elif len(imsize) == 2:
            M = max(0, np.floor(imsize[0]).astype(int))
            N = max(0, np.floor(imsize[1]).astype(int))
        else:
            raise IndexError("mycol2im: illegal option imsize. We ignore it.")

    if transform != None:
        tr = transform

    if size != None:
        if len(size) == 1:
            Ms = max(0, np.floor(size).astype(int))
            Ns = Ms
        elif len(size) == 2:
            Ms = max(0, np.floor(size[0]).astype(int))
            Ns = max(0, np.floor(size[1]).astype(int))
        else:
            raise IndexError("mycol2im: illegal option size, we ignore it")

    if neighborhood != None:
        if len(neighborhood) <= 2:
            nei = np.ones(
                ((neighborhood[0]).astype(int), (neighborhood[1]).astype(int))
            )
            Mn, Nn = nei.shape
        elif len(neighborhood > 2):
            nei = neighborhood
            Mn, Nn = nei.shape
        else:
            raise IndexError("mycol2im: illegal option neighborhood, we ignore it")

    if increment != None:
        if len(increment) == 1:
            Mi = max(0, np.floor(increment).astype(int))
            Ni = Mi
        elif len(increment) == 2:
            Mi = max(0, np.floor(increment[0]).astype(int))
            Ni = max(0, np.floor(increment[1]).astype(int))
        elif increment == "distinct":
            Mi = 0
            Ni = 0
        elif increment == "sliding":
            Mi = 1
            Ni = 1
        else:
            raise IndexError("mycol2im: illegal option increment. We ignore it.")

    # check and display options
    if (Ms == 0) or (Ns == 0):
        if tr == None:
            Ms = 4
            Ns = 4
        else:
            Ms = 8
            Ns = 8

    if tr == "lot" or tr == "elt":
        Ms = Ms - np.mod(Ms, 2)
        Ns = Ns - np.mod(Ns, 2)

    if (
        (not tr == None)
        and (not tr == "lot")
        and (not tr == "elt")
        and (not tr == "dct")
    ):
        Ms = max(2, np.power(2, (np.floor(np.log2(Ms))).astype(int)))
        Ns = Ms

    if type(tr) is np.ndarray:  # transform is given as a np matrix
        Ms = tr.shape[1]
        Ns = Ms
        P = np.floor(tr.shape[0] / Ms)

        if Ms * P != tr.shape[0]:
            tr = tr[0 : Ms * P]

    if (Mn == 0) or (Nn == 0):
        nei = np.ones((Ms, Ns))
        Mn, Nn = nei.shape
    if (Mi == 0) or (Ni == 0):
        Mi = Mn
        Ni = Nn

    # find size of restored image
    # according to: L = ((M - Mn + Mi) / Mi) * ((N - Nn + Ni) / Ni)
    L = X.shape[1]
    if M == 0:
        factors = list(primefac.primefac(L))
        M = np.prod(factors[0::2]) * Mi + Mn - Mi
    if N == 0:
        N = (L * Mi / (M - Mn + Mi)) * Ni + Nn - Ni

    if not (L == (((M - Mn + Mi) / Mi) * ((N - Nn + Ni) / Ni))):
        print(["mycol2im: Given  image size is ", str(M), "x", str(N)])
        print("Can not make given image size fit data X.")
        factors = list(primefac.primefac(L))
        M = np.prod(factors[0::2]) * Mi + Mn - Mi
        N = (L * Mi / (M - Mn + Mi)) * Ni + Nn - Ni

    # put the blocks into the image and taking average
    A = np.zeros((M, N))
    Ac = np.zeros((M, N))  # counts
    index = np.nonzero(nei)
    if not (L == ((A.shape[1] - Nn + 1) // Ni + 1) * ((A.shape[0] - Mn + 1) // Mi + 1)):
        print("Can still not make image size fit data X.")

    block = np.zeros((Mn, Nn))

    k = 0
    for j in range(0, A.shape[1] - Nn + 1, Ni):
        for i in range(0, A.shape[0] - Mn + 1, Mi):
            block[index[1], index[0]] = X[:, k]
            A[i : (i + Mn), j : (j + Nn)] = A[i : (i + Mn), j : (j + Nn)] + block
            block = np.ones((Mn, Nn))
            Ac[i : (i + Mn), j : (j + Nn)] = Ac[i : (i + Mn), j : (j + Nn)] + block
            k = k + 1

    index = np.nonzero(Ac)
    A[index] = A[index] / Ac[index]

    # do the transform
    A = myilwt2(A, tr, np.log2(Ms))

    return A
