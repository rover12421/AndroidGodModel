package com.rover12421.android.godmodel.core.util

import org.gradle.api.Project

object ProjectUtil {
    fun <T> findOrCreateExtNoArgs(project: Project, extName: String, extType: Class<T>): T {
        return  project.extensions.findByType(extType) ?: project.extensions.create(extName, extType)
    }

    fun <T> findOrCreateExtArgProject(project: Project, extName: String, extType: Class<T>): T {
        return  project.extensions.findByType(extType) ?: project.extensions.create(extName, extType, project)
    }
}