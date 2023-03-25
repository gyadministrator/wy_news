package com.android.wy.news.fragment

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.adapter.VideoAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.FragmentTabVideoBinding
import com.android.wy.news.entity.VideoEntity
import com.android.wy.news.layoutmanager.VideoLayoutManager
import com.android.wy.news.listener.OnViewPagerListener
import com.android.wy.news.viewmodel.VideoTabViewModel
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer

class VideoTabFragment : BaseFragment<FragmentTabVideoBinding, VideoTabViewModel>(),
    OnRefreshListener, OnLoadMoreListener, VideoAdapter.OnVideoListener, OnViewPagerListener {
    private lateinit var rvContent: RecyclerView
    private var isRefresh = false
    private var isLoading = false
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var shimmerRecyclerView: ShimmerRecyclerView
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var layoutManager: VideoLayoutManager
    private var currentPosition: Int = 0

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
        layoutManager = VideoLayoutManager(mActivity, OrientationHelper.VERTICAL, false)
        rvContent.layoutManager = layoutManager
        rvContent.adapter = videoAdapter
        layoutManager.setOnViewPagerListener(this)
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
        mViewModel.getVideoList()
    }

    override fun onVideoItemClickListener(view: View, videoEntity: VideoEntity) {

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        isRefresh = true
        getVideoData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        isLoading = true
        getVideoData()
    }

    override fun onInitComplete() {
        playVideo(0)
    }

    private fun playVideo(position: Int) {
        Handler(Looper.getMainLooper()).post {
            val holder = rvContent.findViewHolderForAdapterPosition(position)
            if (holder is VideoAdapter.ViewHolder){
                holder.playVideo.startButton.performClick()
            }
        }
    }

    override fun onPageRelease(isNext: Boolean, position: Int) {
        releaseVideo(position)
    }

    private fun releaseVideo(index: Int) {
        //JCVideoPlayer.releaseAllVideos()
    }

    override fun onPageSelected(position: Int, isBottom: Boolean) {
        currentPosition = position
        playVideo(position)
    }

    override fun onPause() {
        super.onPause()
        JCVideoPlayer.releaseAllVideos()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            JCVideoPlayer.releaseAllVideos()
        } else {
            playVideo(currentPosition)
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("super.setUserVisibleHint(isVisibleToUser)"))
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        playVideo(currentPosition)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    JCVideoPlayer.backPress()
                }
            })
    }

    override fun handleBackPressed(): Boolean {
        JCVideoPlayer.backPress()
        return true
    }
}