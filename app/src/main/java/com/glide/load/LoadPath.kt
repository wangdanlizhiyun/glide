package com.glide.load

import android.graphics.Bitmap
import com.glide.load.codec.ResourceDecoder
import java.io.IOException

/**
 * Created by 李志云 2019/4/18 14:03
 */
class LoadPath<Data>(val dataClass:Class<Data>,val decoders:List<ResourceDecoder<Data>>) {
    fun runLoad(data: Data, width: Int, height: Int): Bitmap? {
        var result: Bitmap? = null
        for (decoder in decoders) {
            try {
                //判断是否支持解码
                if (decoder.handles(data)) {
                    result = decoder.decode(data, width, height)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (result != null) {
                break
            }
        }
        return result
    }
}