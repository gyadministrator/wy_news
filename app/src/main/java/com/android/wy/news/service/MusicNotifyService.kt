package com.android.wy.news.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.text.TextUtils
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.android.wy.news.R
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.dialog.LoadingDialog
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.event.MusicEvent
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.MusicNotifyHelper
import com.android.wy.news.music.MusicState
import com.android.wy.news.util.AppUtil
import com.android.wy.news.util.JsonUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import org.greenrobot.eventbus.EventBus
import java.util.Timer
import java.util.TimerTask

class MusicNotifyService : Service() {
    private var mNotifyHelper: MusicNotifyHelper? = null
    private var notifyLayout: RemoteViews? = null
    private var mediaHelper: MediaPlayerHelper? = null
    private var timer: Timer? = null

    companion object {
        private const val notifyID = GlobalData.MUSIC_NOTIFY_ID
        private const val CHANNEL_ID = "wy_news_music_channel_id"
        private const val CHANNEL_NAME = "wy_news_music_channel_name"
        private const val CHANNEL_DESCRIPTION = "wy_news_music_channel_description"
        const val MUSIC_PRE_ACTION = "service.action.play_pre"
        const val MUSIC_NEXT_ACTION = "service.action.play_next"
        const val MUSIC_PLAY_ACTION = "service.action.play"
        const val MUSIC_PAUSE_ACTION = "service.action.pause"
        const val MUSIC_COMPLETE_ACTION = "service.action.complete"
        const val MUSIC_STATE_ACTION = "service.action.state"
        const val MUSIC_PREPARE_ACTION = "service.action.prepare"
        const val MUSIC_CLOSE_ACTION = "service.action.close"
        const val MUSIC_LOCK_ACTION = "service.action.lock"
        const val MUSIC_INFO_KEY = "music.info.key"
        const val MUSIC_URL_KEY = "music.url.key"
    }

