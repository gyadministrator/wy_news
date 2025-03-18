package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.PrivacyStatus
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.ActivitySplashBinding
import com.android.wy.news.location.LocationHelper
import com.android.wy.news.location.OnPrivacyListener
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.permission.PermissionHelper
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.viewmodel.SplashViewModel

@SuppressLint("CustomSplashScreen")
@Route(path = RouteManager.PATH_ACTIVITY_SPLASH)
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(),
    OnPrivacyListener {
    private lateinit var ivAd: ImageView
    private lateinit var rlContent: RelativeLayout

    private var splashAD: String? = null
    private var isShowAD = false
    private var delayTime = 500L
    private var url: String = ""

    override fun initView() {
        ivAd = mBinding.ivAd
        rlContent = mBinding.rlContent
        mBinding.tvDown.setOnClickListener {
            jump()
        }
    }

    override fun initData() {
        PermissionHelper.initPermission(this)
        val i = SpTools.getInt(GlobalData.SpKey.PRIVACY_STATUS)
        if (i == PrivacyStatus.PRIVACY_STATUS_AGREE) {
            handlerAgree()
        } else {
            LocationHelper.privacyCompliance(this, this)
        }
        mViewModel.init(this)
    }

    private fun handlerAgree() {
        loadAD()
    }

    override fun initEvent() {
    }

    private fun loadAD() {
        splashAD = SpTools.getString(GlobalData.SpKey.SPLASH_AD)
        if (!TextUtils.isEmpty(splashAD)) {
            isShowAD = true
            rlContent.visibility = View.VISIBLE
            splashAD?.let { CommonTools.loadImage(it, ivAd) }
        }
    }

    override fun getViewBinding(): ActivitySplashBinding {
        installSplashScreen()
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
            val i = SpTools.getInt(GlobalData.SpKey.PRIVACY_STATUS)
            if (i == PrivacyStatus.PRIVACY_STATUS_AGREE) {
                handlerRead(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handlerRead(it: Boolean?) {
        if (it == true) {
            if (isShowAD) {
                delayTime = 3000L
            }
            TaskUtil.runOnUiThread({
                jump()
            }, delayTime)
        }
    }

    private fun jump() {
        val intent = Intent(mActivity, HomeActivity::class.java)
        intent.putExtra(WebActivity.WEB_URL, url)
        startActivity(intent)
        finish()
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

    override fun isFollowNightMode(): Boolean {
        return false
    }

    override fun onClickAgree() {
        handlerAgree()
        handlerRead(true)
    }
}