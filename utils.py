import os
import struct

PROJECT_ROOT = os.path.dirname(os.path.abspath(__file__))

# two python dict, just like C++ class
class Dictionarys:
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
