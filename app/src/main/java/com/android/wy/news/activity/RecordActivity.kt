package com.android.wy.news.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityRecordBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.listener.IMusicItemChangeListener
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.sql.RecordMusicRepository
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.view.MusicRecyclerView
import com.android.wy.news.viewmodel.RecordViewModel

@Route(path = RouteManager.PATH_ACTIVITY_RECORD)
class RecordActivity : BaseActivity<ActivityRecordBinding, RecordViewModel>(),
    IMusicItemChangeListener {
    private var rvContent: MusicRecyclerView? = null
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
        rvContent?.seItemListener(this)
    }

    override fun initData() {
        val recordMusicRepository = RecordMusicRepository(this.applicationContext)
        TaskUtil.runOnThread {
            val recordMusicList = recordMusicRepository.getRecordMusicList()
            if (recordMusicList.size > 0) {
                for (i in 0 until recordMusicList.size) {
                    val recordMusicEntity = recordMusicList[i]
                    val musicInfoJson = recordMusicEntity.musicInfoJson
                    val musicInfo =
                        JsonUtil.parseJsonToObject(musicInfoJson, MusicInfo::class.java)
                    musicInfo?.let { dataList.add(it) }
                }
                TaskUtil.runOnUiThread {
                    rvContent?.refreshData(dataList)
                }
            }
        }
    }

    override fun initEvent() {
        rvContent?.let {
            rvContent?.getMusicAdapter()?.let { it1 ->
                PlayMusicManager.initMusicInfo(
                    this,
                    it, null, this, it1
                )
            }
        }
    }

    override fun getViewBinding(): ActivityRecordBinding {
        return ActivityRecordBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): RecordViewModel {
        return CommonTools.getViewModel(this, RecordViewModel::class.java)
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