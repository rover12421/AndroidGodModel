package com.rover12421.godmodel.test.myapp

import com.rover12421.android.godmodel.hash.base.IntHash
import com.rover12421.android.godmodel.namehash.base.HashName

@IntHash
@HashName
object CanProguard {
    @HashName("test1", "test001")
    fun test001() {
        println("test00123")
    }

    @HashName
    fun test002() {
        println("test001234567")
    }
}

@HashName("c2")
class CanProguard2 {
    @HashName("ss")
    val ss: String = "ssss"

    class cc() {
        @HashName("ss1")
        val ss1: String = "ssss"
    }

    inner class cc2() {
        @HashName("ss4")
        val ss3: String = this@CanProguard2.ss
    }
}