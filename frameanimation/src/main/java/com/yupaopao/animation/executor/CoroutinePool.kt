package com.yupaopao.animation.executor

import android.graphics.Rect
import kotlinx.coroutines.*
import java.util.concurrent.Callable
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor

object CoroutinePool {

    const val POOL_NUMBER = 4
    val executors = arrayListOf<ScheduledThreadPoolExecutor>()
    val coroutineDispatchers = arrayListOf<ExecutorCoroutineDispatcher>()

    init {
        for (i in 0 until POOL_NUMBER) {
            executors.add(
                    ScheduledThreadPoolExecutor(
                            1,
                            DefaultThreadFactory(this.javaClass.simpleName),
                            ThreadPoolExecutor.DiscardPolicy()
                    )
            )

        }
        for (i in 0 until POOL_NUMBER) {
            coroutineDispatchers.add(
                    executors[i].asCoroutineDispatcher()
            )
        }
    }

    suspend fun run(runnable: Runnable, id: Long) {
        withContext(coroutineDispatchers[id.rem(POOL_NUMBER).toInt()]) {
            runnable.run()
        }
    }

    suspend fun runDelay(runnable: Runnable, delay: Long, id: Long) {
        withContext(coroutineDispatchers[id.rem(POOL_NUMBER).toInt()]) {
            delay(delay)
            runnable.run()
        }
    }

    suspend fun run(callable: Callable<Rect>, id: Long): Rect {
        return runBlocking(coroutineDispatchers[id.rem(POOL_NUMBER).toInt()]) {
            callable.call()
        }
    }
}