import numpy as np
from PIL import Image
import bz2

from utils import test_Dict_par
from imageapprox import imageapprox

# one channel test
def test_Dict(img, Bitrate_JPEG, Ds):
    #init
    # img = Image.open(filename)
    img = np.array(img,dtype=np.double)
    img_hgt, img_wdt = img.shape
    numBits = Bitrate_JPEG.shape[1]
    psnr_val = np.zeros((1, numBits))
    bpp_val = np.zeros((1,numBits))
    cur_QP = 69
    im_128 = img - 128

    # for num in range(numBits):
    #     if num > 8:
    #         break
    bestQP = cur_QP
    target_bpp = 0.2
    # target_bpp = Bitrate_JPEG[0, num]
    par = test_Dict_par(dictionary = Ds, targetPSNR = 100 - cur_QP, dele = -18, qLimit = [0.4, 1.00], estimateBits = 'Huff06', ompMethod = 'mexOMP', verbose = 0)
    #test_Dict_par is a dict in python
    Ar, PSNR, xC, dele, deldc, thr, thrdc = imageapprox(im_128, par) #the main processing function

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

    #search for better results
    while bestPSNR<30:
        # if cur_bpp > target_bpp:
        #     cur_QP = cur_QP + 1
        #     if dir1==-1:
        #         dirchange = 0
        #     else:
        #         dirchange = 1
        #
        # else:
        #     cur_QP = cur_QP - 1
        #     if dir1==1:
        #         dirchange = 0
        #     else:
        #         dirchange = 1
        #
        # if dirchange==1 or cur_QP>99 or cur_QP<1:
        #     break
        cur_QP = cur_QP - 1


        par = test_Dict_par(dictionary=Ds, targetPSNR=100 - cur_QP, dele=-18, qLimit=[0.4, 1.00],
                            estimateBits='Huff06', ompMethod='mexOMP', verbose=0)
        Ar_now, PSNR_now, xC_now, dele, deldc, thr, thrdc = imageapprox(im_128, par)


        if bestPSNR>PSNR_now:
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
    psnr_val = bestPSNR
    bpp_val = bpp_val_each

    # res_Ar = np.int8(im_128 - np.clip(Ar,-128,127))
    res_Ar = np.int8((im_128 - Ar) + 0.5 * np.sign(im_128 - Ar))

    res_encoded = bz2.compress((str(res_Ar)).encode())

    # rec = np.int8(Ar+0.5 * np.sign(Ar))+res_Ar
    # res = rec-im_128
    # print(np.var(res))
    # print('bestPSNR:')
    # print(bestPSNR)
    # ori = im_128.reshape((-1,1))
    # ori_list = []
    # for i in range(len(ori)):
    #     ori_list.append(ori[i, 0])
    # ori_encoded = bz2.compress((str(ori_list)).encode())
    # ori_bits = len(ori_encoded)

    # save img
    # img_Ar = Image.fromarray(np.uint8(np.clip(Ar,-128,127) + 128))
    # img_Ar.save('test_with_loss.png')
    # img_Ar = Image.fromarray(np.uint8(Ar+res_Ar+128))
    # img_Ar.save('test_noloss.png')


    return psnr_val, bpp_val,res_Ar ,xC, dele_fin, deldc_fin, thr_fin, thrdc_fin, xc_encoded, res_encoded


