package com.glide.cache

import android.content.ComponentCallbacks2
import android.support.v4.util.LruCache
import com.glide.defaultTrimMemory
import java.util.*
import kotlin.math.max

/**
 * Created by 李志云 2019/4/17 05:54
 */
class LruArrayPool constructor(private val maxSize:Int = ARRAY_POOL_SIZE_BYTES):ArrayPool {
    override fun getMaxSize(): Int {
        return maxSize
    }

    companion object {
        val ARRAY_POOL_SIZE_BYTES = 4 * 1024 * 1024
        //单个资源的与maxsize 最大比例
        private val SINGLE_ARRAY_MAX_SIZE_DIVISOR = 2
        //溢出大小
        private val MAX_OVER_SIZE_MULTIPLE = 8
    }
    private var cache: LruCache<Int, ByteArray>
    init {
        cache = object :LruCache<Int,ByteArray>(maxSize){
            override fun sizeOf(key: Int, value: ByteArray): Int {
                return value.size
            }

            override fun entryRemoved(evicted: Boolean, key: Int, oldValue: ByteArray, newValue: ByteArray?) {
                sortedSizes.remove(oldValue.size)
            }
        }
    }

    private val sortedSizes: NavigableMap<Int,Int> = TreeMap<Int, Int>()

    override fun get(len: Int): ByteArray {
        val key = sortedSizes.ceilingKey(len)
        key?.let {
            if (key <= (MAX_OVER_SIZE_MULTIPLE * len)){
                val bytes = cache.remove(key)
                sortedSizes.remove(key)
                return bytes?:ByteArray(len)
            }
        }
        return ByteArray(len)
    }

    override fun put(data: ByteArray) {
        val length = data.size
        if (length > maxSize/SINGLE_ARRAY_MAX_SIZE_DIVISOR )return
        sortedSizes.put(length,1)
        cache.put(length,data)
    }

    override fun clearMemory() {
        cache.evictAll()
    }

    override fun trimMemory(level: Int) {
        cache.defaultTrimMemory(level)
    }

}