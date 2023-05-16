package com.android.wy.news.fragment

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.tablib.adapter.FragmentPageAdapter
import com.android.tablib.view.CustomTabLayout
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.databinding.FragmentTabMusicBinding
import com.android.wy.news.viewmodel.MusicTabViewModel

class MusicTabFragment : BaseFragment<FragmentTabMusicBinding, MusicTabViewModel>() {
    private lateinit var tabLayout: CustomTabLayout
    private lateinit var viewPager: ViewPager

    companion object {
        fun newInstance() = MusicTabFragment()
    }

    override fun initView() {
        tabLayout = mBinding.tabLayout
        viewPager = mBinding.viewPager
    }

    override fun initData() {
        val titleList = GlobalData.mMusicTitleList
        val fragments = ArrayList<Fragment>()
        val mTitles = arrayListOf<String>()
        if (titleList.size > 0) {
            for (i in titleList.indices) {
                val titleEntity = titleList[i]
                val id = titleEntity.id
                mTitles.add(titleEntity.title)
                val fragment = MusicFragment.newInstance(id)
                fragments.add(fragment)
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

    override fun getViewBinding(): FragmentTabMusicBinding {
        return FragmentTabMusicBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): MusicTabViewModel {
        return CommonTools.getViewModel(this, MusicTabViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}