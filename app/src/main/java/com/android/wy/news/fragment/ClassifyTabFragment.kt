package com.android.wy.news.fragment

import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.tablib.adapter.FragmentPageAdapter
import com.android.tablib.view.CustomTabLayout
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.databinding.FragmentTabClassifyBinding
import com.android.wy.news.viewmodel.ClassifyTabViewModel

class ClassifyTabFragment : BaseFragment<FragmentTabClassifyBinding, ClassifyTabViewModel>() {
    private lateinit var tabLayout: CustomTabLayout
    private lateinit var viewPager: ViewPager

    companion object {
        fun newInstance() = ClassifyTabFragment()
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
            tabLayout.setSelectedTabIndicatorHeight(0)
        }
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): FragmentTabClassifyBinding {
        return FragmentTabClassifyBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): ClassifyTabViewModel {
        return CommonTools.getViewModel(this, ClassifyTabViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}