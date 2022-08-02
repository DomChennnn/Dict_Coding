import numpy as np

def HuffTabLen(HL):
    Mfile='HuffTabLen'
    # KeepStatistics=0  # we may want to keep statistics to see wether the chosen 
                       # code words are well suited
    HLlen=0       # counting the bits as when they were written in Huff06
    
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
                   HLlen = HLlen+5 
                   # PutBit(1)PutBit(1)PutBit(0)PutBit(1)PutBit(1)
                ZeroCount=0 
             elif ZeroCount<19: 
                HLlen = HLlen+7 
                # PutBit(1)PutBit(1)PutBit(1)PutBit(0)PutBit(0)PutBit(0)PutBit(0)
                HLlen = HLlen+4 
                # for (i=4:-1:1) PutBit(bitget(ZeroCount-3,i)) end
                ZeroCount=0 
             elif ZeroCount<275:
                HLlen = HLlen+7 
                # PutBit(1)PutBit(1)PutBit(1)PutBit(0)PutBit(0)PutBit(0)PutBit(1)
                HLlen = HLlen+8 
                # for (i=8:-1:1) PutBit(bitget(ZeroCount-19,i)) end
                ZeroCount=0 
             else: 
                HLlen = HLlen+7 
                # PutBit(1)PutBit(1)PutBit(1)PutBit(0)PutBit(0)PutBit(0)PutBit(1)
                HLlen = HLlen+8 
                # for (i=8:-1:1) PutBit(1) end
                ZeroCount=ZeroCount-274 

          if HL[l]>16:
             HLlen = HLlen+7 
             # PutBit(1)PutBit(1)PutBit(1)PutBit(0)PutBit(0)PutBit(1)PutBit(0)
             HLlen = HLlen+4 
             # for (i=4:-1:1) PutBit(bitget(HL[l]-17,i)) end
          else:
             Inc=HL[l]-Prev
             if Inc<0:
                 Inc=Inc+16
             if (Inc==0):
                HLlen = HLlen+1 
                # PutBit(0)
             elif (Inc==1):
                HLlen = HLlen+2 
                # PutBit(1)PutBit(0)
             elif (Inc==2):
                HLlen = HLlen+5 
                # PutBit(1)PutBit(1)PutBit(0)PutBit(1)PutBit(0)
             elif (Inc==15):
                HLlen = HLlen+4 
                # PutBit(1)PutBit(1)PutBit(0)PutBit(0)
             else:
                HLlen = HLlen+3 
                # PutBit(1)PutBit(1)PutBit(1)
                HLlen = HLlen+4 
                # for (i=4:-1:1) PutBit(bitget(Inc,i)) end
             Prev=HL[l]

    HLlen = HLlen+7 
    # for (i=7:-1:1) PutBit(1) end       # the EOT codeword
    return HLlen