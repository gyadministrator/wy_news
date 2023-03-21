package com.android.wy.news.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.wy.news.adapter.NewsAdapter
import com.android.wy.news.adapter.VideoAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.viewmodel.VideoViewModel
import com.android.wy.news.databinding.FragmentVideoBinding
import com.android.wy.news.entity.VideoEntity
import com.jcodecraeer.xrecyclerview.XRecyclerView

class VideoFragment : BaseFragment<FragmentVideoBinding, VideoViewModel>(),
    XRecyclerView.LoadingListener, VideoAdapter.OnNewsListener {
    private var pageStart = 0
    private lateinit var rvContent: XRecyclerView
    private var isRefresh = false
    private var isLoading = false
    private lateinit var videoAdapter: VideoAdapter

    companion object {
        fun newInstance() = VideoFragment()
    }

    override fun initView() {
        rvContent = mBinding.rvContent
    }

    override fun initData() {
        videoAdapter = VideoAdapter(mActivity, this)
        rvContent.layoutManager = LinearLayoutManager(mActivity)
        rvContent.adapter = videoAdapter
        rvContent.setLoadingListener(this)
    }

    override fun initEvent() {
        getVideoData()
    }

    override fun getViewBinding(): FragmentVideoBinding {
        return FragmentVideoBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): VideoViewModel {
        return CommonTools.getViewModel(this, VideoViewModel::class.java)
    }

    override fun onClear() {
        rvContent.destroy()
    }

    override fun onNotifyDataChanged() {
        mViewModel.dataList.observe(this) {
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
                    videoAdapter.refreshData(it)
                } else {
                    videoAdapter.loadMoreData(it)
                }
            }
            isRefresh = false
            isLoading = false
        }
    }

    override fun onRefresh() {
        isRefresh = true
        rvContent.setNoMore(false)
        pageStart = 0
        getVideoData()
    }

    override fun onLoadMore() {
        isLoading = true
        pageStart += 10
        getVideoData()
    }

    private fun getVideoData() {
        mViewModel.getVideoList(pageStart)
    }

    override fun onNewsItemClickListener(view: View, videoEntity: VideoEntity) {

    }
}