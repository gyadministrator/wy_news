package com.android.wy.news.fragment

import com.android.wy.news.common.CommonTools
import com.android.wy.news.viewmodel.LiveViewModel
import com.android.wy.news.databinding.FragmentLiveBinding

class LiveFragment : BaseFragment<FragmentLiveBinding, LiveViewModel>() {

    companion object {
        fun newInstance() = LiveFragment()
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    override fun getViewBinding(): FragmentLiveBinding {
        return FragmentLiveBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): LiveViewModel {
        return CommonTools.getViewModel(this, LiveViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}