package com.android.wy.news.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.wy.news.activity.WebActivity
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.LiveAdapter
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.databinding.FragmentLiveBinding
import com.android.wy.news.databinding.FragmentMusicBinding
import com.android.wy.news.entity.LiveReview
import com.android.wy.news.entity.music.MusicListResult
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.viewmodel.LiveViewModel
import com.android.wy.news.viewmodel.MusicViewModel
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class MusicFragment : BaseFragment<FragmentMusicBinding, MusicViewModel>(), OnRefreshListener,
    OnLoadMoreListener, BaseNewsAdapter.OnItemAdapterListener<MusicListResult> {
    private var pageStart = 1
    private var categoryId: String? = ""
    private lateinit var rvContent: RecyclerView
    private lateinit var shimmerRecyclerView: ShimmerRecyclerView
    private lateinit var musicAdapter: MusicAdapter
    private var isRefresh = false
    private var isLoading = false
    private lateinit var refreshLayout: SmartRefreshLayout

    companion object {
        private const val mKey: String = "category_id"
        fun newInstance(cateGoryId: String): MusicFragment {
            val musicFragment = MusicFragment()
            val bundle = Bundle()
            bundle.putString(mKey, cateGoryId)
            musicFragment.arguments = bundle
            return musicFragment
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
        musicAdapter = MusicAdapter(this)
        rvContent.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvContent.adapter = musicAdapter
    }

    override fun initEvent() {
        val arguments = arguments
        if (arguments != null) {
            categoryId = arguments.getString(mKey)
        }
        getMusicList()
    }

    private fun getMusicList() {
        categoryId?.let { mViewModel.getMusicList(it) }
        /*MusicRepository.getMusicList(categoryId).observe(this) {
            if (isRefresh) {
                refreshLayout.setNoMoreData(false)
                refreshLayout.finishRefresh()
                refreshLayout.setEnableLoadMore(true)
            }
            if (isLoading) {
                refreshLayout.finishLoadMore()
            }
            val musicListEntity = it.getOrNull()
            val musicListData = musicListEntity?.data
            val musicListResult = musicListData?.result
            if (musicListResult.isNullOrEmpty()) {
                if (isLoading) {
                    refreshLayout.setNoMoreData(true)
                }
            } else {
                if (isRefresh) {
                    musicAdapter.refreshData(musicListResult)
                } else {
                    musicAdapter.loadMoreData(musicListResult)
                }
            }
            shimmerRecyclerView.hideShimmerAdapter()
            isRefresh = false
            isLoading = false
        }*/
    }

    override fun getViewBinding(): FragmentMusicBinding {
        return FragmentMusicBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): MusicViewModel {
        return CommonTools.getViewModel(this, MusicViewModel::class.java)
    }

    override fun onNotifyDataChanged() {
        mViewModel.msg.observe(this) {
            Toast.makeText(mActivity, it, Toast.LENGTH_SHORT).show()
            refreshLayout.setEnableLoadMore(false)
            if (isRefresh) {
                refreshLayout.setNoMoreData(false)
                refreshLayout.finishRefresh(false)
            }
            if (isLoading) {
                refreshLayout.finishLoadMore(false)
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        isRefresh = true
        pageStart = 1
        getMusicList()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        isLoading = true
        pageStart++
        getMusicList()
    }

    override fun onClear() {

    }

    override fun onItemClickListener(view: View, data: MusicListResult) {

    }

}