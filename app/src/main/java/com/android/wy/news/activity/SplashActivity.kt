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
import com.wang.avi.AVLoadingIndicatorView

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {
    private lateinit var avLoading: AVLoadingIndicatorView
    private lateinit var ivAd: ImageView
    private var splashAD: String? = null

    override fun initView() {
        avLoading = mBinding.avLoading
        ivAd = mBinding.ivAd
    }

    override fun initData() {
        mViewModel.init(this)
    }

    override fun initEvent() {
        splashAD = SpTools.get(Constants.SPLASH_AD)
        if (!TextUtils.isEmpty(splashAD)) {
            splashAD?.let { CommonTools.loadImage(mActivity, it, ivAd) }
        } else {
            avLoading.show()
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
                Handler(Looper.getMainLooper()).postDelayed({
                    if (TextUtils.isEmpty(splashAD)) {
                        avLoading.hide()
                    }
                    val intent = Intent(mActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1500)
            }
        }
    }

    override fun setDefaultImmersionBar(): Boolean {
        return true
    }
}