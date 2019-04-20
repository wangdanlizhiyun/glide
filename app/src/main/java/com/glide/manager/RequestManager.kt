package com.glide.manager

import android.util.Log
import com.glide.GlideContext
import com.glide.request.Request
import com.glide.request.RequestBuilder
import com.gucci.lifecycle.Lifecycle
import com.gucci.lifecycle.LifecycleListener
import java.io.File

/**
 * Created by 李志云 2019/4/16 02:01
 */
class RequestManager(val glideContext: GlideContext) : LifecycleListener {
    val requestTrack = RequestTrack()

    override fun onCreate() {

    }

    override fun onDestory() {
        requestTrack.clearRequests()
    }

    override fun onPause() {
    }

    override fun onResume() {
    }

    override fun onStart() {
        requestTrack.resumeRequests()
    }

    override fun onStop() {
        requestTrack.pauseRequests()
    }


    fun load(string: String): RequestBuilder {
        return RequestBuilder(glideContext, this).load(string)
    }

    fun load(file: File): RequestBuilder {
        return RequestBuilder(glideContext, this).load(file)
    }

    fun track(request: Request){
        requestTrack.runRequest(request)
    }
}