package com.android.wy.news.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.DownloadAdapter
import com.android.wy.news.app.App
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.ActivityDownloadBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.sql.DownloadMusicEntity
import com.android.wy.news.sql.DownloadMusicRepository
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.view.MusicRecyclerView
import com.android.wy.news.viewmodel.DownloadViewModel
import java.io.File

@Route(path = RouteManager.PATH_ACTIVITY_DOWNLOAD)
class DownloadActivity : BaseActivity<ActivityDownloadBinding, DownloadViewModel>(),
    BaseNewsAdapter.OnItemAdapterListener<File> {
    private var rvContent: MusicRecyclerView? = null
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
    }

    override fun initData() {
        downloadMusicRepository = DownloadMusicRepository(this.applicationContext)
        TaskUtil.runOnThread {
            val list = downloadMusicRepository?.getDownloadMusicList()
            list?.let { downloadMusicList.addAll(it) }
            if (downloadMusicList.size > 0) {
                for (i in 0 until downloadMusicList.size) {
                    val downloadMusicEntity = downloadMusicList[i]
                    val json = downloadMusicEntity.musicInfoJson
                    val musicInfo = JsonUtil.parseJsonToObject(json, MusicInfo::class.java)
                    musicInfo?.localPath=downloadMusicEntity.localPath
                    musicInfo?.let { dataList.add(it) }
                }
                TaskUtil.runOnUiThread {
                    if (dataList.size > 0) {
                        rvContent?.refreshData(dataList)
                    }
                }
            }
        }
    }

    override fun initEvent() {

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

    override fun onItemClickListener(view: View, data: File) {

    }

    override fun onItemLongClickListener(view: View, data: File) {

    }

}