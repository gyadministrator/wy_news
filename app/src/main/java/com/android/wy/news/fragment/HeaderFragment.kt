package com.android.wy.news.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.android.wy.news.activity.WebActivity
import com.android.wy.news.adapter.HeaderAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.FragmentNewsHeaderBinding
import com.android.wy.news.entity.NewsHeaderEntity
import com.android.wy.news.viewmodel.NewsHeaderViewModel
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.jcodecraeer.xrecyclerview.ProgressStyle
import com.jcodecraeer.xrecyclerview.XRecyclerView

class HeaderFragment : BaseFragment<FragmentNewsHeaderBinding, NewsHeaderViewModel>(),
    HeaderAdapter.OnNewsListener, XRecyclerView.LoadingListener {
    private var pageStart = 0
    private lateinit var rvContent: XRecyclerView
    private lateinit var shimmerRecyclerView: ShimmerRecyclerView
    private var isRefresh = false
    private var isLoading = false
    private lateinit var newsHeaderAdapter: HeaderAdapter

    companion object {
        fun newInstance() = HeaderFragment()
    }

    override fun initView() {
        shimmerRecyclerView = mBinding.shimmerRecyclerView
        shimmerRecyclerView.showShimmerAdapter()
        rvContent = mBinding.rvContent
        rvContent.defaultFootView.setNoMoreHint("没有更多数据了")
        rvContent.defaultFootView.setLoadingHint("正在加载...")
        rvContent.defaultRefreshHeaderView.setRefreshTimeVisible(true)
        rvContent.setLimitNumberToCallLoadMore(2)
        rvContent.setRefreshProgressStyle(ProgressStyle.BallPulse)
        rvContent.setLoadingMoreProgressStyle(ProgressStyle.BallPulse)
        (rvContent.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        (rvContent.itemAnimator as SimpleItemAnimator).changeDuration = 0
        val recycledViewPool = RecyclerView.RecycledViewPool()
        recycledViewPool.setMaxRecycledViews(0, 10)
        rvContent.setRecycledViewPool(recycledViewPool)
    }

    override fun initData() {
        newsHeaderAdapter = HeaderAdapter(mActivity, this)
        rvContent.layoutManager = LinearLayoutManager(mActivity)
        rvContent.adapter = newsHeaderAdapter
        rvContent.setLoadingListener(this)
    }

    override fun initEvent() {
        getHeaderData()
    }

    override fun getViewBinding(): FragmentNewsHeaderBinding {
        return FragmentNewsHeaderBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): NewsHeaderViewModel {
        return CommonTools.getViewModel(this, NewsHeaderViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {
        mViewModel.headDataList.observe(this) {
            shimmerRecyclerView.hideShimmerAdapter()
            if (isRefresh) {
                rvContent.refreshComplete()
            }
            if (isLoading) {
                rvContent.loadMoreComplete()
            }
            if (it.size == 0) {
                if (isLoading) {
                    rvContent.setNoMore(true)
                }
            } else {
                if (isRefresh) {
                    newsHeaderAdapter.refreshData(it)
                } else {
                    newsHeaderAdapter.loadMoreData(it)
                }
            }
            isRefresh = false
            isLoading = false
        }
    }

    override fun onNewsItemClickListener(view: View, newsEntity: NewsHeaderEntity) {
        WebActivity.startActivity(mActivity, newsEntity.docid)
    }

    override fun onRefresh() {
        isRefresh = true
        rvContent.setNoMore(false)
        pageStart = 0
        getHeaderData()
    }

    private fun getHeaderData() {
        mViewModel.getHeaderNews(pageStart)
    }

    override fun onLoadMore() {
        isLoading = true
        pageStart += 10
        getHeaderData()
    }

    override fun onDestroy() {
        super.onDestroy()
        rvContent.destroy()
    }

}