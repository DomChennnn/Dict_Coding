import numpy as np
import os
import scipy.io as scio
import h5py
import glob
import time
import bz2 as zip_lib
from PIL import Image
import math

from test_Dict import test_Dict
from utils import Dictionarys, PROJECT_ROOT

from mycol2im import mycol2im
from uniquant import uniquant
from myreshape import myreshape
from mypred import mypred


def compute_psnr(img1, img2):
    mse = np.mean((img1 / 255.0 - img2 / 255.0) ** 2)
    if mse < 1.0e-10:
        return 100
    PIXEL_MAX = 1
    return 20 * math.log10(PIXEL_MAX / math.sqrt(mse))


# # test on one channel
# # init
# encode_start = time.time()
# foldername = 'GenerallImages'
# blk_size = 8
# transform = 'm79'
#
# path_in = './/Images//' + foldername + '//Test2//'                      # Image dir path
# data = scio.loadmat('.//Dictionary//Dict_RLS_' + foldername + '.mat')   # load Dictionary path
# D = data['dlsRLS'][0, 0][0]                                             # Dictionary
#
# N, K = D.shape
# L = 40000
# Ds = Dictionarys(D, K, N, L, transform)                                 #define Dictionary
# Dimg = glob.glob(path_in + '//**//*.bmp', recursive=True)               # Image path
# NumberImages = len(Dimg)
#
# Bitrate_JPEG = np.transpose(
#     h5py.File('.//Results//Bitrate_JPEG_' + foldername + '.mat', 'r')['Bitrate_JPEG'])  # load bitrate
# Quality_JPEG = np.transpose(
#     h5py.File('.//Results//Quality_JPEG_' + foldername + '.mat', 'r')['Quality_JPEG'])  # load quality
#
# numBit = Bitrate_JPEG.shape[1]
#
# Bitrate_Dict = np.zeros((NumberImages, numBit))
# Quality_Dict = np.zeros((NumberImages, numBit))
#
# #encode, this can be divided into two steps, including dictionary encoding and res encoding
# #dictionary encode
# for ind_img in range(NumberImages):
#     Quality_Dict[ind_img, :], Bitrate_Dict[ind_img,:], res, xc, dele, deldc, thr, thrdc, xc_encoded, res_encoded = test_Dict(Dimg[ind_img], Bitrate_JPEG[[ind_img], :], Ds)
#
# for i in range(8):
#     xc[i] = list(xc[i])
#
# xc_encoded = zip.compress((str(xc)).encode(), 9)
#
# #dictionary encode
# h, w = res.shape
# res = res.reshape((-1, 1))
# res_list = []
# for i in range(len(res)):
#     res_list.append(res[i, 0])
#
# res_encoded = zip.compress((str(res_list)).encode(), 9)
# print(len(xc_encoded))
# print((len(res_encoded) + len(xc_encoded)))
# encode_end = time.time()
# print('encode time:')
# print((encode_end-encode_start)*1000)
#
# #decode
# start_time = time.time()
#
# #res decode
# res_decode = (zip.decompress(res_encoded)).decode()
# res = eval(res_decode)
# res = np.array(res)
# res = res.reshape((h, w))
#
# #dictionary decode
# xc_decode = (zip.decompress(xc_encoded)).decode()
# xc_decode = xc_decode.split('], [')
# xc_decode[0] = xc_decode[0][2:]
# xc_decode[len(xc_decode) - 1] = xc_decode[len(xc_decode) - 1][0:-2]
# for i in range(8):
#     if xc_decode[i] == '':
#         xc_decode[i] = []
#     else:
#         xc_decode[i] = list(eval(xc_decode[i]))
# xc = xc_decode
#
# xCw = xc[0:4]
# xCdc = xc[4:]
# Zw_r = myreshape(xCw)
# Zdc_r = mypred(xCdc)
# Zdc_r = (Zdc_r.T).reshape(1, 988)
#
# Qdc = uniquant(Zdc_r, deldc, thrdc)             # inverse quantizing
# Qw = uniquant(Zw_r, dele, thr)                  # inverse quantizing
# dcnz = np.sum(Zdc_r != 0)                       # number of non-zeros in DC
# S = np.sum(Zw_r != 0)                           # selected number of non-zeros for each column
# sumS = np.sum(S)
#
# Xr = np.concatenate((Qdc, np.zeros((N - 1, 988))), axis=0)
# Xa = np.dot(np.float64(Ds.D), Qw)
# Ar = mycol2im(Xr + Xa, transform='m79', imsize=[304, 208], size=[8, 8])
# Ar = Ar[0: 301, 0: 201]
#
# pic = Image.fromarray(np.uint8(Ar + res + 128))
# pic.save('test.png')
# end_time = time.time()
# print('finish, and the img is saved as test.png')
# print('decoding time:')
# print((end_time - start_time) * 1000)

