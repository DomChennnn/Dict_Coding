import numpy as np
from ArithmeticCodingandHuffmanCodinginPython.Huff06 import Huff06

# TestHuff    Test and example of how to use Huff06

#----------------------------------------------------------------------
# Copyright (c) 2000.  Karl Skretting.  All rights reserved.
# Hogskolen in Stavanger (Stavanger University), Signal Processing Group
# Mail:  karl.skretting@tn.his.no   Homepage:  http://www.ux.his.no/~karlsk/
# 
# HISTORY:
# Ver. 1.0  20.06.2000  KS: function made
#----------------------------------------------------------------------

# first make some data we will use in test
Level = 8
Speed = 0
xC = []
for i in range(15):
    xC.append([])
# randn('state',0)
if 1:                # do not make many values
   xC[0] = np.zeros((1000,1))
   xC[0][23:990:11] = np.floor(10*np.random.randn(0,1,[len(range(23,990,11)),1]))
   for k in range(1,9):
      xC[k]=np.floor(abs(np.random.randn(0,1,[100+100*k,1])*k))

   xC[9]=np.floor(10*np.random.randn(0,1,[998,1]))   # an AR-1 signal
   xC[10]=np.ones((119,1))*7
   xC[11]=[]

xC[13]=[]
xC[14]=4351
# this next sequence gave an error with previous version (Huff04)
xC[15]=np.array([1,39,37,329,294,236,406,114,378,192,159,0,165,9,77,178,225,30,
         286,3,157,34,185,146,15,218,97,82,281,1103,80,45,96,31,90,10,
         105,163,19,10,2,73,114,14,42,553,15,412,76,158,379,440,256,71,
         181,1,36,149,137,55,191,117,124,32,20,0,88,221,8]).T

# now we encode this
y, Res=Huff06(xC, Level, Speed)
# # and decode it
xR=Huff06(y)
# for k=1:15
#    disp(['Number of bits for sequence ',int2str(k),' is ',int2str(Res(k,3))])
#    if (sum(abs(xR{k}-xC{k})))
#       disp(['Sequence no ', int2str(k),' has difference ',int2str(sum(abs(xR{k}-xC{k})))])
#    end
# end
# disp(['Total number of bits ', int2str(Res(16,3))])
a=10



