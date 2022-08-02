import numpy as np
from ArithmeticCodingandHuffmanCodinginPython.HuffLen import HuffLen
from ArithmeticCodingandHuffmanCodinginPython.HuffTabLen import HuffTabLen
from ArithmeticCodingandHuffmanCodinginPython.HuffCode import HuffCode
from ArithmeticCodingandHuffmanCodinginPython.HuffTree import HuffTree

def Huff06(xC, ArgLevel = 8, ArgSpeed = 0):
    global y, Byte, BitPos, Speed, Level

    Mfile = 'Huff06'
    Debug = 0

    if type(xC) != list:
        Encode = 0
        Decode = 1
        y = np.array([b for a in xC for b in a])
    else:
        Encode = 1
        Decode = 0
    
        Speed = ArgSpeed
        Level = np.clip(ArgLevel, 1, 8)
        NumOfX = len(xC)
    
    #Encode
    if Encode:
        Res = np.zeros((NumOfX+1, 4))
        y = np.zeros((10,1))
        Byte = -1
        BitPos = 1
        PutVLIC(NumOfX)
        if Debug:
            print([Mfile, ' (Encode): Level=', str(Level), '  Speed=', str(Speed),
                  '  NumOfX=', str(NumOfX)])
        # now encode each sequence continuously
        Ltot=0
        for num in range(NumOfX):
            x=xC[num]
            x = np.array(x)
            L=len(x)
            Ltot=Ltot+L
            #y=[y[1:Byte],np.zeros((50+2*L,1))]  # make more space available in y
            y=np.concatenate((y[0:Byte+1],np.zeros((50+4*L,1))), axis=0) # make more space available in y:zxf
            # now find some info about x to better code it
            if (L>0):
                maxx=max(x)
                # maxx=maxx[0]
                minx=min(x)
                # minx=minx[0]
            else:
                maxx=0
                minx=0
            
            if (minx<0) :
                Negative=1
            else :
                Negative=0
            
            if ( (((maxx*4)>L) or (maxx>1023)) and (L>1) and (maxx>minx)):
                # the test for LogCode could be better, I think, (ver. 1.3)
                LogCode=1    # this could be 0 if LogCode is not wanted
            else:
                LogCode=0
                
            PutBit(LogCode)
            PutBit(Negative)
            I=np.nonzero(x)                      # non-zero entries in x
            Sg=(np.sign(x[I])+1)/2            # the signs may be needed later, 0/1
            x=abs(x)   
            if LogCode:
                xa=x.copy()                        # additional bits
                x[I]=np.floor(np.log2(x[I]))
                xa[I]=xa[I] - np.power(2, x[I])
                x[I]=x[I]+1
                x = x.astype(int)
                xa = xa.astype(int)
            bits, ent=EncodeVector(x)   # store the (abs and/or log) values
            if Negative:                    # store the signs
                for i in range(len(Sg)):
                    PutBit(Sg[i])
                bits=bits+len(Sg)
                ent=ent+len(Sg)/L

            if LogCode:                     # store the additional bits
                for i in range(L):
                    for ii in range((x[i]-1),0,-1):
                        PutBit(bitget(xa[i],ii-1))

                bits=bits+sum(x)-len(I[0])
                ent=ent+(sum(x)-len(I[0]))/L

            if L>0 :
                Res[num,0]=L 
            else:
                Res[num,0]=1
            Res[num,1]=ent
            Res[num,2]=bits
        
        y=y[0:Byte+1]
        if Ltot<1:
            Ltot=1  # we do not want Ltot to be zero
        Res[NumOfX,2]=(Byte+1)*8
        Res[NumOfX,0]=Ltot
        Res[NumOfX,1]=sum(Res[0:NumOfX,0]*Res[0:NumOfX,1])/Ltot
        Res[:,3]=Res[:,2]/Res[:,0]

        return y, Res


    if Decode:
        Byte = -1
        BitPos = 1
        NumOfX = GetVCIL()
        if Debug:
            print([Mfile, '(Decode):  NumOfX=', str(NumOfX), '  length(y)=', str(len(y))])

        xC = []
        for i in range(NumOfX):
            xC.append([])
        for num in range(NumOfX):
            LogCode = GetBit()
            Negative = GetBit()
            x = DecodeVector()   # get the (abs and/or log) values
            # print(num)
            if type(x) == np.ndarray or type(x) == list:
                L = len(x)
            else:
                x = (np.array([x])).reshape(-1,1)
                L = len(x)
            I= np.nonzero(x)
            if Negative:
                Sg = np.zeros_like(I[0])
                for i in range(len(I[0])):
                    Sg[i] = GetBit()   # and the signs   (0/1)
                Sg = Sg*2-1                              # (-1/1)
            else:
                Sg = np.ones_like(I[0])

            if LogCode:          # read additional bits too
                xa = np.zeros((L,1))
                for i in range(L):
                    for ii in range(1,int(x[i])):
                        xa[i]=2*xa[i]+GetBit()

                x[I] = np.power(2,(x[I]-1))
                x = x+xa

            if len(x)!=0 and len(I[0])!=0:
                x[I] = x[I]*Sg
                xC[num] = x.reshape((-1,))
            else:
                xC[num] = []
        return xC    # end of main function, huff06
    return -1

