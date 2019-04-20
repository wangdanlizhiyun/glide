package com.glide

import android.graphics.Bitmap
import android.os.Build
import java.util.ArrayList
import kotlin.experimental.and

/**
 * Created by 李志云 2019/4/17 02:57
 */
class Utils {
    companion object {
        fun getPixelsCout(config: Bitmap.Config): Int {
            if (config == Bitmap.Config.ARGB_8888) {
                return 4
            } else if (config == Bitmap.Config.RGB_565) {
                return 2
            } else if (config == Bitmap.Config.ARGB_4444) {
                return 2
            } else if (config == Bitmap.Config.ALPHA_8) {
                return 1
            }
            return 1
        }

        fun getByteCount(bitmap: Bitmap?): Int {
            if (null == bitmap) return 0
            return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                bitmap.allocationByteCount
            } else bitmap.byteCount
        }

    }
}