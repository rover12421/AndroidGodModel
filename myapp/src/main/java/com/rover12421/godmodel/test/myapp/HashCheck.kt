package com.rover12421.godmodel.test.myapp

import com.rover12421.android.godmodel.hash.core.*

@IntHash(IntHashType.Size, IntHashType.HashCode)
@LongHash(LongHashType.HashCode)
@StringHash(StringHashType.Base64, StringHashType.MD5, StringHashType.Base64)
object HashObject {
    const val sss = "s1234567890"
    const val int2 = 222
    const val long3 = 333L

    internal val i4 = 444
    private val s5 = "5555"
}

@LongHash(LongHashType.HashCode)
class HashCheck {
    companion object {
        const val s1 = "ssss1111345"

        @Hash()
        const val s2 = "ssss1111111"
    }

    val i1 = 100
    var i2 = 200

    @LongHash(LongHashType.Size)
    fun publicFun() {
        println("my is public")
    }

    fun publicFun222() {
        println("my is public222 - 0123")
    }

    @StringHash
    @HashObjectName("myObj")
    private fun privateFun() {
        println("my is private")
    }

    @Hash(intHash = [IntHashValue(IntHashType.Size, 1), IntHashValue(IntHashType.HashCode, 222)])
    private fun intHashValues() {
        println("my is private 0")
    }

    @Hash(intHash = [IntHashValue(IntHashType.Size, 1), IntHashValue(IntHashType.HashCode, 222)],
    stringHash = [StringHashValue(StringHashType.Base64, "000"), StringHashValue(StringHashType.MD5, "3333")])
    private fun intHashAndStringValues() {
        println("my is private 01234567")
    }
}