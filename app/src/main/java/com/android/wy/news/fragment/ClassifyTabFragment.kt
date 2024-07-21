package com.android.wy.news.fragment

import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.MapsInitializer
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.databinding.FragmentTabClassifyBinding
import com.android.wy.news.location.LocationHelper
import com.android.wy.news.location.OnLocationListener
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.util.ToastUtil
import com.android.wy.news.view.TabViewPager
import com.android.wy.news.viewmodel.ClassifyTabViewModel

class ClassifyTabFragment : BaseFragment<FragmentTabClassifyBinding, ClassifyTabViewModel>() {
    private lateinit var tabViewPager: TabViewPager
    private var currentCity: String = ""

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
        initLocation()
    }

    private fun initLocation() {
        //防止深色模式切换后，activity重启没有设置Key
        AMapLocationClient.setApiKey(GlobalData.LOCATION_KEY)
        MapsInitializer.updatePrivacyShow(mActivity, true, true)
        MapsInitializer.updatePrivacyAgree(mActivity, true)
        TaskUtil.runOnUiThread({
            LocationHelper.startLocation(mActivity, object : OnLocationListener {
                override fun success(aMapLocation: AMapLocation) {
                    currentCity = aMapLocation.city
                    GlobalData.cityChange.postValue(currentCity)
                    //getCityData()
                }

                override fun error(msg: String) {
                    ToastUtil.show(msg)
                }

            })
        }, 500)
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