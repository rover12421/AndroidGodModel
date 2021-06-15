package com.rover12421.android.godmodel.base

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode

open class GodHandBase(val project: Project, val godHandProp: GodHandProp) {
    protected var logger: Logger = project.logger

    protected lateinit var classLoader: ClassLoader

    open fun init(classloader: ClassLoader) {
        this.classLoader = classloader
    }

    open fun isHandJar(): Boolean = !godHandProp.ignoreJar
    open fun isHandClass(className: String): Boolean {
        val ret = godHandProp.filterRegex.matches(className)
//        logger.log(LogLevel.WARN, "[${godHandProp.name}] : ${godHandProp.filterRegex} matches $className = $ret")
        return ret
    }

    open fun wrapperClassReader(cr: ClassReader): ClassReader {
        return cr
    }

    open fun wrapperClassWriter(cw: ClassWriter): ClassWriter {
        return cw
    }

    open fun isHandClassNode(): Boolean {
        return false
    }

    open fun handClassNode(cn: ClassNode) {

    }

    open fun getRunVariant(): RunVariant = godHandProp.runVariant
}