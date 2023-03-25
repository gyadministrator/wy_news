package com.android.wy.news.fragment

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.activity.WebActivity
import com.android.wy.news.adapter.TopAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.databinding.FragmentTabTopBinding
import com.android.wy.news.databinding.LayoutTopCityItemBinding
import com.android.wy.news.entity.Ad
import com.android.wy.news.entity.House
import com.android.wy.news.entity.TopEntity
import com.android.wy.news.viewmodel.TopViewModel
import com.bumptech.glide.Glide
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

class TopTabFragment : BaseFragment<FragmentTabTopBinding, TopViewModel>(), OnRefreshListener,
    OnLoadMoreListener, TopAdapter.OnTopListener {
    private var pageStart = 0
    private lateinit var rvContent: RecyclerView
    private lateinit var shimmerRecyclerView: ShimmerRecyclerView
    private var isRefresh = false
    private var isLoading = false
    private lateinit var topAdapter: TopAdapter
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var llContent: LinearLayout

    companion object {
        fun newInstance() = TopTabFragment()
    }

    override fun initView() {
        shimmerRecyclerView = mBinding.shimmerRecyclerView
        shimmerRecyclerView.showShimmerAdapter()
        rvContent = mBinding.rvContent
        llContent = mBinding.llContent
        refreshLayout = mBinding.refreshLayout
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)
    }

    override fun onPause() {
        super.onPause()
        topAdapter.onPause()
    }

    override fun initData() {
        topAdapter = TopAdapter(mActivity, this)
        rvContent.layoutManager = LinearLayoutManager(mActivity)
        rvContent.adapter = topAdapter
    }

    override fun initEvent() {
        getTopData()
        getCityData()
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    topAdapter.onBackPressed()
                }
            })
    }

    private fun getCityData() {
        mViewModel.getCityNews(Constants.currentCity)
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
            }
            if (isLoading) {
                refreshLayout.finishLoadMore()
            }
            if (it.size == 0) {
                if (isLoading) {
                    refreshLayout.setNoMoreData(true)
                }
            } else {
                if (isRefresh) {
                    topAdapter.refreshData(it)
                } else {
                    topAdapter.loadMoreData(it)
                }
                addBannerHeader(it[0])
            }
            shimmerRecyclerView.hideShimmerAdapter()
            isRefresh = false
            isLoading = false
        }

        mViewModel.msg.observe(this) {
            Toast.makeText(mActivity, it, Toast.LENGTH_SHORT).show()
            if (isRefresh) {
                refreshLayout.setNoMoreData(false)
                refreshLayout.finishRefresh()
            }
            if (isLoading) {
                refreshLayout.finishLoadMore()
            }
        }

        mViewModel.cityNewsList.observe(this) {
            addCityNewsHeader(it)
        }
    }

    private fun addCityNewsHeader(it: ArrayList<House>?) {
        if (it != null && it.size > 0) {
            llContent.removeAllViews()
            for (i in 0 until it.size) {
                val cityItemBinding = LayoutTopCityItemBinding.inflate(layoutInflater)
                val tvTitle = cityItemBinding.tvTitle
                val tvSource = cityItemBinding.tvSource
                val tvTime = cityItemBinding.tvTime

                val house = it[i]
                tvTitle.text = house.title
                val time = CommonTools.parseTime(house.ptime)
                if (TextUtils.isEmpty(time)) {
                    tvTime.text = house.ptime
                } else {
                    tvTime.text = time
                }
                tvSource.text = house.source

                val root = cityItemBinding.root
                val parent = root.parent
                if (parent != null && parent is ViewGroup) {
                    parent.removeView(root)
                }
                llContent.tag = house
                llContent.setOnClickListener(CityNewsClickListener())
                llContent.addView(root)
            }
        }
    }

    private class CityNewsClickListener : View.OnClickListener {
        override fun onClick(p0: View?) {
            if (p0 != null) {
                val house = p0.tag as House
                WebActivity.startActivity(p0.context, house.link)
            }
        }

    }

    private fun addBannerHeader(topEntity: TopEntity) {
        val banner = mBinding.banner
        val titleList = ArrayList<String>()
        val ads = topEntity.ads
        if (ads != null && ads.isNotEmpty()) {
            for (i in ads.indices) {
                val ad = ads[i]
                titleList.add(ad.title)
            }
            banner.setAdapter(object : BannerImageAdapter<Ad?>(ads) {

                override fun onBindView(
                    holder: BannerImageHolder?, data: Ad?, position: Int, size: Int
                ) {
                    if (holder != null && data != null) {
                        //图片加载自己实现
                        Glide.with(holder.itemView).load(data.imgsrc).into(holder.imageView)
                    }
                }
            })
                //.addBannerLifecycleObserver(mActivity!!) //添加生命周期观察者
                .setIndicator(CircleIndicator(mActivity)).setBannerGalleryEffect(10, 10)
                .setIndicatorHeight(10)
            //.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)//设置页码与标题
            //.setBannerTitles(titleList)
        }
    }


    private fun getTopData() {
        mViewModel.getTopNews(pageStart)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        isRefresh = true
        pageStart = 0
        getTopData()
        getCityData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        isLoading = true
        pageStart += 10
        getTopData()
    }

    override fun onTopItemClickListener(view: View, topEntity: TopEntity) {
        val url = Constants.WEB_URL + topEntity.docid + ".html"
        WebActivity.startActivity(mActivity, url)
    }

}