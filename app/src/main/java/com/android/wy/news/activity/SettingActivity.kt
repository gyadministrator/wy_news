package com.android.wy.news.activity

import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivitySettingBinding
import com.android.wy.news.viewmodel.SettingViewModel

class SettingActivity : BaseActivity<ActivitySettingBinding, SettingViewModel>() {
    override fun setDefaultImmersionBar(): Boolean {
        return true
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): SettingViewModel {
        return CommonTools.getViewModel(this, SettingViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}