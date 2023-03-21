package com.android.wy.news.fragment

import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.tablib.adapter.FragmentPageAdapter
import com.android.tablib.view.CustomTabLayout
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.viewmodel.ChannelViewModel
import com.android.wy.news.databinding.FragmentChannelBinding

class ChannelFragment : BaseFragment<FragmentChannelBinding, ChannelViewModel>() {
    private lateinit var tabLayout: CustomTabLayout
    private lateinit var viewPager: ViewPager

    companion object {
        fun newInstance() = ChannelFragment()
    }

    override fun initView() {
        tabLayout = mBinding.tabLayout
        viewPager = mBinding.viewPager
    }

    override fun initData() {
        val titleList = Constants.mNewsTitleList
        val fragments = ArrayList<Fragment>()
        val mTitles = arrayListOf<String>()
        if (titleList.size > 0) {
            for (i in titleList.indices) {
                val titleEntity = titleList[i]
                val tid = titleEntity.tid
                mTitles.add(titleEntity.title)
                if (!TextUtils.isEmpty(tid)) {
                    val fragment = NewsFragment.newInstance(tid)
                    fragments.add(fragment)
                }
            }
            viewPager.offscreenPageLimit = mTitles.size
            viewPager.adapter =
                FragmentPageAdapter(childFragmentManager, fragments, mTitles.toTypedArray())
            tabLayout.setupWithViewPager(viewPager)
            tabLayout.initLayout()
            viewPager.isSaveEnabled = false
        }
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): FragmentChannelBinding {
        return FragmentChannelBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): ChannelViewModel {
        return CommonTools.getViewModel(this, ChannelViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}