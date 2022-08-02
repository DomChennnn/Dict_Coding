import numpy as np

def HuffCode(HL, Display=0):

    if (Display != 1):
       Display = 0

    
    N=len(HL)
    L=int(max(HL))
    HK=np.zeros((N,L))
    HLs,HLi = np.sort(HL,kind='stable', axis=0), np.argsort(HL,kind='stable',axis=0)
    Code=np.zeros((1,L))
    for n in range(N):
       if (HLs[n]>0):
          HK[HLi[n],:] = Code
          k = int(HLs[n])
          while (k>0):                 # actually always!  break ends loop
             Code[0][k-1] = Code[0][k-1] + 1
             if (Code[0][k-1]==2):
                Code[0][k-1] = 0
                k=k-1
             else:
                break

    if Display:
       for n in range(N):
          Linje = '  Symbol '+str(n)
          for i in range(len(Linje),16):
             Linje = Linje+' '

          Linje = Linje+'  gets code: '
          for i in range(HL[n]):
             if (HK[n][i]==0):
                Linje = Linje+'0'
             else:
                Linje = Linje+'1'

          print(Linje)
    return HK