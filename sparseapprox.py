import numpy as np
import time
import jpype

def sparseapprox(X, D, met, targetNonZeros = None, targetRelativeError = None,
                 targetAbsoluteError = None, numberOfIterations = None, p = None, l = None,
                 nC = None, paramSPAMS = None,  globalRD = None,  doOP = None,
                 GMPLS = None,  tSSE = None,  targetSNR = None, v = None):

    mfile = 'sparseapprox'

    #defaults, initial values
    tstart = time.time()
    N,L = X.shape
    K = D.shape[1]
    norm2X = np.sqrt(np.sum(X*X,axis=0)) # ||x(i)||_2     1xL
    W = np.zeros((K,L))      # the weights (coefficients)
    tnz = np.ceil(N/2)*np.ones((1,L)) # target number of non-zeros
    thrActive = False     # is set to true if tnz, tre or tae is given
                          # and used for methods: pinv, backslash, linprog and
                          # FOCUSS
    tae = None
    if doOP == None:
        doOP = True         # do Orthogonal Projection when thresholding
    else:
        doOP = doOP
    relLim = 1e-6
    tre = relLim*np.ones((1,L))   # target relative error: ||r|| <= tre*||x||
    nComb = 20           # used only in javaPS
    nIt = 20             # used only in FOCUSS 
    pFOCUSS = 0.5        # used only in FOCUSS 
    lambdaFOCUSS = 0     # used only in FOCUSS
    deltaWlimit = 1e-8   # used only in FOCUSS
    if GMPLS == None:
        GMPLoopSize = 0      # used only in GMP
    else:
        GMPLoopSize = max(np.floor(GMPLS), 2)

    if globalRD == None:
        globalReDist = 0     # may be used with javaORMP
    else:
        globalReDist = min(max(np.floor(globalRD), 0), 2)  # 0, 1 or 2

    if tSSE == None:
        targetSSE = 0        # may be used with javaORMP
    else:
        targetSSE = min(max(tSSE, 0),np.sum(X*X))
    verbose = 0
    done = False
    javaClass = 'mpv2.MatchingPursuit'   # the important java class
    spams_mex_file = 'mexLasso' # one of the used SPAMS files

    #get the options
    if targetNonZeros != None:
        if met == 'GMP':
            tnz = targetNonZeros   # GMP will distribute the non-zeros
        else:
            if type(targetNonZeros) == float:
                tnz = targetNonZeros * np.ones((1,L))
            elif len(targetNonZeros) == L:
                tnz = targetNonZeros.reshape((1,L))
            else:
                print([mfile,': illegal size of value for option ','targetNonZeros'])
        thrActive = True

    if targetRelativeError != None:
        if len(targetRelativeError) == 1:
           tre = targetRelativeError*np.ones((1,L))
        elif len(targetRelativeError) == L:
           tre = targetRelativeError.reshape((1,L))
        else:
            print([mfile,': illegal size of value for option ','targetRelativeError'])
        thrActive = True

    if targetAbsoluteError != None:
        if len(targetAbsoluteError) == 1:
           tae = targetAbsoluteError*np.ones((1,L))
        elif len(targetAbsoluteError) == L:
           tae = targetAbsoluteError.reshape((1,L))
        else:
            print([mfile,': illegal size of value for option ','targetAbsoluteError'])
        thrActive = True

    if numberOfIterations != None:
        if len(numberOfIterations) == 1:
            nIt = max(np.floor(numberOfIterations), 1)
        else:
            print([mfile,': illegal size of value for option ','numberOfIterations'])

    if p != None:
        if len(p) == 1:
            pFOCUSS = min(p, 1)
        else:
            print([mfile,': illegal size of value for option ','pFOCUSS'])

    if l != None:
        if len(l) == 1:
            lambdaFOCUSS = abs(l)
        else:
            print([mfile,': illegal size of value for option ','lambdaFOCUSS'])

    if nC != None:
        if len(nC)==1:
            nComb = max(np.floor(nC), 2)
        else:
            print([mfile,': illegal size of value for option ','nComb'])

    if paramSPAMS != None:
        paramSPAMS = paramSPAMS

    if targetSNR != None:
        if type(targetSNR)==float:
            targetSSE = 10^(-abs(targetSNR)/10) * sum(sum(X*X))
        else:
            print([mfile,': illegal size of value for option ','targetSNR'])

    if v != None:
        verbose = v

    if tae != None:  #if both exist 'tae' overrules 'tre'
        tre = tae/norm2X
    else:# 'tre' was given a default value
        tae = tre*norm2X


    #Method of Frames
    if met =='MOF' or met == 'MethodOfFrames' or met == 'pinv':
        textMethod = 'Method of Frames with pseudoinverse (pinv).'
        if verbose:
            print([mfile, ': ', textMethod])
        W = np.dot(np.linalg.pinv(D) , X)
        if thrActive: # then adjust w by setting more to zero
            W = setSmallWeightsToZero(X, D, W, tnz, tae, doOP)
        done = True


    # #Backslash method just find an exact solution with N non-zeros
    # if met == 'BackSlash':
    #     textMethod = 'Matlab backslash operator.'
    #     if verbose:
    #         print([mfile,': ',textMethod])
    #     W = np.dot(np.linalg.pinv(D) , X)
    #     if thrActive:  #then adjust w by setting more to zero
    #         W = setSmallWeightsToZero(X,D,W,tnz,tae,doOP)
    #     done = True
    #
    # ##  Basis Pursuit
    # if met == 'BP' or met == 'BasisPursuit' or met == 'linprog':
    #     textMethod = 'Basis Pursuit with Matlab function linprog.'
    #     if verbose:
    #         print[mfile,': ',textMethod])
    #     f = np.ones((2*K,1))
    #     A = np.concatenate((D,-D), axis=0)
    #     LB = np.zeros((2*K,1))         # lower bound for w
    #     UB = np.ones((2*K,1))*inf      # upper bound for w
    #     for columnNumber in range(L):
    #         x = X[:,columnNumber]
    #         w2 = np.linprog(f,A,x,A,x,LB,UB)     # minimize 1-norm of w
    #         W(:,columnNumber) = w2(1:K)-w2((1+K):(2*K))
    #     if thrActive:  # then adjust w by setting more to zero
    #         W = setSmallWeightsToZero(X,D,W,tnz,tae,doOP)
    #     done = True

    # java func
    jvmPath = jpype.getDefaultJVMPath()  # the path of jvm.dll
    classpath = "C:\dmcprojects\pythonProject1\javaclasses"  # the path of PasswordCipher.class
    jvmArg = "-Djava.class.path=" + classpath
    if not jpype.isJVMStarted():  # test whether the JVM is started
        jpype.startJVM(jvmPath, jvmArg)  # start JVM

    SimpleMatrix = jpype.JClass("mpv2.SimpleMatrix")  # create the Java class
    MatchingPursuit = jpype.JClass("mpv2.MatchingPursuit")
    SymmetricMatrix = jpype.JClass("mpv2.SymmetricMatrix")

    if met[0: min(len(met), 4)] == 'java':
        jD = SimpleMatrix(D)
        if (L == 1):
            jMP = MatchingPursuit(jD)
        else:
            start_time = time.time()
            jDD = SymmetricMatrix(K, K)
            jDD.eqInnerProductMatrix(jD)
            jMP = MatchingPursuit(jD, jDD)
            end_time = time.time()
            usedtime = (end_time-start_time)*1000

    if (met == 'javaORMP') or (met == 'javaOrderRecursiveMatchingPursuit'):
        #
        # This could be as simple as javaOMP, but since globalReDist was
        # reintroduced it is now quite complicated here.
        if (targetSSE > 0):
            # This is initialization of tre (and tnz ?) for the special case of
            # global distribution of non-zeros where a target sum og squared
            # errors is given as an input argument.
            # Perhaps tnz also should be set to an appropriate value
            # tnz = 2*ones(1,L) 
            tre = np.sqrt(targetSSE/L)/norm2X
            globalReDist = 2
            textMethod = ['javaORMP with global distribution of non-zeros ',
                'given target SSE (or SNR).']
        elif (globalReDist == 1):
            textMethod = ['javaORMP with global distribution of non-zeros ',
                'keeping the total number of non-zeros fixed.']
        elif (globalReDist == 2):
            textMethod = ['javaORMP with global distribution of non-zeros ',
                'keeping the total SSE fixed.']
        else:
            textMethod = 'Order Recursive Matching Pursuit, Java implementation.'
        #
        # below is the javaORMP lines
        if verbose:
            print([mfile,': ',textMethod])

        for j in range(L):
            if (tnz[0,j] > 0) and (tre[j] < 1):
                W[:,j] = jMP.vsORMP(X[:,j], np.int32(tnz[0,j]), tre[j])
    # 
        # below is the globalReDist lines
        # ******* START Global distribution of non-zeros.***** 
        # The structure is:  
        #    1. Initializing:  Sm1 <= S <= Sp1  and  SEm1 >= SE >= SEp1
        #    2. Add atoms until SSE is small enough
        #    3. or remove atoms until SSE is large enough
        #    4. Add one atom as long as one (or more) may be removed and the
        #       SSE is reduced
        if (globalReDist > 0):
            # part 1
            R = np.float32(X - np.dot(D , W))  # representation error
            S = sum(W != 0)  # selected number of non-zeros for each column
            SE = sum(R*R)   # squared error for each (when S is selected)
            sumSinit = sum(S)
            SSE = np.float32(sum(SE))
            SSEinit = SSE      # store initial SSE
            Sp1 = S + 1        # selected number of non-zeros plus one
            Sp1[Sp1 > N] = N
            Sm1 = S - 1        # selected number of non-zeros minus one
            Sm1[Sm1 < 0] = 0
            SEp1 = np.zeros((L, 1), np.float32)  # initializing corresponding squared error
            SEm1 = np.zeros((L, 1), np.float32)
            for j in range(L):
                x = X[:,j]
                if Sp1[j] == S[j]:  # == N
                    w = W[:,j]
                else:
                    w = jMP.vsORMP(x, Sp1[j], relLim)
                r = x.reshape(-1,1)-(np.dot(D,w)).reshape(-1,1)
                SEp1[j] = np.dot(r.T, r)
                if Sm1[j] == 0:
                    w = np.zeros((K,1))
                else:
                    w = jMP.vsORMP(x, Sm1[j], relLim)
                r = x.reshape(-1,1)-(np.dot(D,w)).reshape(-1,1)
                SEm1[j] = np.dot(r.T, r)
            SEdec = SE.reshape(-1,1) - SEp1   # the decrease in error by selectiong one more
            SEinc = SEm1-SE.reshape(-1,1)   # the increase in error by selectiong one less
            SEinc[S == 0] = np.inf  # not possible to select fewer than zero
            addedS = 0
            removedS = 0
            addedSE = np.float32(0)
            removedSE = 0
            valinc, jinc = np.min(SEinc), np.argmin(SEinc) # min increase in SE by removing one atom
            valdec, jdec = np.max(SEdec), np.argmax(SEdec) # max reduction in SE by adding one atom
            
            if (targetSSE > 0):
                if (SSEinit > targetSSE):  # part 2
                    if verbose:
                        print(['(part 2 add atoms, target SSE = ',str(targetSSE),
                            ' and initial SSE = ',str(SSEinit),')'])
                    while (SSE > targetSSE):
                        j = jdec    # an atom is added to vector j
                        addedS = addedS+1
                        removedSE = removedSE + valdec
                        SSE = SSE - valdec
                        # shift in  Sm1,S,Sp1  and  SEm1,SE,SEp1
                        Sm1[j], S[j], Sp1[j] = S[j], Sp1[j], min(Sp1[j]+1, N)
                        SEm1[j], SE[j] = SE[j], SEp1[j]  # and SEp1(j)=SEp1(j)
                        if (Sp1[j] > S[j]) :  # the normal case, find new SEp1(j)
                            w = jMP.vsORMP(X[:,j], Sp1[j], relLim)
                            r = X[:,[j]] -(np.dot(D,w)).reshape(-1,1)
                            SEp1[j] = np.dot(r.T, r)
                        SEinc[j] = SEdec[j]      # SE cost by removing this again
                        SEdec[j] = SE[j]-SEp1[j] # SE gain by adding one more atom
                        #
                        W[:,j] = jMP.vsORMP(X[:,j], S[j], relLim)
                        valdec, jdec = np.max(SEdec), np.argmax(SEdec)

                    valinc, jinc = np.min(SEinc), np.argmax(SEdec)
                elif ((SSEinit+valinc) < targetSSE):  # part 3
                    if (verbose):
                        print(['(part 3 remove atoms, target SSE = ',str(targetSSE),
                            ' and initial SSE = ',str(SSEinit),')'])
                    while ((SSE+valinc) < targetSSE):
                        j = jinc   # an atom is removed from vector j
                        removedS = removedS+1
                        addedSE = addedSE + valinc
                        SSE = SSE + valinc
                        # shift in  Sm1,S,Sp1  and  SEm1,SE,SEp1
                        Sm1[j], S[j], Sp1[j] = max(Sm1[j]-1, 0), Sm1[j], S[j]
                        SE[j], SEp1[j] = SEm1[j], SE[j] # and SEm1(j)=SEm1(j)
                        if (Sm1[j] > 0):
                            w = jMP.vsORMP((X[:,j]), Sm1[j], relLim)
                            r = X[:,[j]]-(np.dot(D,w)).reshape(-1,1)
                        else:
                            r = X[:,j]

                        SEm1[j] = np.dot(r.T, r)
                        #
                        SEdec[j] = SEinc[j]  # SE gain by adding this atom again
                        if (S[j] > 0): # SE cost by removing another atom
                            W[:,j] = jMP.vsORMP(X[:,j], S[j], relLim)
                            SEinc[j] = SEm1[j]-SE[j]
                        else:
                            W[:,j] = 0
                            SEinc[j] = np.inf  # can not select fewer and increase error

                        valinc, jinc = np.min(SEinc),np.argmin(SEinc)

                    valdec, jdec = np.max(SEdec), np.argmax(SEdec)
                else:  #
                    if verbose:
                        print(['(target SSE = ',str(targetSSE),
                            ' is close to initial SSE = ',str(SSEinit),')'])

            else:
                targetSSE = SSEinit
            #
            # part 4
            while ((valinc < valdec) and (jinc != jdec)):
                j = jdec
                addedS = addedS+1
                removedSE = removedSE + valdec
                SSE = SSE - valdec
                # shift in  Sm1,S,Sp1  and  SEm1,SE,SEp1
                Sm1[j], S[j], Sp1[j] = S[j], Sp1[j], min(Sp1[j]+1, N)
                SEm1[j], SE[j] = SE[j], SEp1[j]  # and SEp1(j)=SEp1(j)
                if (Sp1[j] > S[j]):  # the normal case, find new SEp1(j)
                    w = jMP.vsORMP(X[:,j], Sp1[j], relLim)
                    r = X[:,[j]]-(np.dot(D,w)).reshape(-1,1)
                    SEp1[j] = np.dot(r.T, r)

                SEinc[j] = SEdec[j]      # SE cost by removing this again
                SEdec[j] = SE[j]-SEp1[j] # SE gain by adding one more atom
                W[:,j] = jMP.vsORMP(X[:,j], S[j], relLim)
                valinc, jinc = np.min(SEinc), np.argmin(SEinc)
                #
                while ((SSE+valinc) < targetSSE):
                    j = jinc
                    removedS = removedS+1
                    addedSE = addedSE + valinc
                    SSE = SSE + valinc
                    # shift in  Sm1,S,Sp1  and  SEm1,SE,SEp1
                    Sm1[j], S[j], Sp1[j] = max(Sm1[j]-1, 0), Sm1[j], S[j]
                    SE[j], SEp1[j] = SEm1[j], SE[j] # and SEm1(j)=SEm1(j)
                    if (Sm1[j] > 0):
                        w = jMP.vsORMP(X[:,j], Sm1[j], relLim)
                        r = X[:,[j]]-(np.dot(D,w)).reshape(-1,1)
                    else:
                        r = X[:,j]
                    SEm1[j] = np.dot(r.T, r)
                    #
                    SEdec[j] = SEinc[j]  # SE gain by adding this atom again
                    if (S[j] > 0): # SE cost by removing another atom
                        W[:,j] = jMP.vsORMP(X[:,j], S[j], relLim)
                        SEinc[j] = SEm1[j]-SE[j]
                    else:
                        W[:,j] = 0
                        SEinc[j] = np.inf  # can not select fewer and increase error
                    valinc, jinc = np.min(SEinc), np.argmin(SEinc)
                    if (globalReDist == 1):
                        break
                valdec, jdec = np.max(SEdec), np.argmax(SEdec)    # next now
        #  ******* END Global distribution of non-zeros.*****
        #
        #
        done = True
    return W



##functions
def setSmallWeightsToZero(X, D, W, tnz, tae, doOP):
    K, W = W.shape
    for i in range(L):
        absw, I = sorted(W[:,[i]],reverse=True)
    return 1