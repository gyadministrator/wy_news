package com.android.wy.news.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.android.wy.news.R
import com.android.wy.news.common.Logger
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.event.MusicEvent
import com.android.wy.news.event.PlayEvent
import com.android.wy.news.fragment.MusicFragment
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.MusicNotifyHelper
import com.android.wy.news.music.MusicState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import org.greenrobot.eventbus.EventBus
import java.util.Timer
import java.util.TimerTask

class MusicService : Service() {
    private var mNotifyHelper: MusicNotifyHelper? = null
    private var notifyLayout: RemoteViews? = null
    private var mediaHelper: MediaPlayerHelper? = null
    private var timer: Timer? = null

    companion object {
        private const val notifyID = 100
        private const val CHANNEL_ID = "wy_news_music_channel_id"
        private const val CHANNEL_NAME = "wy_news_music_channel_name"
        private const val CHANNEL_DESCRIPTION = "wy_news_music_channel_description"
        const val MUSIC_PRE_ACTION = "service.action.play_pre"
        const val MUSIC_NEXT_ACTION = "service.action.play_next"
        const val MUSIC_PLAY_ACTION = "service.action.play"
        const val MUSIC_PAUSE_ACTION = "service.action.pause"
        const val MUSIC_COMPLETE_ACTION = "service.action.complete"
        const val MUSIC_STATE_ACTION = "service.action.state"
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

        fun setMusic(musicInfo: MusicInfo, url: String) {
            Logger.i("setMusic:$musicInfo")
            this.musicService.mediaHelper?.setPath(url)
            val receiverIntent = Intent()
            this.musicService.mediaHelper?.setOnMediaHelperListener(object :
                MediaPlayerHelper.OnMediaHelperListener {
                override fun onPreparedState(mp: MediaPlayer?) {
                    Logger.i("onPreparedState: ")
                    musicService.mediaHelper?.start()
                }

                override fun onPauseState() {
                    Logger.i("onPauseState: ")
                    EventBus.getDefault().postSticky(PlayEvent())
                    musicService.startMusicForeground(musicInfo)
                    musicService.timer?.cancel()
                    musicService.timer = null
                    musicService.mediaHelper?.pause()
                    receiverIntent.action = MUSIC_PAUSE_ACTION
                    musicService.sendBroadcast(receiverIntent)
                }

                override fun onPlayingState() {
                    Logger.i("onPlayingState: ")
                    EventBus.getDefault().postSticky(PlayEvent())
                    musicService.startMusicForeground(musicInfo)
                    musicService.timer?.cancel()
                    musicService.timer = null
                    musicService.setProgress()
                    receiverIntent.action = MUSIC_PLAY_ACTION
                    musicService.sendBroadcast(receiverIntent)
                }

                override fun onCompleteState() {
                    Logger.i("onCompleteState: ")
                    musicService.timer?.cancel()
                    musicService.timer = null
                    receiverIntent.action = MUSIC_COMPLETE_ACTION
                    musicService.sendBroadcast(receiverIntent)
                }

                override fun onBufferState(percent: Int) {
                    Logger.i("onBufferState: $percent")
                }

                override fun onErrorState(what: Int, extra: Int) {
                    Logger.i("onErrorState: what:$what  extra:$extra")
                }
            })
        }
    }

    private fun setProgress() {
        if (timer == null) {
            //时间监听器
            timer = Timer()
        }
        timer?.schedule(object : TimerTask() {
            override fun run() {
                val time = mediaHelper?.getCurrentPosition()
                Logger.i("setProgress--->>>time:$time")
                val musicEvent = time?.let { MusicEvent(MusicState.STATE_PLAY, it) }
                EventBus.getDefault().postSticky(musicEvent)
            }
        }, 0, 50)
    }

    override fun onCreate() {
        super.onCreate()
        mNotifyHelper = MusicNotifyHelper.getInstance(this)
        notifyLayout = RemoteViews(packageName, R.layout.layout_music_controller_notification)
        mediaHelper = MediaPlayerHelper.getInstance(this)
        initPendingIntent()
    }

    private fun initPendingIntent() {
        val stateIntent = Intent(this, PlayService::class.java)
        stateIntent.action = MUSIC_STATE_ACTION
        val statePendingIntent = PendingIntent.getService(
            this,
            0,
            stateIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notifyLayout?.setOnClickPendingIntent(R.id.iv_play, statePendingIntent)

        val preIntent = Intent(this, PlayService::class.java)
        preIntent.action = MUSIC_PRE_ACTION
        val prePendingIntent = PendingIntent.getService(
            this,
            1,
            preIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notifyLayout?.setOnClickPendingIntent(R.id.iv_pre, prePendingIntent)

        val nextIntent = Intent(this, PlayService::class.java)
        nextIntent.action = MUSIC_NEXT_ACTION
        val nextPendingIntent = PendingIntent.getService(
            this,
            2,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notifyLayout?.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent)
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
        if (mediaHelper!!.isPlaying()) {
            notifyLayout?.setImageViewResource(R.id.iv_play, R.mipmap.music_play)
        } else {
            notifyLayout?.setImageViewResource(R.id.iv_play, R.mipmap.music_pause)
        }
        notifyLayout?.setTextViewText(R.id.tv_title, musicInfo.artist)
        notifyLayout?.setTextViewText(R.id.tv_desc, musicInfo.album)
        builder?.build()?.flags = NotificationCompat.FLAG_AUTO_CANCEL
        startForeground(notifyID, builder?.build())

        Glide.with(this).asBitmap().load(musicInfo.pic)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(40)))
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
                    notifyLayout?.setImageViewBitmap(R.id.iv_cover, resource)
                    startForeground(notifyID, builder?.build())
                }
            })
    }
}