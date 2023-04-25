package com.android.wy.news.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.android.wy.news.notification.NotificationHelper
import com.android.wy.news.notification.NotificationUtil

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/12 17:11
  * @Version:        1.0
  * @Description:    
 */
class NewsService : Service() {
    private val notifyID = 100

    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //val notification = NotificationHelper.testNotification(this)
        val notification = NotificationHelper.testPlayNotification(this)
        Handler(Looper.getMainLooper()).postDelayed({
            startForeground(notifyID, notification)
        }, 1000)
        NotificationUtil.openNotification(this)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }
}