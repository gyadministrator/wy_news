package com.android.wy.news.music

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import java.io.IOException


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 13:38
  * @Version:        1.0
  * @Description:    
 */
class MediaPlayerHelper(context: Context) : MusicListener() {
    private var context: Context? = null
    private var mediaPlayer: MediaPlayer? = null
    private var onMediaHelperListener: OnMediaHelperListener? = null
    private var mResID = -5
    private var currentPath: String? = null
    private var audioManager: AudioManager? = null

    fun setOnMediaHelperListener(onMediaHelperListener: OnMediaHelperListener?) {
        this.onMediaHelperListener = onMediaHelperListener
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: MediaPlayerHelper? = null

        fun getInstance(context: Context): MediaPlayerHelper? {
            if (instance == null) {
                synchronized(MediaPlayerHelper::class.java) {
                    if (instance == null) {
                        instance = MediaPlayerHelper(context)
                    }
                }
            }
            return instance
        }
    }

    init {
        this.context = context
        this.mediaPlayer = MediaPlayer()
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    /**
     * 当播放本地uri中音时调用
     * @param path path
     */
    fun setPath(path: String?) {
        currentPath = path
        if (path == null) {
            Toast.makeText(context, "获取播放地址错误,请重试!!!", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            mediaPlayer?.reset()
            context?.let { mediaPlayer!!.setDataSource(it, Uri.parse(path)) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mediaPlayer?.prepareAsync()
        initListener()
    }

    private fun initListener() {
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnCompletionListener(this)
        mediaPlayer?.setOnErrorListener(this)
        mediaPlayer?.setOnBufferingUpdateListener(this)
    }

    /**
     * 当调用raw下的文件时使用
     * @param resId resId
     */
    fun setRawFile(resId: Int) {
        if (resId == mResID && mResID != -5) {
            //相同音乐id或者且不是第一次播放，就直接返回
            return
        }
        mResID = resId
        val afd = context?.resources?.openRawResourceFd(resId)
        try {
            mediaPlayer?.reset()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            if (afd != null) {
                mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
            mediaPlayer?.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mediaPlayer?.setOnPreparedListener { mp ->
            onMediaHelperListener?.onPreparedState(mp)
            try {
                afd?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        mediaPlayer?.setOnCompletionListener {
            onMediaHelperListener?.onPauseState()
        }
        mediaPlayer?.setOnErrorListener(this)
    }

    fun start() {
        val requestAudioFocus = audioManager?.requestAudioFocus(
            focusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        )
        if (requestAudioFocus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            if (mediaPlayer!!.isPlaying) {
                return
            }
            mediaPlayer?.start()
            if (onMediaHelperListener != null) {
                onMediaHelperListener?.onPlayingState()
            }
        }
    }

    fun pause() {
        if (!mediaPlayer!!.isPlaying) {
            return
        }
        mediaPlayer?.pause()
        if (onMediaHelperListener != null) {
            onMediaHelperListener?.onPauseState()
        }
        audioManager?.abandonAudioFocus(focusChangeListener)
    }

    private fun stop() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
        audioManager?.abandonAudioFocus(focusChangeListener)
    }

    fun isPlaying(): Boolean {
        return mediaPlayer != null && mediaPlayer?.isPlaying == true
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer!!.currentPosition
    }

    fun getDuration(): Int {
        return mediaPlayer!!.duration
    }

    fun seekTo(progress: Int) {
        mediaPlayer!!.seekTo(progress)
    }

    override fun onError(p0: MediaPlayer?, what: Int, extra: Int): Boolean {
        onMediaHelperListener?.onErrorState(what, extra)
        return super.onError(p0, what, extra)
    }

    override fun onCompletion(p0: MediaPlayer?) {
        super.onCompletion(p0)
        onMediaHelperListener?.onCompleteState()
    }

    override fun onBufferingUpdate(p0: MediaPlayer?, percent: Int) {
        super.onBufferingUpdate(p0, percent)
        onMediaHelperListener?.onBufferState(percent)
    }

    override fun onPrepared(p0: MediaPlayer?) {
        super.onPrepared(p0)
        onMediaHelperListener?.onPreparedState(p0)
    }

    private val focusChangeListener =
        AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    if (isPlaying()) {
                        pause()
                    }
                }

                /*AudioManager.AUDIOFOCUS_GAIN -> {
                    if (currentPath != null && !isPlaying()) {
                        start()
                    }
                }*/

                AudioManager.AUDIOFOCUS_LOSS -> {
                    stop()
                }

                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    //stop()
                    if (currentPath != null && !isPlaying()) {
                        start()
                    }
                }

                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                    stop()
                }
            }
        }

    interface OnMediaHelperListener {
        //音乐准备好之后调用
        fun onPreparedState(mp: MediaPlayer?)

        //音乐暂停状态
        fun onPauseState()

        //音乐播放状态
        fun onPlayingState()

        //音乐播放完成状态
        fun onCompleteState()

        //加载
        fun onBufferState(percent: Int)

        //音乐加载错误
        fun onErrorState(what: Int, extra: Int)
    }

}