package com.glide.cache

import android.content.Context
import com.glide.Utils
import com.glide.cache.disklrucache.DiskLruCache
import java.io.File
import java.security.MessageDigest

/**
 * Created by 李志云 2019/4/17 05:26
 */
class DiskLruCacheWrapper(val directory: File,val maxSize:Long ):DiskCache {
    override fun get(key: Key): File? {
        val k = getKey(key)
        var result:File? = null
        try {
            diskLruCache?.get(k)?.let { result = it.getFile(0) }
        }catch (e:java.lang.Exception){}
        return result
    }

    override fun put(key: Key, writer: DiskCache.Writer) {
        val k = getKey(key)
        try {
            diskLruCache?.get(k)?.let { return }
            val editor = diskLruCache?.edit(k)
            editor?.let {
                try {
                    val file = editor.getFile(0)
                    if (writer.write(file)){
                        editor.commit()
                    }
                }catch (e:java.lang.Exception){}finally {
                    editor.abortUnlessCommitted()
                }
            }
        }catch (e:java.lang.Exception){}
    }

    override fun delete(key: Key) {
        try {
            diskLruCache?.remove(getKey(key))
        }catch (e:java.lang.Exception){}
    }

    override fun clear() {
        try {
            diskLruCache?.delete()
        }catch (e:java.lang.Exception){}finally {
            diskLruCache = null
        }
    }

    companion object {
        internal val DEFAULT_DISK_CACHE_SIZE = 250 * 1024 * 1024
        internal val DEFAULT_DISK_CACHE_DIR = "image_manager_disk_cache"
    }
    lateinit var MD: MessageDigest
    var diskLruCache:DiskLruCache? = null
    init {
        try {
            MD = MessageDigest.getInstance("SHA-256")
            diskLruCache = DiskLruCache.open(directory,1,1,maxSize)
        }catch (e:Exception){}
    }

    constructor(context: Context): this(File(context.cacheDir,DEFAULT_DISK_CACHE_DIR),DEFAULT_DISK_CACHE_SIZE.toLong())

    fun getKey(key: Key):String{
        key.updateDiskCacheKey(MD)
        return CacheUtil.sha256BytesToHex(MD.digest())
    }
}