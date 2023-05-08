package com.android.wy.news.manager

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.android.wy.news.service.MusicPlayService


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/8 19:03
  * @Version:        1.0
  * @Description:    
 */
class MediaSessionManager(musicPlayService: MusicPlayService) {
    private var musicPlayService: MusicPlayService? = musicPlayService
    private var mMediaSession: MediaSessionCompat? = null
    private var stateBuilder: PlaybackStateCompat.Builder? = null

    companion object{
        private const val MY_MEDIA_ROOT_ID = "MediaSessionManager"
    }

    init {
        initSession()
    }

    private fun initSession() {
        try {
            mMediaSession = MediaSessionCompat(musicPlayService, MY_MEDIA_ROOT_ID)
            mMediaSession?.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
            mMediaSession?.setPlaybackState(stateBuilder!!.build())
            mMediaSession?.setCallback(sessionCb)
            mMediaSession?.isActive = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updatePlaybackState(currentState: Int) {
        /*val state =
            if (currentState == MusicPlayService.PLAY_STATE_PAUSED) PlaybackStateCompat.STATE_PAUSED else PlaybackStateCompat.STATE_PLAYING
        stateBuilder?.setState(state, musicPlayService.mMediaPlyer.getCurrentPosition(), 1.0f)
        mMediaSession?.setPlaybackState(stateBuilder!!.build())*/
    }

    fun updateLocMsg() {
        /*try {
            //同步歌曲信息
            val md = MediaMetadataCompat.Builder()
            md.putString(
                MediaMetadataCompat.METADATA_KEY_TITLE,
                MusicUtil.getInstance().getCurrPlayMusicInfo().getName()
            )
            md.putString(
                MediaMetadataCompat.METADATA_KEY_ARTIST,
                MusicUtil.getInstance().getCurrPlayMusicInfo().getAuthor()
            )
            md.putString(
                MediaMetadataCompat.METADATA_KEY_ALBUM,
                MusicUtil.getInstance().getCurrPlayMusicInfo().getAlbum()
            )
            md.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                MusicUtil.getInstance().getCurrPlayMusicInfo().getDuration()
            )
            mMediaSession!!.setMetadata(md.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    private val sessionCb: MediaSessionCompat.Callback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            //musicPlayService.handleStartPlay()
        }

        override fun onPause() {
            super.onPause()
            //musicPlayService.handlePausePlay()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            //musicPlayService.handleNextPlay()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            //musicPlayService.handlePrePlay()
        }
    }

    fun release() {
        mMediaSession?.setCallback(null)
        mMediaSession?.isActive = false
        mMediaSession?.release()
    }
}