package com.android.wy.news.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.android.wy.news.R
import com.android.wy.news.common.Logger
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.MusicNotifyHelper
import com.android.wy.news.receiver.MusicBroadCastReceiver
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/12 17:11
  * @Version:        1.0
  * @Description:     /**
     * 此服务，用于展示音乐播放器，并实现音乐播放
     * 在此说明一下我们音乐播放器的实现
     * 1.  整体界面使用通知实现前台服务
     * 2. 上面的图片按钮操作选择了另外的Service和BroadCast
     * 3. 操作方面其实后台的话，服务和广播都是不错的选择
     * 4. 切换歌曲我选择了广播，暂停和继续播放我选择了另外的服务
     */
 */
class MusicService : Service() {
    companion object {
        private const val notifyID = 100
        private const val CHANNEL_ID = "wy_news_music_channel_id"
        private const val CHANNEL_NAME = "wy_news_music_channel_name"
        private const val CHANNEL_DESCRIPTION = "wy_news_music_channel_description"
        const val MUSIC_PRE_ACTION = "com.android.wy.news.service.broadcasts.PLAY_PRE"
        const val MUSIC_NEXT_ACTION = "com.android.wy.news.service.broadcasts.PLAY_NEXT"
        private const val MODE_PLAY = true
        private const val MODE_PAUSE = false
    }

    private var mNotifyHelper: MusicNotifyHelper? = null
    private var mMediaHelper: MediaPlayerHelper? = null
    private var notifyLayout: RemoteViews? = null
    private var musicBroadListener: MusicBroadCastReceiver? = null
    private var flag = false


    override fun onBind(p0: Intent?): IBinder {
        return MusicBinder(this)
    }

    override fun onCreate() {
        super.onCreate()
        Logger.i("MusicService start...")
        mMediaHelper = MediaPlayerHelper.getInstance(this)
        mNotifyHelper = MusicNotifyHelper.getInstance(this)
        notifyLayout = RemoteViews(packageName, R.layout.layout_music_controller_notification)
        musicBroadListener = MusicBroadCastReceiver.instance
        flag = MODE_PAUSE
        registerBroadCast()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 动态注册广播，因为8.0版本以后静态注册可能不起作用
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerBroadCast() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(MUSIC_PRE_ACTION)
        intentFilter.addAction(MUSIC_NEXT_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(musicBroadListener, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(musicBroadListener, intentFilter)
        }
    }

    class MusicBinder(musicService: MusicService) : Binder() {
        private var musicService: MusicService

        init {
            this.musicService = musicService
        }

        fun getService(): MusicService {
            return this.musicService
        }

        fun setMusic(musicInfo: MusicInfo) {

            Logger.i("setMusic:$musicInfo")
            musicService.defaultMusicState(musicInfo)

            musicService.mMediaHelper?.setOnMediaHelperListener(object :
                MediaPlayerHelper.OnMediaHelperListener {
                override fun onPrepared(mp: MediaPlayer?) {
                    Logger.i("onPrepared: ")
                    musicService.flag = MODE_PAUSE
                    musicService.playMusicState(musicInfo)
                    musicService.mMediaHelper?.start()
                }

                override fun onPauseState() {
                    Logger.i("onPauseState: ")
                    musicService.pauseMusicState(musicInfo)
                }

                override fun onPlayingState() {
                    Logger.i("onPlayingState: ")
                    musicService.playMusicState(musicInfo)
                }

                override fun onError(what: Int, extra: Int) {
                    Logger.i("onError: what:$what  extra:$extra")
                    musicService.defaultMusicState(musicInfo)
                }
            })
            musicService.changeMusic()
            //musicService.mMediaHelper?.setRawFile(musicResult.pic as Int)
            musicService.mMediaHelper?.setPath(musicInfo.pic)
        }
    }

    private fun changeMusic() {
        /**
         * 发送广播，进行切歌到上一首操作
         */
        val preIntent = Intent(MUSIC_PRE_ACTION)
        val prePendingIntent = PendingIntent.getBroadcast(
            this,
            6,
            preIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notifyLayout?.setOnClickPendingIntent(R.id.iv_pre, prePendingIntent)
        /**
         * 发送广播，进行切歌到下一首操作
         */
        val nextIntent = Intent(MUSIC_NEXT_ACTION)
        val nextPendingIntent = PendingIntent.getBroadcast(
            this,
            7,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notifyLayout?.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent)
    }

    /**
     * 更改了相关设置，比如notifyLayout布局显示之后，需要重新发送前台通知来更新UI
     * 太坑了，居然是这样的
     * 此外，由于我们是显示成一个播放器，因此通知id，使用固定id，就可以保证每次更新之后是同一个通知。
     * @param musicInfo musicInfo
     */
    private fun startMusicForeground(musicInfo: MusicInfo) {
        Logger.i("startMusicForeground start...")
        mNotifyHelper?.createChannel(CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION)

        val builder: NotificationCompat.Builder? =
            mNotifyHelper?.createForeNotification(CHANNEL_ID, notifyLayout)
        builder?.setContentTitle(musicInfo.artist)
        builder?.setContentText(musicInfo.album)
        startForeground(notifyID, builder?.build())

        Glide.with(this).asBitmap().load(musicInfo.pic)
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
                    builder?.setLargeIcon(resource)
                    startForeground(notifyID, builder?.build())
                }
            })
    }

    /**
     * 处于播放状态下的音乐应该具有的一些配置
     * @param musicInfo
     */
    private fun playMusicState(musicInfo: MusicInfo) {
        Logger.i("playMusicState: ")
        if (flag == MODE_PLAY) return
        flag = MODE_PLAY
        val pauseIntent = Intent(this, PauseService::class.java)
        val pausePendingIntent = PendingIntent.getService(
            this,
            0,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notifyLayout?.setOnClickPendingIntent(R.id.iv_play, pausePendingIntent)
        notifyLayout?.setImageViewResource(R.id.iv_cover, R.mipmap.icon)
        notifyLayout?.setImageViewResource(R.id.iv_play, R.mipmap.music_pause)
        startMusicForeground(musicInfo)
    }

    /**
     * 处于播放状态下的音乐应该具有的一些配置
     * @param musicInfo
     */
    private fun defaultMusicState(musicInfo: MusicInfo) {
        Logger.i("defaultMusicState: ")
        val pauseIntent = Intent(this, PauseService::class.java)
        val pausePendingIntent = PendingIntent.getService(
            this,
            1,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notifyLayout?.setOnClickPendingIntent(R.id.iv_play, pausePendingIntent)
        notifyLayout?.setImageViewResource(R.id.iv_cover, R.mipmap.icon)
        notifyLayout?.setImageViewResource(R.id.iv_play, R.mipmap.music_play)
        startMusicForeground(musicInfo)
    }

    /**
     * 处于暂停状态下的音乐应该具有的一些配置
     * @param musicInfo
     */
    private fun pauseMusicState(musicInfo: MusicInfo) {
        Logger.i("pauseMusicState: ")
        if (flag == MODE_PAUSE) return
        flag = MODE_PAUSE
        val playIntent = Intent(this, PlayService::class.java)
        val playPendingIntent = PendingIntent.getService(
            this,
            2,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notifyLayout?.setOnClickPendingIntent(R.id.iv_play, playPendingIntent)
        notifyLayout?.setImageViewResource(R.id.iv_cover, R.mipmap.icon)
        notifyLayout?.setImageViewResource(R.id.iv_play, R.mipmap.music_pause)
        startMusicForeground(musicInfo)
    }


    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
        unregisterReceiver(musicBroadListener)
    }
}