#
# # RGB
# def test_Dict(filename, Bitrate_JPEG, Ds):
#     img = Image.open(filename).convert('RGB')
#     img = np.array(img,dtype=np.double)
#     img_hgt, img_wdt, img_ch = img.shape
#     numBits = Bitrate_JPEG.shape[1]
#     psnr_val = np.zeros((1, numBits))
#     bpp_val = np.zeros((1,numBits))
#     cur_QP = 60
#     im_128 = img - 128
#
#
#     bestQP = cur_QP
#     target_bpp = 0.2
#     par = test_Dict_par(dictionary = Ds, targetPSNR = 100 - cur_QP, dele = -18, qLimit = [0.4, 1.00], estimateBits = 'Huff06', ompMethod = 'mexOMP', verbose = 0)
#     for i in range(3):
#         Ar, PSNR, xC, dele, deldc, thr, thrdc = imageapprox(im_128[:,:,i], par)
#
#         xc_encoded = bz2.compress((str(xC)).encode())
#         bits = len(xc_encoded)*8
#
#         bestPSNR = PSNR
#         bpp_val_each = bits/(img_hgt*img_wdt)
#         cur_bpp = bpp_val_each
#
#         if i == 0:
#             R = Ar
#             bits_R, dele_R, deldc_R, thr_R, thrdc_R = bits, dele, deldc, thr, thrdc
#         elif i == 1:
#             G = Ar
#             bits_G, dele_G, deldc_G, thr_G, thrdc_G = bits, dele, deldc, thr, thrdc
#         else:
#             B = Ar
#             bits_B, dele_B, deldc_B, thr_B, thrdc_B = bits, dele, deldc, thr, thrdc
#
#     if cur_bpp > target_bpp:
#         dir1 = -1
#     else:
#         dir1 = 1
#
#     dirchange = 0
#     while bestPSNR<30:
#         if cur_bpp > target_bpp:
#             cur_QP = cur_QP + 1
#             if dir1==-1:
#                 dirchange = 0
#             else:
#                 dirchange = 1
#
#         else:
#             cur_QP = cur_QP - 1
#             if dir1==1:
#                 dirchange = 0
#             else:
#                 dirchange = 1
#
#         if dirchange==1 or cur_QP>99 or cur_QP<1:
#             break
#
#         par = test_Dict_par(dictionary=Ds, targetPSNR=100 - cur_QP, dele=-18, qLimit=[0.4, 1.00],
#                             estimateBits='Huff06', ompMethod='mexOMP', verbose=0)
#         for i in range(3):
#             Ar_now, PSNR_now, xC_now, dele, deldc, thr, thrdc = imageapprox(im_128[:,:,i], par)
#
#             if bestPSNR>PSNR_now:
#                 continue
#             else:
#                 xC = xC_now
#                 xc_encoded = bz2.compress((str(xC)).encode())
#                 bits = len(xc_encoded) * 8
#                 bestPSNR = PSNR_now
#                 bpp_val_each = (bits) / (img_hgt * img_wdt)
#                 cur_bpp = bpp_val_each
#
#                 if i == 0:
#                     R = Ar_now
#                     bits_R, dele_R, deldc_R, thr_R, thrdc_R = bits, dele, deldc, thr, thrdc
#                 elif i == 1:
#                     G = Ar_now
#                     bits_G, dele_G, deldc_G, thr_G, thrdc_G = bits, dele, deldc, thr, thrdc
#                 else:
#                     B = Ar_now
#                     bits_B, dele_B, deldc_B, thr_B, thrdc_B = bits, dele, deldc, thr, thrdc
#
#     psnr_val = bestPSNR
#     bpp_val = bpp_val_each
#     bits = bits_R + bits_G + bits_B
#     RGB = np.stack((R,G,B),2)
#
#     res_Ar = np.uint8(im_128 - RGB)
#     res_encoded = bz2.compress((str(res_Ar)).encode())
#     res_bits = len(res_encoded) * 8
#     print(res_bits+bits)
#
#     dele_fin = [dele_R, dele_G, dele_B]
#     deldc_fin = [deldc_R, deldc_G, deldc_B]
#     thr_fin = [thr_R,thr_G, thr_B]
#     thrdc_fin = [thrdc_R,thrdc_G,thrdc_B]
#
#     img_Ar = Image.fromarray(np.uint8(np.clip((RGB+128),0,255)))
#     img_Ar.save('test_with_loss.png')
#
#     img_Ar = Image.fromarray(np.uint8(RGB+res_Ar+128))
#     img_Ar.save('test_no_loss.png')
#     return psnr_val, bpp_val,res_Ar ,xC, dele_fin, deldc_fin, thr_fin, thrdc_fin, xc_encoded, res_encoded


