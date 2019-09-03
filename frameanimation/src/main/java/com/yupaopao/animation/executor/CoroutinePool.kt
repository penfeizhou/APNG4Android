package com.yupaopao.animation.executor

import android.graphics.Rect
import kotlinx.coroutines.*
import java.util.concurrent.Callable
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor

object CoroutinePool {
    val executor = ScheduledThreadPoolExecutor(
            1,
            DefaultThreadFactory(this.javaClass.simpleName),
            ThreadPoolExecutor.DiscardPolicy()
    )
    val coroutineDispatcher = executor.asCoroutineDispatcher()

    suspend fun run(runnable: Runnable) {
        withContext(coroutineDispatcher) {
            runnable.run()
        }
    }

    suspend fun runDelay(runnable: Runnable, delay: Long) {
        withContext(coroutineDispatcher) {
            delay(delay)
            runnable.run()
        }
    }

    suspend fun run(callable: Callable<Rect>): Rect {
        return runBlocking(coroutineDispatcher) {
            callable.call()
        }
    }
}