def PutVLIC(N):
    global y, Bytes, BitPos
    if (N<0):
        raise IndexError('Huff06-PutVLIC: Number is negative.') 
    elif (N<16):
        PutBit(0)
        PutBit(0)
        for i in range(3,-1,-1):
            PutBit(bitget(N,i))
    elif (N<272):
       PutBit(0)
       PutBit(1)
       N=N-16
       for i in range(7, -1, -1):
           PutBit(bitget(N,i))
    elif (N<4368):
       PutBit(1)
       PutBit(0)
       N=N-272
       for i in range(11, -1, -1):
           PutBit(bitget(N,i))
    elif (N<69940):
       PutBit(1)
       PutBit(1)
       PutBit(0)
       N=N-4368
       for i in range(15, -1, -1):
           PutBit(bitget(N,i))
    elif (N<1118480):
       PutBit(1)
       PutBit(1)
       PutBit(1)
       PutBit(0)
       N=N-69940
       for i in range(19, -1, -1):
           PutBit(bitget(N,i))
    elif (N<17895696):
       PutBit(1)
       PutBit(1)
       PutBit(1)
       PutBit(1)
       N=N-1118480
       for i in range(23, -1, -1):
           PutBit(bitget(N,i))
    elif (N<286331152):
       PutBit(1)
       PutBit(1)
       PutBit(1)
       PutBit(1)
       PutBit(0)
       N=N-17895696
       for i in range(27, -1, -1):
           PutBit(bitget(N,i))
    else:
       raise IndexError('Huff06-PutVLIC: Number is too large.')
    return


