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
import java.lang.reflect.Modifier

@Suppress("UNCHECKED_CAST")
class HashGodHand(project: Project, godHandProp: GodHandProp) : GodHand(project, godHandProp) {

    val prop = HashPluginProp()

    init {
        prop.removeHashObjectNameAnnotation = godHandProp.getBoolenProp("removeHashObjectNameAnnotation", true)
        prop.skipConstructor        = godHandProp.getBoolenProp("skipConstructor", true)
        prop.skipNonPublicField     = godHandProp.getBoolenProp("skipNonPublicField", true)
        prop.skipNonPublicMethod    = godHandProp.getBoolenProp("skipNonPublicMethod", true)
        prop.skipMethod             = godHandProp.getBoolenProp("skipMethod", false)
        prop.skipField              = godHandProp.getBoolenProp("skipField", false)
        prop.skipNonStaticField     = godHandProp.getBoolenProp("skipNonStaticField", false)
        prop.skipNonStaticMethod    = godHandProp.getBoolenProp("skipNonStaticMethod", false)
    }

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
        val needAddAnnotations = classIntHashTypeSet.isNotEmpty() || classLongHashTypeSet.isNotEmpty() || classStringHashTypeSet.isNotEmpty()

        cn.fields.forEach { fieldNode ->
            var objName = fieldNode.name
            val objNameAnns = fieldNode.visibleAnnotations?.filter { it.desc.equals(HashObjectName::class.toJvmType()) }
            if (!objNameAnns.isNullOrEmpty() && objNameAnns.first().values.size == 2) {
                objName = objNameAnns.first().values[1].toString()
                if (prop.removeHashObjectNameAnnotation) {
                    fieldNode.visibleAnnotations.removeAll(objNameAnns)
                }
            }

            val intHashTypeSet = checkIntHash(fieldNode.visibleAnnotations)
            val longHashTypeSet = checkLongHash(fieldNode.visibleAnnotations)
            val stringHashTypeSet = checkStringHash(fieldNode.visibleAnnotations)

            val isStatic = Modifier.isStatic(fieldNode.access)
            val isPublic = Modifier.isPublic(fieldNode.access)
            val hasAnnotation = intHashTypeSet.isNotEmpty() || longHashTypeSet.isNotEmpty() || stringHashTypeSet.isNotEmpty()
            if (!needAddAnnotations && !hasAnnotation) {
                return@forEach
            }
            if (needAddAnnotations && !hasAnnotation) {
                if (prop.skipField
                    || (!isStatic && prop.skipNonStaticField)
                    || (!isPublic && prop.skipNonPublicField)) {
                    return@forEach
                }
            }

            intHashTypeSet.addAll(classIntHashTypeSet)
            longHashTypeSet.addAll(classLongHashTypeSet)
            stringHashTypeSet.addAll(classStringHashTypeSet)

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
                if (prop.removeHashObjectNameAnnotation) {
                    methodNode.visibleAnnotations.removeAll(objNameAnns)
                }
            }

            val intHashTypeSet = checkIntHash(methodNode.visibleAnnotations)
            val longHashTypeSet = checkLongHash(methodNode.visibleAnnotations)
            val stringHashTypeSet = checkStringHash(methodNode.visibleAnnotations)

            val isStatic = Modifier.isStatic(methodNode.access)
            val isPublic = Modifier.isPublic(methodNode.access)
            val isConstructor = methodNode.name.equals("<init>")
            val isStaticConstructor = methodNode.name.equals("<clinit>")
            val hasAnnotation = intHashTypeSet.isNotEmpty() || longHashTypeSet.isNotEmpty() || stringHashTypeSet.isNotEmpty()
            if (isStaticConstructor || (!needAddAnnotations && !hasAnnotation)) {
                return@forEach
            }

            if (needAddAnnotations && !hasAnnotation) {
                if (prop.skipMethod
                    || (!isStatic && prop.skipNonStaticMethod)
                    || (!isPublic && prop.skipNonPublicMethod)
                    || (isConstructor && prop.skipConstructor)
                ) {
                    return@forEach
                }
            }

            intHashTypeSet.addAll(classIntHashTypeSet)
            longHashTypeSet.addAll(classLongHashTypeSet)
            stringHashTypeSet.addAll(classStringHashTypeSet)

            methodNode.visibleAnnotations?.removeIf { it.desc.equals(Hash::class.toJvmType()) }
            val annotationVisitor = methodNode.visitAnnotation(Hash::class.toJvmType(), true)
            addIntHashAnnotations(annotationVisitor, intHashTypeSet, objName)
            addLongHashAnnotations(annotationVisitor, longHashTypeSet, objName)
            addStringHashAnnotations(annotationVisitor, stringHashTypeSet, objName)
        }
    }
}