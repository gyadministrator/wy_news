package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.SingerMvAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivitySingerMvBinding
import com.android.wy.news.entity.music.Mvlist
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.view.CustomLoadingView
import com.android.wy.news.viewmodel.SingerMvViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

@Route(path = RouteManager.PATH_ACTIVITY_SINGER_MV)
class SingerMvActivity : BaseActivity<ActivitySingerMvBinding, SingerMvViewModel>(),
    OnLoadMoreListener, OnRefreshListener, BaseNewsAdapter.OnItemAdapterListener<Mvlist> {
    private lateinit var rvContent: RecyclerView
    private var isLoading = false
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var loadingView: CustomLoadingView
    private var page = 1
    private var artistId = ""
    private var singerMvAdapter: SingerMvAdapter? = null

    companion object {
        private const val ARTIST_ID = "artist_id"
        fun startActivity(context: Context, artistId: String) {
            val intent = Intent(context, SingerMvActivity::class.java)
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

        singerMvAdapter = SingerMvAdapter(this)
        rvContent.layoutManager = GridLayoutManager(mActivity, 2)
        rvContent.adapter = singerMvAdapter
    }

    override fun initData() {
        artistId = intent.getStringExtra(ARTIST_ID).toString()
        getData()
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivitySingerMvBinding {
        return ActivitySingerMvBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): SingerMvViewModel {
        return CommonTools.getViewModel(this, SingerMvViewModel::class.java)
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
        MusicRepository.getArtistMv(artistId, page).observe(this) {
            loadingView.visibility = View.GONE
            if (isLoading) {
                refreshLayout.finishLoadMore()
            } else {
                refreshLayout.finishRefresh()
            }
            val artistMvEntity = it.getOrNull()
            if (artistMvEntity != null) {
                val data = artistMvEntity.data
                val mvList = data.mvlist
                if (isLoading) {
                    singerMvAdapter?.loadMoreData(mvList)
                } else {
                    if (mvList.size == 0) {
                        refreshLayout.setNoMoreData(true)
                    } else {
                        singerMvAdapter?.refreshData(mvList)
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

    override fun onItemClickListener(view: View, data: Mvlist) {

    }

    override fun onItemLongClickListener(view: View, data: Mvlist) {

    }
}