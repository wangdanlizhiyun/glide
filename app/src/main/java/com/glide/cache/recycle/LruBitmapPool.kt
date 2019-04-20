package com.glide.cache.recycle

import android.content.ComponentCallbacks2
import android.graphics.Bitmap
import android.support.v4.util.LruCache
import com.glide.Utils
import java.util.*

/**
 * Created by 李志云 2019/4/17 04:58
 */
class LruBitmapPool(maxSize: Int) : LruCache<Int, Bitmap>(maxSize), BitmapPool {

    private val MAX_OVER_SIZE_MULTIPLE = 2
    private var isRemoved = false
    val map: NavigableMap<Int, Int> = TreeMap<Int, Int>()

    override fun put(bitmap: Bitmap) {
        if (!bitmap.isMutable) {
            bitmap.recycle()
            return
        }
        val size = Utils.getByteCount(bitmap)
        if (size >= maxSize()) {
            bitmap.recycle()
            return
        }
        put(size, bitmap)
        map.put(size, 0)
    }

    override fun get(width: Int, height: Int, config: Bitmap.Config): Bitmap? {
        val size = width * height * Utils.getPixelsCout(config)
        val key = map.ceilingKey(size)
        if (key != null && key <= size * MAX_OVER_SIZE_MULTIPLE) {
            isRemoved = true
            val remove = remove(key)
            isRemoved = false
            return remove
        }
        return null
    }

    override fun sizeOf(key: Int, value: Bitmap): Int {
        return Utils.getByteCount(value)
    }

    override fun entryRemoved(evicted: Boolean, key: Int, oldValue: Bitmap, newValue: Bitmap?) {
        map.remove(key)
        if (!isRemoved) {
            oldValue.recycle()
        }
    }

    override fun clearMemory() {
        evictAll()
    }

    override fun trimMemory(level: Int) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            clearMemory()
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            trimToSize(maxSize() / 2)
        }
    }

}