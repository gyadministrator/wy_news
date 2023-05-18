package com.android.wy.news.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.android.wy.news.common.Logger

class MusicPlayService : Service() {
    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val receiverIntent = Intent()
        if (intent != null) {
            val action = intent.action
            Logger.i("onStartCommand--->>>action：$action")
            when (action) {
                MusicNotifyService.MUSIC_PRE_ACTION -> {
                    receiverIntent.action = MusicNotifyService.MUSIC_PRE_ACTION
                    sendBroadcast(receiverIntent)
                }

                MusicNotifyService.MUSIC_NEXT_ACTION -> {
                    receiverIntent.action = MusicNotifyService.MUSIC_NEXT_ACTION
                    sendBroadcast(receiverIntent)
                }

                MusicNotifyService.MUSIC_STATE_ACTION -> {
                    receiverIntent.action = MusicNotifyService.MUSIC_STATE_ACTION
                    sendBroadcast(receiverIntent)
                }

                MusicNotifyService.MUSIC_CLOSE_ACTION -> {
                    receiverIntent.action = MusicNotifyService.MUSIC_CLOSE_ACTION
                    sendBroadcast(receiverIntent)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}