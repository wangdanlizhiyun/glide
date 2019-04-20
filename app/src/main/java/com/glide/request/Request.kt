package com.glide.request

import android.graphics.drawable.Drawable
import android.net.NetworkInfo
import android.support.v4.content.res.ResourcesCompat
import com.glide.GlideContext
import com.glide.Target
import com.glide.cache.recycle.Resource
import com.glide.load.Engine
import dalvik.system.PathClassLoader

/**
 * Created by 李志云 2019/4/16 02:19
 */
class Request(var context: GlideContext?, var requestOptions: RequestOptions?, var model: Any?, var target: Target?):Target.SizeReadyCallback ,ResourceCallback{
    override fun onResourceReady(resource: Resource?) {
        this.loadStatus = null
        this.resource = resource
        if (resource == null){
            status = Status.FAILED
            setErrorPlaceHolder()
            return
        }
        target?.onResourceReady(resource.bitmap)
    }

    override fun onSizeReady(width: Int, height: Int) {
        status = Status.RUNNING
        context?.let {context->
            model?.let {model->
                loadStatus = engine?.load(context,model,width,height,this)
            }
        }

    }

    enum class Status {
        PENDING,
        RUNNING,
        WAITING_FOR_SIZE,
        COMPLETE,
        FAILED,
        CANCELLED,
        CLEARED,
        PAUSED,
    }

    var resource:Resource? = null
    var status = Status.PENDING
    var loadStatus:Engine.LoadStatus? = null

    var engine = context?.engine

    private var errorDrawable: Drawable? = null

    private var placeholderDrawable: Drawable? = null

    fun getError():Drawable?{
        if (errorDrawable == null && requestOptions?.errorId ?: 0 > 0) {
            errorDrawable = loadDrawable(requestOptions?.errorId ?: 0)
        }
        return errorDrawable
    }
    fun getPlaceHolder():Drawable?{
        if (placeholderDrawable == null && requestOptions?.placeholderId ?: 0 > 0) {
            placeholderDrawable = loadDrawable(requestOptions?.placeholderId ?: 0)
        }
        return placeholderDrawable
    }


    fun begin() {
        status = Status.WAITING_FOR_SIZE
        target?.onLoadStarted(getPlaceHolder())
        var width = requestOptions?.overrideWidth?:0
        var height = requestOptions?.overrideHeight?:0
        if (width > 0 && height > 0){
            onSizeReady(width,height)
        }else{
            //TODO 计算view大小
            target?.getSize(this)
        }
    }

    fun cancel() {
        target?.cancel()
        status = Status.CANCELLED
        loadStatus?.let {
            it.cancel()
            loadStatus = null
        }

    }

    fun recycle() {
        context = null
        model = null
        requestOptions = null
        target = null
        errorDrawable = null
        placeholderDrawable = null
        loadStatus = null
    }

    fun clear() {
        if (status == Status.CLEARED){
            return
        }
        cancel()
        resource?.let { releaseResource(it) }
        status = Status.CLEARED
    }

    fun pause() {
        clear()
        status == Status.PAUSED
    }

    fun isRunning(): Boolean {
        return status == Status.RUNNING || status == Status.WAITING_FOR_SIZE
    }

    fun isComplete(): Boolean {
        return status == Status.COMPLETE
    }

    fun isCancelled(): Boolean {
        return status == Status.CANCELLED || status == Status.CLEARED
    }

    fun isPaused(): Boolean {
        return status == Status.PAUSED
    }

    fun loadDrawable(resourceId: Int): Drawable? {
        context?.let {
            return ResourcesCompat.getDrawable(it.context.resources, resourceId, it.context.theme)
        }
        return null
    }


    fun setErrorPlaceHolder(){
        target?.onLoadFailed(getError()?: getPlaceHolder())
    }

    fun releaseResource(resource:Resource){
        resource.release()
        this.resource = null
    }
}