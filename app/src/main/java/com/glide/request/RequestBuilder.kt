package com.glide.request

import android.widget.ImageView
import com.glide.GlideContext
import com.glide.Target
import com.glide.manager.RequestManager
import java.io.File

/**
 * Created by 李志云 2019/4/17 03:29
 */
class RequestBuilder(private val glideContext:GlideContext, val requestManager:RequestManager) {
        private var requestOptions = glideContext.defaultRequestOptions
        lateinit var model:Any

    fun apply(requestOptions:RequestOptions):RequestBuilder{
        this.requestOptions = requestOptions
        return this
    }

    fun load(string: String):RequestBuilder{
        this.model = string
        return this
    }
    fun load(file: File):RequestBuilder{
        this.model = file
        return this
    }


    fun into(view: ImageView){
        val target = Target(view)
        val request = Request(glideContext,requestOptions,model,target)
        requestManager.track(request)
    }

}