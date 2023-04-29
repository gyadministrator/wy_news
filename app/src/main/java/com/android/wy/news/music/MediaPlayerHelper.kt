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
    private var mContext: Context? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mOnMediaHelperListener: OnMediaHelperListener? = null
    private var mResID = -5

    fun setOnMediaHelperListener(mOnMediaHelperListener: OnMediaHelperListener?) {
        this.mOnMediaHelperListener = mOnMediaHelperListener
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
        mContext = context
        mMediaPlayer = MediaPlayer()
    }

    /**
     * 当播放本地uri中音时调用
     * @param path path
     */
    fun setPath(path: String?) {
        if (path == null) {
            Toast.makeText(mContext, "获取播放地址错误,请重试!!!", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            mMediaPlayer?.reset()
            mContext?.let { mMediaPlayer!!.setDataSource(it, Uri.parse(path)) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mMediaPlayer?.prepareAsync()
        initListener()
    }

    private fun initListener() {
        mMediaPlayer?.setOnPreparedListener(this)
        mMediaPlayer?.setOnCompletionListener(this)
        mMediaPlayer?.setOnErrorListener(this)
        mMediaPlayer?.setOnBufferingUpdateListener(this)
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
        //mOnInitMusicListener.initMode();
        mResID = resId
        val afd = mContext?.resources?.openRawResourceFd(resId)
        try {
            mMediaPlayer?.reset()
            mMediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            if (afd != null) {
                mMediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
            mMediaPlayer?.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mMediaPlayer?.setOnPreparedListener { mp ->
            mOnMediaHelperListener?.onPreparedState(mp)
            try {
                afd?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        mMediaPlayer?.setOnCompletionListener {
            mOnMediaHelperListener?.onPauseState()
        }
        mMediaPlayer?.setOnErrorListener(this)
    }

    fun start() {
        if (mMediaPlayer!!.isPlaying) {
            return
        }
        mMediaPlayer?.start()
        if (mOnMediaHelperListener != null) {
            mOnMediaHelperListener?.onPlayingState()
        }
    }

    fun pause() {
        if (!mMediaPlayer!!.isPlaying) {
            return
        }
        mMediaPlayer?.pause()
        if (mOnMediaHelperListener != null) {
            mOnMediaHelperListener?.onPauseState()
        }
    }

    fun isPlaying(): Boolean {
        return mMediaPlayer != null && mMediaPlayer?.isPlaying == true
    }

    fun getCurrentPosition(): Int {
        return mMediaPlayer!!.currentPosition
    }

    fun getDuration(): Int {
        return mMediaPlayer!!.duration
    }

    fun seekTo(progress: Int) {
        mMediaPlayer!!.seekTo(progress)
    }

    override fun onError(p0: MediaPlayer?, what: Int, extra: Int): Boolean {
        mOnMediaHelperListener?.onErrorState(what, extra)
        return super.onError(p0, what, extra)
    }

    override fun onCompletion(p0: MediaPlayer?) {
        super.onCompletion(p0)
        mOnMediaHelperListener?.onCompleteState()
    }

    override fun onBufferingUpdate(p0: MediaPlayer?, percent: Int) {
        super.onBufferingUpdate(p0, percent)
        mOnMediaHelperListener?.onBufferState(percent)
    }

    override fun onPrepared(p0: MediaPlayer?) {
        super.onPrepared(p0)
        mOnMediaHelperListener?.onPreparedState(p0)
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