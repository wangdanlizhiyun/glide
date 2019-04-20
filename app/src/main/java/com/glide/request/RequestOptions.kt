package com.glide.request

/**
 * Created by 李志云 2019/4/16 02:24
 */
class RequestOptions {
    var errorId:Int = 0
    var placeholderId = 0
    var overrideWidth = -1
    var overrideHeight = -1

    fun placeholder(resourceId:Int):RequestOptions{
        this.placeholderId = resourceId
        return this
    }
    fun error(resourceId:Int):RequestOptions{
        this.errorId = resourceId
        return this
    }
    fun override(width:Int,height:Int):RequestOptions{
        this.overrideWidth = width
        this.overrideHeight = height
        return this
    }

}
