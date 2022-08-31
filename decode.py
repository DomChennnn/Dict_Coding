import zlib
import numpy as np
from PIL import Image

from mycol2im import mycol2im
from uniquant import uniquant
from myreshape import myreshape
from mypred import mypred
from utils import read_uints


def img_decode(path_in, path_out, Ds, mode=0):
    """
    img_decode: decode bin into img
    path_in: the bin path
    path_out: the img path
    Ds: dictionary
    mode: choose the compress mode, 0 for lossy and 1 for lossless
    """
    if mode == 0:
        saved = open(path_in, mode="rb+")
        img_shape = read_uints(saved, 2)
        h, w = img_shape[0], img_shape[1]
        ad_h, ad_w = int(np.ceil(h / 8) * 8), int(np.ceil(w / 8) * 8)
        shape = read_uints(saved, 3)
        deldc = list(read_uints(saved, 3))
        thrdc = [a / 2 for a in deldc]
        dele = [a for a in deldc]
        thr = [a for a in deldc]

        xc_decode_R = saved.read(shape[0])
        xc_decode_G = saved.read(shape[1])
        xc_decode_B = saved.read(shape[2])

        xc_decode_R = (zlib.decompress(xc_decode_R)).decode()
        xc_decode_G = (zlib.decompress(xc_decode_G)).decode()
        xc_decode_B = (zlib.decompress(xc_decode_B)).decode()

        xc_decodedmtx = [xc_decode_R, xc_decode_G, xc_decode_B]
        for idx_channel in range(3):
            xc_decode = xc_decodedmtx[idx_channel]
            xc = eval(xc_decode)

            xCw = xc[0:4]
            xCdc = xc[4:]
            Zw_r = myreshape(xCw)

            Zdc_r = mypred(xCdc)

            Zdc_r = Zdc_r.T.reshape(1, -1)

            Qdc = uniquant(
                Zdc_r, deldc[idx_channel], thrdc[idx_channel]
            )  # inverse quantizing
            Qw = uniquant(
                Zw_r, dele[idx_channel], thr[idx_channel]
            )  # inverse quantizing

            Xr = np.concatenate(
                (Qdc, np.zeros((Ds.N - 1, int(ad_h * ad_w // 64)))), axis=0
            )
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
        pic.save(path_out)
    elif mode == 1:
        saved = open(path_in, mode="rb+")
        img_shape = read_uints(saved, 2)
        h, w = img_shape[0], img_shape[1]
        ad_h, ad_w = int(np.ceil(h / 8) * 8), int(np.ceil(w / 8) * 8)
        shape = read_uints(saved, 6)
        deldc = list(read_uints(saved, 3))
        thrdc = [a / 2 for a in deldc]
        dele = [a for a in deldc]
        thr = [a for a in deldc]

        xc_decode_R = saved.read(shape[0])
        xc_decode_G = saved.read(shape[1])
        xc_decode_B = saved.read(shape[2])

        res_R = saved.read(shape[3])
        res_G = saved.read(shape[4])
        res_B = saved.read(shape[5])

        xc_decode_R = (zlib.decompress(xc_decode_R)).decode()
        xc_decode_G = (zlib.decompress(xc_decode_G)).decode()
        xc_decode_B = (zlib.decompress(xc_decode_B)).decode()
        res_decode_R = (zlib.decompress(res_R)).decode()
        res_decode_G = (zlib.decompress(res_G)).decode()
        res_decode_B = (zlib.decompress(res_B)).decode()

        res_R = eval(res_decode_R)
        res_G = eval(res_decode_G)
        res_B = eval(res_decode_B)

        res_R = np.array(res_R)
        res_G = np.array(res_G)
        res_B = np.array(res_B)

        res_R = res_R.reshape((h, w))
        res_G = res_G.reshape((h, w))
        res_B = res_B.reshape((h, w))

        xc_decodedmtx = [xc_decode_R, xc_decode_G, xc_decode_B]
        for idx_channel in range(3):
            xc_decode = xc_decodedmtx[idx_channel]
            xc = eval(xc_decode)

            xCw = xc[0:4]
            xCdc = xc[4:]
            Zw_r = myreshape(xCw)

            Zdc_r = mypred(xCdc)

            Zdc_r = (Zdc_r.T).reshape(1, -1)

            Qdc = uniquant(
                Zdc_r, deldc[idx_channel], thrdc[idx_channel]
            )  # inverse quantizing
            Qw = uniquant(
                Zw_r, dele[idx_channel], thr[idx_channel]
            )  # inverse quantizing

            Xr = np.concatenate(
                (Qdc, np.zeros((Ds.N - 1, int(ad_h * ad_w // 64)))), axis=0
            )
            Xa = np.dot(np.float64(Ds.D), Qw)

            Ar = mycol2im(Xr + Xa, transform=Ds.transform, imsize=[ad_h, ad_w], size=[8, 8])
            Ar = Ar[0:h, 0:w]
            if idx_channel == 0:
                Ar_R = Ar
            if idx_channel == 1:
                Ar_G = Ar
            if idx_channel == 2:
                Ar_B = Ar
        Ar = np.stack((Ar_R, Ar_G, Ar_B), 2)

        res = np.stack((res_R, res_G, res_B), 2)
        pic = Image.fromarray(np.uint8(np.int8(Ar + 0.5 * np.sign(Ar)) + res) + 128)
        pic.save(path_out)
    else:
        print("error:mode should be 0 or 1")
    return 0
