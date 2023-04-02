package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.ImageView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.ActivitySplashBinding
import com.android.wy.news.viewmodel.SplashViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {
    private lateinit var ivAd: ImageView
    private var splashAD: String? = null
    private var isShowAD = false
    private var delayTime = 0L

    override fun initView() {
        ivAd = mBinding.ivAd
    }

    override fun initData() {
        mViewModel.init(this)
    }

    override fun initEvent() {
        splashAD = SpTools.get(Constants.SPLASH_AD)
        if (!TextUtils.isEmpty(splashAD)) {
            isShowAD = true
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

    override fun onNotifyDataChanged() {
        mViewModel.isReadFinish.observe(this) {
            if (it) {
                if (isShowAD) {
                    delayTime = 3000
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(mActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }, delayTime)
            }
        }
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