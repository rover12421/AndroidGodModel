package com.rover12421.android.godmodel.base.asm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes

object ClassReaderUtil {
    const val OBJECT_TYPE = "java/lang/Object"

    fun isInterface(cr: ClassReader): Boolean {
        return cr.access and Opcodes.ACC_INTERFACE != 0
    }

    fun getClassReader(cl: ClassLoader, className: String): ClassReader? {
        return cl.getResourceAsStream("${className.replace(".", "/")}.class")?.use { inputStream ->
            ClassReader(inputStream)
        }
    }

    fun isImplements(cr: ClassReader, interfaceName: String, cl: ClassLoader): Boolean {
        var classInfo: ClassReader? = cr
        while (classInfo != null) {
            if (classInfo.interfaces.contains(interfaceName)) {
                return true
            }

            classInfo.interfaces.forEach {
                val interfaceInfo = getClassReader(cl, it)
                if (interfaceInfo != null && isImplements(interfaceInfo, interfaceName, cl)) {
                    return true
                }
            }

            if (classInfo.superName == null || classInfo.superName == OBJECT_TYPE) {
                break
            }
            classInfo = getClassReader(cl, classInfo.superName)
        }
        return false
    }

    fun getSuperClassName(cl: ClassLoader, className: String): String? {
        return getClassReader(cl, className)?.superName
    }
}