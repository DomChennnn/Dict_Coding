import numpy as np
import zlib
from PIL import Image

from test_Dict import test_Dict
from utils import write_uints, BmpAlphaImageFile, Bmp16bitImageFile
import RLE

# def img_encode(path_in, path_out, Ds, mode=0):
#     """
#     img_encode: encode img into bin
#     path_in: the img path
#     path_out: the bin path
#     Ds: dictionary
#     mode: choose the compress mode, 0 for lossly and 1 for lossless
#     """
#     try:
#         img = BmpAlphaImageFile(path_in)
#         img_A = list(img.getdata(3))
#         RGBA_mode = 1
#     except IOError:
#         img = Image.open(path_in)
#         RGBA_mode = 0
#     img = np.array(img)
#     h, w = img.shape[0], img.shape[1]
#     compress_level = 9
#
#     _, _, res_R, xc_R, dele_R, deldc_R, thr_R, thrdc_R, _, _ = test_Dict(
#         img[:, :, 0], Ds
#     )
#     _, _, res_G, xc_G, dele_G, deldc_G, thr_G, thrdc_G, _, _ = test_Dict(
#         img[:, :, 1], Ds
#     )
#     _, _, res_B, xc_B, dele_B, deldc_B, thr_B, thrdc_B, _, _ = test_Dict(
#         img[:, :, 2], Ds
#     )
#
#     for i in range(len(xc_R)):
#         xc_R[i] = list(xc_R[i])
#         xc_G[i] = list(xc_G[i])
#         xc_B[i] = list(xc_B[i])
#
#     xc_encoded_R = zlib.compress((str(xc_R)).encode(), compress_level)
#     xc_encoded_G = zlib.compress((str(xc_G)).encode(), compress_level)
#     xc_encoded_B = zlib.compress((str(xc_B)).encode(), compress_level)
#
#     if RGBA_mode:
#         xc_encoded_A = RLE.encodeImage(img_A, h, w, 'RGBA', 'C')
#
#     if mode == 0:
#         # mybits: dictionary encoded bits
#         # info0: image height and image width
#         # info1: len of each dictionary encoded bits
#         # info2: some information for uniquant and iuniquant
#         if RGBA_mode:
#             mybits = xc_encoded_R + xc_encoded_G + xc_encoded_B + xc_encoded_A
#             info0 = [h, w]
#             info1 = [len(xc_encoded_R), len(xc_encoded_G), len(xc_encoded_B), len(xc_encoded_A)]
#             info2 = [deldc_R, deldc_G, deldc_B]
#             saved = open(path_out, mode="wb")
#             write_uints(saved, (info0[0], info0[1]))
#             write_uints(saved, (info1[0], info1[1], info1[2], info1[3]))
#             write_uints(saved, (info2[0], info2[1], info2[2]))
#             saved.write(mybits)
#             saved.close()
#         else:
#             mybits = xc_encoded_R + xc_encoded_G + xc_encoded_B
#             info0 = [h, w]
#             info1 = [len(xc_encoded_R), len(xc_encoded_G), len(xc_encoded_B)]
#             info2 = [deldc_R, deldc_G, deldc_B]
#             saved = open(path_out, mode="wb")
#             write_uints(saved, (info0[0], info0[1]))
#             write_uints(saved, (info1[0], info1[1], info1[2]))
#             write_uints(saved, (info2[0], info2[1], info2[2]))
#             saved.write(mybits)
#             saved.close()
#     elif mode == 1:
#         # res encode
#         res_R = res_R.reshape((-1, 1))
#         res_G = res_G.reshape((-1, 1))
#         res_B = res_B.reshape((-1, 1))
#         res_list_R = []
#         res_list_G = []
#         res_list_B = []
#         for i in range(len(res_R)):
#             res_list_R.append(res_R[i, 0])
#             res_list_G.append(res_G[i, 0])
#             res_list_B.append(res_B[i, 0])
#
#         res_R = res_R.reshape((1, -1))
#         res_G = res_G.reshape((1, -1))
#         res_B = res_B.reshape((1, -1))
#         res_list_R = []
#         res_list_G = []
#         res_list_B = []
#         for i in range(len(res_R[0])):
#             res_list_R.append(res_R[0, i])
#             res_list_G.append(res_G[0, i])
#             res_list_B.append(res_B[0, i])
#
#         res_encoded_R = zlib.compress((str(res_list_R)).encode(), compress_level)
#         res_encoded_G = zlib.compress((str(res_list_G)).encode(), compress_level)
#         res_encoded_B = zlib.compress((str(res_list_B)).encode(), compress_level)
#
#         # mybits: dictionary encoded bits and res encoded bits
#         # info0: image height and image width
#         # info1: len of each dictionary encoded bits and len of each res encoded bits
#         # info2: some information for uniquant and iuniquant
#         mybits = (
#             xc_encoded_R
#             + xc_encoded_G
#             + xc_encoded_B
#             + res_encoded_R
#             + res_encoded_G
#             + res_encoded_B
#             + xc_encoded_A
#         )
#         info0 = [h, w]
#         info1 = [
#             len(xc_encoded_R),
#             len(xc_encoded_G),
#             len(xc_encoded_B),
#             len(res_encoded_R),
#             len(res_encoded_G),
#             len(res_encoded_B),
#             len(xc_encoded_A)
#         ]
#         info2 = [deldc_R, deldc_G, deldc_B]
#         saved = open(path_out, mode="wb")
#         write_uints(saved, (info0[0], info0[1]))
#         write_uints(
#             saved, (info1[0], info1[1], info1[2], info1[3], info1[4], info1[5], info1[6])
#         )
#         write_uints(saved, (info2[0], info2[1], info2[2]))
#         saved.write(mybits)
#         saved.close()
#     else:
#         print("error:mode should be 0 or 1")
#     return 0


