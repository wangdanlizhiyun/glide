package com.glide.cache

import android.support.v4.util.LruCache
import com.glide.Utils
import com.glide.cache.recycle.Resource
import com.glide.defaultTrimMemory

/**
 * Created by 李志云 2019/3/2 06:35
 */
open class LruMemoryCache(maxSize: Int) : LruCache<Key, Resource>(maxSize), MemoryCache {
    var isRemoved = false
    var listener: MemoryCache.ResouceRemoveListener? = null

    override fun setResourceRemoveListener(listener: MemoryCache.ResouceRemoveListener) {
        this.listener = listener
    }

    override fun remove2(key: Key): Resource? {
        isRemoved = true
        val resource = remove(key)
        isRemoved = false
        return resource
    }
    override fun put2(key: Key, resource: Resource): Resource? {
        return put(key,resource)
    }

    override fun sizeOf(key: Key, value: Resource): Int {
        return Utils.getByteCount(value.bitmap)
    }

    override fun entryRemoved(evicted: Boolean, key: Key, oldValue: Resource, newValue: Resource?) {
        if (isRemoved) return
        listener?.onResouceRemove(oldValue)
    }

    override fun clearMemory() {
        evictAll()
    }

    override fun trimMemory(level: Int) {
        defaultTrimMemory(level)
    }
}