# # maybe used in lossless compression
# def test_Dict(filename, Bitrate_JPEG, Ds):
#     img = Image.open(filename).convert('RGB')
#     img = np.array(img,dtype=np.double)
#     img_hgt, img_wdt, img_ch = img.shape
#     numBits = Bitrate_JPEG.shape[1]
#     psnr_val = np.zeros((1, numBits))
#     bpp_val = np.zeros((1,numBits))
#     cur_QP = 66
#     im_128 = img - 128
#
#     # for num in range(numBits):
#     #     # print(num)
#     #     if num > 8:
#     #         break
#
#     bestQP = cur_QP
#     target_bpp = 0.2
#     par = test_Dict_par(dictionary = Ds, targetPSNR = 100 - cur_QP, dele = -18, qLimit = [0.4, 1.00], estimateBits = 'Huff06', ompMethod = 'mexOMP', verbose = 0)
#
#     img_in = np.concatenate((im_128[:,:,0],im_128[:,:,1],im_128[:,:,2]),axis=0)
#     Ar, PSNR, xC, dele, deldc, thr, thrdc = imageapprox(img_in, par)
#
#     xc_encoded = bz2.compress((str(xC)).encode())
#     bits = len(xc_encoded)*8
#
#     bestPSNR = PSNR
#     bpp_val_each = bits/(img_hgt*img_wdt)
#     cur_bpp = bpp_val_each
#
#     if cur_bpp > target_bpp:
#         dir1 = -1
#     else:
#         dir1 = 1
#
#     dirchange = 0
#     while bestPSNR<40:
#         if cur_bpp > target_bpp:
#             cur_QP = cur_QP + 1
#             if dir1==-1:
#                 dirchange = 0
#             else:
#                 dirchange = 1
#
#         else:
#             cur_QP = cur_QP - 1
#             if dir1==1:
#                 dirchange = 0
#             else:
#                 dirchange = 1
#
#         if dirchange==1 or cur_QP>99 or cur_QP<1:
#             break
#
#         par = test_Dict_par(dictionary=Ds, targetPSNR=100 - cur_QP, dele=-18, qLimit=[0.4, 1.00],
#                             estimateBits='Huff06', ompMethod='mexOMP', verbose=0)
#
#         Ar_now, PSNR_now, xC_now, dele, deldc, thr, thrdc = imageapprox(img_in, par)
#
#         if bestPSNR>PSNR_now:
#             continue
#         else:
#             Ar = Ar_now
#             xC = xC_now
#             xc_encoded = bz2.compress((str(xC)).encode())
#             bits = len(xc_encoded) * 8
#             bestPSNR = PSNR_now
#             bpp_val_each = (bits) / (img_hgt * img_wdt)
#             cur_bpp = bpp_val_each
#
#     dele_fin, deldc_fin, thr_fin, thrdc_fin = dele, deldc, thr, thrdc
#     psnr_val = bestPSNR
#     bpp_val = bpp_val_each
#
#     R = Ar[0:256,:]
#     G = Ar[256:512,:]
#     B = Ar[512:,:]
#
#     RGB = np.stack((R,G,B),2)
#
#     res_Ar = np.uint8(im_128 - RGB)
#     res_encoded = bz2.compress((str(res_Ar)).encode())
#
#     img_Ar = Image.fromarray(np.uint8(np.clip(RGB,-128,127)+128))
#     img_Ar.save('test_RGB.png')
#     return psnr_val, bpp_val,res_Ar ,xC, dele_fin, deldc_fin, thr_fin, thrdc_fin, xc_encoded, res_encoded