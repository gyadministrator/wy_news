package com.android.wy.news.view

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import tv.danmaku.ijk.media.player.IMediaPlayer

class NewsVideoPlayer : JCVideoPlayerStandard {
    private var onVideoListener: OnVideoListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    fun addVideoListener(onVideoListener: OnVideoListener) {
        this.onVideoListener = onVideoListener
    }

    override fun setUiWitStateAndScreen(state: Int) {
        super.setUiWitStateAndScreen(state)
        if (currentState == CURRENT_STATE_NORMAL) {
            startButton.visibility = View.GONE
        }
    }

    override fun onCompletion() {
        super.onCompletion()
    }

    override fun onAutoCompletion() {
        super.onAutoCompletion()
        onVideoListener?.onVideoFinish()
    }

    override fun onBufferingUpdate(percent: Int) {
        super.onBufferingUpdate(percent)
        onVideoListener?.onPercent(percent)
    }

    override fun onSeekComplete() {
        super.onSeekComplete()
    }

    override fun onError(what: Int, extra: Int) {
        super.onError(what, extra)
    }

    override fun onInfo(what: Int, extra: Int) {
        super.onInfo(what, extra)
        if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            onVideoListener?.onBuffStart()
        } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            onVideoListener?.onBuffEnd()
        }
    }

    override fun onVideoSizeChanged() {
        super.onVideoSizeChanged()
    }

    override fun goBackThisListener() {
        super.goBackThisListener()
    }

    override fun backToOtherListener(): Boolean {
        return super.backToOtherListener()
    }

    override fun onScrollChange() {
        super.onScrollChange()
    }

    override fun getScreenType(): Int {
        return super.getScreenType()
    }

    override fun getUrl(): String {
        return super.getUrl()
    }

    override fun getState(): Int {
        return super.getState()
    }

    override fun autoFullscreen(x: Float) {
        super.autoFullscreen(x)
    }

    override fun autoQuitFullscreen() {
        super.autoQuitFullscreen()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        super.onProgressChanged(seekBar, progress, fromUser)
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        super.onSurfaceTextureAvailable(surface, width, height)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        super.onSurfaceTextureSizeChanged(surface, width, height)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return super.onSurfaceTextureDestroyed(surface)
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        super.onSurfaceTextureUpdated(surface)
    }

    override fun setUp(
        url: String?,
        screen: Int,
        mapHeadData: MutableMap<String, String>?,
        vararg objects: Any?
    ): Boolean {
        return super.setUp(url, screen, mapHeadData, *objects)
    }

    override fun prepareVideo() {
        super.prepareVideo()
        //清除界面UI
        changeUiToPlayingClear()
    }

    override fun addTextureView() {
        super.addTextureView()
    }

    override fun startProgressTimer() {
        super.startProgressTimer()
    }

    override fun cancelProgressTimer() {
        super.cancelProgressTimer()
    }

    override fun clearFullscreenLayout() {
        super.clearFullscreenLayout()
    }

    override fun startWindowFullscreen() {
        super.startWindowFullscreen()
    }

    override fun startWindowTiny() {
        super.startWindowTiny()
    }

    override fun getCurrentPositionWhenPlaying(): Int {
        return super.getCurrentPositionWhenPlaying()
    }

    override fun getDuration(): Int {
        return super.getDuration()
    }

    override fun setTextAndProgress(secProgress: Int) {
        super.setTextAndProgress(secProgress)
    }

    override fun release() {
        super.release()
    }

    override fun isCurrentMediaListener(): Boolean {
        return super.isCurrentMediaListener()
    }

    override fun isCurrenPlayingUrl(): Boolean {
        return super.isCurrenPlayingUrl()
    }

    override fun onEvent(type: Int) {
        super.onEvent(type)
    }

    override fun refreshCache() {
        super.refreshCache()
    }

    override fun clearCacheImage() {
        super.clearCacheImage()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        //拦截触摸事件
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //拦截触摸事件
        return true
    }

    interface OnVideoListener {
        fun onVideoFinish()
        fun onBuffStart()
        fun onBuffEnd()
        fun onPercent(percent: Int)
    }
}