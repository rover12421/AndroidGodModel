package com.rover12421.android.godmodel.core

import com.android.utils.FileUtils
import com.rover12421.android.godmodel.core.asm.GodClassWriter
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

open class GodHand(project: Project, godHandProp: GodHandProp) : GodHandBase(project, godHandProp) {
    val godHands = mutableListOf<GodHandBase>()

    /**
     * 是否处理Jar
     */
    override fun isHandJar(): Boolean = false

    fun handJar(inJar: File, outJar: File) {
        if (!isHandJar() && godHands.firstOrNull { it.isHandJar() } == null) {
            FileUtils.copyFile(inJar, outJar)
            return
        }

        ZipFile(inJar).use { inZip ->
            outJar.outputStream().use { os ->
                ZipOutputStream(os).use { zos ->
                    val entries = inZip.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        inZip.getInputStream(entry).use { inputStream ->
                            val entryName = entry.name
                            var bytes = inputStream.readBytes()
                            if (entryName.endsWith(".class")) {
                                val className = entryName.substring(0, entryName.length - 6).replace("/", ".")
                                if (isHandClass(className)){
                                    bytes = handClass(bytes, true)
                                }
                            }
                            val outEntry = ZipEntry(entryName)
                            outEntry.method = ZipEntry.DEFLATED
                            zos.putNextEntry(outEntry)
                            zos.write(bytes)
                            zos.closeEntry()
                        }
                    }
                }
            }
        }
    }

    override fun isHandClass(className: String): Boolean {
        if (this::class.java.name != GodHand::class.java.name) {
            return super.isHandClass(className)
        }

        if (godHandProp.debug) {
            logger.warn( "isHandClass >>> : $className | godHands.size: ${godHands.size}")
        }
        if (className.startsWith("com.rover12421.android.godmodel")) {
            return false
        }
        if (godHands.firstOrNull {
                logger.warn( "[${it.godHandProp.name}] isHandClass : $className >>> ${it.isHandClass(className)}")
                it.isHandClass(className)
        } != null) {
            logger.warn( "isHandClass : $className is true")
            return true
        }
        return false
    }

    private fun checkHand(hand: GodHandBase, className: String, isJar: Boolean): Boolean {
        if (godHandProp.debug) {
            logger.warn( "checkHand [${hand.godHandProp.name}] >>> : $className, isJar: $isJar, hand.isHandJar(): ${hand.isHandJar()}, size: ${godHands.size}")
        }
        if (isJar && !hand.isHandJar()) {
            return false
        }
        return hand.isHandClass(className)
    }

    private fun wrapperClassWriter(cw: ClassWriter, className: String, isJar: Boolean): ClassVisitor {
        var result:ClassVisitor = cw
        godHands.forEach { hand ->
            if (checkHand(hand, className, isJar)) {
                result = hand.handClassVisitor(Opcodes.ASM7, result)
            }
        }
        return result
    }

    private fun isHandClassNode(className: String, isJar: Boolean): Boolean {
        return godHands.firstOrNull{ checkHand(it, className, isJar) && it.isHandClassNode() } != null
    }

    private fun handClassNode(cn: ClassNode, className: String, isJar: Boolean) {
        godHands.forEach { hand ->
            if (checkHand(hand, className, isJar) && hand.isHandClassNode()) {
                hand.handClassNode(cn)
            }
        }
    }

    private fun handClass(classBytes: ByteArray, isJar: Boolean): ByteArray {
        val cr = ClassReader(classBytes)
        if (godHandProp.debug) {
            logger.warn( "[handClass] ${cr.className} >>1 size : ${godHands.size}")
        }
        val className: String = cr.className.replace("/", ".")
        val cw = GodClassWriter(classLoader, ClassWriter.COMPUTE_MAXS)
        val cv = wrapperClassWriter(cw, className, isJar)
        if (isHandClassNode(className, isJar)) {
            val cn = ClassNode()
            cr.accept(cn, ClassReader.EXPAND_FRAMES)
            handClassNode(cn, className, isJar)
            cn.accept(cv)
        } else {
            cr.accept(cv, ClassReader.EXPAND_FRAMES)
        }

        return cw.toByteArray()
    }

    fun handClassFile(from: File, to: File) {
        FileUtils.mkdirs(to.parentFile)
        to.writeBytes(
            handClass(
                from.readBytes(), false
            )
        )
    }
}