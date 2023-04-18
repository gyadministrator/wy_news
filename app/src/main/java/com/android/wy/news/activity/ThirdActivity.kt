package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityThirdBinding
import com.android.wy.news.viewmodel.ThirdViewModel

class ThirdActivity : BaseActivity<ActivityThirdBinding, ThirdViewModel>() {
    private lateinit var tvGaoDeLink: TextView

    companion object {
        fun startThirdActivity(context: Context) {
            val intent = Intent(context, ThirdActivity::class.java)
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
        tvGaoDeLink = mBinding.tvGaodeLink
    }

    override fun initData() {

    }

    override fun initEvent() {
        tvGaoDeLink.setOnClickListener {
            WebActivity.startActivity(this, tvGaoDeLink.text.toString())
        }
    }

    override fun getViewBinding(): ActivityThirdBinding {
        return ActivityThirdBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): ThirdViewModel {
        return CommonTools.getViewModel(this, ThirdViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}