# the EncodeVector and DecodeVector functions are the ones
# where actual coding is going on.
# This function calls itself recursively
def EncodeVector(x, bits = None, HL = None, Maxx = None, Meanx = None):
    global y,Byte,BitPos,Speed,Level 
    Debug=0
    Level = Level - 1
    MaxL=50000        # longer sequences is split in the middle
    L=len(x)
    # first handle some special possible exceptions,
    
    if L==0:
        PutBit(0)       # indicate that a sequence is coded
        PutVLIC(L)      # with length 0 (0 is 6 bits)
        PutBit(0)       # 'confirm' this by a '0', Run + Value is indicated by a '1'
        bits=2+6
        ent=0
        Level = Level + 1
        return bits, ent    # end of EncodeVector
    
    if L==1:
        PutBit(0)       # indicate that a sequence is coded
        PutVLIC(L)      # with length 1 (6 bits) 
        PutVLIC(x[0])   # containing this integer    
        bits=1+2*6
        if (x[0]>=16): 
            bits=bits+4
        if (x[0]>=272): 
            bits=bits+4
        if (x[0]>=4368): 
            bits=bits+5
        if (x[0]>=69904): 
            bits=bits+5
        if (x[0]>=1118480): 
            bits=bits+4
        ent=0
        Level = Level + 1
        return bits, ent   # end of EncodeVector

    if max(x)==min(x):
       PutBit(0)       # indicate that a sequence is coded
       PutVLIC(L)      # with length L
       for i in range(7):
           PutBit(1)   # write end of Huffman Table
       PutVLIC(x[0])   # containing this integer    
       bits=1+6+7+6      
       if (x[0]>=16):
           bits=bits+4
       if (x[0]>=272):
           bits=bits+4
       if (x[0]>=4368):
           bits=bits+5
       if (x[0]>=69904):
           bits=bits+5
       if (x[0]>=1118480):
           bits=bits+4
       if (L>=16):
           bits=bits+4
       if (L>=272):
           bits=bits+4
       if (L>=4368):
           bits=bits+5
       if (L>=69904):
           bits=bits+5
       if (L>=1118480):
           bits=bits+4
       ent=0
       Level = Level + 1
       return  bits, ent    # end of EncodeVector
    if (L <= 5):      # ver. 1.9 feb. 2010 KS
       PutBit(0)       # indicate that a sequence is coded
       PutVLIC(L)      # with length 1 (6 bits) 
       bits=1+6
       for i in range(L):
            PutVLIC((x[i]).astype(int))   # containing this integer
            bits=bits+6
            if (x[i]>=16): 
                bits=bits+4 
            if (x[i]>=272): 
                bits=bits+4 
            if (x[i]>=4368): 
                bits=bits+5 
            if (x[i]>=69904): 
                bits=bits+5 
            if (x[i]>=1118480): 
                bits=bits+4 
       
       ent=0
       Level = Level + 1
       return  bits, ent    #  of EncodeVector
    
    # here we test if Run + Value coding should be done
    I=list(np.nonzero(x)[0])  # the non-zero indices of x
    if (L/2-len(I))>50:
       Maxx=max(x)
       Hi=IntHist(x,0,int(Maxx))  # find the histogram
       Hinz=Hi[np.nonzero(Hi)]
       ent=np.log2(L)-sum(Hinz*np.log2(Hinz))/L  # find entropy
       # there are few non-zero indices => Run+Value coding of x
       x2=x[I]  # the values  
       I = [i+1 for i in I]
       I.append(L+1)# include length of x
       for i in range(len(I)-1,0,-1):
           I[i]=I[i]-I[i-1]
       x1=[i-1 for i in I]   # the runs
       # code this as an unconditional split (like if L is large)
       if Speed:
          Byte=Byte+1    # since we add 8 bits
       else:
          PutBit(0)       # this is idicated like when a sequence 
          PutVLIC(0)      # of length 0 is coded, but we add one extra bit
          PutBit(1)       # Run + Value is indicated by a '1'

       bits1, temp = EncodeVector(x1)
       bits2, temp = EncodeVector(x2)
       bits=bits1+bits2+8
       Level = Level + 1
       return  bits, ent    # end of EncodeVector

    if (bits == None and HL == None and Maxx == None and Meanx == None):
       Maxx=max(x)
       Meanx=np.mean(x)
       Hi=IntHist(x,0,int(Maxx))  # find the histogram
       Hinz=Hi[np.nonzero(Hi)]
       ent=np.log2(L)-sum(Hinz*np.log2(Hinz))/L  # find entropy
       HL=HuffLen(Hi)
       HLlen=HuffTabLen(HL)
       # find number of bits to use, store L, HL and x
       bits=6+HLlen+sum(HL*Hi)
       if (L>=16):
           bits=bits+4
       if (L>=272):
           bits=bits+4
       if (L>=4368):
           bits=bits+5
       if (L>=69904):
           bits=bits+5
       if (L>=1118480):
           bits=bits+4
       if Debug:
          print(['bits=',str(bits),'  HLlen=',str(HLlen),
             '   HClen=',str(sum(HL*Hi))])
    else:                # arguments are given, do not need to be calculated
       ent=0

    if (L>MaxL):
        L1 = np.ceil(L/2)
        L2 = L - L1
        x1 = x[0:L1]
        x2 = x[L1:]
    elif (Level>0 and L>10):
        xm = np.median(x)
        x1 = np.zeros((L,1))
        x2 = np.zeros((L,1))
        x2[0] = x[0]
        i1 = -1
        i2 = 0
        for i in range(1,L):
            if (x[i-1] <= xm):
                i1 = i1 + 1
                x1[i1] = x[i]
            else:
                i2 = i2 + 1
                x2[i2] = x[i]
        x1=x1[0:i1+1]
        x2=x2[0:i2+1]
        # find bits1 and bits2 for x1 and x2
        L1=len(x1) 
        L2=len(x2) 
        Maxx1=max(x1) 
        Maxx2=max(x2) 
        Meanx1=np.mean(x1) 
        Meanx2=np.mean(x2) 
        Hi1=IntHist(x1,0,int(Maxx1))   # find the histogram
        Hi2=IntHist(x2,0,int(Maxx2))   # find the histogram
        HL1=HuffLen(Hi1) 
        HL2=HuffLen(Hi2) 
        HLlen1=HuffTabLen(HL1) 
        HLlen2=HuffTabLen(HL2) 
        bits1=6+HLlen1+sum(HL1*Hi1) 
        bits2=6+HLlen2+sum(HL2*Hi2) 
        if (L1>=16 ):
            bits1=bits1+4
        if (L1>=272 ):
            bits1=bits1+4
        if (L1>=4368 ):
            bits1=bits1+5
        if (L1>=69904 ):
            bits1=bits1+5
        if (L1>=1118480 ):
            bits1=bits1+4
        if (L2>=16 ):
            bits2=bits2+4
        if (L2>=272 ):
            bits2=bits2+4
        if (L2>=4368 ):
            bits2=bits2+5
        if (L2>=69904 ):
            bits2=bits2+5
        if (L2>=1118480 ):
            bits2=bits2+4
    else:
        bits1 = bits
        bits2 = bits

    # Here we may have: x1, bits1, L1, HL1, Maxx1, Meanx1
    # and               x2, bits2, L2, HL2, Maxx2, Meanx2
    # but at least we have bits1 and bits2  (and bits)
    if Debug:
       print(['Level=',str(Level),'  bits=',str(bits),'  bits1=',str(bits1),
             '  bits2=',str(bits2),'  sum=',str(bits1+bits2)])

    if (L>MaxL):
       if Speed:
          BitPos=BitPos-1
          if (not BitPos):
              Byte=Byte+1
              BitPos=8
       else:
          PutBit(1)       # indicate sequence is splitted into two

       bits1, temp = EncodeVector(x1)
       bits2, temp = EncodeVector(x2)
       bits=bits1+bits2+1
    elif ((bits1+bits2) < bits):
       if Speed:
          BitPos=BitPos-1
          if (not BitPos):
              Byte=Byte+1
              BitPos=8
       else:
          PutBit(1)       # indicate sequence is splitted into two

       bits1, temp = EncodeVector(x1, bits1, HL1, Maxx1, Meanx1)
       bits2, temp = EncodeVector(x2, bits2, HL2, Maxx2, Meanx2)
       bits=bits1+bits2+1
    else:
       bits=bits+1      # this is how many bits we are going to write
       if Debug:
          print(['EncodeVector: Level=',str(Level),'  ',str(L),
                ' sybols stored in ',str(bits),' bits.'])
       if Speed:
          # advance Byte and BitPos without writing to y
          Byte=Byte+np.floor(bits/8)
          BitPos=BitPos-np.mod(bits,8)
          if (BitPos<=0):
              BitPos=BitPos+8
              Byte=Byte+1
       else:
          # put the bits into y
          StartPos=Byte*8-BitPos     # control variable
          PutBit(0)       # indicate that a sequence is coded
          PutVLIC(L)       
          PutHuffTab(HL)
          HK=HuffCode(HL)
          for i in range(L):
             n=int(x[i])   # symbol number (value 0 is first symbol, symbol 1)
             for k in range(int(HL[n])):
                PutBit(HK[n][k])
          # check if one has used as many bits as calculated
          BitsUsed=Byte*8-BitPos-StartPos
          if (BitsUsed != bits):
             print(['L=',str(L),'  max(x)=',str(max(x)),'  min(x)=',str(min(x))])
             print(['BitsUsed=',str(BitsUsed),'  bits=',str(bits)])
             raise IndexError(['Huff06-EncodeVector: Logical error, (BitsUsed~=bits).'])

    Level = Level + 1
    return bits, ent





