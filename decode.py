for i in range(8):
    xC[i] = list(xC[i])

xc_encoded = bz2.compress((str(xC)).encode())
xc_decode = (bz2.decompress(xc_encoded)).decode()

aaa = xc_decode.split('], [')

aaa[0] = aaa[0][2:]
aaa[len(aaa)-1] = aaa[len(aaa)-1][0:-2]

for i in range(8):
    if aaa[i]=='':
        aaa[i] = []
    else:
        aaa[i] = list(eval(aaa[i]))

