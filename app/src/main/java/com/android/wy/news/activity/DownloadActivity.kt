package com.android.wy.news.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityDownloadBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.listener.IMusicItemChangeListener
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.sql.DownloadMusicEntity
import com.android.wy.news.sql.DownloadMusicRepository
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.view.MusicRecyclerView
import com.android.wy.news.viewmodel.DownloadViewModel
import com.cooltechworks.views.shimmer.ShimmerRecyclerView

@Route(path = RouteManager.PATH_ACTIVITY_DOWNLOAD)
class DownloadActivity : BaseActivity<ActivityDownloadBinding, DownloadViewModel>(),
    IMusicItemChangeListener {
    private var rvContent: MusicRecyclerView? = null
    private var shimmerRecyclerView: ShimmerRecyclerView? = null
    private var downloadMusicRepository: DownloadMusicRepository? = null
    private val downloadMusicList = ArrayList<DownloadMusicEntity>()
    private val dataList = ArrayList<MusicInfo>()

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
        shimmerRecyclerView = mBinding.shimmerRecyclerView
        rvContent?.seItemListener(this)
    }

    override fun initData() {
        shimmerRecyclerView?.showShimmerAdapter()
        downloadMusicRepository = DownloadMusicRepository(this.applicationContext)
        TaskUtil.runOnThread {
            val list = downloadMusicRepository?.getDownloadMusicList()
            list?.let { downloadMusicList.addAll(it) }
            for (i in 0 until downloadMusicList.size) {
                val downloadMusicEntity = downloadMusicList[i]
                val json = downloadMusicEntity.musicInfoJson
                val musicInfo = JsonUtil.parseJsonToObject(json, MusicInfo::class.java)
                musicInfo?.localPath = downloadMusicEntity.localPath
                musicInfo?.let { dataList.add(it) }
            }
            TaskUtil.runOnUiThread {
                shimmerRecyclerView?.hideShimmerAdapter()
                rvContent?.refreshData(dataList)
            }
        }
    }

    override fun initEvent() {
        rvContent?.let {
            rvContent?.getMusicAdapter()?.let { it1 ->
                PlayMusicManager.initMusicInfo(
                    this,
                    it, this, it1
                )
            }
        }
    }

    override fun getViewBinding(): ActivityDownloadBinding {
        return ActivityDownloadBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): DownloadViewModel {
        return CommonTools.getViewModel(this, DownloadViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

    override fun onItemClick(view: View, data: MusicInfo) {
        val tag = view.tag
        if (tag is Int) {
            PlayMusicManager.prepareMusic(tag)
        }
    }

    override fun onItemLongClick(view: View, data: MusicInfo) {

    }

}