package com.android.wy.news.fragment

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.wy.news.activity.SettingActivity
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.FragmentTabMineBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.entity.music.MusicRecommendEntity
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.listener.IMusicItemChangeListener
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.sql.RecordMusicRepository
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.view.MusicRecyclerView
import com.android.wy.news.viewmodel.MineTabViewModel

class MineTabFragment : BaseFragment<FragmentTabMineBinding, MineTabViewModel>(),
    IMusicItemChangeListener {
    private lateinit var llSetting: LinearLayout
    private lateinit var llLive: LinearLayout
    private lateinit var llLocal: LinearLayout
    private lateinit var llDownload: LinearLayout
    private lateinit var llRecord: LinearLayout
    private lateinit var ivCover: ImageView
    private lateinit var ivRecommendCover: ImageView
    private lateinit var rlRecentPlay: RelativeLayout
    private lateinit var rlReCommendPlay: RelativeLayout
    private lateinit var tvPlay: TextView
    private lateinit var rvContent: MusicRecyclerView
    private lateinit var tvTitle: TextView

    companion object {
        fun newInstance() = MineTabFragment()
    }

    override fun initView() {
        llSetting = mBinding.llSetting
        llLive = mBinding.llLive
        llLocal = mBinding.llLocal
        llDownload = mBinding.llDownload
        ivCover = mBinding.ivCover
        tvPlay = mBinding.tvPlay
        rlRecentPlay = mBinding.rlRecentPlay
        llRecord = mBinding.llRecord
        ivRecommendCover = mBinding.ivRecommendCover
        rlReCommendPlay = mBinding.rlRecommendPlay
        rvContent = mBinding.rvContent
        tvTitle = mBinding.tvTitle
    }

    override fun initData() {
        rvContent.getMusicAdapter()
            ?.let { PlayMusicManager.initMusicInfo(mActivity, rvContent, null, this, it) }
        showRecentPlay()
        MusicRepository.getRecommendMusic().observe(this) {
            setContent(it)
        }
    }

    private fun setContent(it: Result<MusicRecommendEntity>?) {
        val musicRecommendEntity = it?.getOrNull()
        if (musicRecommendEntity != null) {
            val data = musicRecommendEntity.data
            CommonTools.loadImage(data.img700, ivRecommendCover)
            rlReCommendPlay.visibility = View.VISIBLE
            val name = data.name
            if (!TextUtils.isEmpty(name)) {
                tvTitle.text = name
            }
            val musicList = CommonTools.filterMusicList(data.musicList)
            rvContent.refreshData(musicList)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showRecentPlay() {
        val s = SpTools.getString(GlobalData.SpKey.LAST_PLAY_MUSIC_KEY)
        val currentMusicInfo = JsonUtil.parseJsonToObject(s, MusicInfo::class.java)
        currentMusicInfo?.pic?.let { CommonTools.loadImage(it, ivCover) }

        val recordMusicRepository = RecordMusicRepository(mActivity.applicationContext)
        TaskUtil.runOnThread {
            val recordMusicList = recordMusicRepository.getRecordMusicList()
            if (recordMusicList.size > 0) {
                TaskUtil.runOnUiThread {
                    tvPlay.text = "已播歌曲 ${recordMusicList.size}"
                }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            showRecentPlay()
        }
    }

    override fun initEvent() {
        llSetting.setOnClickListener {
            SettingActivity.startSettingActivity(mActivity)
        }
        llLive.setOnClickListener {
            RouteManager.go(RouteManager.PATH_ACTIVITY_LIVE)
        }
        llLocal.setOnClickListener {
            RouteManager.go(RouteManager.PATH_ACTIVITY_MUSIC_LOCAL)
        }
        llDownload.setOnClickListener {
            RouteManager.go(RouteManager.PATH_ACTIVITY_DOWNLOAD)
        }
        llRecord.setOnClickListener {
            goRecentPlayPage()
        }
        rlRecentPlay.setOnClickListener {
            goRecentPlayPage()
        }
        rvContent.seItemListener(this)
    }

    private fun goRecentPlayPage() {
        RouteManager.go(RouteManager.PATH_ACTIVITY_RECORD)
    }

    override fun getViewBinding(): FragmentTabMineBinding {
        return FragmentTabMineBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): MineTabViewModel {
        return CommonTools.getViewModel(this, MineTabViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {
        GlobalData.indexChange.observe(this) {
            if (it == 4) {
                showRecentPlay()
            }
        }
    }

    override fun onItemClick(view: View, data: MusicInfo) {
        val i = view.tag as Int
        PlayMusicManager.prepareMusic(i)
        rvContent.updatePosition(i)
        showRecentPlay()
    }

    override fun onItemLongClick(view: View, data: MusicInfo) {
    }
}