#Functions to write and read a Bit
def PutBit(Bit):
    global y,Byte,BitPos
    BitPos = BitPos-1
    if (not BitPos):
        Byte=Byte+1
        BitPos=8
    y[Byte] = setBit(y[Byte], BitPos-1, Bit)
    return

def GetBit():
    global y, Byte, BitPos
    BitPos = BitPos - 1
    if (not BitPos):
        Byte = Byte + 1
        BitPos = 8
    Bit = bitget(y[Byte],BitPos-1)
    return Bit


def setBit(int_type, offset, v):
    int_type = int_type.astype(int)
    if v:
        mask = 1 << offset
        return(int_type | mask)
    else:
        mask = ~(1 << offset)
        return (int_type & mask)

def bitget(int_type, offset):
    int_type = int(int_type)
    mask = 1 << offset
    return 1 if (int_type & mask)>0 else 0

# this function is a variant of the standard hist function
def IntHist(W,i1,i2):
    #if (rem(i1,1) | rem(i2,1))   error('Non integers') end
    L=len(W)
    W = np.array(W)
    if W.dtype != int:
        W = W.astype(int)
    Hi=np.zeros((i2-i1+1,1))
    if (i2-i1)>50:
       for l in range(L):
          i=W[l]-i1
          Hi[i]=Hi[i]+1
    else:
       for i in range(i1, i2+1):
          I=np.where(W==i)
          Hi[i-i1]=len(I[0])
    return Hi


