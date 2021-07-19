package com.rover12421.android.godmodel.core.util

import com.android.build.api.transform.TransformInput
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import java.io.File
import java.lang.RuntimeException
import java.net.URLClassLoader

object ClassLoaderHelper {

    private fun getAndroidJarPath(project: Project): String {
        val appExtension = project.extensions.findByType(AppExtension::class.java) ?: project.extensions.findByType(LibraryExtension::class.java)
        if (appExtension != null) {
            return "${appExtension.sdkDirectory}${File.separator}platforms${File.separator}${appExtension.compileSdkVersion}${File.separator}android.jar"
        } else {
            throw RuntimeException("Not Found AppExtension/LibraryExtension !!!")
        }
    }

    private fun checkInputs(inputs: Collection<TransformInput>, libs: MutableList<File>) {
        inputs.forEach{ input ->
            input.directoryInputs.forEach { directoryInput ->
                if (directoryInput.file.isDirectory) {
                    libs.add(directoryInput.file)
                }
            }
            input.jarInputs.forEach { jarInput ->
                if (jarInput.file.isFile) {
                    libs.add(jarInput.file)
                }
            }
        }
    }


    fun getClassLoader(
        project: Project,
        inputs: Collection<TransformInput>,
        referencedInputs: Collection<TransformInput>
    ): ClassLoader {

        val libs = mutableListOf(File(getAndroidJarPath(project)))
        checkInputs(inputs, libs)
        checkInputs(referencedInputs, libs)

        return URLClassLoader(libs.map { it.toURI().toURL() }.toTypedArray())
    }
}