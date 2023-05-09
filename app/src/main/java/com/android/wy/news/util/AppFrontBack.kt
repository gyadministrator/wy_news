package com.android.wy.news.util

import android.app.Activity
import android.app.Application
import android.os.Bundle

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/9 13:34
  * @Version:        1.0
  * @Description:    
 */
object AppFrontBack {
    /**
     * 打开的Activity数量统计
     */
    private var activityStartCount = 0

    /**
     * 注册状态监听，仅在Application中使用
     * @param application application
     * @param listener listener
     */
    fun register(application: Application, listener: AppFrontBackListener) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
                activityStartCount++
                if (activityStartCount == 1) {
                    listener.onFront(activity)
                }
            }

            override fun onActivityStopped(activity: Activity) {
                activityStartCount--
                if (activityStartCount == 0) {
                    listener.onBack(activity)
                }
            }

        })
    }
}

/**
 * App状态监听
 */
interface AppFrontBackListener {
    /**
     * 前台
     */
    fun onFront(activity: Activity?)

    /**
     * 后台
     */
    fun onBack(activity: Activity?)
}