package com.android.wy.news.fragment

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.tablib.adapter.FragmentPageAdapter
import com.android.tablib.view.CustomTabLayout
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.databinding.FragmentTabMusicBinding
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.view.TabViewPager
import com.android.wy.news.viewmodel.MusicTabViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MusicTabFragment : BaseFragment<FragmentTabMusicBinding, MusicTabViewModel>() {
    private lateinit var tabViewPager: TabViewPager

    companion object {
        fun newInstance() = MusicTabFragment()
    }

    override fun initView() {
        tabViewPager = mBinding.tabViewPager
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
            tabViewPager.initData(childFragmentManager, fragments, mTitles)
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