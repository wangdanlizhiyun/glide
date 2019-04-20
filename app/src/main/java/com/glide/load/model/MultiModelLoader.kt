package com.glide.load.model

/**
 * Created by 李志云 2019/4/18 13:18
 */
class MultiModelLoader<Model,Data>(val modelLoaders:List<ModelLoader<Model, Data>>):ModelLoader<Model,Data> {

    override fun handles(model: Model): Boolean {
        for (modelLoader in modelLoaders) {
            if (modelLoader.handles(model)) {
                return true
            }
        }
        return false
    }

    override fun buildData(model: Model): ModelLoader.LoadData<Data>? {
        for (i in modelLoaders.indices) {
            val modelLoader = modelLoaders[i]
            // Model=>Uri:http
            if (modelLoader.handles(model)) {
                return modelLoader.buildData(model)
            }
        }
        return null
    }
}