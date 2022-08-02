import numpy as np

def myreshape(inn, method = None, verbose = None):
    
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
    Mdc = 0
    Ndc = 0
    nofSdc = 3   # may be used as nofS in mypred
    m3variant = 1

    ##start of function
    if type(inn) == np.ndarray:# encoding
        W = np.float64(inn)   # make sure we can calculate on W
        del inn
        K, L = W.shape
        if verbose:
            print(['myreshape: from ',str(K),'x',str(L),
                  ' matrix to cell array (method = ',str(method),
                  ', sortrows = ',str(sortrows),
                  ', largeLimit = ',str(largeLimit),').'])
            
        if (method == 2):  # coding of sparse W (non-zeros randomly  placed)
            # into 4 sequences: info, values, indexes, large-indexes
            # A 'largeLimit' is used, values in seq 3 >= largeLimit are copied
            # to seq 4 and largeLimit is written into these positions in seq 3
            # seq 1 : info on W and method
            # seq 2 : the non-zero values
            # seq 3 : number of zeros before each value and after last
            # seq 4 : large values from sequence 2
            xC = [[],[],[],[]]
            xC[0] = [K, L, method, largeLimit]
            W = W.T.reshape(-1, 1)
            index, temp = np.nonzero(W)
            value = W[index, temp]
            index[1:] = np.diff(index)   # DPCM of index
            I = np.where(index >= largeLimit)
            large = index[I]
            index[I] = largeLimit
            xC[1] = value
            xC[2] = index-1
            xC[3] = large - (largeLimit-32)  # code large numbers ok in Huff06
        ut = xC

    elif type(inn) == list:# decoding
        xC = inn
        del inn
        K = xC[0][0]
        L = xC[0][1]
        method = xC[0][2]
        W = np.zeros((int(K),int(L)))

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
            index[index == largeLimit] = xC[3] + (largeLimit-32)
            for i in range(1,len(index)):
                index[i] = index[i-1]+index[i]
            if len(xC[1])==0 or len(index)==0:
                W = np.zeros((K,L))
            else:
                W[index%W.shape[0],index//W.shape[0]] = xC[1]


            ut = W


    return ut
