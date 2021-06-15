package com.rover12421.android.godmodel.base

import com.android.utils.FileUtils
import com.rover12421.android.godmodel.base.asm.GodClassWriter
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
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

//        logger.log(LogLevel.WARN, "isHandClass >>> : $className | godHands.size: ${godHands.size}")
        if (className.startsWith("com.rover12421.android.godmodel")) {
            return false
        }
        if (godHands.firstOrNull {
                logger.log(LogLevel.WARN, "[${it.godHandProp.name}] isHandClass : $className >>> ${it.isHandClass(className)}")
                it.isHandClass(className)
        } != null) {
            logger.log(LogLevel.WARN, "isHandClass : $className is true")
            return true
        }
        return false
    }

    private fun checkHand(hand: GodHandBase, className: String, isJar: Boolean): Boolean {
//        logger.log(LogLevel.WARN, "checkHand [${hand.godHandProp.name}] >>> : $className, isJar: $isJar, hand.isHandJar(): ${hand.isHandJar()}, size: ${godHands.size}")
        if (isJar && !hand.isHandJar()) {
            return false
        }
        return hand.isHandClass(className)
    }

    private fun wrapperClassReader(cr: ClassReader, className: String, isJar: Boolean): ClassReader {
        var result = cr
        godHands.forEach { hand ->
            if (checkHand(hand, className, isJar)) {
                result = hand.wrapperClassReader(result)
            }
        }
        return result
    }

    private fun wrapperClassWriter(cw: ClassWriter, className: String, isJar: Boolean): ClassWriter {
        var result = cw
        godHands.forEach { hand ->
            if (checkHand(hand, className, isJar)) {
                result = hand.wrapperClassWriter(result)
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
        val classReader = ClassReader(classBytes)
//        logger.log(LogLevel.WARN, "[handClass] ${classReader.className} >>1 size : ${godHands.size}")
        val className: String = classReader.className.replace("/", ".")
        val cr = wrapperClassReader(classReader, className, isJar)
        val cw = wrapperClassWriter(GodClassWriter(classLoader, ClassWriter.COMPUTE_MAXS), className, isJar)
        if (isHandClassNode(className, isJar)) {
            val cn = ClassNode()
            cr.accept(cn, ClassReader.EXPAND_FRAMES)
            handClassNode(cn, className, isJar)
            cn.accept(cw)
        } else {
            cr.accept(cw, ClassReader.EXPAND_FRAMES)
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