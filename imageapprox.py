import time

import numpy as np
from scipy.sparse import csr_matrix
import copy

from myim2col import myim2col
from mycol2im import mycol2im
from sparseapprox import sparseapprox
from uniquant import uniquant
from myreshape import myreshape
from mypred import mypred

# imageapprox     Make a (sparse) approximation of the image.
# The main steps in this function are
# 1. The image is arraged into column vectors, X = myim2col(A, ...)
# 2. A sparse approximation of the columns in X is made using the supplied dictionary
#    Sparseness is made based on targetPSNR or tsf. 
# 3. The weights 'Zw', and the DC component 'Zdc', may be quantized using a
#    uniform quantizer with step delta (and a threshold), uniquant.
# 4. 'Zw' and 'Zdc' may be rearranged into sequences and bit rate is then
#    estimated, or a byte sequence 'y' may even be made.
#
# The function returns the results in a struct. The input arguments are the
# image A and several pairs of  input arguments collected in a dict
#----------------------------------------------------------------------
# need functions: myimadjust, myim2col, mycol2im, myreshape and sparseapprox
# Vector Selection: the 'mpv2' Java package
# from my Comp toolbox: uniquant, myreshape, mypred 
def imageapprox(A, par):
    '''
    res   a struct with the results, fields may be:
       tr (transform), PSNR, PSNRbq (before quantizing), delta,
       threshold, nonzeros, sf (sparseness factor), Ar (restored image),
       Zdc (quantized DC component), Zw (sparse matrix with quantized
       weights), Xdc (DC component before quantizing), W (sparse matrix
       with the weights)

    A     an image, a matrix of doubles (centered around 0), size Ma x Na.
       If image size does not match transform size (dictionary) image
       will be extended (by myim2col). A should be only one layer.
       ex: A = double(imread('lena.bmp')) - 128; # lena.bmp is grayscale

    The input arguments (options) may be:
    'peak' the peak for image A (used in PSNR), default 255
    't' or 'transform' is as in myim2col, default is 'none'.
    's' or 'size' is for the size of the transform, also as in myim2col.
       Transform and its size may be overruled by the dictionary
    'd' or 'dictionary' is the dictionary to use. It may be:
       a. A dictionary do not need to be given, option may be omitted
       b. The dictionary as a matrix D
       c. A struct with a field named 'D', and possible also 'transform'
       d. The name of a mat-file, containing 'D' (and 'transform')
       Cases c and d usually override 'transform' and 'size'

       The sparse representation is done by sparseapprox.m, options used
    'tPSNR' or 'targetPSNR' is target PSNR, default 0 (not used)
       if tPSNR is not given 'tsf' may be used
    'tsf' or 'targetSparsenessFactor' is target sparseness factor, it
       includes DC componet (which is always selected). So when 8x8
       blocks are used to form the columns in X, 1/64 < tsf < 1

       Quantizing is done by uniquant.m, options used
    'del' is binsize in uniform quantizer. Use 0 for no quantizing, and
       a negative value, ex -10, for adapting (using -del as first)
    'thr' gives size of zero-bin from -thr to +thr, default is 'del'
       which makes zero-bin twice the size of other bins.
    'delDC' as 'del' but used for DC component, default 'del'
    'thrDC' as 'thr' but used for DC component, defualt 'delDC'/2
    'qLimit' gives limits for how much we want quantizing to reduce PSNR
       given as [min, target, max], quantizing loop stops when PSNR is
       reduced by d, and d is between min and max. May give
       [min, max] and target is (min+max)/2, or just may give just
       target, and min/max = target +- 0.05, default qLimit = 0.4
    'eb' or 'estimateBits' estimates (or often finds) the number of bits
       needed to code the quantized values, i.e. Zdc and Zw.
       Value may be '' (default), 'none','Huff06', 'Arith06' or 'Arith07'
       where empty string ('') not estimate bits and 'none' estimate bits
       based on zero-order entropy but do no entropy coding

    'v' or 'verbose' to indicate verboseness, default 0
    '''
    # default options
    peak = 255
    tr = ''  # transform
    Ms = 0
    Ns = 0  # size (of transform)
    amet = 'extend'  # adjust method in myim2col(as in myimadjust)
    tpsnr = 0  # target PSNR
    tsf = 0.1  # or target sparseness factor
    dele = 0
    thr = 0
    deldc = 0
    thrdc = 0
    verbose = 0
    qlimit = [0.35, 0.4, 0.45]
    eb = ''

    # get the options
    dele = par.dele
    Ds = par.dictionary
    eb = par.estimateBits

    if len(par.qLimit) == 1:
        qlimit = [par.qLimit - 0.05, par.qLimit, par.qLimit + 0.05]
    elif len(par.qLimit) == 2:
        qlimit = [par.qLimit[0], (par.qLimit[0] + par.qLimit[1]) / 2, par.qLimit[1]]
    else:
        qlimit = [par.qLimit[0], par.qLimit[1], par.qLimit[2]]

    tpsnr = par.targetPSNR
    tr = Ds.transform
    verbose = par.verbose

    # check and display arguements and options
    if Ds.D.shape[0] == 64:
        Ms = 8
        Ns = 8

    if ((Ms == 0) or (Ns == 0)):
        if tr == None:
            Ms = 4
            Ns = 4
        else:
            Ms = 8
            Ns = 8

    if ((tr == 'lot') or (tr == 'elt')):
        Ms = Ms - np.mod(Ms, 2)
        Ns = Ns - np.mod(Ns, 2)

    if (not tr == None) and (not tr == 'lot') and (not tr == 'elt') and (not tr == 'dct'):
        Ms = max(2, np.power(2, (np.floor(np.log2(Ms))).astype(int)))
        Ns = Ms

    if (dele != 0):
        if thr == 0:
            thr = dele
        if deldc == 0:
            deldc = dele
        if thrdc == 0:
            thrdc = deldc / 2

    thr = np.abs(thr)
    deldc = np.abs(deldc)
    thrdc = np.abs(thrdc)

    if verbose:
        print(' ')
        print('imageapprox: Input image is ', str(A.shape[0]), ' x ', str(A.shape[1]), 'of class', A.dtype)
        print('Arguments are: transform = ', tr,
              ', tPSNR = ', str(tpsnr),
              ', peak = ', str(peak),
              ', tsf = ', str(tsf),
              ', del = ', str(dele),
              ', thr = ', str(thr),
              ', delDC = ', str(deldc),
              ', thrDC = ', str(thrdc)
              )

    # do the transform and make the column vectors from the image
    Ma, Na = A.shape
    X = myim2col(A, transform=tr, size=[Ms, Ns], adjust='amet', neighborhood=[Ms, Ns], increment=[Ms, Ns])
    N, L = X.shape
    if Ma * Na == N * L:
        imageadjust = False
        Maa = Ma
        Naa = Na
    else:
        imageadjust = True
        Maa = np.ceil(Ma / Ms) * Ms
        Naa = np.ceil(Na / Ns) * Ns
        if Maa * Naa != N * L:
            raise IndexError('imageapprox: can not match size of A and X')

    if verbose:
        print(['Image A of size ', str(Ma), 'x', str(Na),
               ' (', str(Ma * Na), ' pixels)',
               ' was divided into blocks of size ', str(Ms),
               'x', str(Ns), ' (transform: ', tr, '),'])
        print(['and then arranged into X of size ', str(N),
               'x', str(L), ' (', str(N * L), ' elements).'])

    # Extract DC both for transform only and (transform and) dictionary
    if tr == None:  # extract DC as first row ( or mean)
        Xdc = np.sqrt(N) * np.mean(X)
        Xr = np.ones((N, 1)) * (Xdc / np.sqrt(N))  # reconstructed using only Xdc
        X = X - Xr
    else:
        Xdc = (copy.deepcopy(X[0, :])).reshape(1, -1)
        X[0, :] = np.zeros((1, L))
        Xr = np.concatenate((Xdc, np.zeros((N - 1, L))), axis=0)

    Ar = mycol2im(Xr, transform=tr, imsize=[Maa, Naa], size=[Ms, Ns])

    if imageadjust:
        Ar = Ar[0:Ma, 0:Na]
    R = A - Ar
    PSNRdc = 10 * np.log10(((Ma * Na) / (np.sum(R * R) / (peak * peak))))
    if verbose:
        print(['Using only the ', str(L),
               ' elements in the DC component, PSNRdc = ', str(PSNRdc), '.'])

    # do a sparse representation of X using the dictionary or thresholding
    if (Ds != None):
        viktigKlasse = 'mpv2.DictionaryLearning'
        if tpsnr > 0:
            tSSE = ((Ma * Na) * peak * peak) * np.power(10, (-tpsnr / 10))
            if verbose:
                print(['imageapprox calls sparseapprox using',
                       ' tSSE = ', str(tSSE),
                       ' (target PSNR = ', str(tpsnr), ')'])

            # tic = time.time()
            W = sparseapprox(X, Ds.D, 'javaORMP', targetNonZeros=N / 2, tSSE=tSSE, v=max(0, verbose - 1))
            # tac = time.time()
            # print(tac-tic)
        elif (tsf < 1):
            tnnz = np.floor(Ma * Na * tsf) - L
            if verbose:
                print(['imageapprox call sparseapprox using',
                       ' tnz = ', str(tnnz),
                       ' (target sparseness factor = ', str(tsf), ')'])
            W = sparseapprox(X, Ds.D, 'GMP', targetNonZeros=tnnz, v=max(0, verbose - 1))
            W = sparseapprox(X, Ds.D, 'javaORMP', targetNonZeros=sum(W != 0), globalRD=1, v=max(0, verbose - 1))
            W = csr_matrix(W)
        else:
            print('imageapprox do not call sparseapprox (use W = D\X;).')
            W = Ds.D / X
        S = sum(W != 0)
        sumS = sum(S)
        Xa = np.dot(np.float64(Ds.D), W)
        Ar = mycol2im(Xr + Xa, transform=tr, imsize=[Maa, Naa], size=[Ms, Ns])
        if imageadjust:
            Ar = Ar[0: Ma, 0: Na]
        R = A - Ar
        PSNRbq = 10 * np.log10((((Ma * Na) * peak * peak) / sum(sum(R * R))))  # sparse rep
        if verbose:  # display results so far
            print(['After dictionary D is used: non-zeros including DC is ',
                   str(sumS + L), ' and PSNR = ', str(PSNRbq, 6)])

    ## Quantizing and find restored image
    adaptdelta = False
    if (dele < 0):  # try to select an appropriate value
        dele = -dele
        adaptdelta = True
    for dummycounter in range(10):
        # in loop only display if (verbose >= 2)
        if (dele > 0):
            Zdc = uniquant(Xdc, deldc, thrdc, 2000)
            Zw = uniquant(W, dele, thr, 2000)
            Qdc = uniquant(Zdc, deldc, thrdc)  # inverse quantizing
            Qw = uniquant(Zw, dele, thr)  # inverse quantizing
            dcnz = np.sum(Zdc != 0)  # number of non-zeros in DC
            S = np.sum(Zw != 0)  # selected number of non-zeros for each column
            sumS = np.sum(S)
            if verbose >= 2:
                print(['Quantizing loop number ', str(dummycounter), ':'])
                print(['  Total number of non-zeros after quantizing ',
                       str(dcnz + sumS), ' (Xdc:', str(dcnz), ', W:', str(sumS),
                       '), sparseness ', str((dcnz + sumS) / len(A)), '.'])
                print(['  using del = ', str(dele),
                       ', thr = ', str(thr),
                       ', deldc = ', str(deldc),
                       ', thrdc = ', str(thrdc), '.'])
        else:  # no quantizing  (note Zdc and Zw are not defined)
            Qw = W
            Qdc = Xdc
            dcnz = np.sum(Xdc != 0)  # number of non-zeros in DC
            S = np.sum(W != 0)  # selected number of non-zeros for each column
            sumS = np.sum(S)

        if tr == None:  # find Xr from Qdc
            Xr = np.ones((N, 1)) * (Qdc / np.sqrt(N))
        else:
            Xr = np.concatenate((Qdc, np.zeros((N - 1, L))), axis=0)

        if Ds != None:  # sparse W, Xa = Ds.D * W,
            Xa = np.dot(np.float64(Ds.D), Qw)
            Ar = mycol2im(Xr + Xa, transform=tr, imsize=[Maa, Naa], size=[Ms, Ns])
        else:
            Ar = mycol2im(Xr + Qw, transform=tr, imsize=[Maa, Naa], size=[Ms, Ns])

        if imageadjust:
            Ar = Ar[0: Ma, 0: Na]
        R = A - Ar
        PSNRq = 10 * np.log10((((Ma * Na) * peak * peak) / sum(sum(R * R))))  # after quant.

        if verbose >= 2:
            if (dele > 0):
                print(['  Quantizing reduced PSNR with ',
                       str(PSNRbq - PSNRq), ' dB to PSNRq = ', str(PSNRq), '.'])
            print(['  Sparseness factor is ', str((dcnz + sumS)),
                   '/', str(len(A)), ' = ', str((dcnz + sumS) / len(A)), '.'])

        if (not adaptdelta):
            break
        ratios = [thr / dele, deldc / dele, thrdc / dele, dele]
        d = (PSNRbq - PSNRq)  # difference (=reduction) in PSNR due to quatizing
        if ((qlimit[0] < d) and (d < qlimit[2])):
            break
        # what the factor f should be is here ad-hoc, this often work
        # (adapted to m79 transform)
        f = np.interp(d, [0, 0.2, 0.3, 0.4, 0.6, 3, 10, 50],
                      [2.5, 1.5, 0.5, 0.3, 0.1, 0.08, 0.09, 0.1])
        dele = np.power(10, (f * (qlimit[1] - d))) * dele
        if verbose >= 2:
            print(['  del was ', str(ratios[3]),
                   ', d (PSNRbq-PSNRq) = ', str(d),
                   ', f = ', str(f), ' qlimit(2) = ', str(qlimit[1])])
            print(['  In the end of quantizing loop ', str(dummycounter),
                   ' del was changed to ', str(dele)])

        thr = dele * ratios[0]
        deldc = dele * ratios[1]
        thrdc = dele * ratios[2]

        if (verbose == 1) and (dele > 0):
            print(['Quantizing using del = ', str(dele),
                   ', thr = ', str(thr),
                   ', deldc = ', str(deldc),
                   ', thrdc = ', str(thrdc), '.'])
            print(['  ==>  nonzeros = (', str(dcnz), '+', str(sumS),
                   ') = ', str(dcnz + sumS),
                   ', sparseness = ', str((dcnz + sumS) / len(A)),
                   ' and PSNR = ', str(PSNRq), '.'])

        if (verbose >= 1) and (dele == 0):
            print('No quantizing was done.')

    ## reshape
    if (len(eb) > 1):
        # estimate bits
        if Ds != None:  # a dictionary was used
            xCw = myreshape(Zw, method=2, verbose=0)
            xCdc = mypred(Zdc.reshape((int(Maa // 8), int(Naa // 8)), order='F'), nofS=3, verbose=0)
            xC = []
            for i_len in range(len(xCw) + len(xCdc)):
                xC.append([])

            for i in range(len(xCw)):
                xC[i] = xCw[i]
            for j in range(len(xCdc)):
                xC[i + j + 1] = xCdc[j]
    return Ar, PSNRq, xC, dele, deldc, thr, thrdc