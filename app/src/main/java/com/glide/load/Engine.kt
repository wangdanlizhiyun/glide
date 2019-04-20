package com.glide.load

import android.util.Log
import com.glide.GlideContext
import com.glide.cache.ActiveResource
import com.glide.cache.DiskCache
import com.glide.cache.Key
import com.glide.cache.MemoryCache
import com.glide.cache.recycle.BitmapPool
import com.glide.cache.recycle.Resource
import com.glide.request.ResourceCallback
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by 李志云 2019/4/16 02:21
 */
class Engine(val memoryCache:MemoryCache,val diskCache:DiskCache,val bitmapPool:BitmapPool,val threadPool:ThreadPoolExecutor)
    :MemoryCache.ResouceRemoveListener
    ,Resource.ResourceListener
    ,EngineJob.EngineJobListener {



    var activeResources:ActiveResource
    val jobs:MutableMap<Key,EngineJob> = HashMap()

    private val TAG = "Engine"

    init {
        activeResources = ActiveResource(this)
    }


    override fun onEngineJobComplete(engineJob: EngineJob, key: Key, resource: Resource?) {
        if (resource != null) {
            //设置引用计数为0(没有在使用了)的回调
            resource.setResourceListener(key, this)
            //加入活动缓存
            activeResources.activate(key, resource)
        }

        jobs.remove(key)
    }
    override fun onResouceRemove(resource: Resource) {
        Log.e(TAG, "内存缓存移除，加入复用池")
        bitmapPool.put(resource.bitmap)
    }
    override fun onEngineJobCancelled(engineJob: EngineJob?, key: Key?) {
        jobs.remove(key)
    }

    override fun onResourceReleased(key: Key, resource: Resource) {
        Log.e(TAG, "引用计数为0,移除活跃缓存，加入内存缓存:$key")
        activeResources.deactivate(key)
        memoryCache.put2(key, resource)
    }


    fun shutdown(){
        val shutdownSeconds: Long = 5
        threadPool.shutdown()
        try {
            //5s 需要停掉线程池
            if (!threadPool.awaitTermination(shutdownSeconds, TimeUnit.SECONDS)) {
                threadPool.shutdownNow()
                if (!threadPool.awaitTermination(shutdownSeconds, TimeUnit.SECONDS)) {
                    throw RuntimeException("Failed to shutdown")
                }
            }
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }

        diskCache.clear()
        activeResources.shutdown()
    }
    fun load(glideContext: GlideContext,model:Any,width:Int,height:Int,cb:ResourceCallback):LoadStatus?{
        val engineKey = EngineKey(model, width, height)
        //1、先从活动资源当中查找对应的图片
        var resource = activeResources.get(engineKey)
        if (null != resource) {
            Log.e(TAG, "使用活跃缓存数据:" + resource!!)
            //引用数+1
            resource!!.acquire()
            cb.onResourceReady(resource)
            return null
        }
        //2、从内存缓存当中找图片
        //从缓存移除 将它加入到活跃缓冲中
        resource = memoryCache.remove2(engineKey)
        if (null != resource) {
            Log.e(TAG, "使用内存缓存数据")
            // 加入正在使用集合 引用数+1
            activeResources.activate(engineKey, resource)
            resource!!.acquire()
            resource!!.setResourceListener(engineKey, this)
            cb.onResourceReady(resource)
            return null
        }

        //3、文件缓存 或者 图片的源地址加载  IO操作

        // 从集合从检查是否有同样图片的加载工作
        // 如果存在 本次加载只需要等待上一次加载工作完成
        // 重复的请求 获得上一次的工作 并添加监听器
        // 请求完成 回调所有监听器
        var engineJob: EngineJob? = jobs.get(engineKey)
        if (engineJob != null) {
            Log.e(TAG, "数据正在加载,添加数据加载状态监听")
            engineJob.addCallback(cb)
            return LoadStatus(cb, engineJob)
        }
        // 创建一个新的加载任务
        engineJob = EngineJob(threadPool, engineKey, this)
        engineJob.addCallback(cb)
        //加载任务
        val decodeJob = DecodeJob(
            glideContext, diskCache, model, width, height,
            engineJob
        )
        //启动加载任务
        engineJob.start(decodeJob)
        jobs.put(engineKey, engineJob)
        return LoadStatus(cb, engineJob)
    }
    class LoadStatus(val cb:ResourceCallback,val engineJob:EngineJob){
        fun cancel(){
            engineJob.removeCallback(cb)
        }
    }

}