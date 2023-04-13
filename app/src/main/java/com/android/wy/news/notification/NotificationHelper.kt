package com.android.wy.news.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.StrictMode
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.android.wy.news.R
import com.android.wy.news.activity.WebActivity
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.entity.House
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/12 13:23
  * @Version:        1.0
  * @Description:    
 */
class NotificationHelper {
    companion object {
        private var notifyId = 0
        private var notificationManager: NotificationManager? = null
        private const val CHANNEL_ID = "WyNewsNotification"
        private const val CHANNEL_NAME = "新闻快报通知栏"

        private fun sendNormalNotification(
            context: Context, pendingIntent: PendingIntent, title: String, content: String
        ): Notification {
            initNotificationManager(context)
            //notificationManager?.notify(getNotifyId(), notification)
            return NotificationCompat.Builder(context, CHANNEL_ID).setOngoing(true)
                //设置点击通知后自动清除通知
                .setAutoCancel(true)
                //设置通知的标题内容
                .setContentTitle(title)
                //设置通知的正文内容
                .setContentText(content)
                //设置通知被创建的时间
                .setWhen(System.currentTimeMillis())
                //设置通知的小图标
                //注意：只能使用纯alpha图层的图片进行设置，小图标会显示在系统状态栏上
                .setSmallIcon(R.mipmap.notify_icon)
                //设置通知的大图标
                //下拉系统状态栏时就能看见
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.notify_icon))
                //设置点击通知后的跳转意图
                .setContentIntent(pendingIntent)
                //设置自定义通知
                //.setContent()
                //设置通知的重要程度
                //默认的重要程度，和不设置效果是一样的
                //public static final int PRIORITY_DEFAULT = 0;
                //最低的重要程度，系统可能只会在特定的场合显示这条通知
                //public static final int PRIORITY_MIN = -2;
                //较低的重要程度，系统可能会将这类通知缩小，或改变其显示的顺序
                //public static final int PRIORITY_LOW = -1;
                //较高的重要程度，系统可能会将这类通知放大，或改变其显示的顺序
                //public static final int PRIORITY_HIGH = 1;
                //最高的重要程度，表示这类通知消息必须让用户看到，甚至做出响应
                //public static final int PRIORITY_MAX = 2;
                //注意：当设置最高重要程度后，其显示效果和QQ发送好友消息一样，如果正在其他APP内，消息会显示在屏幕上让用户看见
                .setPriority(NotificationCompat.PRIORITY_MAX)
                //设置通知的样式
                //比如设置长文字、大图片等等
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                //设置默认
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                //设置呼吸闪烁效果
                //.setLights()
                //设置通知音效
                //.setSound()
                //设置震动效果，数组包含手机静止时长和震动时长
                //下标0代表手机静止时长
                //下标1代表手机整的时长
                //下标2代表手机静止时长
                //下标3，4，5.......以此类推
                //还需要在AndroidManifest.xml中声明权限：
                //<uses-permission android:name="android.permission.VIBRATE"/>
                //.setVibrate()
                //设置通知栏颜色
                //.setColor()
                //设置通知类别
                //.setCategory()
                //设置弹窗显示
                .setFullScreenIntent(pendingIntent, true).build()
        }

        //初始化NotificationManager
        private fun initNotificationManager(context: Context) {
            if (notificationManager == null) {
                notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            //判断是否为8.0以上：Build.VERSION_CODES.O为26
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //创建通知渠道ID
                val channelId = CHANNEL_ID
                //创建通知渠道名称
                val channelName = CHANNEL_NAME
                //创建通知渠道重要性
                val importance = NotificationManager.IMPORTANCE_DEFAULT

                val channel = NotificationChannel(channelId, channelName, importance)
                //channel有很多set方法
                //获取ChannelId
                //channel.id
                //是否开启指示灯（是否在桌面icon右上角展示小红点）
                channel.enableLights(true)
                //设置指示灯颜色
                channel.lightColor = context.getColor(R.color.text_select_color)
                //是否开启震动
                channel.enableVibration(false)
                //设置震动频率
                //channel.vibrationPattern
                //设置频道重要性
                //channel.importance
                //设置声音
                //channel.setSound()
                //设置ChannelGroup
                //channel.group
                //设置绕过免打扰模式
                channel.setBypassDnd(false)
                //检测是否绕过免打扰模式
                //channel.canBypassDnd()
                //获取通知渠道名称
                //channel.name
                //设置是否应在锁定屏幕上显示此频道的通知
                channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                //设置是否显示角标
                channel.setShowBadge(true)
                //为NotificationManager设置通知渠道
                notificationManager?.createNotificationChannel(channel)
            }
        }


        private fun getNotifyId(): Int {
            return notifyId++
        }

        fun cancelNotification(context: Context, notifyId: Int) {
            initNotificationManager(context)
            notificationManager?.cancel(notifyId)
        }

        fun cancelAll(context: Context) {
            initNotificationManager(context)
            notificationManager?.cancelAll()
        }

        fun testNotification(context: Context): Notification {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(WebActivity.WEB_URL, "https://www.baidu.com/")
            val pendingIntent = PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            return sendNormalNotification(
                context, pendingIntent = pendingIntent, "你好", "测试"
            )
        }

        fun sendCustomNotification(context: Context, house: House) {
            val intent = Intent(context, WebActivity::class.java)
            val url = Constants.WEB_URL + house.docid + ".html"
            intent.putExtra(WebActivity.WEB_URL, url)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            sendNewsNotification(
                context, pendingIntent = pendingIntent, house
            )
        }

        private fun sendNewsNotification(
            context: Context, pendingIntent: PendingIntent, house: House?
        ) {
            initNotificationManager(context)
            val remoteViews = RemoteViews(context.packageName, R.layout.layout_remote_view)
            remoteViews.setTextViewText(R.id.tv_title, house?.title)
            remoteViews.setTextViewText(
                R.id.tv_time,
                house?.ptime?.let { CommonTools.getTimeDiff(it) })

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setOngoing(true)
                //普通视图，高度限制为64dp
                //.setContent(remoteViews)
                //普通视图，高度限制为64dp
                //.setCustomContentView(remoteViews)
                //扩展视图，高度可以扩展为256dp
                //.setCustomBigContentView(remoteViews)
                //悬浮通知视图
                //.setCustomHeadsUpContentView(remoteViews)
                //设置点击通知后自动清除通知
                .setAutoCancel(true)
                //设置通知被创建的时间
                .setWhen(System.currentTimeMillis())
                //设置通知的小图标
                //注意：只能使用纯alpha图层的图片进行设置，小图标会显示在系统状态栏上
                .setSmallIcon(R.mipmap.icon)
                //.setContentTitle(house?.ptime?.let { CommonTools.getTimeDiff(it) })
                .setContentTitle(house?.source)
                .setContentText(house?.title)
                //内容下面的一小段文字
                //.setSubText(house?.title)
                //收到信息后状态栏显示的文字信息
                //.setTicker("收到信息后状态栏显示的文字信息~")
                //设置通知的大图标
                //下拉系统状态栏时就能看见
                //.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.icon))
                //设置点击通知后的跳转意图
                .setContentIntent(pendingIntent)
                //设置自定义通知
                //.setContent()
                //设置通知的重要程度
                //默认的重要程度，和不设置效果是一样的
                //public static final int PRIORITY_DEFAULT = 0;
                //最低的重要程度，系统可能只会在特定的场合显示这条通知
                //public static final int PRIORITY_MIN = -2;
                //较低的重要程度，系统可能会将这类通知缩小，或改变其显示的顺序
                //public static final int PRIORITY_LOW = -1;
                //较高的重要程度，系统可能会将这类通知放大，或改变其显示的顺序
                //public static final int PRIORITY_HIGH = 1;
                //最高的重要程度，表示这类通知消息必须让用户看到，甚至做出响应
                //public static final int PRIORITY_MAX = 2;
                //注意：当设置最高重要程度后，其显示效果和QQ发送好友消息一样，如果正在其他APP内，消息会显示在屏幕上让用户看见
                .setPriority(NotificationCompat.PRIORITY_MAX)
                //设置通知的样式
                //比如设置长文字、大图片等等
                //.setStyle()
                //设置默认
                //.setVisibility()
                //设置呼吸闪烁效果
                //.setLights()
                //设置通知音效
                //.setSound()
                //设置震动效果，数组包含手机静止时长和震动时长
                //下标0代表手机静止时长
                //下标1代表手机整的时长
                //下标2代表手机静止时长
                //下标3，4，5.......以此类推
                //还需要在AndroidManifest.xml中声明权限：
                //<uses-permission android:name="android.permission.VIBRATE"/>
                //.setVibrate()
                //设置通知栏颜色
                .setColor(context.getColor(R.color.white))
            //设置通知类别
            //.setCategory()
            //设置弹窗显示
            //.setFullScreenIntent()
            //.build()

            val id = getNotifyId()

            if (house != null) {
                val picInfo = house.picInfo
                if (!picInfo.isNullOrEmpty()) {
                    val info = picInfo[0]
                    info.let {
                        Glide.with(context).asBitmap().load(info?.url)
                            //.apply(RequestOptions.bitmapTransform(RoundedCorners(4)))
                            .diskCacheStrategy(DiskCacheStrategy.ALL).override(
                                //关键代码，加载原始大小
                                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
                            )
                            //设置为这种格式去掉透明度通道，可以减少内存占有
                            .format(DecodeFormat.PREFER_RGB_565).into(object : SimpleTarget<Bitmap>(
                                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
                            ) {
                                override fun onResourceReady(
                                    resource: Bitmap, transition: Transition<in Bitmap?>?
                                ) {
                                    //remoteViews.setImageViewBitmap(R.id.iv_cover, resource)
                                    builder.setLargeIcon(resource)
                                    notificationManager?.notify(id, builder.build())
                                }
                            })
                    }
                } else {
                    //remoteViews.setImageViewResource(R.id.iv_cover, R.mipmap.icon)
                    builder.setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.mipmap.icon
                        )
                    )
                    notificationManager?.notify(id, builder.build())
                }
            }
        }

        fun sendProgressNotification(context: Context, progress: Int, isFinish: Boolean) {
            initNotificationManager(context)
            val builder = NotificationCompat.Builder(context, CHANNEL_ID).setOngoing(true)
                .setSmallIcon(R.mipmap.icon) //小图标
                .setContentTitle("正在下载...")  //通知标题
                //.setContentIntent(pendingIntent) //点击通知栏跳转到指定页面
                .setAutoCancel(true)    //点击通知后关闭通知
                .setOnlyAlertOnce(true); //设置提示音只响一次

            val id = getNotifyId()
            if (isFinish) {
                builder.setContentText("下载完成")
                notificationManager?.notify(id, builder.build())
            } else {
                builder.setProgress(100, progress, false)
                builder.setContentText("下载$progress%")
                notificationManager?.notify(id, builder.build())
            }
        }
    }
}