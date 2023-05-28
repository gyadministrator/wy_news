package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.ActivityRecognitionBinding
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.manager.SpeechRecognizerManager
import com.android.wy.news.util.AppUtil
import com.android.wy.news.viewmodel.RecognitionViewModel


@Route(path = RouteManager.PATH_ACTIVITY_RECOGNITION)
class RecognitionActivity : BaseActivity<ActivityRecognitionBinding, RecognitionViewModel>() {
    private var tvStartListen: TextView? = null
    private var tvListenTimer: TextView? = null
    private var isListen = false
    private var listenTime = 0
    private var animation: Animation? = null
    private var ivListen: ImageView? = null

    companion object {
        private const val MAX_LISTEN_TIME = 20
    }


    override fun setDefaultImmersionBar(): Boolean {
        return true
    }

    override fun hideStatusBar(): Boolean {
        return false
    }

    override fun hideNavigationBar(): Boolean {
        return false
    }

    override fun isFollowNightMode(): Boolean {
        return true
    }

    override fun initView() {
        tvStartListen = mBinding.tvStartListen
        tvListenTimer = mBinding.tvListenTimer
        ivListen = mBinding.ivListen
    }

    override fun initData() {
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_listen)
        startListen()
    }

    fun startAnim() {
        ivListen?.startAnimation(animation)
    }

    fun stopAnim() {
        ivListen?.clearAnimation()
    }

    override fun initEvent() {
        tvStartListen?.setOnClickListener {
            if (isListen) {
                stopListen()
            } else {
                startListen()
            }
        }
    }

    private fun stopListen() {
        stopAnim()
        isListen = false
        tvStartListen?.text = AppUtil.getString(this, R.string.start_listen)
        tvListenTimer?.visibility = View.GONE
        stopCountDownHandler()
        SpeechRecognizerManager.stopListen()
    }

    private fun startListen() {
        val checkListen = SpeechRecognizerManager.checkListen(this)
        if (!checkListen) return
        startAnim()
        isListen = true
        tvStartListen?.text = AppUtil.getString(this, R.string.stop_listen)
        tvListenTimer?.visibility = View.VISIBLE
        countDownHandler()
        SpeechRecognizerManager.startListen(this)
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> {
                    if (listenTime < MAX_LISTEN_TIME) {
                        listenTime++
                        tvListenTimer?.text = "识别中 " + listenTime.toString() + "s"
                        countDownHandler()
                    } else {
                        stopCountDownHandler()
                    }
                }
            }
        }
    }

    fun countDownHandler() {
        mHandler.sendEmptyMessageDelayed(1, 1000)
    }

    private fun stopCountDownHandler() {
        stopAnim()
        mHandler.removeCallbacksAndMessages(null)
        if (listenTime == MAX_LISTEN_TIME) {
            tvListenTimer?.text = "识别超时"
        } else {
            tvListenTimer?.text = ""
        }
        listenTime = 0
        isListen = false
        tvStartListen?.text = AppUtil.getString(this, R.string.start_listen)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCountDownHandler()
        SpeechRecognizerManager.stopListen()
    }

    override fun getViewBinding(): ActivityRecognitionBinding {
        return ActivityRecognitionBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): RecognitionViewModel {
        return CommonTools.getViewModel(this, RecognitionViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {
        MusicRepository.getMusicByKey("123").observe(this) {
            Logger.i("getMusicByKey--->>>$it")
        }
    }

}