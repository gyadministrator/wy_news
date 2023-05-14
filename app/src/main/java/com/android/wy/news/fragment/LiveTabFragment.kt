package com.android.wy.news.fragment

import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.tablib.adapter.FragmentPageAdapter
import com.android.tablib.view.CustomTabLayout
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.databinding.FragmentTabLiveBinding
import com.android.wy.news.view.CustomLoadingView
import com.android.wy.news.viewmodel.LiveTabViewModel

class LiveTabFragment : BaseFragment<FragmentTabLiveBinding, LiveTabViewModel>() {
    private lateinit var tabLayout: CustomTabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var llContent: LinearLayout
    private lateinit var loadingView: CustomLoadingView

    companion object {
        fun newInstance() = LiveTabFragment()
    }

    override fun initView() {
        tabLayout = mBinding.tabLayout
        viewPager = mBinding.viewPager
        llContent = mBinding.llContent
        loadingView = mBinding.loadingView
    }

    override fun initData() {
        val titleList = Constants.mNewsLiveTitleList
        val fragments = ArrayList<Fragment>()
        val mTitles = arrayListOf<String>()
        if (titleList.size > 0) {
            llContent.visibility = View.VISIBLE
            loadingView.visibility = View.GONE
            for (i in titleList.indices) {
                val titleEntity = titleList[i]
                if (titleEntity.visible) {
                    mTitles.add(titleEntity.name)
                    fragments.add(LiveFragment.newInstance(titleEntity.id))
                }
            }
            viewPager.offscreenPageLimit = mTitles.size
            viewPager.adapter =
                FragmentPageAdapter(childFragmentManager, fragments, mTitles.toTypedArray())
            tabLayout.setupWithViewPager(viewPager)
            tabLayout.initLayout()
            viewPager.isSaveEnabled = false
            tabLayout.setSelectedTabIndicatorHeight(0)
        }
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): FragmentTabLiveBinding {
        return FragmentTabLiveBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): LiveTabViewModel {
        return CommonTools.getViewModel(this, LiveTabViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}