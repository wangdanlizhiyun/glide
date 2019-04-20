package com.glide.load.model

import com.glide.cache.Key
import com.glide.load.model.data.DataFetcher

/**
 * Created by 李志云 2019/4/18 13:16
 */
interface ModelLoader<Model,Data> {
    interface ModelLoaderFactory<Model, Data> {
        fun build(registry: ModelLoaderRegistry): ModelLoader<Model, Data>
    }

    open class LoadData<Data>(//缓存的key
        public val key: Key, //加载数据
        open val fetcher: DataFetcher<Data>
    )

    /**
     * 此Loader是否能够处理对应Model的数据
     *
     * @param model
     * @return
     */
    fun handles(model: Model): Boolean

    /**
     * 创建加载数据
     *
     * @param model
     * @return
     */
    fun buildData(model: Model): LoadData<Data>?
}