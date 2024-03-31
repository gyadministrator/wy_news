package com.android.wy.news.fragment

import android.content.Context
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import cn.jzvd.Jzvd
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.VideoAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.databinding.FragmentTabVideoBinding
import com.android.wy.news.entity.RecommendVideoData
import com.android.wy.news.listener.OnViewPagerListener
import com.android.wy.news.manager.JsoupManager
import com.android.wy.news.manager.VideoLayoutManager
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.util.ToastUtil
import com.android.wy.news.view.ScreenVideoView
import com.android.wy.news.viewmodel.VideoTabViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class VideoTabFragment : BaseFragment<FragmentTabVideoBinding, VideoTabViewModel>(),
    OnRefreshListener, OnLoadMoreListener, VideoAdapter.OnVideoListener, OnViewPagerListener,
    BaseNewsAdapter.OnItemAdapterListener<RecommendVideoData> {
    private lateinit var rvContent: RecyclerView
    private var isRefresh = false
    private var isLoading = false
    private var videoAdapter: VideoAdapter? = null
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var layoutManager: VideoLayoutManager
    private var currentPosition: Int = 0
    private var pageStart = 0

    companion object {
        fun newInstance() = VideoTabFragment()
    }

    override fun initView() {
        rvContent = mBinding.rvContent
        refreshLayout = mBinding.refreshLayout
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)
        refreshLayout.setEnableFooterFollowWhenNoMoreData(true)
    }

    override fun initData() {
        videoAdapter = VideoAdapter(this, this)
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
                    videoAdapter?.refreshData(it)
                } else {
                    val i = videoAdapter?.loadMoreData(it)
                    if (isLoading) {
                        //加载完成，直接滑动到新加载的第一条数据
                        if (i != null) {
                            rvContent.scrollToPosition(i + 1)
                        }
                        if (i != null) {
                            playVideo(i + 1)
                        }
                    }
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

        GlobalData.indexChange.observe(this) {
            if (it == 2) {
                rePlay()
            }
        }
    }

    private fun getVideoData() {
        //mViewModel.getRecommendVideoList(pageStart)
        mViewModel.getRecommendVideoList()
    }

    override fun onVideoFinish() {
        currentPosition++
        rvContent.scrollToPosition(currentPosition)
        playVideo(currentPosition)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageStart = 0
        isRefresh = true
        getVideoData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageStart++
        isLoading = true
        getVideoData()
    }

    override fun onInitComplete() {
        playVideo(0)
    }

    private fun playVideo(position: Int) {
        TaskUtil.runOnUiThread {
            val holder = rvContent.findViewHolderForAdapterPosition(position)
            if (holder is VideoAdapter.VideoViewHolder) {
                val tag = holder.playVideo.tag
                if (tag is String) {
                    getRealUrl(holder.playVideo, tag)
                }
            }
        }
    }

    private fun getRealUrl(screenVideoView: ScreenVideoView, vid: String) {
        TaskUtil.runOnThread {
            //val videoUrl = JsoupManager.getVideoUrl(vid)
            TaskUtil.runOnUiThread {
                //screenVideoView.setUp(videoUrl)
                screenVideoView.play()
            }
        }
    }

    override fun onPageRelease(isNext: Boolean, position: Int) {
        releaseVideo(position)
    }

    private fun releaseVideo(index: Int) {
        //Jzvd.releaseAllVideos()
    }

    override fun onPageSelected(position: Int, isBottom: Boolean) {
        currentPosition = position
        playVideo(position)
    }

    private fun rePlay() {
        if (videoAdapter != null) {
            val dataList = videoAdapter?.getDataList()
            if (currentPosition < dataList!!.size) {
                val recommendVideoEntity = dataList[currentPosition]
                recommendVideoEntity.isPlaying = true
                videoAdapter?.notifyItemChanged(currentPosition)
                playVideo(currentPosition)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Jzvd.backPress()
                }
            })
    }

    override fun handleBackPressed(): Boolean {
        Jzvd.backPress()
        return true
    }

    override fun onItemClickListener(view: View, data: RecommendVideoData) {

    }

    override fun onItemLongClickListener(view: View, data: RecommendVideoData) {

    }
}