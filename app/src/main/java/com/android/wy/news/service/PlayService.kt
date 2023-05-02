package com.android.wy.news.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.android.wy.news.music.MediaPlayerHelper

class PlayService : Service() {
    private var mediaPlayer: MediaPlayerHelper? = null

    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayerHelper.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val receiverIntent = Intent()
        if (mediaPlayer!!.isPlaying()) {
            mediaPlayer?.pause()
            receiverIntent.action = MusicService.MUSIC_PAUSE_ACTION
            sendBroadcast(receiverIntent)
        } else {
            mediaPlayer?.start()
            receiverIntent.action = MusicService.MUSIC_PLAY_ACTION
            sendBroadcast(receiverIntent)
        }
        return super.onStartCommand(intent, flags, startId)
    }
}