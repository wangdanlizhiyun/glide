package com.glide.load.generator

import com.glide.cache.Key

/**
 * Created by 李志云 2019/4/18 14:28
 */
interface DataGenerator {
    interface DataGeneratorCallback {

        enum class DataSource {
            REMOTE,
            CACHE
        }

        fun onDataReady(sourceKey: Key?, data: Any, dataSource: DataSource)

        fun onDataFetcherFailed(sourceKey: Key?, e: Exception)
    }

    fun startNext(): Boolean

    fun cancel()

}