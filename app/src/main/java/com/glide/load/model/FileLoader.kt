package com.glide.load.model

import android.net.Uri
import java.io.File
import java.io.InputStream

/**
 * Created by 李志云 2019/4/18 13:53
 */
class FileLoader<Data>(val loader:ModelLoader<Uri, Data>):ModelLoader<File,Data> {

    override fun handles(model: File): Boolean {
        return true
    }

    override fun buildData(file: File): ModelLoader.LoadData<Data>? {
        return loader.buildData(Uri.fromFile(file))
    }

    class Factory : ModelLoader.ModelLoaderFactory<File, InputStream> {

        override fun build(modelLoaderRegistry: ModelLoaderRegistry): ModelLoader<File, InputStream> {
            return FileLoader(modelLoaderRegistry.build(Uri::class.java, InputStream::class.java))
        }

    }
}