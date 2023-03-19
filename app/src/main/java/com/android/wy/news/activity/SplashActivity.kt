package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.android.wy.news.databinding.ActivitySplashBinding
import com.android.wy.news.common.CommonTools
import com.android.wy.news.view.LoadingView
import com.android.wy.news.viewmodel.SplashViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {
    private lateinit var loadingView: LoadingView

    override fun initView() {
        loadingView = mBinding.loadingView
        loadingView.startLoadingAnim()
    }

    override fun initData() {
        mViewModel.init()
        Handler(Looper.getMainLooper()).postDelayed({
            loadingView.stopLoadingAnim()
            val intent = Intent(mActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500)
    }

    override fun initEvent() {

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

    }
}