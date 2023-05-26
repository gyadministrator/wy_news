package com.android.wy.news.fragment

import android.widget.LinearLayout
import com.android.wy.news.activity.SettingActivity
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.FragmentTabMineBinding
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.viewmodel.MineTabViewModel

class MineTabFragment : BaseFragment<FragmentTabMineBinding, MineTabViewModel>() {
    private lateinit var llSetting: LinearLayout
    private lateinit var llLive: LinearLayout
    private lateinit var llLocal: LinearLayout

    companion object {
        fun newInstance() = MineTabFragment()
    }

    override fun initView() {
        llSetting = mBinding.llSetting
        llLive = mBinding.llLive
        llLocal = mBinding.llLocal
    }

    override fun initData() {

    }

    override fun initEvent() {
        llSetting.setOnClickListener {
            SettingActivity.startSettingActivity(mActivity)
        }
        llLive.setOnClickListener {

        }
        llLocal.setOnClickListener {
            RouteManager.go(RouteManager.PATH_ACTIVITY_MUSIC_DOWNLOAD)
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