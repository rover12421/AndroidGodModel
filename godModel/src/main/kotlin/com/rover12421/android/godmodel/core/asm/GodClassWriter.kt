package com.rover12421.android.godmodel.core.asm

import com.rover12421.android.godmodel.core.asm.ClassReaderUtil.OBJECT_TYPE
import com.rover12421.android.godmodel.core.asm.ClassReaderUtil.getClassReader
import com.rover12421.android.godmodel.core.asm.ClassReaderUtil.getSuperClassName
import com.rover12421.android.godmodel.core.asm.ClassReaderUtil.isImplements
import com.rover12421.android.godmodel.core.asm.ClassReaderUtil.isInterface
import org.objectweb.asm.ClassWriter

class GodClassWriter(val cl: ClassLoader, val flags: Int) : ClassWriter(flags) {

    override fun getCommonSuperClass(type1: String?, type2: String?): String {
        if (type1 == null || type2 == null || type1 == OBJECT_TYPE || type2 == OBJECT_TYPE) {
            return OBJECT_TYPE
        }

        if (type1 == type2) return type1

        val cr1 = getClassReader(cl, type1)
        val cr2 = getClassReader(cl, type2)
        if (cr1 == null || cr2 == null) return OBJECT_TYPE

        if (isInterface(cr1)) {
            if (isImplements(cr2, type1, cl)) {
                return type1
            }
            if (isInterface(cr2)) {
                if (isImplements(cr1, type2, cl)) {
                    return type2
                }
            }
            return OBJECT_TYPE
        }

        if (isInterface(cr2)) {
            if (isImplements(cr1, type2, cl)) {
                return type2
            }
        }

        val superClassNames = mutableSetOf(type1, type2)
        var type1SuperClassName = cr1.superName
        var type2SuperClassName = cr2.superName
        while (type1SuperClassName != null || type2SuperClassName != null) {
            if (type1SuperClassName != null) {
                if (!superClassNames.add(type1SuperClassName)) {
                    return type1SuperClassName
                }
                type1SuperClassName = getSuperClassName(cl, type1SuperClassName)

            }

            if (type2SuperClassName != null) {
                if (!superClassNames.add(type2SuperClassName)) {
                    return type2SuperClassName
                }
                type2SuperClassName = getSuperClassName(cl, type2SuperClassName)
            }
        }

        return OBJECT_TYPE
    }

}