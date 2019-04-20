package com.glide.cache.recycle

import android.graphics.Bitmap
import com.glide.cache.Key
import java.lang.IllegalStateException

/**
 * Created by 李志云 2019/3/2 06:09
 */
class Resource(val bitmap: Bitmap) {
    //引用计数
    var acquired:Int = 0
    var listener:ResourceListener? = null
    fun setResourceListener(key:Key,listener: ResourceListener?){
        this.key = key
        this.listener = listener
    }
    var key: Key? = null

    interface ResourceListener{
        fun onResourceReleased(key:Key,resource:Resource)
    }

    fun release(){
        if (--acquired == 0){
            key?.let {
                listener?.onResourceReleased(it,this)
            }
        }
    }
    fun acquire(){
        if (bitmap.isRecycled){
            throw IllegalStateException("Acquire a recycled resource")
        }
        ++acquired
    }
    fun recycle(){
        if (acquired > 0){
            return
        }
        if (!bitmap.isRecycled){
            bitmap.recycle()
        }
    }
}