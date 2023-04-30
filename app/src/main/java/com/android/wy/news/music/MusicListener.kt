package com.android.wy.news.music

import android.media.MediaPlayer

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/26 14:30
  * @Version:        1.0
  * @Description:    
 */
open class MusicListener :
    MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnInfoListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnSeekCompleteListener,
    MediaPlayer.OnVideoSizeChangedListener {
    /**
     * 该接口的作用是在流媒体缓冲状态发生改变的时候回调，
     * percent表示已经缓冲了的或者播放了的媒体流百分比
     */
    override fun onBufferingUpdate(p0: MediaPlayer?, percent: Int) {

    }

    /**
     * 在媒体流播放完毕之后回调。
     * 可以在该回调中设置播放下一个视频文件
     */
    override fun onCompletion(p0: MediaPlayer?) {

    }

    /**
     * 在异步操作中出现错误时会回调该方法, 其它情况下出现错误时直接抛出异常。
     * what：出现的错误类型。
     * extra：针对与具体错误的附加码, 用于定位错误更详细信息
     */
    override fun onError(p0: MediaPlayer?, what: Int, extra: Int): Boolean {
        return true
    }

    /**
     * 该方法在媒体播放时出现信息或者警告时回调该方法。
     * what：信息或者警告的类型。
     * extra：信息或者警告的附加码，关于警告更详细信息
     */
    override fun onInfo(p0: MediaPlayer?, what: Int, extra: Int): Boolean {
        return false
    }

    /**
     * 该方法在进入Prepared状态并开始播放的时候回调
     */
    override fun onPrepared(p0: MediaPlayer?) {

    }

    /**
     * 查找操作完成的时候回调该方法
     */
    override fun onSeekComplete(p0: MediaPlayer?) {

    }

    /**
     * 当视频大小首次加载的时候及视频大小更新时回调该方法，如果没有视频返回0
     */
    override fun onVideoSizeChanged(p0: MediaPlayer?, width: Int, height: Int) {

    }
}