package com.glide.manager

import com.glide.request.Request
import com.gucci.lifecycle.getSnapshot
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by 李志云 2019/4/17 02:49
 */
class RequestTrack {
    val requests = Collections.newSetFromMap(WeakHashMap<Request,Boolean>())
    val pendingRequests = ArrayList<Request>()
    var isPaused = false
    fun runRequest(request: Request){
        requests.add(request)
        if (!isPaused){
            request.begin()
        }else{
            pendingRequests.add(request)
        }
    }

    fun pauseRequests(){
        isPaused = true
        requests.getSnapshot().forEach {
            if (it.isRunning()){
                it.pause()
                pendingRequests.add(it)
            }
        }
    }

    fun resumeRequests(){
        isPaused = false
        requests.getSnapshot().forEach {
            if (!it.isComplete() && !it.isCancelled()){
                it.begin()
            }
        }
        pendingRequests.clear()
    }

    fun clearRequests(){
        requests.getSnapshot().forEach {
            requests.remove(it)
            it.clear()
            it.recycle()
        }
        pendingRequests.clear()
    }


}