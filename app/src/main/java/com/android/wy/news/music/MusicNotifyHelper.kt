package com.android.wy.news.music

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.android.wy.news.R
import com.android.wy.news.activity.HomeActivity
import java.lang.ref.WeakReference


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/26 10:30
  * @Version:        1.0
  * @Description:    
 */
class MusicNotifyHelper(context: Context) {
    private var mContext: Context = context

    companion object {
        private var instance: WeakReference<MusicNotifyHelper>? = null
        fun getInstance(context: Context): MusicNotifyHelper? {
            if (instance == null) {
                instance = WeakReference(MusicNotifyHelper(context))
            }
            return instance?.get()
        }
    }

    fun createChannel(channelId: String?, channelName: CharSequence?, description: String?) {
        //8.0以上版本通知适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = description
            val notificationManager = mContext.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    /**
     * 返回一个前台通知
     * @param channelId  通知渠道id，注意8.0创建通知的时候渠道id与此要匹配
     * @param remoteViews 自定义通知样式的对象，但是与View不同，不提供findViewById方法，详细建议看看源码和官方文档
     * @return Notification
     */
    fun createForeNotification(
        channelId: String,
        remoteViews: RemoteViews?
    ): NotificationCompat.Builder {
        val intent = Intent(mContext, HomeActivity::class.java)
        val mainIntent = PendingIntent.getActivity(
            mContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(mContext, channelId)
            //设置不可手动清除，除非app死掉或者在代码中取消
            .setOngoing(true)
            .setSmallIcon(R.mipmap.notice)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            //setCustomBigContentView() 为展开后通知设置不同的布局
            //.setCustomBigContentView(remoteViews)
            //setCustomContentView() 以设置收起后通知的布局
            .setCustomContentView(remoteViews)
            .setContentIntent(mainIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

}