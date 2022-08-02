import numpy as np
from ArithmeticCodingandHuffmanCodinginPython.HuffCode import HuffCode


def HuffTree(HL, HK = None):

    if HK == None:
        HK = HuffCode(HL)

    N = len(HL)
    Htree=np.zeros((N*2,3))
    root=1
    next=2
    for n in range(N):
       if HL[n]>0:
          # place this symbol correct in Htree
          pos=root
          for k in range(int(HL[n])):
             if ((Htree[pos-1,0]==0) and (Htree[pos-1,1]==0)):
                # it's a branching point but yet not activated
                Htree[pos-1,1]=next
                Htree[pos-1,2]=next+1
                next=next+2

             if HK[n][k]:
                pos=int(Htree[pos-1,2])     # goto right branch
             else:
                pos=int(Htree[pos-1,1])      # goto left branch

          Htree[pos-1,0]=1   # now the position is a leaf
          Htree[pos-1,1]=n+1   # and this is the symbol number it represent

    if N==1:
       Htree[0,2]=2

    return Htree