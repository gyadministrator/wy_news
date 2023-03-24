package com.android.wy.news.fragment

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.activity.WebActivity
import com.android.wy.news.adapter.TopAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.databinding.FragmentTabTopBinding
import com.android.wy.news.entity.Ad
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
    private lateinit var newsHeaderAdapter: TopAdapter
    private lateinit var refreshLayout: SmartRefreshLayout

    companion object {
        fun newInstance() = TopTabFragment()
    }

    override fun initView() {
        shimmerRecyclerView = mBinding.shimmerRecyclerView
        shimmerRecyclerView.showShimmerAdapter()
        rvContent = mBinding.rvContent
        refreshLayout = mBinding.refreshLayout
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)
    }

    override fun initData() {
        newsHeaderAdapter = TopAdapter(mActivity, this)
        rvContent.layoutManager = LinearLayoutManager(mActivity)
        rvContent.adapter = newsHeaderAdapter
    }

    override fun initEvent() {
        getHeaderData()
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
        mViewModel.headDataList.observe(this) {
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
                    newsHeaderAdapter.refreshData(it)
                } else {
                    newsHeaderAdapter.loadMoreData(it)
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


    private fun getHeaderData() {
        mViewModel.getHeaderNews(pageStart)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        isRefresh = true
        pageStart = 0
        getHeaderData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        isLoading = true
        pageStart += 10
        getHeaderData()
    }

    override fun onTopItemClickListener(view: View, topEntity: TopEntity) {
        val url = Constants.WEB_URL + topEntity.docid + ".html"
        WebActivity.startActivity(mActivity, url)
    }

}