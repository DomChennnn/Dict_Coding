# import os
# import jpype
#
# jvmPath = jpype.getDefaultJVMPath()
# jpype.startJVM(jvmPath)
# jpype.java.lang.System.out.println("hello world!")
# jpype.shutdownJVM()



# import jpype
# import numpy as np
#
#
# D = np.ones((100,100))
#
# jvmPath = jpype.getDefaultJVMPath()  # the path of jvm.dll
# classpath = "C:\dmcprojects\pythonProject1"  # the path of PasswordCipher.class
# jvmArg = "-Djava.class.path=" + classpath
# if not jpype.isJVMStarted():  # test whether the JVM is started
#     jpype.startJVM(jvmPath, jvmArg)  # start JVM
# javaClass = jpype.JClass("pyfunj")  # create the Java class
#
# javaClass.func1()
# javaClass.func3(D)
# # javaClass.func0()
# jpype.shutdownJVM()  # shut down JVM

import jpype
import numpy as np


D = np.ones((100,100))

jvmPath = jpype.getDefaultJVMPath()  # the path of jvm.dll
classpath = "C:\dmcprojects\pythonProject1\javaclasses"  # the path of PasswordCipher.class
jvmArg = "-Djava.class.path=" + classpath
if not jpype.isJVMStarted():  # test whether the JVM is started
    jpype.startJVM(jvmPath, jvmArg)  # start JVM
javaClass = jpype.JClass("mpv2.SimpleMatrix")  # create the Java class

s1 = javaClass(D)
print(s1)

jpype.shutdownJVM()  # shut down JVM