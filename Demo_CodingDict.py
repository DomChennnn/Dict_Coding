import numpy as np
import scipy.io as scio
import h5py
import glob
import time
import bz2
from PIL import Image

from test_Dict import test_Dict
from utils import Dictionarys

from myim2col import myim2col
from mycol2im import mycol2im
from sparseapprox import sparseapprox
from uniquant import uniquant
from myreshape import myreshape
from mypred import mypred


#init

foldername = ['Face','Fingerprint','Texuture','GenerallImages']
blk_size = 8
transform = 'm79'

print('start')

for ind_folder in range(3,4):
    path_in = './/Images//' + foldername[ind_folder] + '//Test2//'                              #Image dir path
    data = scio.loadmat('.//Dictionary//Dict_RLS_' + foldername[ind_folder] + '.mat')           #load Dictionary path
    D = data['dlsRLS'][0,0][0]                                                                  #Dictionary

    N, K = D.shape
    L = 40000
    Ds = Dictionarys(D,K,N,L,transform)
    Dimg = glob.glob(path_in + '//**//*.bmp', recursive=True)                                   #Image path
    NumberImages = len(Dimg)

    Bitrate_JPEG = np.transpose(h5py.File('.//Results//Bitrate_JPEG_' + foldername[ind_folder] + '.mat', 'r')['Bitrate_JPEG'])           #load bitrate
    Quality_JPEG = np.transpose(h5py.File('.//Results//Quality_JPEG_' + foldername[ind_folder] + '.mat', 'r')['Quality_JPEG'])           #load quality

    numBit = Bitrate_JPEG.shape[1]

    Bitrate_Dict = np.zeros((NumberImages, numBit))
    Quality_Dict = np.zeros((NumberImages, numBit))

    for ind_img in range(NumberImages):
        Quality_Dict[ind_img, :], Bitrate_Dict[ind_img, :],res, xc, dele, deldc, thr, thrdc, xc_encoded, res_encoded = test_Dict(Dimg[ind_img], Bitrate_JPEG[[ind_img], :], Ds)

    # scio.savemat('C:\dmcprojects\\nju_summary\RLSDLA_SpecialDict\savedir\\Quality_Dict.mat', {'Quality_Dict':Quality_Dict})
    # scio.savemat('C:\dmcprojects\\nju_summary\RLSDLA_SpecialDict\savedir\\Bitrate_Dict.mat', {'Bitrate_Dict':Bitrate_Dict})
    # print('OK')
    #
    # end_time = time.time()
    # print(end_time-start_time)



    for i in range(8):
        xc[i] = list(xc[i])
    
    # 
    h,w = res.shape
    res = res.reshape((-1,1))
    res_list = []
    for i in range(len(res)):
        res_list.append(res[i, 0])

    res_encoded = bz2.compress((str(res_list)).encode(),9)
    xc_encoded = bz2.compress((str(xc)).encode(),9)

    # xc_decode = (bz2.decompress(xc_encoded)).decode()
    print((len(res_encoded)+len(xc_encoded)))


    start_time = time.time()

    res_decode = (bz2.decompress(res_encoded)).decode()
    res = eval(res_decode)
    res = np.array(res)
    res = res.reshape((h,w))

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
    # res = list(res_decode)
    # xc = list(xc_decode)

    xCw = xc[0:4]
    xCdc = xc[4:]
    Zw_r = myreshape(xCw)
    Zdc_r = mypred(xCdc)
    Zdc_r = (Zdc_r.T).reshape(1,988)

    Qdc = uniquant(Zdc_r, deldc, thrdc)  # inverse quantizing
    Qw = uniquant(Zw_r, dele, thr)  # inverse quantizing
    dcnz = np.sum(Zdc_r != 0)  # number of non-zeros in DC
    S = np.sum(Zw_r != 0)  # selected number of non-zeros for each column
    sumS = np.sum(S)

    Xr = np.concatenate((Qdc, np.zeros((N - 1, 988))), axis=0)
    Xa = np.dot(np.float64(Ds.D), Qw)
    Ar = mycol2im(Xr+Xa, transform = 'm79', imsize = [304, 208], size = [8, 8])
    Ar = Ar[0 : 301, 0 : 201]

    pic = Image.fromarray(np.uint8(Ar+res+128))
    pic.save('test.png')
    end_time = time.time()

    print((end_time-start_time)*1000)


