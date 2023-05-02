package com.android.wy.news.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.android.wy.news.R
import com.android.wy.news.common.Logger
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.MusicNotifyHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

class MusicService : Service() {
    private var mNotifyHelper: MusicNotifyHelper? = null
    private var notifyLayout: RemoteViews? = null
    private var mMediaHelper: MediaPlayerHelper? = null

    companion object {
        private const val notifyID = 100
        private const val CHANNEL_ID = "wy_news_music_channel_id"
        private const val CHANNEL_NAME = "wy_news_music_channel_name"
        private const val CHANNEL_DESCRIPTION = "wy_news_music_channel_description"
        const val MUSIC_PRE_ACTION = "service.action.play_pre"
        const val MUSIC_NEXT_ACTION = "service.action.play_next"
        const val MUSIC_PLAY_ACTION = "service.action.play"
        const val MUSIC_PAUSE_ACTION = "service.action.pause"
    }

    override fun onBind(p0: Intent?): IBinder {
        return MusicBinder(this)
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
            musicService.startMusicForeground(musicInfo)
        }
    }

    override fun onCreate() {
        super.onCreate()
        mNotifyHelper = MusicNotifyHelper.getInstance(this)
        notifyLayout = RemoteViews(packageName, R.layout.layout_music_controller_notification)
        mMediaHelper = MediaPlayerHelper.getInstance(this)
        mMediaHelper?.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            if (action != null) {
                when (action) {
                    MUSIC_PLAY_ACTION -> {

                    }

                    MUSIC_PAUSE_ACTION -> {

                    }

                    MUSIC_PRE_ACTION -> {

                    }

                    MUSIC_NEXT_ACTION -> {

                    }

                    else -> {

                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 此外，由于我们是显示成一个播放器，因此通知id，使用固定id，就可以保证每次更新之后是同一个通知。
     * @param musicInfo musicInfo
     */
    private fun startMusicForeground(musicInfo: MusicInfo) {
        Logger.i("startMusicForeground start...")
        mNotifyHelper?.createChannel(
            CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION
        )

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
}