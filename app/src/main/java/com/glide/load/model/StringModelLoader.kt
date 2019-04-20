package com.glide.load.model

import android.net.Uri
import java.io.File
import java.io.InputStream

/**
 * Created by 李志云 2019/4/18 13:23
 */
class StringModelLoader(val loader:ModelLoader<Uri, InputStream>):ModelLoader<String,InputStream> {
    override fun handles(model: String): Boolean {
        return true
    }

    override fun buildData(model: String): ModelLoader.LoadData<InputStream>? {
        val uri: Uri
        if (model.startsWith("/")) {
            uri = Uri.fromFile(File(model))
        } else {
            uri = Uri.parse(model)
        }
        return loader.buildData(uri)
    }

    class StreamFactory : ModelLoader.ModelLoaderFactory<String, InputStream> {

        override fun build(registry: ModelLoaderRegistry): ModelLoader<String, InputStream> {
            return StringModelLoader(registry.build(Uri::class.java, InputStream::class.java))
        }
    }
}