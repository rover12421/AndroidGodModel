package com.rover12421.android.godmodel.namehash.base

data class HashData(val len: Int,
                    val hashcode: Int,
                    val hash: Int) {

    companion object {
        @JvmStatic
        fun of(ann: HashValue): HashData = HashData(ann.len, ann.hashcode, ann.hash)
    }
}