package com.glide.cache

/**
 * Created by 李志云 2019/4/17 04:54
 */
interface ArrayPool {
    fun get(len:Int):ByteArray

    fun put(data:ByteArray)

    fun clearMemory()

    fun trimMemory(level:Int)

    fun getMaxSize():Int
}