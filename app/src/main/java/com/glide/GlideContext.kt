package com.glide

import android.content.Context
import com.glide.load.Engine
import com.glide.request.RequestOptions

/**
 * Created by 李志云 2019/4/16 02:18
 */
class GlideContext(val context: Context,val defaultRequestOptions:RequestOptions,val engine:Engine,val registry:Registry) {

    fun getApplicationContext():Context{
        return context.applicationContext
    }
}