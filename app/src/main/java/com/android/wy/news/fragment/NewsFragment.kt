package com.android.wy.news.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.activity.WebActivity
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.NewsAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.databinding.FragmentNewsBinding
import com.android.wy.news.entity.NewsEntity
import com.android.wy.news.util.ToastUtil
import com.android.wy.news.viewmodel.NewsViewModel
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener


class NewsFragment : BaseFragment<FragmentNewsBinding, NewsViewModel>(), OnRefreshListener,
    OnLoadMoreListener, BaseNewsAdapter.OnItemAdapterListener<NewsEntity> {
    private var pageStart = 0
    private var tid: String? = null
    private lateinit var rvContent: RecyclerView
    private lateinit var shimmerRecyclerView: ShimmerRecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private var isRefresh = false
    private var isLoading = false
    private lateinit var refreshLayout: SmartRefreshLayout

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
        refreshLayout = mBinding.refreshLayout
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)
        refreshLayout.setEnableFooterFollowWhenNoMoreData(true)
    }

    override fun initData() {
        newsAdapter = NewsAdapter(this)
        rvContent.layoutManager = LinearLayoutManager(mActivity)
        rvContent.adapter = newsAdapter
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
                if (isRefresh) {
                    newsAdapter.refreshData(it)
                } else {
                    newsAdapter.loadMoreData(it)
                }
            }
            shimmerRecyclerView.hideShimmerAdapter()
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
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        isRefresh = true
        pageStart = 0
        getNewsData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        isLoading = true
        pageStart += 10
        getNewsData()
    }

    override fun onItemClickListener(view: View, data: NewsEntity) {
        val url = GlobalConstant.WEB_URL + data.docid + ".html"
        WebActivity.startActivity(mActivity, url)
    }
}