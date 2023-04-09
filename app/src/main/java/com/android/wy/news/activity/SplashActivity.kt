package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.ActivitySplashBinding
import com.android.wy.news.viewmodel.SplashViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {
    private lateinit var ivAd: ImageView
    private lateinit var rlContent: RelativeLayout
    private lateinit var tvDown: TextView
    private var splashAD: String? = null
    private var isShowAD = false
    private var delayTime = 500L
    private var handlerNum = 3

    override fun initView() {
        ivAd = mBinding.ivAd
        rlContent = mBinding.rlContent
        tvDown = mBinding.tvDown
        tvDown.setOnClickListener {
            stopCountDownHandler()
        }
    }

    override fun initData() {
        mViewModel.init(this)
    }

    override fun initEvent() {
        splashAD = SpTools.getString(Constants.SPLASH_AD)
        if (!TextUtils.isEmpty(splashAD)) {
            isShowAD = true
            rlContent.visibility = View.VISIBLE
            splashAD?.let { CommonTools.loadImage(it, ivAd) }
        }
    }

    override fun getViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): SplashViewModel {
        return CommonTools.getViewModel(this, SplashViewModel::class.java)
    }

    override fun onClear() {
    }

    @SuppressLint("SetTextI18n")
    override fun onNotifyDataChanged() {
        mViewModel.isReadFinish.observe(this) {
            if (it) {
                if (isShowAD) {
                    tvDown.text = handlerNum.toString() + "s"
                    countDownHandler()
                } else {
                    mHandler.postDelayed({
                        jump()
                    }, delayTime)
                }
            }
        }
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> {
                    if (handlerNum > 0) {
                        handlerNum--
                        tvDown.text = handlerNum.toString() + "s"
                        countDownHandler()
                    } else {
                        stopCountDownHandler()
                    }
                }
            }
        }
    }

    private fun jump() {
        val intent = Intent(mActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun countDownHandler() {
        mHandler.sendEmptyMessageDelayed(1, 1000)
    }

    fun stopCountDownHandler() {
        mHandler.removeCallbacksAndMessages(null)
        jump()
    }


    override fun onDestroy() {
        super.onDestroy()
        stopCountDownHandler()
    }

    override fun setDefaultImmersionBar(): Boolean {
        return true
    }

    override fun hideStatusBar(): Boolean {
        return true
    }

    override fun hideNavigationBar(): Boolean {
        return true
    }
}