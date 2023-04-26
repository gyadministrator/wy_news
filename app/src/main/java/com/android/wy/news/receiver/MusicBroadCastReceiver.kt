package com.android.wy.news.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.android.wy.news.common.Logger
import com.android.wy.news.service.MusicService

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/26 10:57
  * @Version:        1.0
  * @Description:    
 */


class MusicBroadCastReceiver : BroadcastReceiver() {
    private var musicBroadListener: OnMusicBroadListener? = null

    fun setMusicBroadListener(onMusicBroadListener: OnMusicBroadListener) {
        this.musicBroadListener = onMusicBroadListener
    }

    override fun onReceive(context: Context, intent: Intent) {
        Logger.i("MusicBroadCastReceiver---onReceive[action=${intent.action}]")
        if (PLAY_PRE == intent.action) {
            Toast.makeText(
                context,
                "即将切换至上一首，若切换失败，请回到界面点击音乐实现切换",
                Toast.LENGTH_LONG
            ).show()
            musicBroadListener?.playPre()
        } else if (PLAY_NEXT == intent.action) {
            Toast.makeText(
                context,
                "即将切换至下一首，若切换失败，请回到界面点击音乐实现切换",
                Toast.LENGTH_LONG
            ).show()
            musicBroadListener?.playNext()
        }
    }

    /**
     * 定义外放接口,在MusAdapter类中我进行了实现
     */
    interface OnMusicBroadListener {
        fun playPre()
        fun playNext()
    }

    companion object {
        /**
         * 切换歌曲的广播，这里仍采用单例模式。
         * 原因是我们要将切换歌曲的接口外放到MusAdapter
         */
        var instance: MusicBroadCastReceiver? = null
            get() {
                if (field == null) {
                    field = MusicBroadCastReceiver()
                }
                return field
            }
            private set
        private const val PLAY_PRE = MusicService.MUSIC_PRE_ACTION
        private const val PLAY_NEXT = MusicService.MUSIC_NEXT_ACTION
    }
}

