package com.rover12421.godmodel.test.mylib

import com.rover12421.android.godmodel.hash.base.IntHash
import com.rover12421.android.godmodel.namehash.base.HashName

@IntHash
@HashName
object LibCanProguard {
    @HashName("test1", "test001")
    fun test001() {
        println("test001230")
    }

    @HashName
    fun test002() {
        println("test0012345678")
    }
}

@HashName("c2")
class LibCanProguard2 {
    @HashName("ss")
    val ss: String = "ssss123450"

    class llcc() {
        @HashName("ss1")
        val ss1: String = "ssss"
    }

    inner class llcc2() {
        @HashName("ss4")
        val ss3: String = this@LibCanProguard2.ss
    }
}