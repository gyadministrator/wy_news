package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivitySingerMusicBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.viewmodel.SingerMusicViewModel
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

@Route(path = RouteManager.PATH_ACTIVITY_SINGER_MUSIC)
class SingerMusicActivity : BaseActivity<ActivitySingerMusicBinding, SingerMusicViewModel>(),
    OnLoadMoreListener, OnRefreshListener, BaseNewsAdapter.OnItemAdapterListener<MusicInfo> {
    private lateinit var rvContent: RecyclerView
    private var isLoading = false
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var shimmerRecyclerView: ShimmerRecyclerView
    private var page = 1
    private var artistId = ""
    private var artistMusicAdapter: MusicAdapter? = null

    companion object {
        private const val ARTIST_ID = "artist_id"
        fun startActivity(context: Context, artistId: String) {
            val intent = Intent(context, SingerMusicActivity::class.java)
            intent.putExtra(ARTIST_ID, artistId)
            context.startActivity(intent)
        }
    }

    override fun setDefaultImmersionBar(): Boolean {
        return true
    }

    override fun hideStatusBar(): Boolean {
        return false
    }

    override fun hideNavigationBar(): Boolean {
        return false
    }

    override fun isFollowNightMode(): Boolean {
        return true
    }

    override fun initView() {
        rvContent = mBinding.rvContent
        refreshLayout = mBinding.refreshLayout
        shimmerRecyclerView = mBinding.shimmerRecyclerView
        refreshLayout.setOnLoadMoreListener(this)
        refreshLayout.setOnRefreshListener(this)

        artistMusicAdapter = MusicAdapter(this)
        rvContent.layoutManager = LinearLayoutManager(this)
        rvContent.adapter = artistMusicAdapter

        PlayMusicManager.initMusicInfo(this, rvContent, null, this, artistMusicAdapter!!)
    }

    override fun initData() {
        artistId = intent.getStringExtra(ARTIST_ID).toString()
        shimmerRecyclerView.showShimmerAdapter()
        getData()
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivitySingerMusicBinding {
        return ActivitySingerMusicBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): SingerMusicViewModel {
        return CommonTools.getViewModel(this, SingerMusicViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        isLoading = true
        page++
        getData()
    }

    private fun getData() {
        MusicRepository.getArtistMusic(artistId, page).observe(this) {
            shimmerRecyclerView.hideShimmerAdapter()
            if (isLoading) {
                refreshLayout.finishLoadMore()
            } else {
                refreshLayout.finishRefresh()
            }
            val artistMusicEntity = it.getOrNull()
            if (artistMusicEntity != null) {
                val data = artistMusicEntity.data
                val dataList = data.list
                if (dataList.size == 0) {
                    refreshLayout.setNoMoreData(true)
                } else {
                    if (isLoading) {
                        artistMusicAdapter?.loadMoreData(dataList)
                    } else {
                        artistMusicAdapter?.refreshData(dataList)
                    }
                }
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        isLoading = false
        page = 1
        getData()
    }

    override fun onItemClickListener(view: View, data: MusicInfo) {
        PlayMusicManager.setClickMusicInfo(data)
        val i = view.tag as Int
        PlayMusicManager.prepareMusic(i)
    }

    override fun onItemLongClickListener(view: View, data: MusicInfo) {

    }

}