package com.glide.load.model.data

import android.net.Uri
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by 李志云 2019/4/18 13:05
 */
class HttpUriFetcher(val uri: Uri):DataFetcher<InputStream> {
    var isCanceled = false
    override fun loadData(callback: DataFetcher.DataFetcherCallback<in InputStream>) {
        var conn: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            val url = URL(uri.toString())
            conn = url.openConnection() as HttpURLConnection
            conn.connect()
            inputStream = conn.inputStream
            val responseCode = conn.responseCode
            if (isCanceled) {
                return
            }
            if (responseCode == HttpURLConnection.HTTP_OK) {
                callback.onFetcherReady(inputStream)
            } else {
                callback.onLoadFaled(RuntimeException(conn.responseMessage))
            }
        } catch (e: Exception) {
            callback.onLoadFaled(e)
        } finally {
            inputStream?.let {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            conn?.disconnect()
        }
    }

    override fun cancel() {
        isCanceled = true
    }

    override fun getDataClass(): Class<*> {return InputStream::class.java }
}