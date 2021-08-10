package com.rover12421.android.godmodel.removeAnnotation.plugin

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor

class RemoveAnnotationVisitor(api: Int, classVisitor: ClassVisitor?, val removeAnnotations: Array<String>) : ClassVisitor(api, classVisitor) {

    var name: String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        this.name = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {

        if (descriptor != null) {
            var desc = descriptor
            if (desc.startsWith("L")) {
                desc = desc.removeSurrounding("L", ";").toJavaStyle()
            }
            if (desc in removeAnnotations) {
                return null
            }
        }

        return super.visitAnnotation(descriptor, visible)
    }
}