import os
from utils import PROJECT_ROOT


class JavaORMP:
    def __init__(self, D, K, L):
        import jpype

        jvmPath = jpype.getDefaultJVMPath()  # the path of jvm.dll
        classpath = os.path.join(PROJECT_ROOT, "javaclasses")  # the path of PasswordCipher.class
        jvmArg = "-Djava.class.path=" + classpath
        if not jpype.isJVMStarted():  # test whether the JVM is started
            jpype.startJVM(jvmPath, jvmArg)  # start JVM

        SimpleMatrix = jpype.JClass("mpv2.SimpleMatrix")  # create the Java class
        MatchingPursuit = jpype.JClass("mpv2.MatchingPursuit")
        SymmetricMatrix = jpype.JClass("mpv2.SymmetricMatrix")

        jD = SimpleMatrix(D)
        if L == 1:
            self.jMP = MatchingPursuit(jD)
        else:
            jDD = SymmetricMatrix(K, K)
            jDD.eqInnerProductMatrix(jD)
            self.jMP = MatchingPursuit(jD, jDD)

    def apply(self, ad, i, d1):
        return self.jMP.vsORMP(ad, i, d1)


class CppORMP:
    def __init__(self, D, K, L):
        import mpv2

        jD = mpv2.SimpleMatrix(mpv2.DoubleVectors.from_np_array(D))
        if L == 1:
            self.jMP = mpv2.MatchingPursuit(jD)
        else:
            jDD = mpv2.SymmetricMatrix(K, K)
            jDD.eqInnerProductMatrix(jD)
            self.jMP = mpv2.MatchingPursuit(jD, jDD)

    def apply(self, ad, i, d1):
        import mpv2

        return self.jMP.vsORMP(mpv2.DoubleVectors.from_np_array(ad), i, d1).to_np_array()
