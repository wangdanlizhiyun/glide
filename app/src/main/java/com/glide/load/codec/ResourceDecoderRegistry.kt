package com.glide.load.codec

/**
 * Created by 李志云 2019/4/18 12:51
 */
class ResourceDecoderRegistry {
    private val entries = ArrayList<Entry<*>>()
    fun <Data> getDecoders(dataClass: Class<Data>): List<ResourceDecoder<Data>> {
        val docoders = java.util.ArrayList<ResourceDecoder<Data>>()
        for (entry in entries) {
            if (entry.handles(dataClass)) {
                docoders.add(entry.decoder as ResourceDecoder<Data>)
            }
        }
        return docoders
    }

    fun <T> add(dataClass: Class<T>, decoder: ResourceDecoder<T>) {
        entries.add(Entry(dataClass, decoder))
    }

    private class Entry<T>(private val dataClass: Class<T>, internal val decoder: ResourceDecoder<T>) {

        fun handles(dataClass: Class<*>): Boolean {
            //判定此 Class 对象所表示的类或接口与指定的 Class 参数所表示的类或接口是否相同，或是否是其超类或超接口
            return this.dataClass.isAssignableFrom(dataClass)
        }
    }

}