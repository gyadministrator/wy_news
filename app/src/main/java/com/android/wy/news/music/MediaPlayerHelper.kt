package com.android.wy.news.music

import android.content.Context
import android.media.AudioManager
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
class MediaPlayerHelper(context: Context) : MusicListener() {
    private var mContext: Context? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mOnMediaHelperListener: OnMediaHelperListener? = null
    private var mResID = -5

    fun setOnMediaHelperListener(mOnMediaHelperListener: OnMediaHelperListener?) {
        this.mOnMediaHelperListener = mOnMediaHelperListener
    }

    companion object {
        private var instance: WeakReference<MediaPlayerHelper>? = null

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
        if (mMediaPlayer?.isPlaying == true) {
            mMediaPlayer?.reset()
        }
        try {
            mContext?.let { mMediaPlayer!!.setDataSource(it, Uri.parse(path)) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mMediaPlayer?.prepareAsync()
        mMediaPlayer?.setOnPreparedListener { mp ->
            mOnMediaHelperListener?.onPrepared(mp)
        }
        mMediaPlayer?.setOnCompletionListener {
            mOnMediaHelperListener?.onPauseState()
        }
        mMediaPlayer?.setOnErrorListener(this)
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
            mOnMediaHelperListener?.onPrepared(mp)
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
        if (mMediaPlayer?.isPlaying == true) {
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
        mOnMediaHelperListener?.onError(what, extra)
        return super.onError(p0, what, extra)
    }

    interface OnMediaHelperListener {
        //音乐准备好之后调用
        fun onPrepared(mp: MediaPlayer?)

        //音乐暂停状态
        fun onPauseState()

        //音乐播放状态
        fun onPlayingState()

        fun onError(what: Int, extra: Int)
    }

}