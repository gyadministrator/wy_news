package com.android.wy.news.fragment

import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.databinding.FragmentTabClassifyBinding
import com.android.wy.news.view.TabViewPager
import com.android.wy.news.viewmodel.ClassifyTabViewModel

class ClassifyTabFragment : BaseFragment<FragmentTabClassifyBinding, ClassifyTabViewModel>() {
    private lateinit var tabViewPager: TabViewPager

    companion object {
        fun newInstance() = ClassifyTabFragment()
    }

    override fun initView() {
        tabViewPager = mBinding.tabViewPager
    }

    override fun initData() {
        val titleList = GlobalData.mNewsTitleList
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
            tabViewPager.initData(childFragmentManager, fragments, mTitles)
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