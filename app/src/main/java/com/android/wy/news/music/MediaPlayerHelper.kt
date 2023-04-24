package com.android.wy.news.music

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import java.io.IOException
import java.lang.ref.WeakReference


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 13:38
  * @Version:        1.0
  * @Description:    
 */
class MediaPlayerHelper(context: Context) {
    @SuppressLint("StaticFieldLeak")
    private var instance: WeakReference<MediaPlayerHelper?>? = null
    private var mContext: Context? = context
    private var mMediaPlayer: MediaPlayer? = null
    private var mPath: String? = null
    private var onMediaPlayerHelperListener: OnMediaPlayerHelperListener? = null

    fun setOnMediaPlayerHelperListener(onMediaPlayerHelperListener: OnMediaPlayerHelperListener?) {
        this.onMediaPlayerHelperListener = onMediaPlayerHelperListener
    }

    fun getInstance(context: Context): MediaPlayerHelper? {
        if (instance == null) {
            synchronized(MediaPlayerHelper::class.java) {
                if (instance == null) {
                    instance = WeakReference(MediaPlayerHelper(context))
                }
            }
        }
        return instance?.get()
    }

    init {
        mMediaPlayer = MediaPlayer()
    }

    fun setPath(path: String) {
        if (mMediaPlayer != null) {
            /**
             * 1.音乐正在播放，重置音乐播放状态
             * 2.设置播放音乐路径
             * 3.准备播放
             */
            if (mMediaPlayer!!.isPlaying || path != mPath) {
                mMediaPlayer?.reset()
            }
            mPath = path
            try {
                mContext?.let { mMediaPlayer?.setDataSource(it, Uri.parse(path)) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mMediaPlayer?.prepareAsync()
            mMediaPlayer?.setOnPreparedListener { mediaPlayer ->
                if (onMediaPlayerHelperListener != null) {
                    onMediaPlayerHelperListener?.onPrepared(mediaPlayer)
                }
            }
            mMediaPlayer?.setOnCompletionListener { mediaPlayer ->
                if (onMediaPlayerHelperListener != null) {
                    onMediaPlayerHelperListener?.onCompletion(mediaPlayer)
                }
            }
        }
    }

    /**
     * 返回正在播放的音乐路径
     *
     * @return
     */
    fun getPath(): String? {
        return mPath
    }

    /**
     * 判断音乐是否在播放
     *
     * @return
     */
    fun isPlaying(): Boolean {
        return if (mMediaPlayer != null) {
            mMediaPlayer!!.isPlaying
        } else false
    }

    /**
     * 播放音乐
     */
    fun start() {
        if (mMediaPlayer!!.isPlaying) return
        mMediaPlayer?.start()
    }

    /**
     * 暂停音乐
     */
    fun pause() {
        mMediaPlayer?.pause()
    }

    interface OnMediaPlayerHelperListener {
        fun onPrepared(mediaPlayer: MediaPlayer?)
        fun onCompletion(mediaPlayer: MediaPlayer?)
    }

}