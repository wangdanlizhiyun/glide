package com.glide.load

import com.glide.byteArray
import com.glide.cache.Key
import java.security.MessageDigest


/**
 * Created by 李志云 2019/4/18 13:29
 */
class ObjectKey(val any: Any): Key {
    override fun getKeyBytes(): ByteArray {
        return any.toString().byteArray()
    }

    override fun updateDiskCacheKey(md: MessageDigest) {
        md.update(getKeyBytes())
    }

    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (other == null || javaClass != other.javaClass) return false

        val objectKey = other as ObjectKey

        return any == objectKey.any
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}