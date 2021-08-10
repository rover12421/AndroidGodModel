package com.rover12421.android.godmodel.removeAnnotation.plugin

import com.rover12421.android.godmodel.core.GodHand
import com.rover12421.android.godmodel.core.GodHandProp
import com.rover12421.android.godmodel.core.util.AsmUtil
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor
import kotlin.reflect.KClass

inline fun String.toJvmStyle() = AsmUtil.toJvmClazz(this)
inline fun String.toJavaStyle() = AsmUtil.toJavaClazz(this)
inline fun String.toJvmType() = AsmUtil.toJvmType(this)
inline fun Class<*>.toJvmType() = AsmUtil.toJvmType(this.name)
inline fun KClass<*>.toJvmType() = AsmUtil.toJvmType(this.java.name)

@Suppress("UNCHECKED_CAST")
class RemoveAnnotationGodHand(project: Project, godHandProp: GodHandProp) : GodHand(project, godHandProp) {
    lateinit var removeAnnotations: Array<String>

    init {
        try {
            removeAnnotations = (godHandProp.property["remove"] as Collection<String>?)?.toTypedArray() ?: emptyArray()
        } catch (e: Throwable) {
            println("godHandProp.property[\"remove\"] >>> ${godHandProp.property["remove"]}")
            e.printStackTrace()
        }
    }

    override fun handClassVisitor(api: Int, cv: ClassVisitor): ClassVisitor {
        return RemoveAnnotationVisitor(api, cv, removeAnnotations)
    }
}