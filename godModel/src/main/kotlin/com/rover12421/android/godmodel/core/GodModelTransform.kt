package com.rover12421.android.godmodel.core

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.rover12421.android.godmodel.core.util.ClassLoaderHelper
import com.rover12421.android.godmodel.core.util.ProjectUtil
import kotlinx.coroutines.*
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import java.io.File
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

open class GodModelTransform(val project: Project) : Transform() {
    private val logger: Logger = project.logger

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val workList = CopyOnWriteArrayList<Job>()

    private fun addTask(block: () -> Unit) {
        if (godModelExtension.parallel) {
            workList.add(coroutineScope.launch {
                try {
                    block.invoke()
                } catch (e: Throwable) {
                    logger.log(LogLevel.WARN, "GodModelTransform", e)
                }
            })
        } else {
            try {
                block.invoke()
            } catch (e: Throwable) {
                logger.log(LogLevel.WARN, "GodModelTransform", e)
            }
        }
    }

    private fun waitAllTaskCompleted() {
        runBlocking {
            workList.joinAll()
        }
    }

    val godModelExtension: GodModelExtension = ProjectUtil.findOrCreateExtArgProject(project,
        GodModelExtension.ExtName, GodModelExtension::class.java)

    override fun getName(): String {
        return "GodModel"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        if (project.extensions.findByType(AppExtension::class.java) != null) {
            return TransformManager.SCOPE_FULL_PROJECT
        } else {
            return TransformManager.PROJECT_ONLY
        }
    }

    override fun isIncremental(): Boolean {
        return godModelExtension.incremental
    }

    override fun isCacheable(): Boolean {
        return godModelExtension.cacheable
    }

    open fun getRunVariant(): RunVariant {
        return godModelExtension.runVariant
    }

    var godHand: GodHand = GodHand(project, GodHandProp("GodHand"))
    val isDebug : Boolean = godModelExtension.debug

    override fun transform(transformInvocation: TransformInvocation) {
        val outputProvider = transformInvocation.outputProvider
        if (!isIncremental) {
            outputProvider.deleteAll()
        }
        val context = transformInvocation.context
        val runVariant = getRunVariant()

        val contextVariantName = context.variantName.toLowerCase(Locale.getDefault())
        logger.warn("[GodModel] isIncremental($isIncremental), runVariant($runVariant), contextVariantName($contextVariantName)");

        var skip = false
        if (runVariant == RunVariant.NEVER
            || (runVariant != RunVariant.ALWAYS && contextVariantName != runVariant.name.toLowerCase(Locale.getDefault()))
        ) {
            skip = true
        }


        val inputs = transformInvocation.inputs
        val classLoader = ClassLoaderHelper.getClassLoader(project, inputs, transformInvocation.referencedInputs)
        godHand.init(classLoader)

        val hands = mutableListOf<GodHandBase>()
        godModelExtension.godHands.all { prop ->
            val hand = try {
                prop.type.getConstructor(Project::class.java, GodHandProp::class.java).newInstance(project, prop)
            } catch (e: Throwable) {
                prop.type.newInstance()
            }
            logger.log(LogLevel.WARN, "find hand (${prop.name}) : $hand")
            hands.add(hand)
        }

        hands.forEach { hand ->
            if (isDebug) {
                logger.warn("[GodModel] check hand : $hand")
            }
            if (hand is GodHand) {
                hand.init(classLoader)
            }
            if (hand.getRunVariant() == RunVariant.NEVER
                || contextVariantName != hand.getRunVariant().name.toLowerCase(Locale.getDefault())
                    ) {
                logger.warn("[GodModel] add GodHand : $hand")
                godHand.godHands.add(hand)
//                logger.warn("[GodModel] godHand.godHands size : ${godHand.godHands.size}")
            } else {
                logger.warn("[GodModel] skip GodHand : $hand (${hand.getRunVariant()})")
            }
        }

        inputs.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                val dest = outputProvider.getContentLocation(
                    jarInput.file.absolutePath, jarInput.contentTypes, jarInput.scopes, Format.JAR
                )

                if (isIncremental && !skip && !godModelExtension.ignoreJar) {
                    when(jarInput.status) {
                        Status.NOTCHANGED -> {}
                        Status.ADDED, Status.CHANGED -> {
                            transformJar(jarInput.file, dest, skip)
                        }
                        Status.REMOVED -> {
                            FileUtils.deleteRecursivelyIfExists(dest)
                        }
                        else -> {}
                    }
                } else {
                    transformJar(jarInput.file, dest, skip)
                }
            }

            input.directoryInputs.forEach { directoryInput ->
                val dest = outputProvider.getContentLocation(
                    directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY
                )
                FileUtils.mkdirs(dest)
                if (isIncremental && !skip) {
                    val srcDirPath = Paths.get(directoryInput.file.absolutePath)
                    val destDirPath = Paths.get(dest.absolutePath)
                    directoryInput.changedFiles.forEach { (inputFile, status) ->
                        val inPath = Paths.get(inputFile.absolutePath)
                        val desFile = destDirPath.resolve(srcDirPath.relativize(inPath)).toFile()
                        when(status) {
                            Status.NOTCHANGED -> {}
                            Status.ADDED, Status.CHANGED -> {
                                FileUtils.mkdirs(desFile.parentFile)
                                transformFile(inputFile, desFile, skip)
                            }
                            Status.REMOVED -> {
                                FileUtils.deleteRecursivelyIfExists(dest)
                            }
                            else -> {}
                        }
                    }
                } else {
                    transformDir(directoryInput.file, dest, skip)
                }
            }
        }

        waitAllTaskCompleted()
    }

    private fun transformJar(srcJar: File, destJar: File, skip: Boolean) {
        addTask {
            if (skip) {
                destJar.parentFile.mkdirs()
                FileUtils.copyFile(srcJar, destJar)
            } else {
                godHand.handJar(srcJar, destJar)
            }
        }
    }

    private fun transformFile(from: File, to: File, skip: Boolean) {
        addTask {
            if (isDebug) {
                logger.log(LogLevel.DEBUG, "transformFile $from >> $skip >> size: ${godHand.godHands.size}")
            }

            if (skip || !from.name.endsWith(".class")) {
                to.parentFile.mkdirs()
                FileUtils.copyFile(from, to)
            } else {
                godHand.handClassFile(from, to)
            }
        }
    }

    private fun transformDir(from: File, to: File, skip: Boolean) {
        addTask {
            if (isDebug) {
                logger.log(LogLevel.DEBUG, "transformDir $from >> $skip")
            }
            if (skip) {
                to.mkdirs()
                FileUtils.copyDirectory(from, to)
            } else {
                val fromDirPath = Paths.get(from.absolutePath)
                val toDirPath = Paths.get(to.absolutePath)
                FileUtils.getAllFiles(from).forEach { fromFile ->
                    val fromPath = Paths.get(fromFile.absolutePath)
                    val desFile = toDirPath.resolve(fromDirPath.relativize(fromPath)).toFile()
                    transformFile(fromFile, desFile, skip)
                }
            }
        }
    }
}