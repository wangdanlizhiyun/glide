package com.glide.load.model.data

import android.content.ContentResolver
import android.net.Uri
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * Created by 李志云 2019/4/18 13:02
 */
class FileUriFetcher(val uri: Uri, val cr:ContentResolver):DataFetcher<InputStream> {
    override fun loadData(callback: DataFetcher.DataFetcherCallback<in InputStream>) {
        var inputStream: InputStream? = null
        try {
            inputStream = cr.openInputStream(uri)
            callback.onFetcherReady(inputStream)
        } catch (e: FileNotFoundException) {
            callback.onLoadFaled(e)
        } finally {
            inputStream?.let {
                try {
                    it.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun cancel() {
    }

    override fun getDataClass(): Class<*> {
        return InputStream::class.java
    }
}