package com.rover12421.android.godmodel.hash.plugin

import com.rover12421.android.godmodel.base.GodHand
import com.rover12421.android.godmodel.base.GodHandProp
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.objectweb.asm.tree.ClassNode

class HashGodHand(project: Project, godHandProp: GodHandProp) : GodHand(project, godHandProp) {
    override fun isHandClassNode(): Boolean {
        return true
    }

    override fun handClassNode(cn: ClassNode) {
        logger.log(LogLevel.WARN, "[HashGodHand] class: ${cn.name} ")
        cn.fields.forEach { fieldNode ->
            fieldNode.visibleAnnotations?.forEach { annotationNode ->
                logger.log(LogLevel.WARN, "[HashGodHand] >>>[${cn.name}] Field: ${fieldNode.name} => annotationNode.desc : ${annotationNode.desc}")
            }
        }

        cn.methods.forEach { methodNode ->
            methodNode.visibleAnnotations?.forEach { annotationNode ->
                logger.log(LogLevel.WARN, "[HashGodHand] >>>[${cn.name}] Method: ${methodNode.name} ${methodNode.signature} => annotationNode.desc : ${annotationNode.desc}")
            }
        }
    }
}