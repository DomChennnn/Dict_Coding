import numpy as np

def HuffLen(S):

    if (len(S) == 0):
        print('HuffLen:symbol sequence is empty')
        HL = 0
        return HL

    S[S<0] = 0

    if (sum(S)==max(S)):
        HL = np.zeros_like(S)
        return HL

    HL = np.zeros_like(S)
    Ip = np.where(S>0)
    Sp = S[Ip]

    N = len(Sp)
    HLp = np.zeros_like(Sp)
    C = np.concatenate((Sp, np.zeros((N-1,))),axis=0)
    Top = np.array(range(0,N))
    So, Si = np.sort(-Sp), np.argsort(-Sp)
    last_num = N-1
    next_num = N

    while (last_num > 0):
       # the two smallest "trees" are put together
       C[next_num]=C[Si[last_num]]+C[Si[last_num-1]]
       I=np.where(Top==Si[last_num])
       HLp[I]=HLp[I]+1   # one extra bit added to elements in "tree"
       Top[I]=next_num
       I=np.where(Top==Si[last_num-1])
       HLp[I]=HLp[I]+1   # and one extra bit added to elements in "tree"
       Top[I]=next_num
       last_num=last_num-1                 
       Si[last_num]=next_num
       next_num=next_num+1
       # Si shall still be indexes for descending symbols or nodes
       count=last_num-1
       while ((count>= 0) and (C[Si[count+1]] >= C[Si[count]])):
          temp=Si[count]
          Si[count]=Si[count+1]
          Si[count+1]=temp
          count=count-1

    HL[Ip] = HLp

    return HL