def img_encode(path_in, path_out, Ds, l_mode=0):
    """
    img_encode: encode img into bin
    path_in: the img path
    path_out: the bin path
    Ds: dictionary
    mode: choose the compress mode, 0 for lossly and 1 for lossless
    """
    img = open(path_in, "rb")
    img.seek(28)
    # 28-30 byte define the bits of image
    biBitCount = int.from_bytes(img.read(2), byteorder="little", signed=True)

    # lossly : 0 - 3 lossless: 4 - 7
    mode = int(l_mode * 8 + (biBitCount // 8 - 1))
    compress_level = 9
    if biBitCount == 8:
        img = Image.open(path_in)
        img = np.array(img)
        h, w = img.shape[0], img.shape[1]

        _, _, res, xc, dele, deldc, thr, thrdc, _, _ = test_Dict(img, Ds)

        for i in range(len(xc)):
            xc[i] = list(xc[i])

        xc_encoded = zlib.compress((str(xc)).encode(), compress_level)

        if mode == 0:
            mybits = xc_encoded
            info0 = [h, w]
            info1 = [len(xc_encoded)]
            info2 = [deldc]
            saved = open(path_out, mode="wb")
            write_uints(saved, (mode, info0[0], info0[1]))
            write_uints(saved, (info1[0]))
            write_uints(saved, (info2[0]))
            saved.write(mybits)
            saved.close()
        elif mode == 4:
            # res encode
            res = res.reshape((-1, 1))
            res_list = []
            for i in range(len(res)):
                res_list.append(res[i, 0])

            res = res.reshape((1, -1))
            res_list = []
            for i in range(len(res[0])):
                res_list.append(res[0, i])

            res_encoded = zlib.compress((str(res_list)).encode(), compress_level)

            # mybits: dictionary encoded bits and res encoded bits
            # info0: image height and image width
            # info1: len of each dictionary encoded bits and len of each res encoded bits
            # info2: some information for uniquant and iuniquant
            mybits = xc_encoded + res_encoded
            info0 = [h, w]
            info1 = [len(xc_encoded), len(res_encoded)]
            info2 = [deldc]
            saved = open(path_out, mode="wb")
            write_uints(saved, (mode, info0[0], info0[1]))
            write_uints(saved, (info1[0], info1[1]))
            write_uints(saved, (info2[0]))
            saved.write(mybits)
            saved.close()
        else:
            print("error in encode: mode error")

    elif biBitCount == 16:
        img = Bmp16bitImageFile(path_in)
        img = np.array(img)
        h, w = img.shape[0], img.shape[1]

        _, _, res_R, xc_R, dele_R, deldc_R, thr_R, thrdc_R, _, _ = test_Dict(
            img[:, :, 0], Ds
        )
        _, _, res_G, xc_G, dele_G, deldc_G, thr_G, thrdc_G, _, _ = test_Dict(
            img[:, :, 1], Ds
        )
        _, _, res_B, xc_B, dele_B, deldc_B, thr_B, thrdc_B, _, _ = test_Dict(
            img[:, :, 2], Ds
        )

        for i in range(len(xc_R)):
            xc_R[i] = list(xc_R[i])
            xc_G[i] = list(xc_G[i])
            xc_B[i] = list(xc_B[i])

        xc_encoded_R = zlib.compress((str(xc_R)).encode(), compress_level)
        xc_encoded_G = zlib.compress((str(xc_G)).encode(), compress_level)
        xc_encoded_B = zlib.compress((str(xc_B)).encode(), compress_level)

        if mode == 1:
            mybits = xc_encoded_R + xc_encoded_G + xc_encoded_B
            info0 = [h, w]
            info1 = [len(xc_encoded_R), len(xc_encoded_G), len(xc_encoded_B)]
            info2 = [deldc_R, deldc_G, deldc_B]
            saved = open(path_out, mode="wb")
            write_uints(saved, (mode, info0[0], info0[1]))
            write_uints(saved, (info1[0], info1[1], info1[2]))
            write_uints(saved, (info2[0], info2[1], info2[2]))
            saved.write(mybits)
            saved.close()
        elif mode == 5:
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
            mybits = (
                xc_encoded_R
                + xc_encoded_G
                + xc_encoded_B
                + res_encoded_R
                + res_encoded_G
                + res_encoded_B
            )
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
            write_uints(saved, (mode, info0[0], info0[1]))
            write_uints(
                saved, (info1[0], info1[1], info1[2], info1[3], info1[4], info1[5])
            )
            write_uints(saved, (info2[0], info2[1], info2[2]))
            saved.write(mybits)
            saved.close()
        else:
            print("error in encode: mode error")

    elif biBitCount == 24:
        img = Image.open(path_in)
        img = np.array(img)
        h, w = img.shape[0], img.shape[1]

        _, _, res_R, xc_R, dele_R, deldc_R, thr_R, thrdc_R, _, _ = test_Dict(
            img[:, :, 0], Ds
        )
        _, _, res_G, xc_G, dele_G, deldc_G, thr_G, thrdc_G, _, _ = test_Dict(
            img[:, :, 1], Ds
        )
        _, _, res_B, xc_B, dele_B, deldc_B, thr_B, thrdc_B, _, _ = test_Dict(
            img[:, :, 2], Ds
        )

        for i in range(len(xc_R)):
            xc_R[i] = list(xc_R[i])
            xc_G[i] = list(xc_G[i])
            xc_B[i] = list(xc_B[i])

        xc_encoded_R = zlib.compress((str(xc_R)).encode(), compress_level)
        xc_encoded_G = zlib.compress((str(xc_G)).encode(), compress_level)
        xc_encoded_B = zlib.compress((str(xc_B)).encode(), compress_level)

        if mode == 2:
            mybits = xc_encoded_R + xc_encoded_G + xc_encoded_B
            info0 = [h, w]
            info1 = [len(xc_encoded_R), len(xc_encoded_G), len(xc_encoded_B)]
            info2 = [deldc_R, deldc_G, deldc_B]
            saved = open(path_out, mode="wb")
            write_uints(saved, (mode, info0[0], info0[1]))
            write_uints(saved, (info1[0], info1[1], info1[2]))
            write_uints(saved, (info2[0], info2[1], info2[2]))
            saved.write(mybits)
            saved.close()
        elif mode == 6:
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
            mybits = (
                xc_encoded_R
                + xc_encoded_G
                + xc_encoded_B
                + res_encoded_R
                + res_encoded_G
                + res_encoded_B
            )
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
            write_uints(saved, (mode, info0[0], info0[1]))
            write_uints(
                saved, (info1[0], info1[1], info1[2], info1[3], info1[4], info1[5])
            )
            write_uints(saved, (info2[0], info2[1], info2[2]))
            saved.write(mybits)
            saved.close()
        else:
            print("error in encode: mode error")

    elif biBitCount == 32:
        img = BmpAlphaImageFile(path_in)
        img_A = list(img.getdata(3))
        img = np.array(img)
        h, w = img.shape[0], img.shape[1]

        _, _, res_R, xc_R, dele_R, deldc_R, thr_R, thrdc_R, _, _ = test_Dict(
            img[:, :, 0], Ds
        )
        _, _, res_G, xc_G, dele_G, deldc_G, thr_G, thrdc_G, _, _ = test_Dict(
            img[:, :, 1], Ds
        )
        _, _, res_B, xc_B, dele_B, deldc_B, thr_B, thrdc_B, _, _ = test_Dict(
            img[:, :, 2], Ds
        )

        for i in range(len(xc_R)):
            xc_R[i] = list(xc_R[i])
            xc_G[i] = list(xc_G[i])
            xc_B[i] = list(xc_B[i])

        xc_encoded_R = zlib.compress((str(xc_R)).encode(), compress_level)
        xc_encoded_G = zlib.compress((str(xc_G)).encode(), compress_level)
        xc_encoded_B = zlib.compress((str(xc_B)).encode(), compress_level)
        xc_encoded_A = RLE.encodeImage(img_A, h, w, "RGBA", "C")

        if mode == 3:
            mybits = xc_encoded_R + xc_encoded_G + xc_encoded_B + xc_encoded_A
            info0 = [h, w]
            info1 = [
                len(xc_encoded_R),
                len(xc_encoded_G),
                len(xc_encoded_B),
                len(xc_encoded_A),
            ]
            info2 = [deldc_R, deldc_G, deldc_B]
            saved = open(path_out, mode="wb")
            write_uints(saved, (mode, info0[0], info0[1]))
            write_uints(saved, (info1[0], info1[1], info1[2], info1[3]))
            write_uints(saved, (info2[0], info2[1], info2[2]))
            saved.write(mybits)
            saved.close()
        elif mode == 7:
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
            mybits = (
                xc_encoded_R
                + xc_encoded_G
                + xc_encoded_B
                + res_encoded_R
                + res_encoded_G
                + res_encoded_B
                + xc_encoded_A
            )
            info0 = [h, w]
            info1 = [
                len(xc_encoded_R),
                len(xc_encoded_G),
                len(xc_encoded_B),
                len(res_encoded_R),
                len(res_encoded_G),
                len(res_encoded_B),
                len(xc_encoded_A),
            ]
            info2 = [deldc_R, deldc_G, deldc_B]
            saved = open(path_out, mode="wb")
            write_uints(saved, (mode, info0[0], info0[1]))
            write_uints(
                saved,
                (info1[0], info1[1], info1[2], info1[3], info1[4], info1[5], info1[6]),
            )
            write_uints(saved, (info2[0], info2[1], info2[2]))
            saved.write(mybits)
            saved.close()
        else:
            print("error in encode: mode error")
    else:
        print("error: BMP should be 8 16 24 or 32 bits")
    return 0
