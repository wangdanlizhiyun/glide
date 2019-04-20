package com.glide.manager

import android.content.Context
import com.glide.GlideContext
import com.gucci.lifecycle.ActivityFragmentLifecycle
import com.gucci.lifecycle.ManagerRetriever

/**
 * Created by 李志云 2019/4/16 02:14
 */
class GlideRequestManagerRetriver(val glideContext: GlideContext) {

    fun get(context: Context): RequestManager {
        val lifecycle = ManagerRetriever.get(context)
        var requestManager: RequestManager? = null
        lifecycle.getListeners().forEach {
            if (it is RequestManager) {
                requestManager = it
                return@forEach
            }
        }
        if (null == requestManager) {
            val r = RequestManager(glideContext)
            if (lifecycle is ActivityFragmentLifecycle) {
                lifecycle.addListener(r)
            }
            return r
        }
        return requestManager as RequestManager
    }

}