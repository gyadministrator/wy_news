package com.android.wy.news.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import cn.jzvd.JzvdStd
import cn.jzvd.R

class CustomVideoPlayer : JzvdStd {
    private var onVideoListener: OnVideoListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initUiState()
    }

    private fun initUiState() {
        fullscreenButton.visibility = View.GONE
        currentTimeTextView.visibility = View.GONE
        totalTimeTextView.visibility = View.GONE
    }

    override fun updateStartImage() {
        //super.updateStartImage()
        when (state) {
            STATE_PLAYING -> {
                //startButton.visibility = VISIBLE
                //startButton.setImageResource(R.drawable.jz_click_pause_selector)
                replayTextView.visibility = GONE
            }

            STATE_ERROR -> {
                startButton.visibility = INVISIBLE
                replayTextView.visibility = GONE
            }

            STATE_AUTO_COMPLETE -> {
                startButton.visibility = VISIBLE
                startButton.setImageResource(R.drawable.jz_click_replay_selector)
                replayTextView.visibility = VISIBLE
            }

            else -> {
                //startButton.setImageResource(R.drawable.jz_click_play_selector)
                //startButton.visibility = VISIBLE
                replayTextView.visibility = GONE
            }
        }
    }

    override fun setAllControlsVisiblity(
        topCon: Int,
        bottomCon: Int,
        startBtn: Int,
        loadingPro: Int,
        posterImg: Int,
        bottomPro: Int,
        retryLayout: Int
    ) {
        super.setAllControlsVisiblity(
            topCon,
            bottomCon,
            startBtn,
            loadingPro,
            posterImg,
            bottomPro,
            retryLayout
        )
        startButton.visibility = GONE
    }

    fun play() {
        startVideo()
    }

    fun addVideoListener(onVideoListener: OnVideoListener) {
        this.onVideoListener = onVideoListener
    }

    override fun onStateAutoComplete() {
        super.onStateAutoComplete()
        onVideoListener?.onVideoFinish()
    }

    override fun showWifiDialog() {
        //super.showWifiDialog()
        Toast.makeText(context, "当前不是WiFi环境下,请注意流量使用", Toast.LENGTH_SHORT).show()
        if (state == STATE_PAUSE) {
            startButton.performClick()
        } else {
            play()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val id = v!!.id
        if (id == R.id.surface_container) {
            onVideoListener?.onPlayState()
        }
        return true
    }

    interface OnVideoListener {
        fun onVideoFinish()
        fun onPlayState()
    }

    override fun getLayoutId(): Int {
        return com.android.wy.news.R.layout.layout_video_player
    }
}