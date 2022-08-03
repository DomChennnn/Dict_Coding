import numpy as np


def myreshape(inn, method=None, verbose=None):
    '''
    myreshape       Reshape between matrix and cell array of sequences.
    The sequences should be fitted for entropy coding, see estimateBits.m
    If first argument is a cell array reshape is from cell array to matrix
    else first argument should be matrix and reshape is from matrix to cell
    array.

    use:
    xC = myreshape(W, ...);
    W  = myreshape(xC, ...);
    ------------------------------------------------------------------------
    :param or :return xC     cell array of integer sequences
         size of W and method are stored as xC{1}, i.e [K,L,met]
         more options may also be stored in xC{1}
    :param or :return W      KxL matrix of integers, i.e. quantized transform coeffients
         Options:
    :param 'm' or 'method' gives the method to use, default 1
         1 : my own variant of End-Of-Block (EOB) coding into several
             sequences. Suited for quantized transform (or wavelet)
             coefficients with 3 levels, i.e. size(W,1)=64.
             Additional arguments may be: 'sortRows', 'sDC'
             (Note, algorithm is slow for large W)
         2 : index + value sequences, suited for sparse matrices with no
             DC component and size(W,1) might as well be large (ex. 440).
         3 : is like method 1, with row sorted to match the order in 2D
             wavelet, i.e. myim2col (with wavelet transform mylwt2) or as
             lwt2 (inplace) followd by im2col. K = 16, 64, 256, 1024
             This is usually only better than method 1 for K = 256 or 1024
    :param 'v' or 'verbose' to indicate verboseness, default 0
------------------------------------------------------------------------
    '''


    ## default options and get the options
    if method == None:
        method = 1
    else:
        method = max(1, np.floor(method))
    sortrows = True
    largeLimit = 400
    if verbose == None:
        verbose = 0
    else:
        if type(verbose) == bool:
            verbose = 1
        else:
            verbose = verbose

    ##start of function
    if type(inn) == np.ndarray:  # encoding
        W = np.float64(inn)  # make sure we can calculate on W
        del inn
        K, L = W.shape
        if verbose:
            print(['myreshape: from ', str(K), 'x', str(L),
                   ' matrix to cell array (method = ', str(method),
                   ', sortrows = ', str(sortrows),
                   ', largeLimit = ', str(largeLimit), ').'])

        if (method == 2):  # coding of sparse W (non-zeros randomly  placed)
            # into 4 sequences: info, values, indexes, large-indexes
            # A 'largeLimit' is used, values in seq 3 >= largeLimit are copied
            # to seq 4 and largeLimit is written into these positions in seq 3
            # seq 1 : info on W and method
            # seq 2 : the non-zero values
            # seq 3 : number of zeros before each value and after last
            # seq 4 : large values from sequence 2
            xC = [[], [], [], []]
            xC[0] = [K, L, method, largeLimit]
            W = W.T.reshape(-1, 1)
            index, temp = np.nonzero(W)
            value = W[index, temp]
            index[1:] = np.diff(index)  # DPCM of index
            I = np.where(index >= largeLimit)
            large = index[I]
            index[I] = largeLimit
            xC[1] = value
            xC[2] = index - 1
            xC[3] = large - (largeLimit - 32)  # code large numbers ok in Huff06
        ut = xC

    elif type(inn) == list:  # decoding
        xC = inn
        del inn
        K = xC[0][0]
        L = xC[0][1]
        method = xC[0][2]
        W = np.zeros((int(K), int(L)))

        if verbose:
            print(['myreshape: from cell array (', str(len(xC)),
                   ' seqences) to ', str(K), 'x', str(L),
                   ' matrix (method = ', str(method), ').'])

        for i in range(len(xC)):
            if type(xC[i]) != np.ndarray:
                xC[i] = np.array(xC[i])
        if method == 2:

            largeLimit = xC[0][3]
            index = xC[2] + 1
            index[index == largeLimit] = xC[3] + (largeLimit - 32)
            for i in range(1, len(index)):
                index[i] = index[i - 1] + index[i]
            if len(xC[1]) == 0 or len(index) == 0:
                W = np.zeros((K, L))
            else:
                W[index % W.shape[0], index // W.shape[0]] = xC[1]

            ut = W

    return ut
