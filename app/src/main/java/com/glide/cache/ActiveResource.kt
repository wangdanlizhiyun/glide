package com.glide.cache

import com.glide.cache.recycle.Resource
import java.lang.RuntimeException
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * Created by 李志云 2019/3/2 07:39
 */
class ActiveResource(private var resourceListener: Resource.ResourceListener? = null) {
    val activeResources = HashMap<Key,ResourceWeakReference>()
    private var queue:ReferenceQueue<Resource> = ReferenceQueue()
    private var cleanReferenceQueueThread:Thread? = null
    private var isShutdown = false

    fun activate(key: Key,resource: Resource){
        resource.setResourceListener(key,resourceListener)
        activeResources.put(key, ResourceWeakReference(key,resource,getReferenceQueue()))
    }
    fun deactivate(key: Key):Resource?{
        val reference = activeResources.remove(key)
        return reference?.get()
    }
    fun get(key: Key):Resource?{
        return activeResources.get(key)?.get()
    }

    private fun getReferenceQueue(): ReferenceQueue<in Resource> {
        if (null == cleanReferenceQueueThread){
            cleanReferenceQueueThread = Thread(Runnable {
                while (!isShutdown){
                    try {
                        val ref:ResourceWeakReference = queue.remove() as ResourceWeakReference
                        activeResources.remove(ref.key)
                    }catch (e:Exception){
                    }
                }
            })
            cleanReferenceQueueThread?.start()
        }
        return queue
    }
    fun shutdown(){
        isShutdown = true
        cleanReferenceQueueThread?.let {
            it.interrupt()
            try {
                it.join(TimeUnit.SECONDS.toMillis(5))
                if (it.isAlive){
                    throw RuntimeException("Faild to join to time")
                }
            }catch (e:java.lang.Exception){}
        }
    }

    companion object {
        class ResourceWeakReference(var key:Key, resource: Resource, queue: ReferenceQueue<in Resource>
        ): WeakReference<Resource>(resource, queue) {

        }
    }

}