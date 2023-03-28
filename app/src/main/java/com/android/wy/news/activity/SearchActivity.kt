package com.android.wy.news.activity

import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivitySearchBinding
import com.android.wy.news.viewmodel.SearchViewModel

class SearchActivity : BaseActivity<ActivitySearchBinding, SearchViewModel>() {
    override fun setDefaultImmersionBar(): Boolean {
        return true
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivitySearchBinding {
        return ActivitySearchBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): SearchViewModel {
        return CommonTools.getViewModel(this, SearchViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }
}