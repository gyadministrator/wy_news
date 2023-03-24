package com.android.wy.news.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.wy.news.activity.WebActivity
import com.android.wy.news.adapter.LiveAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.databinding.FragmentLiveBinding
import com.android.wy.news.entity.LiveReview
import com.android.wy.news.viewmodel.LiveViewModel
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class LiveFragment : BaseFragment<FragmentLiveBinding, LiveViewModel>(),
    OnRefreshListener, OnLoadMoreListener, LiveAdapter.OnNewsListener {
    private var pageStart = 1
    private var liveId: Int = 0
    private lateinit var rvContent: RecyclerView
    private lateinit var shimmerRecyclerView: ShimmerRecyclerView
    private lateinit var liveAdapter: LiveAdapter
    private var isRefresh = false
    private var isLoading = false
    private lateinit var refreshLayout: SmartRefreshLayout

    companion object {
        private const val mKey: String = "liveID"
        fun newInstance(liveId: Int): LiveFragment {
            val liveContentFragment = LiveFragment()
            val bundle = Bundle()
            bundle.putInt(mKey, liveId)
            liveContentFragment.arguments = bundle
            return liveContentFragment
        }
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
        liveAdapter = LiveAdapter(mActivity, this)
        rvContent.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvContent.adapter = liveAdapter
    }

    override fun initEvent() {
        val arguments = arguments
        if (arguments != null) {
            liveId = arguments.getInt(mKey)
        }
        getLiveList()
    }

    private fun getLiveList() {
        mViewModel.getLiveContentList(liveId, pageStart)
    }

    override fun getViewBinding(): FragmentLiveBinding {
        return FragmentLiveBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): LiveViewModel {
        return CommonTools.getViewModel(this, LiveViewModel::class.java)
    }

    override fun onNotifyDataChanged() {
        mViewModel.dataList.observe(this) {
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
                    liveAdapter.refreshData(it)
                } else {
                    liveAdapter.loadMoreData(it)
                }
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

    override fun onNewsItemClickListener(view: View, liveReview: LiveReview) {
        val url = Constants.LIVE_WEB_URL + liveReview.roomId + ".html"
        WebActivity.startActivity(mActivity, url)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        isRefresh = true
        pageStart = 1
        getLiveList()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        isLoading = true
        pageStart++
        getLiveList()
    }

    override fun onClear() {

    }

}