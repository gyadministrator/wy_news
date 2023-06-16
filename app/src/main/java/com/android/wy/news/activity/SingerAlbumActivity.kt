package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.SingerAlbumAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivitySingerAlbumBinding
import com.android.wy.news.entity.music.Album
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.viewmodel.SingerAlbumViewModel
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

@Route(path = RouteManager.PATH_ACTIVITY_SINGER_ALBUM)
class SingerAlbumActivity : BaseActivity<ActivitySingerAlbumBinding, SingerAlbumViewModel>(),
    OnLoadMoreListener, OnRefreshListener, BaseNewsAdapter.OnItemAdapterListener<Album> {
    private lateinit var rvContent: RecyclerView
    private var isLoading = false
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var shimmerRecyclerView: ShimmerRecyclerView
    private var page = 1
    private var artistId = ""
    private var singerAlbumAdapter: SingerAlbumAdapter? = null

    companion object {
        private const val ARTIST_ID = "artist_id"
        fun startActivity(context: Context, artistId: String) {
            val intent = Intent(context, SingerAlbumActivity::class.java)
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

        singerAlbumAdapter = SingerAlbumAdapter(this)
        rvContent.layoutManager = GridLayoutManager(mActivity, 2)
        rvContent.adapter = singerAlbumAdapter
    }

    override fun initData() {
        artistId = intent.getStringExtra(ARTIST_ID).toString()
        shimmerRecyclerView.showShimmerAdapter()
        getData()
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivitySingerAlbumBinding {
        return ActivitySingerAlbumBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): SingerAlbumViewModel {
        return CommonTools.getViewModel(this, SingerAlbumViewModel::class.java)
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
        MusicRepository.getArtistAlbum(artistId, page).observe(this) {
            shimmerRecyclerView.hideShimmerAdapter()
            if (isLoading) {
                refreshLayout.finishLoadMore()
            } else {
                refreshLayout.finishRefresh()
            }
            val artistAlbumEntity = it.getOrNull()
            if (artistAlbumEntity != null) {
                val data = artistAlbumEntity.data
                val albumList = data.albumList
                if (albumList.size == 0) {
                    refreshLayout.setNoMoreData(true)
                } else {
                    if (isLoading) {
                        singerAlbumAdapter?.loadMoreData(albumList)
                    } else {
                        singerAlbumAdapter?.refreshData(albumList)
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

    override fun onItemClickListener(view: View, data: Album) {
        SingerMusicActivity.startActivity(this, data.albumid.toString(), 1)
    }

    override fun onItemLongClickListener(view: View, data: Album) {

    }

}