@file:Suppress("NOTHING_TO_INLINE")

package com.rover12421.android.godmodel.core.util

import kotlin.reflect.KClass

inline fun String.toJvmStyle() = AsmUtil.toJvmClazz(this)
inline fun String.toJavaStyle() = AsmUtil.toJavaClazz(this)
inline fun String.toJvmType() = AsmUtil.toJvmType(this)
inline fun Class<*>.toJvmType() = AsmUtil.toJvmType(this.name)
inline fun KClass<*>.toJvmType() = AsmUtil.toJvmType(this.java.name)

/**
 * Created by rover12421 on 3/10/17.
 */
object AsmUtil {
    /**
     * 转出Jvm类型
     */
    @JvmStatic
    fun toJvmType(type: String): String {
        var typeStr = type.toJvmStyle()
        val sb = StringBuilder()
        while (typeStr[0] == '[') {
            sb.append('[')
            typeStr = typeStr.substring(1)
        }

        when (typeStr) {
            "byte", "B" -> sb.append("B")
            "char", "C" -> sb.append("C")
            "double", "D" -> sb.append("D")
            "float", "F" -> sb.append("F")
            "int", "I" -> sb.append("I")
            "short", "S" -> sb.append("S")
            "void", "V" -> sb.append("V")
            "long", "J" -> sb.append("J")
            "boolean", "Z" -> sb.append("Z")
            else -> if (typeStr.startsWith("L") && typeStr.endsWith(";")) {
                sb.append(toJvmClazz(typeStr))
            } else {
                sb.append("L")
                sb.append(toJvmClazz(typeStr))
                sb.append(";")
            }
        }
        return sb.toString()
    }

    @JvmStatic
    fun toJvmMethodDesc(retType: Class<*>, vararg paramTypes: Class<*>): String {
        return toJvmMethodDesc(retType.name, *paramTypes.map { it.name }.toTypedArray())
    }

    /**
     * 通过返回类型,参数类型,转换成Jvm格式的方法描述
     */
    @JvmStatic
    fun toJvmMethodDesc(retType: String, vararg paramTypes: String): String {
        val sb = StringBuilder()
        sb.append("(")
        for (paramType in paramTypes) {
            sb.append(toJvmType(paramType))
        }

        sb.append(")")
        sb.append(toJvmType(retType))
        return sb.toString()
    }

    /**
     * 转换成 . 模式的Java类描述
     */
    @JvmStatic
    inline fun toJavaClazz(clazz: String): String {
        return clazz.replace("/", ".")
    }

    /**
     * 转换成 / 模式的Jvm类描述
     */
    @JvmStatic
    inline fun toJvmClazz(clazz: String): String {
        return clazz.replace(".", "/")
    }

    @JvmStatic
    fun getFullClassNameByJavaStyple(packageName: String?, clazzName: String): String {
        if (packageName == null || packageName.isBlank()) {
            return clazzName
        }

        return toJavaClazz("$packageName.$clazzName")
    }

    @JvmStatic
    fun getFullClassNameByJvmStyple(packageName: String?, clazzName: String): String {
        if (packageName == null || packageName.isBlank()) {
            return clazzName
        }

        return toJvmClazz("$packageName.$clazzName")
    }

    @JvmStatic
    fun getPackageName(fullClazz: String): String {
        val clazz = fullClazz.toJavaStyle()
        return if (clazz.contains(".")) {
            clazz.substring(0, clazz.lastIndexOf("."))
        } else {
            ""
        }
    }

    @JvmStatic
    fun getSimpleClassName(fullClazz: String): String {
        val clazz = fullClazz.toJavaStyle()
        return if (clazz.contains(".")) {
            clazz.substring(clazz.lastIndexOf(".")+1)
        } else {
            clazz
        }
    }

//    @JvmStatic
//    fun main(args: Array<String>) {
//        println(AsmUtil::class.toJvmType())
//    }
}