package com.android.wy.news.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/5/10 19:43
  * @Version:        1.0
  * @Description:    
 */
object TaskUtil {
    private val aliveTasks: MutableList<Task> = ArrayList()

    private var fixedExecutor: ThreadPoolExecutor? = null

    private val uiThreadHandler = Handler(Looper.getMainLooper())

    fun runOnUiThread(runnable: Runnable) {
        val task = Task(uiThreadHandler, runnable, 0, 0)
        synchronized(aliveTasks) { aliveTasks.add(task) }
        task.start()
    }

    fun runOnUiThread(runnable: Runnable, delay: Long) {
        val task = Task(uiThreadHandler, runnable, delay, 0)
        synchronized(aliveTasks) { aliveTasks.add(task) }
        task.start()
    }

    fun runOnUiThread(runnable: Runnable, delay: Long, period: Long) {
        val task = Task(uiThreadHandler, runnable, delay, period)
        synchronized(aliveTasks) { aliveTasks.add(task) }
        task.start()
    }

    fun removeUiThreadCallback(runnable: Runnable) {
        synchronized(aliveTasks) {
            for (i in aliveTasks.indices) {
                val task = aliveTasks[i]
                if (task.originRunnable === runnable) {
                    task.period = 0
                    task.runnable?.let { uiThreadHandler.removeCallbacks(it) }
                    aliveTasks.remove(task)
                    return
                }
            }
        }
    }

    private fun removeTask(task: Task) {
        synchronized(aliveTasks) { aliveTasks.remove(task) }
    }

    /**
     * run in background thread
     *
     *
     * 异常保护：
     * 由于线程池退出时做了shutDown的操作，但是某些销毁逻辑在线程中又开线程，
     * 因此当线程池销毁后再调用，就起新线程来运行
     *
     * @param runnable
     */
    fun runOnThread(runnable: Runnable) {
        if (fixedExecutor == null || fixedExecutor!!.isShutdown) {
            fixedExecutor = ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                1000,
                TimeUnit.MILLISECONDS,
                LinkedBlockingDeque()
            )
        }
        if (fixedExecutor!!.activeCount >= fixedExecutor!!.corePoolSize * 2) {
            Thread(runnable).start()
        } else {
            fixedExecutor?.execute(runnable)
        }
    }

    private class Task(
        handler: Handler,
        runnable: Runnable,
        var delay: Long,
        var period: Long
    ) {
        var handler: Handler? = handler
        var runnable: Runnable? = null
        var originRunnable: Runnable? = runnable

        init {
            this.runnable = object : Runnable {
                override fun run() {
                    runnable.run()
                    if (period > 0) {
                        handler.postDelayed(this, period)
                    } else {
                        removeTask(this@Task)
                    }
                }
            }
        }

        fun start() {
            runnable?.let { handler?.postDelayed(it, delay) }
        }
    }
}