def PutHuffTab(HL):

    global y,Byte,BitPos

    # if (max(HL) > 32) 
    #    disp(['PutHuffTab: To large value in HL, max(HL)=',int2str(max(HL))]) 
    # end
    # if (min(HL) < 0)
    #    disp(['PutHuffTab: To small value in HL, min(HL)=',int2str(min(HL))]) 
    # end
    Prev=2
    ZeroCount=0
    L=len(HL)
    
    for l in range(L):
       if HL[l]==0:
          ZeroCount=ZeroCount+1
       else:
          while (ZeroCount > 0):
             if ZeroCount<3:
                for i in range(ZeroCount):
                    PutBit(1)
                    PutBit(1)
                    PutBit(0)
                    PutBit(1)
                    PutBit(1)

                ZeroCount=0 
             elif ZeroCount<19:
                PutBit(1)
                PutBit(1)
                PutBit(1)
                PutBit(0)
                PutBit(0)
                PutBit(0)
                PutBit(0)
                for i in range(4,0,-1):
                    PutBit(bitget(ZeroCount-3,i-1))
                ZeroCount=0 
             elif ZeroCount<275:
                PutBit(1)
                PutBit(1)
                PutBit(1)
                PutBit(0)
                PutBit(0)
                PutBit(0)
                PutBit(1)
                for i in range(8,0,-1):
                    PutBit(bitget(ZeroCount-19,i-1))
                ZeroCount=0 
             else:
                PutBit(1)
                PutBit(1)
                PutBit(1)
                PutBit(0)
                PutBit(0)
                PutBit(0)
                PutBit(1)
                for i in range(8,0,-1):
                    PutBit(1)
                ZeroCount=ZeroCount-274
          if HL[l]>16:
             PutBit(1)
             PutBit(1)
             PutBit(1)
             PutBit(0)
             PutBit(0)
             PutBit(1)
             PutBit(0)
             for i in range(4,0,-1):
                 PutBit(bitget(HL[l]-17,i-1))
          else:
             Inc=HL[l]-Prev
             if Inc<0:
                 Inc=Inc+16
             if (Inc==0):
                PutBit(0)
             elif (Inc==1):
                PutBit(1)
                PutBit(0)
             elif (Inc==2):
                PutBit(1)
                PutBit(1)
                PutBit(0)
                PutBit(1)
                PutBit(0)
             elif (Inc==15):
                PutBit(1)
                PutBit(1)
                PutBit(0)
                PutBit(0)
             else:
                PutBit(1)
                PutBit(1)
                PutBit(1)
                for i in range(4,0,-1):
                    PutBit(bitget(Inc,i-1))
             Prev=HL[l]

    for i in range(7,0,-1):
        PutBit(1)# the EOT codeword
    return


def GetVCIL():
    global y, Byte, BitPos
    N = 0
    if GetBit():
        if GetBit():
            if GetBit():
                if GetBit():
                    for i in range(24):
                        N = N * 2 + GetBit()
                    N = N + 1118480
                else:
                    for i in range(20):
                        N = N * 2 + GetBit()
                    N = N + 69940
            else:
                for i in range(16):
                    N = N * 2 + GetBit()
                N = N + 4368
        else:
            for i in range(12):
                N = N * 2 + GetBit()
            N = N + 272
    else:
        if GetBit():
            for i in range(8):
                N = N * 2 + GetBit()
            N = N + 16
        else:
            for i in range(4):
                N = N * 2 + GetBit()

    return N


