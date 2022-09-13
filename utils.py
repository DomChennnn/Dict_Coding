import os
import struct
from PIL import ImageFile, BmpImagePlugin
import numpy as np
import matplotlib.pyplot as plt
import matplotlib
from struct import unpack
import cv2

PROJECT_ROOT = os.path.dirname(os.path.abspath(__file__))

# two python dict, just like C++ class
class Dictionaries:
    def __init__(self, D, K, N, L, transform):
        self.D = D
        self.k = K
        self.N = N
        self.L = L
        self.transform = transform


class test_Dict_par:
    def __init__(
        self, dictionary, targetPSNR, dele, qLimit, estimateBits, ompMethod, verbose
    ):
        self.dictionary = dictionary
        self.targetPSNR = targetPSNR
        self.dele = dele
        self.qLimit = qLimit
        self.estimateBits = estimateBits
        self.ompMethod = ompMethod
        self.verbose = verbose


def write_uints(fd, values, fmt=">{:d}I"):
    fd.write(struct.pack(fmt.format(len(values)), *values))
    return len(values) * 4


def write_uchars(fd, values, fmt=">{:d}B"):
    fd.write(struct.pack(fmt.format(len(values)), *values))
    return len(values) * 1


def read_uints(fd, n, fmt=">{:d}I"):
    sz = struct.calcsize("I")
    return struct.unpack(fmt.format(n), fd.read(n * sz))


def read_uchars(fd, n, fmt=">{:d}B"):
    sz = struct.calcsize("B")
    return struct.unpack(fmt.format(n), fd.read(n * sz))


def write_bytes(fd, values, fmt=">{:d}s"):
    if len(values) == 0:
        return
    fd.write(struct.pack(fmt.format(len(values)), values))
    return len(values) * 1


def read_bytes(fd, n, fmt=">{:d}s"):
    sz = struct.calcsize("s")
    return struct.unpack(fmt.format(n), fd.read(n * sz))[0]


def read_body(fd):
    shape = read_uints(fd, 3)

    return shape


def write_body(fd, shape, out_strings):
    bytes_cnt = write_uints(fd, (shape[0], shape[1], len(out_strings)))
    for s in out_strings:
        bytes_cnt += write_bytes(fd, s)
    return bytes_cnt


_i16, _i32 = BmpImagePlugin.i16, BmpImagePlugin.i32


class BmpAlphaImageFile(ImageFile.ImageFile):
    format = "BMP+Alpha"
    format_description = "BMP with full alpha channel"

    def _open(self):
        s = self.fp.read(14)
        if s[:2] != b"BM":
            raise SyntaxError("Not a BMP file")
        offset = _i32(s[10:])

        self._read_bitmap(offset)

    def _read_bitmap(self, offset):

        s = self.fp.read(4)
        s += ImageFile._safe_read(self.fp, _i32(s) - 4)

        if len(s) not in (40, 108, 124):
            # Only accept BMP v3, v4, and v5.
            raise IOError("Unsupported BMP header type (%d)" % len(s))

        bpp = _i16(s[14:])
        if bpp != 32:
            # Only accept BMP with alpha.
            raise IOError("Unsupported BMP pixel depth (%d)" % bpp)

        compression = _i32(s[16:])
        if compression == 3:
            # BI_BITFIELDS compression
            mask = (
                _i32(self.fp.read(4)),
                _i32(self.fp.read(4)),
                _i32(self.fp.read(4)),
                _i32(self.fp.read(4)),
            )
            # XXX Handle mask.
        elif compression != 0:
            # Only accept uncompressed BMP.
            raise IOError("Unsupported BMP compression (%d)" % compression)

        self.mode, rawmode = "RGBA", "BGRA"

        self._size = (_i32(s[4:]), _i32(s[8:]))
        direction = -1
        if s[11] == "\xff":
            # upside-down storage
            self._size = self._size[0], 2**32 - self._size[1]
            direction = 0

        self.info["compression"] = compression

        # data descriptor
        self.tile = [("raw", (0, 0) + self._size, offset, (rawmode, 0, direction))]


def byte_to_int(str1):
    # 从一个str类型的byte到int
    result = 0
    for i in range(len(str1)):
        y = int(str1[len(str1) - 1 - i])
        result += y * 2**i
    return result


def breakup_byte(num1, n):
    # byte为输入的类型为byte的参数,n为每个数要的位数
    result = []  # 返回的数字
    num = num1[2:]
    num_len = len(num)
    str1 = ""
    for i in range(8 - num_len):
        str1 += str(0)
    num = str1 + num
    for i in range(int(8 / n)):
        temp = num[8 - n * (i + 1) : 8 - n * i]
        result.append(byte_to_int(temp))
    result.reverse()
    return result


def breakup_16byte(str1, str2):
    # 16位采用小端方式储存
    num1 = str1[2:]
    num2 = str2[2:]
    str1_ = ""
    str2_ = ""
    num_len1 = len(num1)
    num_len2 = len(num2)
    for i in range(8 - num_len1):
        str1_ += str(0)
    num1 = str1_ + num1
    for i in range(8 - num_len2):
        str2_ += str(0)
    num2 = str2_ + num2
    num = num2 + num1
    # 16位用两个字节表示rgb设为555最后一个补0
    result = []
    r = byte_to_int(num[0:5])
    g = byte_to_int(num[5:11])
    b = byte_to_int(num[11:])
    result.append(r * 8)
    result.append(g * 4)
    result.append(b * 8)
    return result


