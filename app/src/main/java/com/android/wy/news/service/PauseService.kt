package com.android.wy.news.service

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/26 10:56
  * @Version:        1.0
  * @Description:    
 */
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.android.wy.news.music.MediaPlayerHelper


class PauseService : Service() {
    /**
     * 用于使音乐暂停的服务
     */
    private var mMediaHelper: MediaPlayerHelper? = null
    override fun onCreate() {
        super.onCreate()
        mMediaHelper = MediaPlayerHelper.getInstance(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mMediaHelper?.pause()
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }
}

