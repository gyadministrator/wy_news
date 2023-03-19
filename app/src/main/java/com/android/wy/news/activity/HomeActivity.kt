package com.android.wy.news.activity

import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.tablib.adapter.FragmentPageAdapter
import com.android.tablib.view.CustomTabLayout
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityHomeBinding
import com.android.wy.news.common.Constants
import com.android.wy.news.fragment.NewsFragment
import com.android.wy.news.viewmodel.HomeViewModel

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>() {
    private lateinit var tabLayout: CustomTabLayout
    private lateinit var viewPager: ViewPager

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
                FragmentPageAdapter(supportFragmentManager, fragments, mTitles.toTypedArray())
            tabLayout.setupWithViewPager(viewPager)
            tabLayout.initLayout()
        }
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): HomeViewModel {
        return CommonTools.getViewModel(this, HomeViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}