package com.glide.load.codec

import android.graphics.Bitmap
import java.io.IOException

/**
 * Created by 李志云 2019/4/18 12:47
 */
interface ResourceDecoder<T>{
    @Throws(IOException::class)
    abstract fun handles(source: T): Boolean

    @Throws(IOException::class)
    abstract fun decode(source: T, width: Int, height: Int): Bitmap
}