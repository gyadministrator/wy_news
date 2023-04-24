package com.android.wy.news.fragment

import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.tablib.adapter.FragmentPageAdapter
import com.android.tablib.view.CustomTabLayout
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.FragmentTabMusicBinding
import com.android.wy.news.entity.music.MusicCategoryEntity
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.view.CustomLoadingView
import com.android.wy.news.viewmodel.MusicTabViewModel

class MusicTabFragment : BaseFragment<FragmentTabMusicBinding, MusicTabViewModel>() {
    private lateinit var tabLayout: CustomTabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var llContent: LinearLayout
    private lateinit var loadingView: CustomLoadingView
    private val mTitles = arrayListOf<String>()
    private val fragments = ArrayList<Fragment>()

    companion object {
        fun newInstance() = MusicTabFragment()
    }

    override fun initView() {
        tabLayout = mBinding.tabLayout
        viewPager = mBinding.viewPager
        llContent = mBinding.llContent
        loadingView = mBinding.loadingView
    }

    override fun initData() {
        MusicRepository.getMusicCateGory().observe(this) {
            addHeader(it)
        }
    }

    private fun addHeader(it: Result<MusicCategoryEntity>?) {
        if (it != null) {
            val musicCategoryEntity = it.getOrNull()
            val musicCateGoryDataList = musicCategoryEntity?.data
            if (musicCateGoryDataList != null && musicCateGoryDataList.size > 1) {
                val musicCateGoryData = musicCateGoryDataList[1]
                val subCate = musicCateGoryData.subCate
                if (subCate.isNotEmpty()) {
                    for (i in subCate.indices) {
                        val subCateGory = subCate[i]
                        val categoryName = subCateGory.categoryName
                        val id = subCateGory.id
                        mTitles.add(categoryName)
                        fragments.add(MusicFragment.newInstance(id))
                    }

                    llContent.visibility = View.VISIBLE
                    loadingView.visibility = View.GONE

                    viewPager.offscreenPageLimit = mTitles.size
                    viewPager.adapter =
                        FragmentPageAdapter(
                            childFragmentManager,
                            fragments,
                            mTitles.toTypedArray()
                        )
                    tabLayout.setupWithViewPager(viewPager)
                    tabLayout.initLayout()
                    viewPager.isSaveEnabled = false
                }
            }
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