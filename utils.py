import os

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
