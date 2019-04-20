package com.glide.cache

import java.io.File

/**
 * Created by 李志云 2019/4/17 05:24
 */
interface DiskCache {
    interface Writer {
        fun write(file: File): Boolean
    }

    fun get(key: Key): File?
    fun put(key: Key, writer: Writer)
    fun delete(key: Key)
    fun clear()
}