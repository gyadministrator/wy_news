package com.android.wy.news.fragment

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.MapsInitializer
import com.android.wy.news.R
import com.android.wy.news.activity.HomeActivity
import com.android.wy.news.activity.WebActivity
import com.android.wy.news.adapter.BannerImgAdapter
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.TopAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.FragmentTabTopBinding
import com.android.wy.news.databinding.LayoutTopCityItemBinding
import com.android.wy.news.entity.HotNewsEntity
import com.android.wy.news.entity.House
import com.android.wy.news.entity.TopEntity
import com.android.wy.news.location.LocationHelper
import com.android.wy.news.location.OnLocationListener
import com.android.wy.news.notification.NotificationHelper
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.util.ToastUtil
import com.android.wy.news.view.CustomLoadingView
import com.android.wy.news.viewmodel.TopViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.youth.banner.config.IndicatorConfig.Direction
import com.youth.banner.listener.OnBannerListener

class TopTabFragment : BaseFragment<FragmentTabTopBinding, TopViewModel>(), OnRefreshListener,
    OnLoadMoreListener, BaseNewsAdapter.OnItemAdapterListener<TopEntity> {
    private var pageStart = 0
    private lateinit var rvContent: RecyclerView
    private var isRefresh = false
    private var isLoading = false
    private var isNotify = false
    private lateinit var topAdapter: TopAdapter
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var llContent: LinearLayout
    private lateinit var loadingView: CustomLoadingView
    private var currentCity: String = ""

    companion object {
        fun newInstance() = TopTabFragment()
    }

    override fun initView() {
        loadingView = mBinding.loadingView
        rvContent = mBinding.rvContent
        llContent = mBinding.llContent
        refreshLayout = mBinding.refreshLayout
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)
        refreshLayout.setEnableFooterFollowWhenNoMoreData(true)
    }

    override fun initData() {
        topAdapter = TopAdapter(this)
        rvContent.layoutManager = LinearLayoutManager(mActivity)
        rvContent.adapter = topAdapter
    }

    override fun initEvent() {
        initLocation()
        getTopData()
        getBannerData()
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
                    getCityData()
                }

                override fun error(msg: String) {
                    ToastUtil.show(msg)
                }

            })
        }, 500)
    }

    private fun getBannerData() {
        mViewModel.getTopNewsData()
    }

    private fun getCityData() {
        mViewModel.getCityNews(currentCity)
    }

    override fun getViewBinding(): FragmentTabTopBinding {
        return FragmentTabTopBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): TopViewModel {
        return CommonTools.getViewModel(this, TopViewModel::class.java)
    }

    override fun onClear() {
    }

    override fun onNotifyDataChanged() {
        mViewModel.topNewsList.observe(this) {
            if (isRefresh) {
                refreshLayout.setNoMoreData(false)
                refreshLayout.finishRefresh()
                refreshLayout.setEnableLoadMore(true)
            }
            if (isLoading) {
                refreshLayout.finishLoadMore()
            }
            if (it.size == 0) {
                if (isLoading) {
                    refreshLayout.setNoMoreData(true)
                }
            } else {
                rvContent.visibility = View.VISIBLE
                loadingView.visibility = View.GONE
                if (isRefresh) {
                    topAdapter.refreshData(it)
                } else {
                    topAdapter.loadMoreData(it)
                }
            }
            isRefresh = false
            isLoading = false
        }

        mViewModel.msg.observe(this) {
            ToastUtil.show(it)
            refreshLayout.setEnableLoadMore(false)
            if (isRefresh) {
                refreshLayout.setNoMoreData(false)
                refreshLayout.finishRefresh()
            }
            if (isLoading) {
                refreshLayout.finishLoadMore()
            }
        }


        mViewModel.cityNewsList.observe(this) {
            loadingView.visibility = View.GONE
            addBannerHeader(it)
            if (it.size > 0) {
                if (mActivity is HomeActivity) {
                    val homeActivity = mActivity as HomeActivity
                    homeActivity.setMessageNum(0, it.size)
                }
            }
            TaskUtil.runOnThread {
                var noticeStatus = SpTools.getBoolean(GlobalData.SpKey.NOTICE_STATUS)
                if (noticeStatus == null) noticeStatus = false
                if (!isNotify && !noticeStatus) {
                    sendNotify(it)
                    isNotify = true
                }
            }
        }

        mViewModel.hotNewsList.observe(this) {
            if (it.size > 0) {
                loadingView.visibility = View.GONE
                addCityNewsHeader(it)
            }
        }

        GlobalData.doubleClickChange.observe(this) {
            if (it == 0) {
                refreshLayout.autoRefresh()
            }
        }

        GlobalData.cityChange.observe(this) {
            currentCity = it
            getCityData()
        }
    }

    private fun sendNotify(it: ArrayList<House>?) {
        if (it != null && it.size > 0) {
            NotificationHelper.cancelAll()
            for (i in it.indices) {
                val house = it[i]
                if (!mActivity.isFinishing) {
                    NotificationHelper.sendCustomNotification(mActivity, house)
                }
            }
        }
    }

    private fun addCityNewsHeader(it: ArrayList<HotNewsEntity>?) {
        if (it != null && it.size > 0) {
            llContent.removeAllViews()
            for (i in 0 until it.size) {
                val cityItemBinding = LayoutTopCityItemBinding.inflate(layoutInflater)
                val tvTitle = cityItemBinding.tvTitle
                val tvSource = cityItemBinding.tvSource
                val tvComment = cityItemBinding.tvComment

                val hotNewsEntity = it[i]
                tvTitle.text = hotNewsEntity.title
                val comment = hotNewsEntity.comment
                if (!TextUtils.isEmpty(comment)) {
                    if (comment.contains("跟贴")) {
                        val s = comment.replace("跟贴", "评论")
                        tvComment.text = s
                    }
                } else {
                    tvComment.text = comment
                }
                val s = tvComment.text.toString()
                if (!TextUtils.isEmpty(s)) {
                    tvComment.visibility = View.VISIBLE
                }
                tvSource.text = hotNewsEntity.source

                val root = cityItemBinding.root
                val parent = root.parent
                if (parent != null && parent is ViewGroup) {
                    parent.removeView(root)
                }
                root.tag = hotNewsEntity
                root.setOnClickListener(CityNewsClickListener())
                llContent.addView(root)
            }
        }
    }

    private class CityNewsClickListener : View.OnClickListener {
        override fun onClick(p0: View?) {
            if (p0 != null) {
                val hotNewsEntity = p0.tag as HotNewsEntity
                WebActivity.startActivity(p0.context, hotNewsEntity.link)
            }
        }

    }


    private fun addBannerHeader(it: ArrayList<House>?) {
        val banner = mBinding.banner
        banner.visibility = View.VISIBLE
        val titleList = ArrayList<String>()

        if (it != null && it.size > 0) {
            for (i in it.indices) {
                val house = it[i]
                titleList.add(house.title)
            }
            banner.setAdapter(BannerImgAdapter(it)).addBannerLifecycleObserver(this) //添加生命周期观察者
                //.setIndicator(CircleIndicator(mActivity))
                .setBannerGalleryEffect(10, 10).setIndicatorHeight(20).setIndicatorHeight(20)
                .setIndicatorNormalColorRes(R.color.text_normal_color)
                .setIndicatorSelectedColorRes(R.color.text_select_color).setIndicatorSpace(15)
                .setIndicatorGravity(Direction.CENTER).setOnBannerListener(bannerItemListener)
        }
    }

    private val bannerItemListener = OnBannerListener<House> { data, _ ->
        val url = GlobalConstant.WEB_URL + data.docid + ".html"
        WebActivity.startActivity(mActivity, url)
    }


    private fun getTopData() {
        mViewModel.getTopNews(pageStart)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refresh()
    }

    private fun refresh() {
        isRefresh = true
        pageStart = 0
        getTopData()
        getCityData()
        getBannerData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        isLoading = true
        pageStart += 10
        getTopData()
    }

    override fun onItemClickListener(view: View, data: TopEntity) {
        if (data.videoinfo == null) {
            val url = GlobalConstant.WEB_URL + data.docid + ".html"
            WebActivity.startActivity(mActivity, url)
        }
    }

}