# def bmp_img_read_save_hist(filename):
#     xxx=1
#     img_path = filename
#     img=open(img_path,"rb")
#     #跳过bmp文件信息的开头，直接读取图片的size信息
#     img.seek(28)
#     bit_num=int.from_bytes(img.read(2), byteorder='little', signed=True)
#     img.seek(10)
#     #从开头到图片数据要的字节数
#     to_img_data=int.from_bytes(img.read(4), byteorder='little', signed=True)
#     img.seek(img.tell()+4)
#     #unpack转为十进制
#     img_width = int.from_bytes(img.read(4), byteorder='little', signed=True)
#     img_height = int.from_bytes(img.read(4), byteorder='little', signed=True)
#     img.seek(50)
#     #颜色索引数
#     color_num = unpack("<i", img.read(4))[0]
#     #1位每个像素一位，4位一个像素0.5字节，8位一个像素1字节，16位一个像素2字节（555+0），24位一个像素3字节（bgr+alpha）
#     #读取指针总共跳过54位到颜色盘,其中16,24位图像不需要调色盘
#     img.seek(54)
#     if(bit_num<=8):
#         #多少字节调色板颜色就有2^n个
#         color_table_num=2**int(bit_num)
#         color_table=np.zeros((color_table_num,3),dtype=np.int)
#         for i in range(color_table_num):
#             b=unpack("B",img.read(1))[0];
#             g = unpack("B", img.read(1))[0];
#             r = unpack("B", img.read(1))[0];
#             alpha=unpack("B", img.read(1))[0];
#             color_table[i][0]=b;
#             color_table[i][1] = g;
#             color_table[i][2] = r;
#     #将数据存入numpy中
#     img.seek(to_img_data)
#     img_np=np.zeros((img_height,img_width,3),dtype=np.int)
#     num=0#计算读入的总字节数
#     #数据排列从左到右，从下到上
#     x=0
#     y=0
#     while y<img_height:
#         while(x<img_width):
#             if (bit_num <= 8):#小于等于8位的图像读取
#                 img_byte= unpack("B", img.read(1))[0]
#                 img_byte=bin(img_byte)
#                 color_index=breakup_byte(img_byte,bit_num)
#                 num+=1
#                 for index in color_index:
#                     if(x<img_width):
#                         img_np[img_height-y-1][x]=color_table[index]
#                         x+=1
#             elif(bit_num==24):#24位的图像读取
#                 num+=3
#                 g=unpack("B", img.read(1))[0]
#                 b=unpack("B", img.read(1))[0]
#                 r=unpack("B", img.read(1))[0]
#                 img_np[img_height - y - 1][x]=[r,b,g]
#                 x+=1
#             elif (bit_num==16):#16位图像读取
#                 str1=bin(unpack("B", img.read(1))[0])
#                 str2=bin(unpack("B", img.read(1))[0])
#                 bgr_color=breakup_16byte(str1,str2)
#                 img_np[img_height - y - 1][x]=[bgr_color[0],bgr_color[1],bgr_color[2]]
#                 num+=2
#                 x+=1
#         x=0
#         y+=1
#         while (num % 4 != 0):  # 每一行的位数都必须为4的倍数
#             num += 1
#             img.read(1)
#         num=0
#     plt.imshow(img_np)
#     plt.show()
#     img.close()
#     #将图片以jpg格式保存在saved_img文件夹中
#     img_name_save="saved_img"+os.sep+"saved_"+img_path.split(os.sep)[1]
#     matplotlib.image.imsave(img_name_save, img_np.astype(np.uint8))
#     #绘制直方图
#     if bit_num<=8:
#         plt.figure("hist")
#         arr = img_np.flatten()
#         plt.hist(arr, bins=2**bit_num,facecolor='green', alpha=0.75)
#         plt.show()
#     else:
#         plt.figure("hist")
#         ar = np.array(img_np[:,:,0]).flatten()
#         plt.hist(ar, bins=256,facecolor='r', edgecolor='r',alpha=0.5)
#         ag = np.array(img_np[:,:,1]).flatten()
#         plt.hist(ag, bins=256, facecolor='g', edgecolor='g',alpha=0.5)
#         ab = np.array(img_np[:,:,2]).flatten()
#         plt.hist(ab, bins=256, facecolor='b', edgecolor='b',alpha=0.5)
#         plt.show()
#     #将图片像素保存到txt文件中,由于numpy中的savetxt只能保存一维或者二维的数组，因此现将img_np展开
#     txt_name="img_txt"+os.sep+"txt_"+(img_path.split(os.sep)[1]).split('.')[0]+'.txt'
#     img_np=np.reshape(img_np,(img_height*3,img_width))
#     np.savetxt(txt_name,img_np)
# # bmp_img_read_save_hist('A004_087.bmp')
# # a=10


def Bmp16bitImageFile(path):
    img = open(path, "rb")
    img.seek(10)
    to_img_data = int.from_bytes(img.read(4), byteorder="little", signed=True)
    img.seek(img.tell() + 4)
    img_width = int.from_bytes(img.read(4), byteorder="little", signed=True)
    img_height = int.from_bytes(img.read(4), byteorder="little", signed=True)

    img.seek(to_img_data)
    img_np = np.zeros((img_height, img_width, 3), dtype=np.uint8)
    num = 0

    x = 0
    y = 0
    while y < img_height:
        while x < img_width:
            str1 = bin(unpack("B", img.read(1))[0])
            str2 = bin(unpack("B", img.read(1))[0])
            bgr_color = breakup_16byte(str1, str2)
            img_np[img_height - y - 1][x] = [bgr_color[0], bgr_color[1], bgr_color[2]]
            num += 2
            x += 1
        x = 0
        y += 1
        while num % 4 != 0:
            num += 1
            img.read(1)
        num = 0
    img.close()
    return img_np
