package com.glide.load

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by 李志云 2019/4/18 16:41
 */
class GlideExecutor {
    companion object {
        private var bestThreadCount: Int = 0

        fun calculateBestThreadCount(): Int {
            if (bestThreadCount == 0) {
                bestThreadCount = Math.min(4, Runtime.getRuntime().availableProcessors() - 1)
            }
            return bestThreadCount
        }


        private class DefaultThreadFactory : ThreadFactory {
            private var threadNum: Int = 0


            @Synchronized
            override fun newThread(runnable: Runnable): Thread {
                val result = Thread(runnable, "glide-thread-$threadNum")
                threadNum++
                return result
            }
        }

        fun newExecutor(): ThreadPoolExecutor {
            val threadCount = calculateBestThreadCount()
            return ThreadPoolExecutor(
                threadCount /* corePoolSize */,
                threadCount /* maximumPoolSize */,
                0 /* keepAliveTime */,
                TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(),
                DefaultThreadFactory()
            )
        }
    }
}