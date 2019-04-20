package com.glide.cache.recycle

import android.graphics.Bitmap

/**
 * Created by 李志云 2019/4/17 04:56
 */
interface BitmapPool {
    fun put(bitmap: Bitmap)

    fun get(width:Int,height:Int,config: Bitmap.Config): Bitmap?

    fun clearMemory()

    fun trimMemory(level:Int)
}