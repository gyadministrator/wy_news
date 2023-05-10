package com.android.wy.news.util

import android.os.Handler
import android.os.Looper
import com.android.wy.news.manager.ThreadExecutorManager
import java.util.LinkedList

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/10 19:43
  * @Version:        1.0
  * @Description:    
 */
object TaskUtil {
    private val runnableList = LinkedList<Runnable>()
    private val handler = Handler(Looper.getMainLooper())

    @Synchronized
    fun postOnUiThread(runnable: Runnable) {
        if (!runnableList.contains(runnable)) {
            handler.post(runnable)
        }
    }

    @Synchronized
    fun postOnThread(runnable: Runnable) {
        if (!runnableList.contains(runnable)) {
            ThreadExecutorManager.mInstance.startExecute(runnable)
        }
    }

    fun removeAllRunnable() {
        synchronized(runnableList) {
            if (runnableList.size > 0) {
                for (i in 0 until runnableList.size) {
                    val runnable = runnableList[i]
                    handler.removeCallbacks(runnable)
                    runnableList.remove(runnable)
                }
            }
        }
    }
}