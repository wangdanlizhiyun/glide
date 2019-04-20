package com.glide.load.generator

import android.content.Context
import android.util.Log
import com.glide.GlideContext
import com.glide.load.model.ModelLoader
import com.glide.load.model.data.DataFetcher

/**
 * Created by 李志云 2019/4/18 14:29
 */
class SourceGenerator(val context: GlideContext, val model: Any, val cb: DataGenerator.DataGeneratorCallback) :
    DataGenerator, DataFetcher.DataFetcherCallback<Any> {
    var loadDatas: List<ModelLoader.LoadData<*>>
    var loadData: ModelLoader.LoadData<*>? = null

    init {
        loadDatas = context.registry.getLoadDatas(model)
    }

    var loadDataListIndex = 0

    override fun onFetcherReady(data: Any) {
        Log.e("test", "加载器加载数据成功回调")
        loadData?.let {
            cb.onDataReady(it.key, data, DataGenerator.DataGeneratorCallback.DataSource.REMOTE)
        }
    }

    override fun onLoadFaled(e: Exception) {
        loadData?.let { cb.onDataFetcherFailed(it.key, e) }
        Log.e("test", "加载器加载数据失败回调")
    }

    override fun startNext(): Boolean {
        Log.e("test", "源加载器开始加载")
        var started = false
        while (!started && hasNextModelLoader()) {
            loadData = loadDatas[loadDataListIndex++]
            Log.e("test", "获得加载设置数据")
            // hasLoadPath : 是否有个完整的加载路径 从将Model转换为Data之后 有没有一个对应的将Data
            // 转换为图片的解码器
            loadData?.let {

                if (context.registry.hasLoadPath(it.fetcher.getDataClass())) {
                    Log.e("test", "加载设置数据输出数据对应能够查找有效的解码器路径,开始加载数据")
                    started = true
                    // 将Model转换为Data
//                    it.fetcher.loadData(this)
                }
            }
        }
        return started
    }

    override fun cancel() {
        loadData?.fetcher?.cancel()
    }

    /**
     * 是否有下一个modelloader支持加载
     * @return
     */
    private fun hasNextModelLoader(): Boolean {
        return loadDataListIndex < loadDatas.size
    }
}