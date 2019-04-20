package com.glide.load.model

import android.content.ContentResolver
import android.net.Uri
import com.glide.load.ObjectKey
import com.glide.load.model.data.FileUriFetcher
import java.io.InputStream

/**
 * Created by 李志云 2019/4/18 13:26
 */
class FileUriLoader(val contentResolver: ContentResolver):ModelLoader<Uri,InputStream> {
    override fun buildData(uri: Uri): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(ObjectKey(uri), FileUriFetcher(uri, contentResolver))
    }

    override fun handles(uri: Uri): Boolean {
        return ContentResolver.SCHEME_FILE.equals(uri.getScheme(), ignoreCase = true)
    }

    class Factory(private val contentResolver: ContentResolver) : ModelLoader.ModelLoaderFactory<Uri, InputStream> {

        override fun build(registry: ModelLoaderRegistry): ModelLoader<Uri, InputStream> {
            return FileUriLoader(contentResolver)
        }
    }
}