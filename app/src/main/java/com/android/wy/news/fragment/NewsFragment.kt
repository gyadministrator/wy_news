package com.android.wy.news.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.android.wy.news.activity.WebActivity
import com.android.wy.news.adapter.NewsAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.FragmentNewsBinding
import com.android.wy.news.entity.NewsEntity
import com.android.wy.news.viewmodel.NewsViewModel
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.jcodecraeer.xrecyclerview.ProgressStyle
import com.jcodecraeer.xrecyclerview.XRecyclerView


class NewsFragment : BaseFragment<FragmentNewsBinding, NewsViewModel>(),
    XRecyclerView.LoadingListener, NewsAdapter.OnNewsListener {
    private var pageStart = 0
    private var tid: String? = null
    private lateinit var rvContent: XRecyclerView
    private lateinit var shimmerRecyclerView: ShimmerRecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private var isRefresh = false
    private var isLoading = false

    companion object {
        private const val mKey: String = "news_tid"
        fun newInstance(tid: String): NewsFragment {
            val newsFragment = NewsFragment()
            val bundle = Bundle()
            bundle.putString(mKey, tid)
            newsFragment.arguments = bundle
            return newsFragment
        }
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
        newsAdapter = NewsAdapter(mActivity, this)
        rvContent.layoutManager = LinearLayoutManager(mActivity)
        rvContent.adapter = newsAdapter
        rvContent.setLoadingListener(this)
    }

    override fun initEvent() {
        val arguments = arguments
        if (arguments != null) {
            tid = arguments.getString(mKey, "")
        }
        getNewsData()
    }

    private fun getNewsData() {
        tid.let {
            if (it != null) {
                mViewModel.getNewsList(it, pageStart)
            }
        }
    }

    override fun getViewBinding(): FragmentNewsBinding {
        return FragmentNewsBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): NewsViewModel {
        return CommonTools.getViewModel(this, NewsViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {
        mViewModel.dataList.observe(this) {
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
                    newsAdapter.refreshData(it)
                } else {
                    newsAdapter.loadMoreData(it)
                }
            }
            isRefresh = false
            isLoading = false
        }
    }

    override fun onRefresh() {
        newsAdapter
        isRefresh = true
        rvContent.setNoMore(false)
        pageStart = 0
        getNewsData()
    }

    override fun onLoadMore() {
        isLoading = true
        pageStart += 10
        getNewsData()
    }

    override fun onDestroy() {
        super.onDestroy()
        rvContent.destroy()
    }

    override fun onNewsItemClickListener(view: View, newsEntity: NewsEntity) {
        WebActivity.startActivity(mActivity, newsEntity.url)
    }

    override fun onNewsItemLongClickListener(view: View, newsEntity: NewsEntity) {

    }
}