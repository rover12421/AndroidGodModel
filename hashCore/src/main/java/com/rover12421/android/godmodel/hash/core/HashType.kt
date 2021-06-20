package com.rover12421.android.godmodel.hash.core

/**
 * Created by rover12421 on 6/4/21.
 * hash 支持类型
 */
enum class IntHashType {
    Size, // 仅使用长度做hash值
    HashCode,
    MurmurHash2,
    MurmurHash3
}

enum class LongHashType {
    Size, // 仅使用长度做hash值
    HashCode,
    MurmurHash2,
    MurmurHash3Part1,
    MurmurHash3Part2,
}

enum class StringHashType {
    Size, // 仅使用长度做hash值
    HashCode,
    Base64,
    MD5,
    SHA1,
}