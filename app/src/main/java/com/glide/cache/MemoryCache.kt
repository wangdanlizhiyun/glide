package com.glide.cache

import com.glide.cache.recycle.Resource

/**
 * Created by 李志云 2019/3/2 06:09
 */
interface MemoryCache {

    fun remove2(key: Key):Resource?

    fun put2(key: Key,resource:Resource):Resource?

    fun setResourceRemoveListener(listener: ResouceRemoveListener)
    interface ResouceRemoveListener{
        fun onResouceRemove(resource: Resource)
    }

    fun clearMemory()
    fun trimMemory(level:Int)

}