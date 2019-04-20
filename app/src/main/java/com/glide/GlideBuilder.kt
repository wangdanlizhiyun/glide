package com.glide

import android.app.ActivityManager
import android.content.Context
import com.glide.cache.*
import com.glide.cache.recycle.BitmapPool
import com.glide.load.Engine
import com.glide.load.GlideExecutor
import com.glide.request.RequestOptions
import java.util.concurrent.ThreadPoolExecutor

/**
 * Created by 李志云 2019/3/2 04:56
 */
class GlideBuilder(val context: Context) {
    var memoryCache: MemoryCache
    var diskCache: DiskCache
    var bitmapPool: BitmapPool
    var arrayPool: ArrayPool
    var executor: ThreadPoolExecutor

    var defaultRequestOptions: RequestOptions = RequestOptions()
    var engine: Engine

    init {
        val activityManager: ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val maxSize = getMaxSize(activityManager)
        arrayPool = LruArrayPool()
        //减去数组缓存后的可用内存大小
        val availableSize = maxSize - (arrayPool?.getMaxSize() ?: 0)
        val displayMetrics = context.resources.displayMetrics
        val widthPixels = displayMetrics.widthPixels
        val heightPixels = displayMetrics.heightPixels
        // 获得一个屏幕大小的argb所占的内存大小
        val screenSize = widthPixels * heightPixels * 4
        //bitmap复用占 4份
        var bitmapPoolSize = screenSize * 4.0f
        //内存缓存占 2份
        var memoryCacheSize = screenSize * 2.0f
        if (bitmapPoolSize + memoryCacheSize <= availableSize) {
            bitmapPoolSize = Math.round(bitmapPoolSize).toFloat()
            memoryCacheSize = Math.round(memoryCacheSize).toFloat()
        } else {
            //把总内存分成 6分
            val part = availableSize / 6.0f
            bitmapPoolSize = Math.round(part * 4).toFloat()
            memoryCacheSize = Math.round(part * 2).toFloat()
        }
        bitmapPool = LruBitmapPool(bitmapPoolSize.toInt())
        memoryCache = LruMemoryCache(memoryCacheSize.toInt())
        diskCache = DiskLruCacheWrapper(context)
        executor = GlideExecutor.newExecutor()
        engine = Engine(memoryCache, diskCache, bitmapPool, executor)
        memoryCache.setResourceRemoveListener(engine)
    }


    fun build(): Glide {
        return Glide(context,this)
    }

    private fun getMaxSize(activityManager: ActivityManager): Int {
        //使用最大可用内存的0.4作为缓存使用  64M
        val memoryClassBytes = activityManager.memoryClass * 1024 * 1024
        return Math.round(memoryClassBytes * 0.4f)
    }
}