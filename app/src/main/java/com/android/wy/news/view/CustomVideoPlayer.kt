package com.android.wy.news.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.jzvd.JzvdStd
import cn.jzvd.R
import com.android.wy.news.common.Constants
import com.android.wy.news.common.SpTools
import com.android.wy.news.dialog.ConfirmDialogFragment

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
        val isNoWifiPlay = SpTools.getBoolean(Constants.NO_WIFI_PLAY)
        val isWifiNotice = SpTools.getBoolean(Constants.IS_WIFI_NOTICE)
        val isWifiNoticeDialog = SpTools.getBoolean(Constants.IS_WIFI_NOTICE_DIALOG)
        if (isNoWifiPlay != null && isNoWifiPlay == true) {
            if (isWifiNotice != null && isWifiNotice == false) {
                Toast.makeText(context, "当前不是WiFi环境下,请注意流量使用", Toast.LENGTH_SHORT)
                    .show()
                SpTools.putBoolean(Constants.IS_WIFI_NOTICE, true)
            }
            if (state == STATE_PAUSE) {
                startButton.performClick()
            } else {
                play()
            }
        } else {
            //弹框提醒
            if (isWifiNoticeDialog != null && isWifiNoticeDialog == false) {
                val dialogFragment = ConfirmDialogFragment.newInstance(
                    "温馨提示",
                    "当前不是WiFi环境下,已为你暂停播放视频,你是否需要继续播放视频？",
                    "确定",
                    "取消"
                )
                val activity = context as AppCompatActivity
                dialogFragment.show(activity.supportFragmentManager, "wy_no_wifi")
                dialogFragment.addListener(object : ConfirmDialogFragment.OnDialogFragmentListener {
                    override fun onClickSure() {
                        SpTools.putBoolean(Constants.NO_WIFI_PLAY, true)
                        Toast.makeText(
                            context,
                            "当前不是WiFi环境下,请注意流量使用",
                            Toast.LENGTH_SHORT
                        ).show()
                        SpTools.putBoolean(Constants.IS_WIFI_NOTICE_DIALOG, true)
                        if (state == STATE_PAUSE) {
                            startButton.performClick()
                        } else {
                            play()
                        }
                    }

                    override fun onClickCancel() {

                    }
                })
            }
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