# test on RGB
# init
foldername = "GenerallImages"
blk_size = 8
transform = "m79"

path_in = os.path.join(PROJECT_ROOT, 'Images', foldername, 'Test2')  # Image dir path
data = scio.loadmat(os.path.join(PROJECT_ROOT, 'Dictionary', "Dict_RLS_" + foldername + ".mat"))  # load Dictionary path

D = data["dlsRLS"][0, 0][0]  # Dictionary

N, K = D.shape
L = 40000
Ds = Dictionarys(D, K, N, L, transform)  # define Dictionary
Dimg = glob.glob(os.path.join(path_in, "*.bmp"), recursive=True)  # Image path
NumberImages = len(Dimg)
print(NumberImages)

Bitrate_JPEG = np.transpose(
    h5py.File(os.path.join(PROJECT_ROOT, "Results", "Bitrate_JPEG_" + foldername + ".mat"), "r")["Bitrate_JPEG"]
)  # load bitrate
Quality_JPEG = np.transpose(
    h5py.File(os.path.join(PROJECT_ROOT, "Results", "Quality_JPEG_" + foldername + ".mat"), "r")["Quality_JPEG"]
)  # load quality

numBit = Bitrate_JPEG.shape[1]

Bitrate_Dict = np.zeros((NumberImages, numBit))
Quality_Dict = np.zeros((NumberImages, numBit))

