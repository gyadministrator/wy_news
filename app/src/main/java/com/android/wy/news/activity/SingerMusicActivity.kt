package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivitySingerMusicBinding
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.view.CustomLoadingView
import com.android.wy.news.viewmodel.SingerMusicViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

@Route(path = RouteManager.PATH_ACTIVITY_SINGER_MUSIC)
class SingerMusicActivity : BaseActivity<ActivitySingerMusicBinding, SingerMusicViewModel>(),
    OnLoadMoreListener, OnRefreshListener {
    private lateinit var rvContent: RecyclerView
    private var isLoading = false
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var loadingView: CustomLoadingView
    private var page = 1
    private var artistId = ""
    private var musicAdapter: MusicAdapter? = null

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
        loadingView = mBinding.loadingView
        refreshLayout.setOnLoadMoreListener(this)
        refreshLayout.setOnRefreshListener(this)
    }

    override fun initData() {
        artistId = intent.getStringExtra(ARTIST_ID).toString()
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
            loadingView.visibility = View.GONE
            if (isLoading) {
                refreshLayout.finishLoadMore()
            } else {
                refreshLayout.finishRefresh()
            }
            val artistMusicEntity = it.getOrNull()
            if (artistMusicEntity != null) {
                val data = artistMusicEntity.data
                val dataList = data.list
                if (isLoading) {
                    musicAdapter?.loadMoreData(dataList)
                } else {
                    if (dataList.size == 0) {
                        refreshLayout.setNoMoreData(true)
                    } else {
                        musicAdapter?.refreshData(dataList)
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

}