import numpy as np

from myimadjust import myimadjust
from mylwt2 import mylwt2

# myim2col        Rearrange image blocks into columns.
# The most important feature is that the columns can be made from a
# coefficient domain of the image, i.e. a transform is done first then the
# blocks are extracted.
# The 'inverse' is mycol2im.py
# functions, i.e: myimadjust, mylwt2
def myim2col(
    A,
    transform=None,
    size=None,
    adjust=None,
    neighborhood=None,
    offset=None,
    increment=None,
):
    """

    ----------------------------------------------------------------------
    :param: A     an image, ex: A = double(imread('lena.bmp')) - 128;
    There may be an additional number of input arguments, a struct, a cell
       or as pairs: argName, argVal, ...
    :param: offset which may be [0,0] (default) or other values [Mo,No]
       This is done first, i.e. the first operation on the image is to
       remove the Mo first rows and the No first columns, thus making
       A(Mo+1,No+1) as the new (upper left) corner of the image
       When used, offset reduce the size of the image
    :param: transform
       The option value may be the name of a transform: 'dct', 'lot'
       or 'elt', or the transform given as a matrix. Note that in
       analysis part (here) the transposed of this matrix is used, while
       for synthesis (mycol2im) the matrix is used as given, this means
       that if the transform is orthogonal the same matrix can be given
       both in myim2col and mycol2im. Alternatively, the option value
       can be the name of a wavelet or the wavelet lifting scheme given
       directly as a cell array. Wavelt name as in mylwt2, that is as
       liftwave (in Matlab  Wavelet Toolbox) or 'j97' or 'm97'.
       The transform is always a separable transform, and except for
       'dct', 'lot' and 'elt' with same size in both directions.
       Default option value is 'none' for reshape in pixel domain.
    :param: size is for the size of the transform. Only for 'dct', 'lot'
       and 'elt' it may be different in the two dimension, [Ms,Ns].
       For wavelet Ns = Ms, and it should be 2,4,8,16 or 32.
    :param: adjust. Before the transform is done the image size must be
       adjusted to match the size of the transform. This is done by
       myimadjust.m and the value of the adjust parameter is used as
       method in myimadjust: 'none' or 'extend' is preferred.
    :param: neighborhood can be used to extract special neighborhoods
       around a pixel. The option value may be 8, [8,8], ones(8) or a
       (logical)  matrix indicating which elements to include,
       for example: kron( ones(4), [1,0]'*[1,0] );
       default is size of transform or [4,4] if transform is 'none'.
    :param: increment which may be  'distinct' for (size of) neighborhood
       blocks (default), or 'sliding' for [1,1] or numbers [Mi,Ni]
    :return: columns
    ----------------------------------------------------------------------
    """

    # default options
    Mo = 0
    No = 0  # offset
    tr = None  # transform
    Ms = 0
    Ns = 0  # size(of transform)
    amet = ""  # method in myimadjust
    nei = []  # neighborhood, input or given by tr
    Mn = 0
    Nn = 0  # size of nei(neighborhood)
    Mi = 0
    Ni = 0  # increment

    # get the options
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
            raise IndexError("myim2col: illegal option size, we ignore it")

    if adjust != None:
        if (
            (adjust == None)
            or (adjust == "extend")
            or (adjust == "zeros")
            or (adjust == "periodic")
            or (adjust == "mirror")
        ):
            amet = adjust

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
            raise IndexError("myim2col: illegal option neighborhood, we ignore it")

    if offset != None:
        if len(offset) == 1:
            Mo = max(0, np.floor(offset).astype(int))
            No = Mo
        elif len(offset) == 2:
            Mo = max(0, np.floor(offset[0]).astype(int))
            No = max(0, np.floor(offset[1]).astype(int))
        else:
            raise IndexError("myim2col: illegal option offset. We ignore it.")

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
            raise IndexError("myim2col: illegal option increment. We ignore it.")

    # check and display options
    if (Ms == 0) or (Ns == 0):
        if tr == None:
            Ms = 4
            Ns = 4
        else:
            Ms = 8
            Ns = 8

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

    if len(amet) == 0:
        if tr == None:
            amet = None
        else:
            amet = "extend"

    if (Mn == 0) or (Nn == 0):
        nei = np.ones((Ms, Ns))
        Mn, Nn = nei.shape
    if (Mi == 0) or (Ni == 0):
        Mi = Mn
        Ni = Nn

    # remove what is left and above offset, and perhaps adjust the size
    if (Mo > 0) or (No > 0):
        A = A[Mo:, No:]

    if tr != None:
        A = myimadjust(A, amet, [Ms, Ns])

    # do the transform
    A = mylwt2(A, tr, np.log2(Ms))

    # find the blocks
    index = np.nonzero(nei)  # reshape to col(start from 0 to the num-1 of the matrix)
    L = ((A.shape[1] - Nn + 1) // Ni + 1) * ((A.shape[0] - Mn + 1) // Mi + 1)

    X = np.zeros((len(index[0]), L))
    k = 0
    for j in range(0, A.shape[1] - Nn + 1, Ni):
        for i in range(0, A.shape[0] - Mn + 1, Mi):
            block = A[i : (i + Mn), j : (j + Nn)]
            X[:, k] = block[index[1], index[0]]
            k = k + 1

    return X
