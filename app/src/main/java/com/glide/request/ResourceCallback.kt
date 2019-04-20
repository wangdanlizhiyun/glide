package com.glide.request

import com.glide.cache.recycle.Resource

/**
 * Created by 李志云 2019/4/16 02:28
 */
interface ResourceCallback {
    fun onResourceReady(resource: Resource?)
}