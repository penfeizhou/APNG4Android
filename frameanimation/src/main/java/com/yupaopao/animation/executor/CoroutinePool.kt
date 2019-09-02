package com.yupaopao.animation.executor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Callable

object CoroutinePool {
    fun run(runnable: Runnable) {
        GlobalScope.launch(Dispatchers.IO) {
            runnable.run()
        }
    }

    fun runDelay(runnable: Runnable, delay: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            delay(delay)
            runnable.run()
        }
    }

    fun run(callable: Callable<Any>) {
        GlobalScope.launch(Dispatchers.IO) {
            callable.call()
        }
    }
}