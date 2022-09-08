import numpy as np
import zlib as zip_lib

from utils import test_Dict_par
from imageapprox import imageapprox


# TODO 取个更好的名字
def test_Dict(img, Ds):
    img = np.array(img, dtype=np.double)
    img_hgt, img_wdt = img.shape
    cur_QP = 69
    im_128 = img - 128
    par = test_Dict_par(
        dictionary=Ds,
        targetPSNR=100 - cur_QP,
        dele=-18,
        qLimit=[0.4, 1.00],
        estimateBits="Huff06",
        ompMethod="mexOMP",
        verbose=0,
    )

    # test_Dict_par is a dict in python
    Ar, PSNR, xC, dele, deldc, thr, thrdc = imageapprox(
        im_128, par
    )  # the main processing function

    xc_encoded = zip_lib.compress((str(xC)).encode())
    bits = len(xc_encoded) * 8

    bestPSNR = PSNR
    bpp_val_each = bits / (img_hgt * img_wdt)

    # search for better results
    while bestPSNR < 30:
        cur_QP = cur_QP - 1

        par = test_Dict_par(
            dictionary=Ds,
            targetPSNR=100 - cur_QP,
            dele=-18,
            qLimit=[0.4, 1.00],
            estimateBits="Huff06",
            ompMethod="mexOMP",
            verbose=0,
        )
        Ar_now, PSNR_now, xC_now, dele, deldc, thr, thrdc = imageapprox(im_128, par)

        if bestPSNR > PSNR_now:
            break
        else:
            Ar = Ar_now
            xC = xC_now
            xc_encoded = zip_lib.compress((str(xC)).encode())
            bits = len(xc_encoded) * 8
            bestPSNR = PSNR_now
            bpp_val_each = (bits) / (img_hgt * img_wdt)

    dele_fin, deldc_fin, thr_fin, thrdc_fin = dele, deldc, thr, thrdc
    psnr_val = bestPSNR
    bpp_val = bpp_val_each

    res_Ar = np.int8((im_128 - Ar) + 0.5 * np.sign(im_128 - Ar))
    res_encoded = zip_lib.compress((str(res_Ar)).encode())

    return (
        psnr_val,
        bpp_val,
        res_Ar,
        xC,
        dele_fin,
        deldc_fin,
        thr_fin,
        thrdc_fin,
        xc_encoded,
        res_encoded,
    )
