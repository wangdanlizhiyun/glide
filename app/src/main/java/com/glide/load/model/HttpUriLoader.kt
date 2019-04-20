package com.glide.load.model

import android.net.Uri
import com.glide.load.ObjectKey
import com.glide.load.model.data.HttpUriFetcher
import java.io.InputStream

/**
 * Created by 李志云 2019/4/18 13:59
 */
class HttpUriLoader:ModelLoader<Uri,InputStream> {
    override fun handles(uri: Uri): Boolean {
        val scheme = uri.getScheme()
        return scheme!!.equals("http", ignoreCase = true) || scheme!!.equals("https", ignoreCase = true)
    }

    override fun buildData(uri: Uri): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData<InputStream>(ObjectKey(uri), HttpUriFetcher(uri))
    }

    class Factory : ModelLoader.ModelLoaderFactory<Uri, InputStream> {

        override fun build(registry: ModelLoaderRegistry): ModelLoader<Uri, InputStream> {
            return HttpUriLoader()
        }
    }
}