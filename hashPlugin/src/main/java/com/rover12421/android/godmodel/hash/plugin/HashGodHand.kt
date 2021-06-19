package com.rover12421.android.godmodel.hash.plugin

import com.rover12421.android.godmodel.base.GodHand
import com.rover12421.android.godmodel.base.GodHandProp
import com.rover12421.android.godmodel.base.util.toJvmType
import com.rover12421.android.godmodel.hash.core.*
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode

@Suppress("UNCHECKED_CAST")
class HashGodHand(project: Project, godHandProp: GodHandProp) : GodHand(project, godHandProp) {
    override fun isHandClassNode(): Boolean {
        return true
    }

    fun checkIntHash(visibleAnnotations: MutableList<AnnotationNode>?): MutableSet<IntHashType> {
        val hashTypeSet = mutableSetOf<IntHashType>()
        if (visibleAnnotations == null) {
            return hashTypeSet
        }
        val findTypeAnnotation = visibleAnnotations.firstOrNull {
            it.desc.equals(IntHash::class.toJvmType())
        }
        if (findTypeAnnotation != null) {
            visibleAnnotations.remove(findTypeAnnotation)
            val values = findTypeAnnotation.values
            if (values != null && values.size == 2) {
                val list = values[1] as ArrayList<*>
                if (list.size > 0) {
                    list.forEach { types ->
                        if (types is Array<*>) {
                            types as Array<String>
                            types.forEach { type ->
                                try {
                                    if (!type.contains(";")) {
                                        hashTypeSet.add(IntHashType.valueOf(type))
                                    }
                                } catch (ignore: Throwable){}
                            }
                        }
                    }
                }
            }
        }
        return hashTypeSet
    }

    fun checkLongHash(visibleAnnotations: MutableList<AnnotationNode>?): MutableSet<LongHashType> {
        val hashTypeSet = mutableSetOf<LongHashType>()
        if (visibleAnnotations == null) {
            return hashTypeSet
        }
        val findTypeAnnotation = visibleAnnotations.firstOrNull { it.desc.equals(LongHash::class.toJvmType()) }
        if (findTypeAnnotation != null) {
            visibleAnnotations.remove(findTypeAnnotation)
            val values = findTypeAnnotation.values
            if (values != null && values.size == 2) {
                val list = values[1] as ArrayList<*>
                if (list.size > 0) {
                    list.forEach { types ->
                        if (types is Array<*>) {
                            types as Array<String>
                            types.forEach { type ->
                                try {
                                    if (!type.contains(";")) {
                                        hashTypeSet.add(LongHashType.valueOf(type))
                                    }
                                } catch (ignore: Throwable){}
                            }
                        }
                    }
                }
            }
        }
        return hashTypeSet
    }

    fun checkStringHash(visibleAnnotations: MutableList<AnnotationNode>?): MutableSet<StringHashType> {
        val hashTypeSet = mutableSetOf<StringHashType>()
        if (visibleAnnotations == null) {
            return hashTypeSet
        }
        val findTypeAnnotation = visibleAnnotations.firstOrNull { it.desc.equals(StringHash::class.toJvmType()) }
        if (findTypeAnnotation != null) {
            visibleAnnotations.remove(findTypeAnnotation)
            val values = findTypeAnnotation.values
            if (values != null && values.size == 2) {
                val list = values[1] as ArrayList<*>
                if (list.size > 0) {
                    list.forEach { types ->
                        if (types is Array<*>) {
                            types as Array<String>
                            types.forEach { type ->
                                try {
                                    if (!type.contains(";")) {
                                        hashTypeSet.add(StringHashType.valueOf(type))
                                    }
                                } catch (ignore: Throwable){}
                            }
                        }
                    }
                }
            }
        }
        return hashTypeSet
    }

    fun addIntHashAnnotations(annotationVisitor: AnnotationVisitor, hashTypeSet: MutableSet<IntHashType>, objName: String) {
        if (hashTypeSet.isNotEmpty()) {
            val values = hashTypeSet.map { hashType ->
                val annotationNode = AnnotationNode(IntHashValue::class.toJvmType())
                annotationNode.visitEnum("type", IntHashType::class.toJvmType(), hashType.name)
                val value: Int = when(hashType) {
                    IntHashType.Size -> objName.length
                    IntHashType.HashCode -> objName.hashCode()
                    else -> 0
                }
                annotationNode.visit("value", value)
                annotationNode
            }
            annotationVisitor.visit("intHash", values)
        }
    }

