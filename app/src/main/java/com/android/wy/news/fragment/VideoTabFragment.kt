package com.android.wy.news.fragment

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.adapter.VideoAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.FragmentTabVideoBinding
import com.android.wy.news.entity.VideoEntity
import com.android.wy.news.viewmodel.VideoTabViewModel
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class VideoTabFragment : BaseFragment<FragmentTabVideoBinding, VideoTabViewModel>(),
    OnRefreshListener, OnLoadMoreListener, VideoAdapter.OnNewsListener {
    private var pageStart = 0
    private lateinit var rvContent: RecyclerView
    private var isRefresh = false
    private var isLoading = false
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var shimmerRecyclerView: ShimmerRecyclerView
    private lateinit var refreshLayout: SmartRefreshLayout

    companion object {
        fun newInstance() = VideoTabFragment()
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
        videoAdapter = VideoAdapter(mActivity, this)
        rvContent.layoutManager = LinearLayoutManager(mActivity)
        rvContent.adapter = videoAdapter
    }

    override fun initEvent() {
        getVideoData()
    }

    override fun getViewBinding(): FragmentTabVideoBinding {
        return FragmentTabVideoBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): VideoTabViewModel {
        return CommonTools.getViewModel(this, VideoTabViewModel::class.java)
    }

    override fun onClear() {

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
                    videoAdapter.refreshData(it)
                } else {
                    videoAdapter.loadMoreData(it)
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

    private fun getVideoData() {
        mViewModel.getVideoList(pageStart)
    }

    override fun onNewsItemClickListener(view: View, videoEntity: VideoEntity) {

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        isRefresh = true
        pageStart = 0
        getVideoData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        isLoading = true
        pageStart += 10
        getVideoData()
    }
}