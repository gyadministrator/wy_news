package com.android.wy.news.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.android.wy.news.common.Logger

class PlayService : Service() {
    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val receiverIntent = Intent()
        if (intent != null) {
            val action = intent.action
            Logger.i("onStartCommand--->>>action：$action")
            when (action) {
                MusicService.MUSIC_PRE_ACTION -> {
                    receiverIntent.action = MusicService.MUSIC_PRE_ACTION
                    sendBroadcast(receiverIntent)
                }

                MusicService.MUSIC_NEXT_ACTION -> {
                    receiverIntent.action = MusicService.MUSIC_NEXT_ACTION
                    sendBroadcast(receiverIntent)
                }

                MusicService.MUSIC_STATE_ACTION -> {
                    receiverIntent.action = MusicService.MUSIC_STATE_ACTION
                    sendBroadcast(receiverIntent)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}