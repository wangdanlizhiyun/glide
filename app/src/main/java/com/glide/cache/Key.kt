package com.glide.cache

import java.security.MessageDigest

/**
 * Created by 李志云 2019/3/2 06:27
 */
interface Key {
    fun updateDiskCacheKey(md:MessageDigest)
    fun getKeyBytes(): ByteArray
}