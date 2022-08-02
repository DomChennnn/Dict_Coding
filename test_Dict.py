import numpy as np
from PIL import Image
import bz2

from utils import test_Dict_par
from imageapprox import imageapprox

#gray
def test_Dict(filename, Bitrate_JPEG, Ds):
    img = Image.open(filename)
    img = np.array(img,dtype=np.double)
    img_hgt, img_wdt = img.shape
    numBits = Bitrate_JPEG.shape[1]
    psnr_val = np.zeros((1, numBits))
    bpp_val = np.zeros((1,numBits))
    cur_QP = 80
    im_128 = img - 128

    for num in range(numBits):
        # print(num)
        if num > 8:
            break

        bestQP = cur_QP
        target_bpp = Bitrate_JPEG[0, num]
        par = test_Dict_par(dictionary = Ds, targetPSNR = 100 - cur_QP, dele = -18, qLimit = [0.4, 1.00], estimateBits = 'Huff06', ompMethod = 'mexOMP', verbose = 0)
        Ar, PSNR, xC, dele, deldc, thr, thrdc = imageapprox(im_128, par)

        xc_encoded = bz2.compress((str(xC)).encode())
        bits = len(xc_encoded)*8

        bestPSNR = PSNR
        bpp_val_each = bits/(img_hgt*img_wdt)
        cur_bpp = bpp_val_each

        if cur_bpp > target_bpp:
            dir1 = -1
        else:
            dir1 = 1

        dirchange = 0
        while (abs(target_bpp - cur_bpp)>0.05) and bestPSNR<30:
        # while (target_bpp>cur_bpp) or bestPSNR<40:
        # while 1:
            if cur_bpp > target_bpp:
                cur_QP = cur_QP + 1
                if dir1==-1:
                    dirchange = 0
                else:
                    dirchange = 1

            else:
                cur_QP = cur_QP - 1
                if dir1==1:
                    dirchange = 0
                else:
                    dirchange = 1

            if dirchange==1 or cur_QP>99 or cur_QP<1:
                break

            par = test_Dict_par(dictionary=Ds, targetPSNR=100 - cur_QP, dele=-18, qLimit=[0.4, 1.00],
                                estimateBits='Huff06', ompMethod='mexOMP', verbose=0)
            Ar_now, PSNR_now, xC_now, dele, deldc, thr, thrdc = imageapprox(im_128, par)


            if bestPSNR>PSNR_now:
                # xc_encoded = bz2.compress((str(xC)).encode())
                # bits = len(xc_encoded)*8
                # bpp_val_each = (bits) / (img_hgt * img_wdt)
                # cur_bpp = bpp_val_each
                break
            else:
                Ar = Ar_now
                xC = xC_now
                xc_encoded = bz2.compress((str(xC)).encode())
                bits = len(xc_encoded) * 8
                bestPSNR = PSNR_now
                bpp_val_each = (bits) / (img_hgt * img_wdt)
                cur_bpp = bpp_val_each
                dele_fin, deldc_fin, thr_fin, thrdc_fin = dele, deldc, thr, thrdc



        psnr_val[0,num] = bestPSNR
        bpp_val[0,num] = bpp_val_each

    res_Ar = np.uint8(im_128 - Ar)
    res_encoded = bz2.compress((str(res_Ar)).encode())

    ori = im_128.reshape((-1,1))
    ori_list = []
    for i in range(len(ori)):
        ori_list.append(ori[i, 0])
    ori_encoded = bz2.compress((str(ori_list)).encode())
    ori_bits = len(ori_encoded)
    print(ori_bits)
    #
    #
    img_Ar = Image.fromarray(np.uint8(Ar+res_Ar+128))
    img_Ar.save('test_noloss.png')


    return psnr_val, bpp_val,res_Ar ,xC, dele_fin, deldc_fin, thr_fin, thrdc_fin, xc_encoded, res_encoded



