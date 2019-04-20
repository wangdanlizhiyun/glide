package com.glide

import android.content.ComponentCallbacks2
import java.util.ArrayList

/**
 * Created by 李志云 2019/4/17 02:59
 */
fun String.byteArray(): ByteArray {
    val length = this.length / 2
    val hexChars = this.toCharArray()
    val d = ByteArray(length)
    for (i in 0..length - 1) {
        val pos = i * 2
        d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
    }
    return d
}

fun <K, V> android.util.LruCache<K, V>.defaultTrimMemory(level: Int) {
    if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
        evictAll()
    } else if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
        trimToSize(maxSize() / 2)
    }
}

fun <K, V> android.support.v4.util.LruCache<K, V>.defaultTrimMemory(level: Int) {
    if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
        evictAll()
    } else if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
        trimToSize(maxSize() / 2)
    }
}

private fun charToByte(c: Char): Byte {
    return "0123456789ABCDEF".indexOf(c).toByte()
}