# encode, this can be divided into two steps, including dictionary encoding and res encoding
# dictionary encode
# NumberImages = 1
for ind_img in range(NumberImages):
    # print(Dimg[ind_img])
    # img = Image.open(Dimg[ind_img]).convert('RGB')
    # ind_img = 48
    encode_start = time.time()
    path = os.path.join(path_in, str(ind_img)+ ".bmp")
    print(path)
    img = Image.open(path).convert("RGB")
    img = np.array(img, dtype=np.double)
    h, w = img.shape[0], img.shape[1]
    ad_h, ad_w = int(np.ceil(h / 8) * 8), int(np.ceil(w / 8) * 8)
    (
        Quality_Dict[ind_img, :],
        Bitrate_Dict[ind_img, :],
        res_R,
        xc_R,
        dele_R,
        deldc_R,
        thr_R,
        thrdc_R,
        _,
        _,
    ) = test_Dict(img[:, :, 0], Bitrate_JPEG[[ind_img], :], Ds)
    (
        Quality_Dict[ind_img, :],
        Bitrate_Dict[ind_img, :],
        res_G,
        xc_G,
        dele_G,
        deldc_G,
        thr_G,
        thrdc_G,
        _,
        _,
    ) = test_Dict(img[:, :, 1], Bitrate_JPEG[[ind_img], :], Ds)
    (
        Quality_Dict[ind_img, :],
        Bitrate_Dict[ind_img, :],
        res_B,
        xc_B,
        dele_B,
        deldc_B,
        thr_B,
        thrdc_B,
        _,
        _,
    ) = test_Dict(img[:, :, 2], Bitrate_JPEG[[ind_img], :], Ds)

    for i in range(len(xc_R)):
        xc_R[i] = list(xc_R[i])
        xc_G[i] = list(xc_G[i])
        xc_B[i] = list(xc_B[i])

    xc_encoded_R = zip_lib.compress((str(xc_R)).encode(), 9)
    xc_encoded_G = zip_lib.compress((str(xc_G)).encode(), 9)
    xc_encoded_B = zip_lib.compress((str(xc_B)).encode(), 9)

    # dictionary encode
    h, w = res_R.shape

    # res_R = res_R.reshape((-1, 1))
    # res_G = res_G.reshape((-1, 1))
    # res_B = res_B.reshape((-1, 1))
    # res_list_R = []
    # res_list_G = []
    # res_list_B = []
    # for i in range(len(res_R)):
    #     res_list_R.append(res_R[i, 0])
    #     res_list_G.append(res_G[i, 0])
    #     res_list_B.append(res_B[i, 0])

    res_R = res_R.reshape((1, -1))
    res_G = res_G.reshape((1, -1))
    res_B = res_B.reshape((1, -1))
    res_list_R = []
    res_list_G = []
    res_list_B = []
    for i in range(len(res_R[0])):
        res_list_R.append(res_R[0, i])
        res_list_G.append(res_G[0, i])
        res_list_B.append(res_B[0, i])

    res_encoded_R = zip_lib.compress((str(res_list_R)).encode(), 9)
    res_encoded_G = zip_lib.compress((str(res_list_G)).encode(), 9)
    res_encoded_B = zip_lib.compress((str(res_list_B)).encode(), 9)

    print(len(xc_encoded_R) + len(xc_encoded_G) + len(xc_encoded_B))
    print(
        len(xc_encoded_R)
        + len(xc_encoded_G)
        + len(xc_encoded_B)
        + len(res_encoded_R)
        + len(res_encoded_G)
        + len(res_encoded_B)
    )
    encode_end = time.time()
    print("encode time:")
    print((encode_end - encode_start) * 1000)

    # decode
    start_time = time.time()

    # res decode
    res_decode_R = (zip_lib.decompress(res_encoded_R)).decode()
    res_decode_G = (zip_lib.decompress(res_encoded_G)).decode()
    res_decode_B = (zip_lib.decompress(res_encoded_B)).decode()
    res_R = eval(res_decode_R)
    res_G = eval(res_decode_G)
    res_B = eval(res_decode_B)
    res_R = np.array(res_R)
    res_G = np.array(res_G)
    res_B = np.array(res_B)
    res_R = res_R.reshape((h, w))
    res_G = res_G.reshape((h, w))
    res_B = res_B.reshape((h, w))

    # dictionary decode

    loss_start_time = time.time()
    xc_encoded = [xc_encoded_R, xc_encoded_G, xc_encoded_B]
    deldc = [deldc_R, deldc_G, deldc_B]
    thrdc = [thrdc_R, thrdc_G, thrdc_B]
    dele = [dele_R, dele_G, dele_B]
    thr = [thr_R, thr_G, thr_B]

    for idx_channel in range(3):
        xc_decode = (zip_lib.decompress(xc_encoded[idx_channel])).decode()
        # xc_decode = xc_decode.split('], [')
        # xc_decode[0] = xc_decode[0][2:]
        # xc_decode[len(xc_decode) - 1] = xc_decode[len(xc_decode) - 1][0:-2]
        # for i in range(len(xc_R)):
        #     if xc_decode[i] == '':
        #         xc_decode[i] = []
        #     else:
        #         xc_decode[i] = list(eval(xc_decode[i]))
        xc = eval(xc_decode)

        xCw = xc[0:4]
        xCdc = xc[4:]
        Zw_r = myreshape(xCw)
        Zdc_r = mypred(xCdc)
        Zdc_r = (Zdc_r.T).reshape(1, -1)

        Qdc = uniquant(
            Zdc_r, deldc[idx_channel], thrdc[idx_channel]
        )  # inverse quantizing
        Qw = uniquant(Zw_r, dele[idx_channel], thr[idx_channel])  # inverse quantizing
        dcnz = np.sum(Zdc_r != 0)  # number of non-zeros in DC
        S = np.sum(Zw_r != 0)  # selected number of non-zeros for each column
        sumS = np.sum(S)

        Xr = np.concatenate((Qdc, np.zeros((N - 1, int(ad_h * ad_w // 64)))), axis=0)
        Xa = np.dot(np.float64(Ds.D), Qw)
        Ar = mycol2im(Xr + Xa, transform="m79", imsize=[ad_h, ad_w], size=[8, 8])
        Ar = Ar[0:h, 0:w]
        if idx_channel == 0:
            Ar_R = Ar
        if idx_channel == 1:
            Ar_G = Ar
        if idx_channel == 2:
            Ar_B = Ar
    Ar = np.stack((Ar_R, Ar_G, Ar_B), 2)
    Ar_withloss = np.clip(Ar, -128, 127)
    pic = Image.fromarray(np.uint8(Ar_withloss + 128))
    pic.save("test_with_loss.png")
    loss_end_time = time.time()
    print("decode time with loss:")
    print((loss_end_time - loss_start_time) * 1000)

    res = np.stack((res_R, res_G, res_B), 2)
    pic = Image.fromarray(np.uint8(np.int8(Ar + 0.5 * np.sign(Ar)) + res) + 128)
    pic.save("test_no_loss.png")
    end_time = time.time()

    print("finish, and the img is saved as test.png")
    print("decoding time:")
    print((end_time - start_time) * 1000)

    print(np.var(np.uint8(np.int8(Ar + 0.5 * np.sign(Ar)) + res + 128) - np.uint8(img)))
    print(compute_psnr(np.uint8(img), np.uint8(Ar_withloss + 128)))


# # test on RGB
# # init
# encode_start = time.time()
# foldername = 'GenerallImages'
# blk_size = 8
# transform = 'm79'
#
# path_in = './/Images//' + foldername + '//Test2//'                      # Image dir path
# data = scio.loadmat('.//Dictionary//Dict_RLS_' + foldername + '.mat')   # load Dictionary path
# D = data['dlsRLS'][0, 0][0]                                             # Dictionary
#
# N, K = D.shape
# L = 40000
# Ds = Dictionarys(D, K, N, L, transform)                                 #define Dictionary
# Dimg = glob.glob(path_in + '//**//*.bmp', recursive=True)               # Image path
# NumberImages = len(Dimg)
#
# Bitrate_JPEG = np.transpose(
#     h5py.File('.//Results//Bitrate_JPEG_' + foldername + '.mat', 'r')['Bitrate_JPEG'])  # load bitrate
# Quality_JPEG = np.transpose(
#     h5py.File('.//Results//Quality_JPEG_' + foldername + '.mat', 'r')['Quality_JPEG'])  # load quality
#
# numBit = Bitrate_JPEG.shape[1]
#
# Bitrate_Dict = np.zeros((NumberImages, numBit))
# Quality_Dict = np.zeros((NumberImages, numBit))
#
# #encode, this can be divided into two steps, including dictionary encoding and res encoding
# #dictionary encode
# for ind_img in range(NumberImages):
#     Quality_Dict[ind_img, :], Bitrate_Dict[ind_img,:], res_R, xc_R, dele_R, deldc_R, thr_R, thrdc_R, _, _ = test_Dict(Dimg[ind_img], Bitrate_JPEG[[ind_img], :], Ds)
# for i in range(8):
#     xc_R[i] = list(xc_R[i])
#     xc_G[i] = list(xc_G[i])
#     xc_B[i] = list(xc_B[i])
#
# xc_encoded_R = zip.compress((str(xc_R)).encode(), 9)
# xc_encoded_G = zip.compress((str(xc_G)).encode(), 9)
# xc_encoded_B = zip.compress((str(xc_B)).encode(), 9)
#
# #dictionary encode
# h, w = res_R.shape
#
# res_R = res_R.reshape((-1, 1))
# res_G = res_G.reshape((-1, 1))
# res_B = res_B.reshape((-1, 1))
# res_list_R = []
# res_list_G = []
# res_list_B = []
# for i in range(len(res_R)):
#     res_list_R.append(res_R[i, 0])
#     res_list_G.append(res_G[i, 0])
#     res_list_B.append(res_B[i, 0])
#
# res_encoded_R = zip.compress((str(res_list_R)).encode(), 9)
# res_encoded_G = zip.compress((str(res_list_G)).encode(), 9)
# res_encoded_B = zip.compress((str(res_list_B)).encode(), 9)
#
# print(len(xc_encoded_R)+len(xc_encoded_G)+len(xc_encoded_B))
# print(len(xc_encoded_R)+len(xc_encoded_G)+len(xc_encoded_B)+len(res_encoded_R)+len(res_encoded_G)+len(res_encoded_B))
# encode_end = time.time()
# print('encode time:')
# print((encode_end-encode_start)*1000)
#
# #decode
# start_time = time.time()
#
# #res decode
# res_decode_R = (zip.decompress(res_encoded_R)).decode()
# res_decode_G = (zip.decompress(res_encoded_G)).decode()
# res_decode_B = (zip.decompress(res_encoded_B)).decode()
# res_R = eval(res_decode_R)
# res_G = eval(res_decode_G)
# res_B = eval(res_decode_B)
# res_R = np.array(res_R)
# res_G = np.array(res_G)
# res_B = np.array(res_B)
# res_R = res_R.reshape((h, w))
# res_G = res_G.reshape((h, w))
# res_B = res_B.reshape((h, w))
#
# #dictionary decode
# xc_encoded = [xc_encoded_R,xc_encoded_G,xc_encoded_B]
# deldc = [deldc_R,deldc_G,deldc_B]
# thrdc = [thrdc_R,thrdc_G,thrdc_B]
# dele = [dele_R,dele_G,dele_B]
# thr = [thr_R,thr_G,thr_B]
#
# for idx_channel in range(3):
#     xc_decode = (zip.decompress(xc_encoded[idx_channel])).decode()
#     xc_decode = xc_decode.split('], [')
#     xc_decode[0] = xc_decode[0][2:]
#     xc_decode[len(xc_decode) - 1] = xc_decode[len(xc_decode) - 1][0:-2]
#     for i in range(8):
#         if xc_decode[i] == '':
#             xc_decode[i] = []
#         else:
#             xc_decode[i] = list(eval(xc_decode[i]))
#     xc = xc_decode
#
#     xCw = xc[0:4]
#     xCdc = xc[4:]
#     Zw_r = myreshape(xCw)
#     Zdc_r = mypred(xCdc)
#     Zdc_r = (Zdc_r.T).reshape(1, -1)
#
#     Qdc = uniquant(Zdc_r, deldc[idx_channel], thrdc[idx_channel])             # inverse quantizing
#     Qw = uniquant(Zw_r, dele[idx_channel], thr[idx_channel])                  # inverse quantizing
#     dcnz = np.sum(Zdc_r != 0)                       # number of non-zeros in DC
#     S = np.sum(Zw_r != 0)                           # selected number of non-zeros for each column
#     sumS = np.sum(S)
#
#     Xr = np.concatenate((Qdc, np.zeros((N - 1, 1024))), axis=0)
#     Xa = np.dot(np.float64(Ds.D), Qw)
#     Ar = mycol2im(Xr + Xa, transform='m79', imsize=[256, 256], size=[8, 8])
#     # Ar = Ar[0: 301, 0: 201]
#     if idx_channel == 0:
#         Ar_R = Ar
#     if idx_channel == 1:
#         Ar_G = Ar
#     if idx_channel == 2:
#         Ar_B = Ar
#
# res = np.stack((res_R,res_G,res_B),2)
# Ar = np.stack((Ar_R,Ar_G,Ar_B),2)
# pic = Image.fromarray(np.uint8(Ar + res + 128))
# pic.save('test_no_loss.png')
# end_time = time.time()
# pic = Image.fromarray(np.uint8(Ar + 128))
# pic.save('test_with_loss.png')
# print('finish, and the img is saved as test.png')
# print('decoding time:')
# print((end_time - start_time) * 1000)