    fun addLongHashAnnotations(annotationVisitor: AnnotationVisitor, hashTypeSet: MutableSet<LongHashType>, objName: String) {
        if (hashTypeSet.isNotEmpty()) {
            val values = hashTypeSet.map { hashType ->
                val annotationNode = AnnotationNode(LongHashValue::class.toJvmType())
                annotationNode.visitEnum("type", LongHashType::class.toJvmType(), hashType.name)
                val value: Long = when(hashType) {
                    LongHashType.Size -> objName.length.toLong()
                    LongHashType.HashCode -> objName.hashCode().toLong()
                    else -> 0L
                }
                annotationNode.visit("value", value)
                annotationNode
            }
            annotationVisitor.visit("longHash", values)
        }
    }

    fun addStringHashAnnotations(annotationVisitor: AnnotationVisitor, hashTypeSet: MutableSet<StringHashType>, objName: String) {
        if (hashTypeSet.isNotEmpty()) {
            val values = hashTypeSet.map { hashType ->
                val annotationNode = AnnotationNode(StringHashValue::class.toJvmType())
                annotationNode.visitEnum("type", StringHashType::class.toJvmType(), hashType.name)
                val value: String = when(hashType) {
                    StringHashType.Size -> objName.length.toString()
                    StringHashType.HashCode -> objName.hashCode().toString()
                    StringHashType.MD5 -> "MD5($objName)"
                    StringHashType.Base64 -> "Base64($objName)"
                    else -> ""
                }
                annotationNode.visit("value", value)
                annotationNode
            }
            annotationVisitor.visit("stringHash", values)
        }
    }

    override fun handClassNode(cn: ClassNode) {
        logger.log(LogLevel.WARN, "[HashGodHand] class: ${cn.name} ")

        /**
         * 获取类定义的hash类型
         */
        val classIntHashTypeSet = checkIntHash(cn.visibleAnnotations)
        val classLongHashTypeSet = checkLongHash(cn.visibleAnnotations)
        val classStringHashTypeSet = checkStringHash(cn.visibleAnnotations)

        cn.fields.forEach { fieldNode ->
            var objName = fieldNode.name
            val objNameAnns = fieldNode.visibleAnnotations?.filter { it.desc.equals(HashObjectName::class.toJvmType()) }
            if (!objNameAnns.isNullOrEmpty() && objNameAnns.first().values.size == 2) {
                objName = objNameAnns.first().values[1].toString()
            }

            val intHashTypeSet = checkIntHash(fieldNode.visibleAnnotations).apply { addAll(classIntHashTypeSet) }
            val longHashTypeSet = checkLongHash(fieldNode.visibleAnnotations).apply { addAll(classLongHashTypeSet) }
            val stringHashTypeSet = checkStringHash(fieldNode.visibleAnnotations).apply { addAll(classStringHashTypeSet) }

            fieldNode.visibleAnnotations?.removeIf { it.desc.equals(Hash::class.toJvmType()) }
            val annotationVisitor = fieldNode.visitAnnotation(Hash::class.toJvmType(), true)
            addIntHashAnnotations(annotationVisitor, intHashTypeSet, objName)
            addLongHashAnnotations(annotationVisitor, longHashTypeSet, objName)
            addStringHashAnnotations(annotationVisitor, stringHashTypeSet, objName)
        }

        cn.methods.forEach { methodNode ->
            var objName = methodNode.name
            val objNameAnns = methodNode.visibleAnnotations?.filter { it.desc.equals(HashObjectName::class.toJvmType()) }
            if (!objNameAnns.isNullOrEmpty() && objNameAnns.first().values.size == 2) {
                objName = objNameAnns.first().values[1].toString()
            }

            val intHashTypeSet = checkIntHash(methodNode.visibleAnnotations).apply { addAll(classIntHashTypeSet) }
            val longHashTypeSet = checkLongHash(methodNode.visibleAnnotations).apply { addAll(classLongHashTypeSet) }
            val stringHashTypeSet = checkStringHash(methodNode.visibleAnnotations).apply { addAll(classStringHashTypeSet) }

            methodNode.visibleAnnotations?.removeIf { it.desc.equals(Hash::class.toJvmType()) }
            val annotationVisitor = methodNode.visitAnnotation(Hash::class.toJvmType(), true)
            addIntHashAnnotations(annotationVisitor, intHashTypeSet, objName)
            addLongHashAnnotations(annotationVisitor, longHashTypeSet, objName)
            addStringHashAnnotations(annotationVisitor, stringHashTypeSet, objName)
        }
    }
}