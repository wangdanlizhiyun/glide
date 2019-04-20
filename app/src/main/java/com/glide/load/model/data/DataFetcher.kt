package com.glide.load.model.data

/**
 * Created by 李志云 2019/4/18 13:00
 */
interface DataFetcher<Data> {
    interface DataFetcherCallback<Data> {
        /**
         * 数据加载完成
         */
        fun onFetcherReady(data: Data)

        /**
         * 加载失败
         *
         * @param e
         */
        fun onLoadFaled(e: Exception)
    }

    fun loadData(callback: DataFetcherCallback<in Data>)

    fun cancel()

    fun getDataClass(): Class<*>
}