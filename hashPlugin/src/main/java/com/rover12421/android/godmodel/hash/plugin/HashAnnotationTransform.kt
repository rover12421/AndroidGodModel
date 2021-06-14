package com.rover12421.android.godmodel.hash.plugin

import com.rover12421.android.godmodel.base.GodModelTransform
import org.gradle.api.Project

/**
 * Created by rover12421 on 6/5/21.
 */
open class HashAnnotationTransform(project: Project) : GodModelTransform(project) {

    override fun getName(): String {
        return "HashTransform"
    }

}