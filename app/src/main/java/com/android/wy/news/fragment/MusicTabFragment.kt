package com.android.wy.news.fragment

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.databinding.FragmentTabMusicBinding
import com.android.wy.news.view.PlayBarView
import com.android.wy.news.view.TabViewPager
import com.android.wy.news.viewmodel.MusicTabViewModel

class MusicTabFragment : BaseFragment<FragmentTabMusicBinding, MusicTabViewModel>() {
    private lateinit var tabViewPager: TabViewPager
    private lateinit var playBarView: PlayBarView

    companion object {
        fun newInstance() = MusicTabFragment()
    }

    override fun initView() {
        tabViewPager = mBinding.tabViewPager
        playBarView = mBinding.playBarView
    }

    fun getPlayBarView(): PlayBarView {
        return playBarView
    }

    override fun initData() {
        val titleList = GlobalData.mMusicTitleList
        val fragments = ArrayList<Fragment>()
        val mTitles = arrayListOf<String>()
        if (titleList.size > 0) {
            for (i in titleList.indices) {
                val titleEntity = titleList[i]
                if (titleEntity.show) {
                    val id = titleEntity.id
                    mTitles.add(titleEntity.title)
                    val fragment = MusicFragment.newInstance(id)
                    fragments.add(fragment)
                }
            }
            tabViewPager.initViewPager(lifecycle, childFragmentManager, fragments, mTitles)
        }
    }

    override fun initEvent() {
        if (activity is AppCompatActivity) {
            playBarView.register(activity as AppCompatActivity)
        }
    }

    override fun getViewBinding(): FragmentTabMusicBinding {
        return FragmentTabMusicBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): MusicTabViewModel {
        return CommonTools.getViewModel(this, MusicTabViewModel::class.java)
    }

    override fun onClear() {
        playBarView.unRegister()
    }

    override fun onNotifyDataChanged() {

    }

}