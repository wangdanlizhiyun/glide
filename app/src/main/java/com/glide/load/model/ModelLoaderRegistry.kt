package com.glide.load.model

import java.util.ArrayList

/**
 * Created by 李志云 2019/4/18 13:17
 */
class ModelLoaderRegistry {
    private val entries = ArrayList<Entry<*, *>>()

    /**
     * 注册 Loader
     *
     * @param modelClass 数据来源类型 String File
     * @param dataClass  数据转换后类型 加载后类型 String/File->InputStream
     * @param factory    创建ModelLoader的工厂
     * @param <Model>
     * @param <Data>
    </Data></Model> */
    @Synchronized
    fun <Model, Data> add(
        modelClass: Class<Model>, dataClass: Class<Data>,
        factory: ModelLoader.ModelLoaderFactory<Model, Data>
    ) {
        entries.add(Entry(modelClass, dataClass, factory))
    }

    /**
     * 获得 对应 model与data类型的 modelloader
     *
     * @param modelClass
     * @param dataClass
     * @param <Model>
     * @param <Data>
     * @return
    </Data></Model> */
    fun <Model, Data> build(modelClass: Class<Model>, dataClass: Class<Data>): ModelLoader<Model, Data> {
        val loaders = ArrayList<ModelLoader<Model, Data>>()
        for (entry in entries) {
            //是我们需要的Model与Data类型的Loader
            if (entry.handles(modelClass, dataClass)) {
                loaders.add(entry.factory.build(this) as ModelLoader<Model, Data>)
            }
        }
        //找到多个匹配的loader
        if (loaders.size > 1) {
            return MultiModelLoader(loaders)
        } else if (loaders.size == 1) {
            return loaders[0]
        }
        throw RuntimeException("No Match:" + modelClass.name + " Data:" + dataClass.name)
    }


    /**
     * 查找匹配的 Model类型的ModelLoader
     * @param modelClass
     * @param <Model>
     * @return
    </Model> */
    fun <Model> getModelLoaders(modelClass: Class<Model>): List<ModelLoader<Model, *>> {
        val loaders = ArrayList<ModelLoader<Model, *>>()
        for (entry in entries) {
            if (entry.handles(modelClass)) {
                loaders.add(entry.factory.build(this) as ModelLoader<Model, *>)
            }
        }
        return loaders
    }


    private class Entry<Model, Data>(
        internal var modelClass: Class<Model>,
        internal var dataClass: Class<Data>,
        internal var factory: ModelLoader.ModelLoaderFactory<Model, Data>
    ) {

        internal fun handles(modelClass: Class<*>, dataClass: Class<*>): Boolean {
            // A.isAssignableFrom(B) B和A是同一个类型 或者 B是A的子类
            return this.modelClass.isAssignableFrom(modelClass) && this.dataClass.isAssignableFrom(dataClass)
        }

        internal fun handles(modelClass: Class<*>): Boolean {
            // A.isAssignableFrom(B) B和A是同一个类型 或者 B是A的子类
            return this.modelClass.isAssignableFrom(modelClass)
        }

    }
}