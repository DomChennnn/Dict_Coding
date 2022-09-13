import numpy as np
import scipy.io as scio
import glob
import math
import time
import os
from PIL import Image

from utils import Dictionaries, PROJECT_ROOT, dumps_np_array_to_file
from encode import img_encode
from decode import img_decode


def compute_psnr(img1, img2):
    mse = np.mean((img1 / 255.0 - img2 / 255.0) ** 2)
    if mse < 1.0e-10:
        return 100
    PIXEL_MAX = 1
    return 20 * math.log10(PIXEL_MAX / math.sqrt(mse))


# init
foldername = "GenerallImages"
blk_size = 8

dir_in = os.path.join(
    PROJECT_ROOT, "Images", foldername, "Test2", "bmp_test", "static"
)  # Image dir path
dir_bin = os.path.join(PROJECT_ROOT, "bins")  # bin dir path
dir_rec = os.path.join(PROJECT_ROOT, "rec")  # rec dir path

data = scio.loadmat(
    os.path.join(PROJECT_ROOT, "Dictionary", "Dict_RLS_" + foldername + ".mat")
)  # load Dictionary path
D = data["dlsRLS"][0, 0][0]  # Dictionary

N, K = D.shape
L = 40000
Ds = Dictionaries(D, K, N, L, transform="m79")  # define Dictionary
Dimg = glob.glob(dir_in + "//**//*.bmp", recursive=True)  # Image path
NumberImages = len(Dimg)


# #some tests
# y = np.zeros((201,1))
# x = np.zeros((201,1))
# D = Ds.D
# for i in range(201):
#     x[i] = -1+i*0.01
#     y[i] = len(D[D<-1+i*0.01]) - sum(y)
#
# plt.plot(x,y)
# plt.xlabel('x')
# plt.ylabel('n')
# plt.xlim(-1,1)
# plt.ylim(0,6000)
# plt.legend()
# plt.show()

for ind_img in range(NumberImages):
    path_img = os.path.join(dir_in, str(ind_img) + ".bmp")
    path_bin = os.path.join(dir_bin, str(ind_img) + ".txt")
    path_rec = os.path.join(dir_rec, str(ind_img) + ".png")

    encode_start_time = time.time()
    img_encode(path_img, path_bin, Ds, 0)
    encode_end_time = time.time()
    img_decode(path_bin, path_rec, Ds)
    decode_end_time = time.time()

    print('idx =', ind_img)
    print((encode_end_time - encode_start_time) * 1000)
    print((decode_end_time - encode_end_time) * 1000)
    bmpsize = os.path.getsize(path_img)
    binsize = os.path.getsize(path_bin)
    print((binsize / bmpsize) * 100)
    print("\n")
