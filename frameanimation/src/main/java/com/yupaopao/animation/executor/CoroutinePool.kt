package com.yupaopao.animation.executor

import android.graphics.Rect
import kotlinx.coroutines.*
import java.util.concurrent.Callable
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicInteger

object CoroutinePool {

    const val POOL_NUMBER = 4
    val executors = arrayListOf<ScheduledThreadPoolExecutor>()
    val coroutineDispatchers = arrayListOf<ExecutorCoroutineDispatcher>()

    private val counter = AtomicInteger(0)
    fun incrementAndGet(): Int {
        var current = 0
        var next = 0
        do {
            current = counter.get();
            if (current >= Int.MAX_VALUE) {
                next = 0
            } else {
                next = current + 1
            }
        } while (!counter.compareAndSet(current, next))

        return next
    }

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

    suspend fun run(runnable: Runnable, id: Int) {
        withContext(coroutineDispatchers[id.rem(POOL_NUMBER)]) {
            runnable.run()
        }
    }

    suspend fun runDelay(runnable: Runnable, delay: Long, id: Int) {
        withContext(coroutineDispatchers[id.rem(POOL_NUMBER)]) {
            delay(delay)
            runnable.run()
        }
    }

    suspend fun run(callable: Callable<Rect>, id: Int): Rect {
        return runBlocking(coroutineDispatchers[id.rem(POOL_NUMBER)]) {
            callable.call()
        }
    }
}