# #RGB
# def test_Dict(filename, Bitrate_JPEG, Ds):
#     img = Image.open(filename).convert('RGB')
#     img = np.array(img,dtype=np.double)
#     img_hgt, img_wdt, img_ch = img.shape
#     numBits = Bitrate_JPEG.shape[1]
#     psnr_val = np.zeros((1, numBits))
#     bpp_val = np.zeros((1,numBits))
#     cur_QP = 80
#     im_128 = img - 128
#
#     for num in range(numBits):
#         # print(num)
#         if num > 8:
#             break
#
#         bestQP = cur_QP
#         target_bpp = Bitrate_JPEG[0, num]
#         par = test_Dict_par(dictionary = Ds, targetPSNR = 100 - cur_QP, dele = -18, qLimit = [0.4, 1.00], estimateBits = 'Huff06', ompMethod = 'mexOMP', verbose = 0)
#         for i in range(3):
#             Ar, PSNR, xC = imageapprox(im_128[:,:,i], par)
#
#             xc_encoded = bz2.compress((str(xC)).encode())
#             bits = len(xc_encoded)*8
#
#             bestPSNR = PSNR
#             bpp_val_each = bits/(img_hgt*img_wdt)
#             cur_bpp = bpp_val_each
#
#             if i == 0:
#                 R = Ar
#             elif i == 1:
#                 G = Ar
#             else:
#                 B = Ar
#
#         if cur_bpp > target_bpp:
#             dir1 = -1
#         else:
#             dir1 = 1
#
#         dirchange = 0
#         while (abs(target_bpp - cur_bpp)>0.05) and bestPSNR<40:
#         # while (target_bpp>cur_bpp) or bestPSNR<40:
#             if cur_bpp > target_bpp:
#                 cur_QP = cur_QP + 1
#                 if dir1==-1:
#                     dirchange = 0
#                 else:
#                     dirchange = 1
#
#             else:
#                 cur_QP = cur_QP - 1
#                 if dir1==1:
#                     dirchange = 0
#                 else:
#                     dirchange = 1
#
#             if dirchange==1 or cur_QP>99 or cur_QP<1:
#                 break
#
#             par = test_Dict_par(dictionary=Ds, targetPSNR=100 - cur_QP, dele=-18, qLimit=[0.4, 1.00],
#                                 estimateBits='Huff06', ompMethod='mexOMP', verbose=0)
#             for i in range(3):
#                 Ar_now, PSNR_now, xC_now = imageapprox(im_128[:,:,i], par)
#
#                 if bestPSNR>PSNR_now:
#                     # xc_encoded = bz2.compress((str(xC)).encode())
#                     # bits = len(xc_encoded)*8
#                     # bpp_val_each = (bits) / (img_hgt * img_wdt)
#                     # cur_bpp = bpp_val_each
#                     continue
#                 else:
#                     Ar = Ar_now
#                     xC = xC_now
#                     xc_encoded = bz2.compress((str(xC)).encode())
#                     bits = len(xc_encoded) * 8
#                     bestPSNR = PSNR_now
#                     bpp_val_each = (bits) / (img_hgt * img_wdt)
#                     cur_bpp = bpp_val_each
#
#                 if i == 0:
#                     R = Ar
#                     bits_R = bits
#                 elif i == 1:
#                     G = Ar
#                     bits_G = bits
#                 else:
#                     B = Ar
#                     bits_B = bits
#
#         psnr_val[0,num] = bestPSNR
#         bpp_val[0,num] = bpp_val_each
#     bits = bits_R+bits_G+bits_B
#     RGB = np.stack((R,G,B),2)
#
#     res_Ar = np.uint8(im_128 - RGB)
#     res_encoded = bz2.compress((str(res_Ar)).encode())
#     res_bits = len(res_encoded) * 8
#     print(res_bits+bits)
#
#
#     img_Ar = Image.fromarray(np.uint8(RGB+res_Ar+128))
#     img_Ar.save('test_noloss.png')
#     return psnr_val, bpp_val

