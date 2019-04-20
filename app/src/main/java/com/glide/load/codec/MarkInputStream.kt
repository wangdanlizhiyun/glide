package com.glide.load.codec

import com.glide.cache.ArrayPool
import java.io.InputStream

/**
 * Created by 李志云 2019/4/17 04:52
 */
class MarkInputStream(val inputStream:InputStream,val arrayPool:ArrayPool): InputStream() {
    var buf:ByteArray? = arrayPool.get(64 * 1024)

    var markPos = -1
    var pos = 0
    var readCount = 0


    override fun read(): Int {
        buf?.let {
            if (pos < readCount){
                return it[pos++].toInt()
            }
            val b = inputStream.read()
            if (b  == -1){
                return b
            }
            if (pos >= it.size){
                resizeBuf(0)
            }
            it[pos++] = b.toByte()
            readCount++
            return b
        }
        return -1
    }

    override fun read(b: ByteArray): Int {
        return read(b,0,b.size)
    }

    override fun read(b: ByteArray,off: Int, len: Int): Int {
        buf?.let {

            var of = off
            var count = len - of
            //buf中的有效数据
            val availables = readCount - pos
            //满足读取的需求
            if (availables >= count){
                System.arraycopy(it,pos,b,of,count)
                pos += count
                return count
            }

            //先将buf中的数据读进b
            if (availables > 0){
                System.arraycopy(it,pos,b,of,availables)
                of += availables
                pos += availables
            }
            //还需要读取的数据长度 从原inputstream读取
            count = len - off
            val readLen = inputStream.read(b,off,count)
            if (readLen == -1){
                return readLen
            }

            //没有足够的空间存放本次数据
            val i = pos + readLen - it.size
            if (i > 0){
                resizeBuf(i)
            }
            System.arraycopy(b,off,it,pos,readLen)
            pos += readLen
            //记录已经读取的总长度
            readCount += readLen
            return readLen
        }
        return -1
    }

    override fun reset() {
        pos = markPos
    }

    override fun markSupported(): Boolean {
        return true
    }

    override fun mark(readlimit: Int) {
        markPos = pos
    }

    override fun close() {
        release()
        inputStream.close()
    }


    fun release(){
        buf?.let {
            arrayPool.put(it)
            buf = null
        }
    }

    private fun resizeBuf(len: Int) {
        buf?.let {
            val newLen = it.size * 2 + len
            val newbuf = arrayPool.get(newLen)
            //拷贝数据
            System.arraycopy(it, 0, newbuf, 0, it.size)
            //加入数组池
            arrayPool.put(it)
            buf = newbuf
        }
    }
}