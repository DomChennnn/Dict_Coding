import numpy as np
from ArithmeticCodingandHuffmanCodinginPython.Huff06 import Huff06


def estimateBits(xC, ecm, v):
    if ecm == 'Huff06':
        y, r = Huff06(xC, 5, 0)
        # xCr = Huff06(y)
        bits = len(y)*8
        if v > 1:
            print(r)
    return bits, r, y