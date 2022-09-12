import numpy as np
import zlib
from PIL import Image

from test_Dict import test_Dict
from utils import write_uints


def img_encode(path_in, path_out, Ds, mode=0):
    """
    img_encode: encode img into bin
    path_in: the img path
    path_out: the bin path
    Ds: dictionary
    mode: choose the compress mode, 0 for lossly and 1 for lossless
    """
    img = Image.open(path_in).convert("RGB")
    img = np.array(img, dtype=np.double)
    h, w = img.shape[0], img.shape[1]
    compress_level = 9

    _, _, res_R, xc_R, _, deldc_R, _, _, _, _ = test_Dict(img[:, :, 0], Ds)
    _, _, res_G, xc_G, _, deldc_G, _, _, _, _ = test_Dict(img[:, :, 1], Ds)
    _, _, res_B, xc_B, _, deldc_B, _, _, _, _ = test_Dict(img[:, :, 2], Ds)

    for i in range(len(xc_R)):
        xc_R[i] = list(xc_R[i])
        xc_G[i] = list(xc_G[i])
        xc_B[i] = list(xc_B[i])

    xc_encoded_R = zlib.compress((str(xc_R)).encode(), compress_level)
    xc_encoded_G = zlib.compress((str(xc_G)).encode(), compress_level)
    xc_encoded_B = zlib.compress((str(xc_B)).encode(), compress_level)

    if mode == 0:
        # mybits: dictionary encoded bits
        # info0: image height and image width
        # info1: len of each dictionary encoded bits
        # info2: some information for uniquant and iuniquant
        mybits = xc_encoded_R + xc_encoded_G + xc_encoded_B
        info0 = [h, w]
        info1 = [len(xc_encoded_R), len(xc_encoded_G), len(xc_encoded_B)]
        info2 = [deldc_R, deldc_G, deldc_B]
        saved = open(path_out, mode="wb")
        write_uints(saved, (info0[0], info0[1]))
        write_uints(saved, (info1[0], info1[1], info1[2]))
        write_uints(saved, (info2[0], info2[1], info2[2]))
        saved.write(mybits)
        saved.close()
    elif mode == 1:
        # res encode
        res_R = res_R.reshape((-1, 1))
        res_G = res_G.reshape((-1, 1))
        res_B = res_B.reshape((-1, 1))
        res_list_R = []
        res_list_G = []
        res_list_B = []
        for i in range(len(res_R)):
            res_list_R.append(res_R[i, 0])
            res_list_G.append(res_G[i, 0])
            res_list_B.append(res_B[i, 0])

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

        res_encoded_R = zlib.compress((str(res_list_R)).encode(), compress_level)
        res_encoded_G = zlib.compress((str(res_list_G)).encode(), compress_level)
        res_encoded_B = zlib.compress((str(res_list_B)).encode(), compress_level)

        # mybits: dictionary encoded bits and res encoded bits
        # info0: image height and image width
        # info1: len of each dictionary encoded bits and len of each res encoded bits
        # info2: some information for uniquant and iuniquant
        mybits = xc_encoded_R + xc_encoded_G + xc_encoded_B + res_encoded_R + res_encoded_G + res_encoded_B
        info0 = [h, w]
        info1 = [
            len(xc_encoded_R),
            len(xc_encoded_G),
            len(xc_encoded_B),
            len(res_encoded_R),
            len(res_encoded_G),
            len(res_encoded_B),
        ]
        info2 = [deldc_R, deldc_G, deldc_B]
        saved = open(path_out, mode="wb")
        write_uints(saved, (info0[0], info0[1]))
        write_uints(saved, (info1[0], info1[1], info1[2], info1[3], info1[4], info1[5]))
        write_uints(saved, (info2[0], info2[1], info2[2]))
        saved.write(mybits)
        saved.close()
    else:
        print("error:mode should be 0 or 1")
    return 0
