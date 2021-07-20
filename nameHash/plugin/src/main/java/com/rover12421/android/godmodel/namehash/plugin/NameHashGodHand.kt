package com.rover12421.android.godmodel.namehash.plugin

import com.rover12421.android.godmodel.core.GodHand
import com.rover12421.android.godmodel.core.GodHandProp
import com.rover12421.android.godmodel.core.util.AsmUtil
import com.rover12421.android.godmodel.namehash.base.Hash
import com.rover12421.android.godmodel.namehash.base.HashName
import com.rover12421.android.godmodel.namehash.base.HashValue
import org.apache.commons.codec.digest.MurmurHash3
import org.gradle.api.Project
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import kotlin.reflect.KClass

inline fun String.toJvmStyle() = AsmUtil.toJvmClazz(this)
inline fun String.toJavaStyle() = AsmUtil.toJavaClazz(this)
inline fun String.toJvmType() = AsmUtil.toJvmType(this)
inline fun Class<*>.toJvmType() = AsmUtil.toJvmType(this.name)
inline fun KClass<*>.toJvmType() = AsmUtil.toJvmType(this.java.name)

@Suppress("UNCHECKED_CAST")
class NameHashGodHand(project: Project, godHandProp: GodHandProp) : GodHand(project, godHandProp) {

    override fun isHandClassNode(): Boolean {
        return true
    }

    fun findNameHashAnnoatation(visibleAnnotations: MutableList<AnnotationNode>?): AnnotationNode? {
        return visibleAnnotations?.firstOrNull {
            it.desc == HashName::class.toJvmType()
        }
    }

    fun handNameHashAnnoatation(visibleAnnotations: MutableList<AnnotationNode>, hashNameNode: AnnotationNode, av: AnnotationVisitor, objName: String) {
        val hashNames = mutableSetOf<String>()
        visibleAnnotations.remove(hashNameNode)
        val annotationValues = hashNameNode.values
        if (annotationValues != null && annotationValues.size == 2) {
            val list = annotationValues[1] as ArrayList<*>
            if (list.size > 0) {
                list.forEach { names ->
                    if (names is Array<*>) {
                        names as Array<String>
                        hashNames.addAll(names)
                    }
                }
            }
        }
        if (hashNames.isEmpty()) {
            hashNames.add(objName)
        }
        val values = hashNames.map { name ->
            val an = AnnotationNode(HashValue::class.toJvmType())
            an.visit("len", name.length)
            an.visit("hashcode", name.hashCode())
            val bytes = name.toByteArray()
            an.visit("hash", MurmurHash3.hash32x86(bytes, 0, bytes.size, MurmurHash3.DEFAULT_SEED))
            an
        }
        av.visit("vaules", values)
    }

    override fun handClassNode(cn: ClassNode) {
        findNameHashAnnoatation(cn.visibleAnnotations)?.let { hashNameNode ->
            cn.visibleAnnotations.remove(hashNameNode)
            val av = cn.visitAnnotation(Hash::class.toJvmType(), true)
            handNameHashAnnoatation(cn.visibleAnnotations, hashNameNode, av, AsmUtil.getSimpleClassName(cn.name))
        }

        cn.fields.forEach { fieldNode ->
            findNameHashAnnoatation(fieldNode.visibleAnnotations)?.let { hashNameNode ->
                val av = fieldNode.visitAnnotation(Hash::class.toJvmType(), true)
                handNameHashAnnoatation(fieldNode.visibleAnnotations, hashNameNode, av, AsmUtil.getSimpleClassName(fieldNode.name))
            }
        }

        cn.methods.forEach { methodNode ->
            findNameHashAnnoatation(methodNode.visibleAnnotations)?.let { hashNameNode ->
                val av = methodNode.visitAnnotation(Hash::class.toJvmType(), true)
                handNameHashAnnoatation(methodNode.visibleAnnotations, hashNameNode, av, AsmUtil.getSimpleClassName(methodNode.name))
            }
        }
    }
}