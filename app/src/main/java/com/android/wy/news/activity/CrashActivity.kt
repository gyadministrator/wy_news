package com.android.wy.news.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.view.View
import android.widget.TextView
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityCrashBinding
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.util.ToastUtil
import com.android.wy.news.viewmodel.CrashViewModel


@Route(path = RouteManager.PATH_ACTIVITY_CRASH)
class CrashActivity : BaseActivity<ActivityCrashBinding, CrashViewModel>() {
    private var tvRestartApp: TextView? = null
    private var tvCloseApp: TextView? = null
    private var tvErrorInfo: TextView? = null


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
        tvRestartApp = mBinding.tvRestart
        tvCloseApp = mBinding.tvClose
        tvErrorInfo = mBinding.tvErrorInfo
    }

    override fun initData() {
        val config = CustomActivityOnCrash.getConfigFromIntent(intent)
        if (config == null) {
            finish()
            return
        }
        if (config.isShowRestartButton && config.restartActivityClass != null) {
            tvRestartApp?.visibility = View.VISIBLE
        } else {
            tvRestartApp?.visibility = View.GONE
        }
        tvRestartApp?.setOnClickListener {
            CustomActivityOnCrash.restartApplication(this, config)
        }
        tvCloseApp?.setOnClickListener {
            CustomActivityOnCrash.closeApplication(this, config)
        }
        if (config.isShowErrorDetails) {
            val errorDetailsFromIntent =
                CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, intent)
            tvErrorInfo?.text = errorDetailsFromIntent
        }
        tvErrorInfo?.setOnLongClickListener {
            copyErrorToClipboard()
            true
        }
    }

    private fun copyErrorToClipboard() {
        val errorDetailsFromIntent =
            CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, intent)
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("error_label", errorDetailsFromIntent)
        clipboard.setPrimaryClip(clip)
        ToastUtil.show("错误日志已复制到剪切板")
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivityCrashBinding {
        return ActivityCrashBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): CrashViewModel {
        return CommonTools.getViewModel(this, CrashViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}