def DecodeVector():
    global y, Byte, BitPos
    MaxL = 50000
    if GetBit():
        x1 = DecodeVector()
        x2 = DecodeVector()
        L = len(x1) + len(x2)
        if L > MaxL:
            x = np.concatenate((x1,x2),axis=0)
        else:
            if len(x1) == 0:
                xm = np.median(x2)
            elif len(x2) == 0:
                xm = np.median(x1)
            else:
                xm = np.median(np.concatenate((x1,x2),axis=0))
            x = np.zeros((L,1))
            x[0] = x2[0]
            i1 = 0
            i2 = 1
            for i in range(1,L):
                if (x[i-1] <= xm):
                    i1 = i1 + 1
                    x[i] = x1[i1-1]
                else:
                    i2 = i2 + 1
                    x[i] = x2[i2-1]


    else:
        L = GetVCIL()
        if (L>5):
            x = np.zeros((L,1))
            HL = GetHuffTab()
            if len(HL):
             Htree=HuffTree(HL)
             root=1
             pos=root
             l=0  # number of symbols decoded so far
             while l<L:
                if GetBit():
                   pos=int(Htree[pos-1,2])
                else:
                   pos=int(Htree[pos-1,1])
                if Htree[pos-1,0]:           # we have arrived at a leaf
                   x[l]=Htree[pos-1,1]-1   # value is one less than symbol number
                   l = l + 1
                   pos=root              # start at root again
                # if l ==L:
                #     a=10
            else:    # HL has length 0, that is empty Huffman table
                x = x + GetVCIL()
        elif L>1: # ver 1.9 feb. 2010 KS
            x = np.zeros((L,1))
            for i in range(L):
                x[i] = GetVCIL()
        elif L==0:
            if GetBit():
                # this is a Run + Value coded sequence
                x1=DecodeVector()
                x2=DecodeVector()
                # now build the actual sequence
                I=x1      # runs
                I=I+1
                L=len(I)  # one more than the number of values in x
                for i in range(1,L):
                    I[i]=I[i-1]+I[i]
                x=np.zeros((int(I[L-1]-1),1))
                x[((I[0:(L-1)]-1).astype(int)).squeeze(1)]=x2  # values
            else:
                x = []
        elif L==1:
            x = GetVCIL()
        else:
            raise IndexError('DecoderVector: illegal length sequence')
    return x


def GetHuffTab():
    global y, Byte, BitPos
    Debug = 0
    Prev = 2
    ZeroCount = 0
    HL = np.zeros((10000, 1))
    HLi = -1
    EndOfTable = 0

    while not EndOfTable:
        if GetBit():
            if GetBit():
                if GetBit():
                    Inc=0
                    for i in range(4):
                        Inc=Inc*2+GetBit()
                    if Inc==0:
                        ZeroCount=0
                        for i in range(4):
                            ZeroCount=ZeroCount*2+GetBit()
                        HLi=HLi+ZeroCount+3
                    elif Inc==1:
                        ZeroCount=0
                        for i in range(8):
                            ZeroCount=ZeroCount*2+GetBit()
                        HLi=HLi+ZeroCount+19
                    elif Inc==2:           # HL(l) is large, >16
                        HLi=HLi+1
                        HL[HLi]=0
                        for i in range(4):
                            HL[HLi]=HL[HLi]*2+GetBit()
                        HL[HLi]=HL[HLi]+17
                    elif Inc==15:
                        EndOfTable=1
                    else:
                       Prev=Prev+Inc
                       if Prev>16:
                           Prev=Prev-16
                       HLi=HLi+1
                       HL[HLi]=Prev

                else:
                    if GetBit():
                       if GetBit():
                          HLi=HLi+1
                       else:
                          Prev=Prev+2
                          if Prev>16 :
                              Prev=Prev-16
                          HLi=HLi+1
                          HL[HLi]=Prev
                    else:
                       Prev=Prev-1
                       if Prev<1:
                           Prev=16
                       HLi=HLi+1
                       HL[HLi]=Prev
            else:
                Prev=Prev+1
                if Prev>16:
                    Prev=1
                HLi=HLi+1
                HL[HLi]=Prev
        else:
            HLi=HLi+1
            HL[HLi]=Prev
    if HLi>0:
        HL=HL[0:HLi+1]
    else:
        HL=[]

    if Debug:
        # check if this is a valid Huffman table
        temp=sum(np.power(2,(-HL[np.nonzeros(HL)])))
        if temp !=1:
          raise IndexError(['GetHuffTab: HL table is no good, temp=',str(temp)])

    return HL


