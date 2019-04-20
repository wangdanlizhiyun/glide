package com.glide.cache

import android.content.ComponentCallbacks2
import android.graphics.Bitmap
import android.os.Build
import android.util.LruCache
import com.glide.cache.recycle.BitmapPool
import com.glide.defaultTrimMemory
import java.util.*

private const val MAX_OVER_SIZE_MULTIPLE = 2
/**
 * Created by 李志云 2019/3/2 17:44
 */
class LruBitmapPool(maxSize: Int) : BitmapPool, LruCache<Int, Bitmap>(maxSize) {
    override fun clearMemory() {
        evictAll()
    }

    override fun trimMemory(level: Int) {
        defaultTrimMemory(level)
    }

    val map:NavigableMap<Int,Int> = TreeMap()
    var isRemoved = false

    override fun put(bitmap: Bitmap) {
        if (!bitmap.isMutable){
            bitmap.recycle()
            return
        }
        val size = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bitmap.allocationByteCount
        } else {
            bitmap.byteCount
        }
        if (size >= maxSize()){
            bitmap.recycle()
            return
        }
        put(size,bitmap)
    }

    override fun get(width: Int, height: Int, config: Bitmap.Config): Bitmap? {
        val size = width*height*4
        val key = map.ceilingKey(size)
        key?.let {
            if (it <= size * MAX_OVER_SIZE_MULTIPLE){
                isRemoved = true
                val bitmap = remove(key)
                isRemoved = false
                return bitmap
            }
        }
        return null
    }

    override fun sizeOf(key: Int?, value: Bitmap): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            value.allocationByteCount
        } else {
            value.byteCount
        }
    }

    override fun entryRemoved(evicted: Boolean, key: Int?, oldValue: Bitmap?, newValue: Bitmap?) {
        map.remove(key)
        if (!isRemoved){
            oldValue?.recycle()
        }
    }
}