    override fun onBind(p0: Intent?): IBinder {
        return Binder()
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            Logger.i("onStartCommand--->>>action:$action")
            when (action) {
                MUSIC_PREPARE_ACTION -> {
                    val s = intent.getStringExtra(MUSIC_INFO_KEY)
                    val url = intent.getStringExtra(MUSIC_URL_KEY)
                    if (!TextUtils.isEmpty(s)) {
                        val musicInfo = JsonUtil.parseJsonToObject(s, MusicInfo::class.java)
                        musicInfo?.let { setMusic(it, url) }
                    }
                }

                MUSIC_STATE_ACTION -> {
                    if (mediaHelper!!.isPlaying()) {
                        mediaHelper?.pause()
                    } else {
                        mediaHelper?.start()
                    }
                }

                else -> {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setMusic(musicInfo: MusicInfo, url: String?) {
        Logger.i("setMusic:$musicInfo")
        mediaHelper?.setPath(url)
        val receiverIntent = Intent()
        mediaHelper?.setOnMediaHelperListener(object :
            MediaPlayerHelper.OnMediaHelperListener {
            override fun onPreparedState(mp: MediaPlayer?) {
                Logger.i("onPreparedState: ")
                mediaHelper?.start()
            }

            override fun onPauseState() {
                GlobalData.isPlaying.postValue(false)
                Logger.i("onPauseState: ")
                startMusicForeground(musicInfo)
                timer?.cancel()
                timer = null
                mediaHelper?.pause()
                receiverIntent.action = MUSIC_PAUSE_ACTION
                sendBroadcast(receiverIntent)
            }

            override fun onPlayingState() {
                LoadingDialog.hide(GlobalData.MUSIC_LOADING_TAG)
                GlobalData.isPlaying.postValue(true)
                Logger.i("onPlayingState: ")
                startMusicForeground(musicInfo)
                timer?.cancel()
                timer = null
                setProgress()
                receiverIntent.action = MUSIC_PLAY_ACTION
                sendBroadcast(receiverIntent)
            }

            override fun onCompleteState() {
                GlobalData.isPlaying.postValue(false)
                Logger.i("onCompleteState: ")
                GlobalData.currentLrcData.clear()
                timer?.cancel()
                timer = null
                receiverIntent.action = MUSIC_COMPLETE_ACTION
                sendBroadcast(receiverIntent)
            }

            override fun onBufferState(percent: Int) {
                Logger.i("onBufferState: $percent")
            }

            override fun onErrorState(what: Int, extra: Int) {
                GlobalData.isPlaying.postValue(false)
                Logger.i("onErrorState: what:$what  extra:$extra")
            }
        })
    }

    private fun initPendingIntent() {
        val stateIntent = Intent(this, MusicPlayService::class.java)
        stateIntent.action = MUSIC_STATE_ACTION
        val statePendingIntent = PendingIntent.getService(
            this,
            0,
            stateIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notifyLayout?.setOnClickPendingIntent(R.id.iv_play, statePendingIntent)

        val preIntent = Intent(this, MusicPlayService::class.java)
        preIntent.action = MUSIC_PRE_ACTION
        val prePendingIntent = PendingIntent.getService(
            this,
            1,
            preIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notifyLayout?.setOnClickPendingIntent(R.id.iv_pre, prePendingIntent)

        val nextIntent = Intent(this, MusicPlayService::class.java)
        nextIntent.action = MUSIC_NEXT_ACTION
        val nextPendingIntent = PendingIntent.getService(
            this,
            2,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notifyLayout?.setOnClickPendingIntent(R.id.iv_next, nextPendingIntent)

        val closeIntent = Intent(this, MusicPlayService::class.java)
        closeIntent.action = MUSIC_CLOSE_ACTION
        val closePendingIntent = PendingIntent.getService(
            this,
            3,
            closeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        notifyLayout?.setOnClickPendingIntent(R.id.iv_close, closePendingIntent)

        val lockIntent = Intent(this, MusicPlayService::class.java)
        lockIntent.action = MUSIC_LOCK_ACTION
        val lockPendingIntent = PendingIntent.getService(
            this,
            4,
            lockIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notifyLayout?.setOnClickPendingIntent(R.id.iv_lock, lockPendingIntent)
    }

    /**
     * 此外，由于我们是显示成一个播放器，因此通知id，使用固定id，就可以保证每次更新之后是同一个通知。
     * @param musicInfo musicInfo
     */
    private fun startMusicForeground(musicInfo: MusicInfo) {
        val background = AppUtil.isBackground(this)
        Logger.i("startMusicForeground start...")
        mNotifyHelper?.createChannel(
            CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESCRIPTION
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder? =
            mNotifyHelper?.createForeNotification(CHANNEL_ID, notifyLayout)

        if (mediaHelper!!.isPlaying()) {
            notifyLayout?.setImageViewResource(R.id.iv_play, R.mipmap.music_play)
        } else {
            notifyLayout?.setImageViewResource(R.id.iv_play, R.mipmap.music_pause)
        }

        if (GlobalData.isLock) {
            notifyLayout?.setImageViewResource(R.id.iv_lock, R.mipmap.lock)
        } else {
            notifyLayout?.setImageViewResource(R.id.iv_lock, R.mipmap.unlock)
        }

        notifyLayout?.setTextViewText(R.id.tv_title, musicInfo.artist)
        notifyLayout?.setTextViewText(R.id.tv_desc, musicInfo.name)
        if (background) {
            notificationManager.notify(notifyID, builder?.build())
        } else {
            startForeground(notifyID, builder?.build())
        }

        Glide.with(this).asBitmap().load(musicInfo.pic)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(4)))
            .diskCacheStrategy(DiskCacheStrategy.ALL).override(50, 50)
            //设置为这种格式去掉透明度通道，可以减少内存占有
            .format(DecodeFormat.PREFER_RGB_565).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap, transition: Transition<in Bitmap?>?
                ) {
                    notifyLayout?.setImageViewBitmap(R.id.iv_cover, resource)
                    if (background) {
                        notificationManager.notify(notifyID, builder?.build())
                    } else {
                        startForeground(notifyID, builder?.build())
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }
}