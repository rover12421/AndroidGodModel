package com.rover12421.godmodel.test.myapp

import com.rover12421.android.godmodel.hash.core.*

class HashCheck {
    @LongHash
    fun publicFun() {
        println("my is public")
    }

    @StringHash
    private fun privateFun() {
        println("my is private")
    }

    @Hash(intHashs = [IntHashValue(0, 1), IntHashValue(2, 222)])
    private fun intHashValues() {
        println("my is private")
    }

    @Hash(intHashs = [IntHashValue(0, 1), IntHashValue(2, 222)],
    stringHashs = [StringHashValue(0, "000"), StringHashValue(3, "3333")])
    private fun intHashAndStringValues() {
        println("my is private")
    }
}