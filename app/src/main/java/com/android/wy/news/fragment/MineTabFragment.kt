package com.android.wy.news.fragment

import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.android.wy.news.activity.SettingActivity
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.FragmentTabMineBinding
import com.android.wy.news.viewmodel.MineTabViewModel

class MineTabFragment : BaseFragment<FragmentTabMineBinding, MineTabViewModel>() {
    private lateinit var llSetting: LinearLayout
    private lateinit var llLive: LinearLayout

    companion object {
        fun newInstance() = MineTabFragment()
    }

    override fun initView() {
        llSetting = mBinding.llSetting
        llLive = mBinding.llLive
    }

    override fun initData() {

    }

    override fun initEvent() {
        llSetting.setOnClickListener {
            SettingActivity.startSettingActivity(mActivity)
        }
        llLive.setOnClickListener {

        }
    }

    override fun getViewBinding(): FragmentTabMineBinding {
        return FragmentTabMineBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): MineTabViewModel {
        return CommonTools.getViewModel(this, MineTabViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }
}