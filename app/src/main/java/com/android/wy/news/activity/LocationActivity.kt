package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityLocationBinding
import com.android.wy.news.viewmodel.LocationViewModel

class LocationActivity : BaseActivity<ActivityLocationBinding, LocationViewModel>() {
    companion object {
        fun startLocationActivity(context: Context) {
            val intent = Intent(context, LocationActivity::class.java)
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

    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivityLocationBinding {
        return ActivityLocationBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): LocationViewModel {
        return CommonTools.getViewModel(this, LocationViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}