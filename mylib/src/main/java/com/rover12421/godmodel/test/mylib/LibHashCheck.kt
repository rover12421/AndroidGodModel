package com.rover12421.godmodel.test.mylib

import com.rover12421.android.godmodel.hash.base.*

@IntHash(IntHashType.Size, IntHashType.HashCode)
@LongHash(LongHashType.HashCode)
@StringHash(StringHashType.Base64, StringHashType.MD5, StringHashType.Base64)
object LibHashObject {
    const val sss = "s123456789012"
    const val int2 = 222
    const val long3 = 333L

    internal val i4 = 444
    private val s5 = "5555123456"
}

@LongHash(LongHashType.HashCode)
class LibHashCheck {
    companion object {
        const val s1 = "ssss1111345"

        @Hash()
        const val s2 = "23"
    }

    val i1 = 100
    var i2 = 200

    @LongHash(LongHashType.Size)
    fun publicFun() {
        println("my is public")
    }

    fun publicFun222() {
        println("my is public222 - 01234")
    }

    @StringHash
    @HashObjectName("myObj")
    private fun privateFun() {
        println("my is private")
    }

    @Hash(intHash = [IntHashValue(IntHashType.Size, 1), IntHashValue(IntHashType.HashCode, 222)])
    private fun intHashValues() {
        println("my is private 01")
    }

    @Hash(intHash = [IntHashValue(IntHashType.Size, 1), IntHashValue(IntHashType.HashCode, 222)],
    stringHash = [StringHashValue(StringHashType.Base64, "000"), StringHashValue(StringHashType.MD5, "3333")])
    private fun intHashAndStringValues() {
        println("my is private 01234567")
    }
}