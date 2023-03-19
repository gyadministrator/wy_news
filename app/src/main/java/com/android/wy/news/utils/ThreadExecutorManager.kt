package com.android.wy.news.utils

import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/2/6 9:19
  * @Version:        1.0
  * @Description:    
 */
class ThreadExecutorManager {
    //corePoolSize ：线程池中核心线程数的最大值
    //maximumPoolSize ：线程池中能拥有最多线程数
    //workQueue：用于缓存任务的阻塞队列
    //keepAliveTime ：表示空闲线程的存活时间
    //TimeUnit unit ：表示keepAliveTime的单位
    //workQueue ：它决定了缓存任务的排队策略

    /*当调用线程池execute() 方法添加一个任务时，线程池会做如下判断：
    如果有空闲线程，则直接执行该任务；
    如果没有空闲线程，且当前运行的线程数少于corePoolSize，则创建新的线程执行该任务；
    如果没有空闲线程，且当前的线程数等于corePoolSize，同时阻塞队列未满，则将任务入队列，而不添加新的线程；
    如果没有空闲线程，且阻塞队列已满，同时池中的线程数小于maximumPoolSize ，则创建新的线程执行任务；
    如果没有空闲线程，且阻塞队列已满，同时池中的线程数等于maximumPoolSize ，则根据构造函数中的 handler 指定的策略来拒绝新的任务。*/
    private var executor: ExecutorService =
        ThreadPoolExecutor(6, 10, 600, TimeUnit.SECONDS, LinkedBlockingQueue())

    companion object {
        val mInstance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ThreadExecutorManager()
        }
    }

    fun startExecute(runnable: Runnable) {
        executor.execute(runnable)
    }

    fun stopExecute() {
        if (executor.isShutdown) {
            executor.shutdown()
        }
    }
}