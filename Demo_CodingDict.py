import numpy as np
import scipy.io as scio
import h5py
import glob
import time
import bz2
from PIL import Image

from test_Dict import test_Dict
from utils import Dictionarys

from mycol2im import mycol2im
from uniquant import uniquant
from myreshape import myreshape
from mypred import mypred

# init
foldername = 'GenerallImages'
blk_size = 8
transform = 'm79'

path_in = './/Images//' + foldername + '//Test2//'                      # Image dir path
data = scio.loadmat('.//Dictionary//Dict_RLS_' + foldername + '.mat')   # load Dictionary path
D = data['dlsRLS'][0, 0][0]                                             # Dictionary

N, K = D.shape
L = 40000
Ds = Dictionarys(D, K, N, L, transform)                                 #define Dictionary
Dimg = glob.glob(path_in + '//**//*.bmp', recursive=True)               # Image path
NumberImages = len(Dimg)

Bitrate_JPEG = np.transpose(
    h5py.File('.//Results//Bitrate_JPEG_' + foldername + '.mat', 'r')['Bitrate_JPEG'])  # load bitrate
Quality_JPEG = np.transpose(
    h5py.File('.//Results//Quality_JPEG_' + foldername + '.mat', 'r')['Quality_JPEG'])  # load quality

numBit = Bitrate_JPEG.shape[1]

Bitrate_Dict = np.zeros((NumberImages, numBit))
Quality_Dict = np.zeros((NumberImages, numBit))

#encode, this can be divided into two steps, including dictionary encoding and res encoding
#dictionary encode
for ind_img in range(NumberImages):
    Quality_Dict[ind_img, :], Bitrate_Dict[ind_img,:], res, xc, dele, deldc, thr, thrdc, xc_encoded, res_encoded = test_Dict(Dimg[ind_img], Bitrate_JPEG[[ind_img], :], Ds)

for i in range(8):
    xc[i] = list(xc[i])

xc_encoded = bz2.compress((str(xc)).encode(), 9)

#dictionary encode
h, w = res.shape
res = res.reshape((-1, 1))
res_list = []
for i in range(len(res)):
    res_list.append(res[i, 0])

res_encoded = bz2.compress((str(res_list)).encode(), 9)

# print((len(res_encoded) + len(xc_encoded)))

#decode
start_time = time.time()

#res decode
res_decode = (bz2.decompress(res_encoded)).decode()
res = eval(res_decode)
res = np.array(res)
res = res.reshape((h, w))

#dictionary decode
xc_decode = (bz2.decompress(xc_encoded)).decode()
xc_decode = xc_decode.split('], [')
xc_decode[0] = xc_decode[0][2:]
xc_decode[len(xc_decode) - 1] = xc_decode[len(xc_decode) - 1][0:-2]
for i in range(8):
    if xc_decode[i] == '':
        xc_decode[i] = []
    else:
        xc_decode[i] = list(eval(xc_decode[i]))
xc = xc_decode

xCw = xc[0:4]
xCdc = xc[4:]
Zw_r = myreshape(xCw)
Zdc_r = mypred(xCdc)
Zdc_r = (Zdc_r.T).reshape(1, 988)

Qdc = uniquant(Zdc_r, deldc, thrdc)             # inverse quantizing
Qw = uniquant(Zw_r, dele, thr)                  # inverse quantizing
dcnz = np.sum(Zdc_r != 0)                       # number of non-zeros in DC
S = np.sum(Zw_r != 0)                           # selected number of non-zeros for each column
sumS = np.sum(S)

Xr = np.concatenate((Qdc, np.zeros((N - 1, 988))), axis=0)
Xa = np.dot(np.float64(Ds.D), Qw)
Ar = mycol2im(Xr + Xa, transform='m79', imsize=[304, 208], size=[8, 8])
Ar = Ar[0: 301, 0: 201]

pic = Image.fromarray(np.uint8(Ar + res + 128))
pic.save('test.png')
end_time = time.time()
print('finish, and the img is saved as test.png')
print('decoding time:')
print((end_time - start_time) * 1000)


