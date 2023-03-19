package com.android.wy.news.fragment

import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.FragmentNewsHeaderBinding
import com.android.wy.news.viewmodel.NewsHeaderViewModel

class NewsHeaderFragment : BaseFragment<FragmentNewsHeaderBinding, NewsHeaderViewModel>() {

    companion object {
        fun newInstance() = NewsHeaderFragment()
    }

    override fun initView() {

    }

    override fun initData() {
        mViewModel.getHeaderNews(0)
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): FragmentNewsHeaderBinding {
        return FragmentNewsHeaderBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): NewsHeaderViewModel {
        return CommonTools.getViewModel(this, NewsHeaderViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {
    }

}