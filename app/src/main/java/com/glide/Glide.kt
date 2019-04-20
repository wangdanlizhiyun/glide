package com.glide

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.support.v4.app.FragmentActivity
import com.glide.cache.ArrayPool
import com.glide.cache.MemoryCache
import com.glide.cache.recycle.BitmapPool
import com.glide.load.Engine
import com.glide.load.codec.StreamBitmapDecoder
import com.glide.load.model.FileLoader
import com.glide.load.model.FileUriLoader
import com.glide.load.model.HttpUriLoader
import com.glide.load.model.StringModelLoader
import com.glide.manager.GlideRequestManagerRetriver
import com.glide.manager.RequestManager
import java.io.File
import java.io.InputStream

/**
 * Created by 李志云 2019/3/2 04:55
 */
class Glide(context: Context, builder: GlideBuilder) : ComponentCallbacks2 {
    var requestManagerRetriever: GlideRequestManagerRetriver
    var glideContext: GlideContext
    var engine: Engine
    var memoryCache: MemoryCache
    var bitmapPool: BitmapPool
    var arrayPool: ArrayPool


    init {

        memoryCache = builder.memoryCache
        bitmapPool = builder.bitmapPool
        arrayPool = builder.arrayPool

        val registry = Registry()

        val contentResolver = context.contentResolver
        registry.add(String::class.java, InputStream::class.java, StringModelLoader.StreamFactory())
            .add(Uri::class.java, InputStream::class.java, HttpUriLoader.Factory())
            .add(Uri::class.java, InputStream::class.java, FileUriLoader.Factory(contentResolver))
            .add(File::class.java, InputStream::class.java, FileLoader.Factory())
            .register(InputStream::class.java, StreamBitmapDecoder(bitmapPool, arrayPool))
        engine = builder.engine
        glideContext = GlideContext(context, builder.defaultRequestOptions, engine, registry)
        requestManagerRetriever = GlideRequestManagerRetriver(glideContext)
    }

    companion object {
        var glide: Glide? = null
        private fun get(context: Context): Glide {
            if (null == glide) {
                synchronized(Glide::class.java) {
                    if (null == glide) {
                        glide = init(context, GlideBuilder(context))
                    }
                }
            }
            return glide!!
        }

        @Synchronized
        fun tearDown() {
            glide?.let {
                it.glideContext.getApplicationContext().unregisterComponentCallbacks(it)
                it.engine.shutdown()
            }
        }

        fun init(context: Context, builder: GlideBuilder): Glide {
            tearDown()
            val applicationContext = context.applicationContext
            val g: Glide = builder.build()
            applicationContext.registerComponentCallbacks(g)
            return g
        }


        fun with(activity: FragmentActivity): RequestManager {
            return get(activity).requestManagerRetriever.get(activity)
        }
    }

    override fun onLowMemory() {
        memoryCache.clearMemory()
        bitmapPool.clearMemory()
        arrayPool.clearMemory()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
    }

    override fun onTrimMemory(level: Int) {
        memoryCache.trimMemory(level)
        bitmapPool.trimMemory(level)
        arrayPool.trimMemory(level)
    }


}