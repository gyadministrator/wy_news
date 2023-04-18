package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityAboutBinding
import com.android.wy.news.viewmodel.AboutViewModel

class AboutActivity : BaseActivity<ActivityAboutBinding, AboutViewModel>() {
    private lateinit var tvVersionInfo: TextView

    companion object {
        fun startAboutActivity(context: Context) {
            val intent = Intent(context, AboutActivity::class.java)
            context.startActivity(intent)
        }
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
        tvVersionInfo = mBinding.tvVersionInfo
    }

    override fun initData() {
        getVersion()
    }

    @SuppressLint("SetTextI18n")
    private fun getVersion() {
        val versionName = CommonTools.getVersionName(this)
        val versionCode = CommonTools.getVersionCode(this)
        tvVersionInfo.text = "Build $versionCode" + "_V$versionName"
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivityAboutBinding {
        return ActivityAboutBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): AboutViewModel {
        return CommonTools.getViewModel(this, AboutViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}