package com.android.wy.news.fragment

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.android.wy.news.R
import com.android.wy.news.activity.SettingActivity
import com.android.wy.news.activity.WebActivity
import com.android.wy.news.adapter.MineBannerImgAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.FragmentTabMineBinding
import com.android.wy.news.dialog.LoadingDialog
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.entity.music.MusicRecommendEntity
import com.android.wy.news.entity.music.PropType
import com.android.wy.news.entity.music.RecommendMusicType
import com.android.wy.news.entity.music.RecommendMusicTypeEntity
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.listener.IMusicItemChangeListener
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.sql.RecordMusicRepository
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.view.MusicRecyclerView
import com.android.wy.news.viewmodel.MineTabViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.listener.OnBannerListener
import java.util.Random

class MineTabFragment : BaseFragment<FragmentTabMineBinding, MineTabViewModel>(),
    IMusicItemChangeListener, OnRefreshListener {
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
    private lateinit var tvLimit: TextView
    private lateinit var rlAd: RelativeLayout
    private lateinit var refreshLayout: SmartRefreshLayout
    private var jumpUrlStr: String? = ""

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
        refreshLayout = mBinding.refreshLayout
        tvLimit = mBinding.tvLimit
        rlAd = mBinding.rlAd
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setEnableLoadMore(false)
        ivRecommendCover.setOnClickListener {
            jumpUrlStr?.let { it1 -> WebActivity.startActivity(mActivity, it1) }
        }
    }

    override fun initData() {
        rvContent.getMusicAdapter()
            ?.let { PlayMusicManager.initMusicInfo(mActivity, rvContent, this, it) }
        showRecentPlay()
        requestPropType()
        requestRecommendMusicType()
    }

    private fun requestRecommendMusicType() {
        MusicRepository.getRecommendMusicType().observe(this) {
            refreshLayout.finishRefresh()
            setMusicType(it)
        }
    }

    private fun setMusicType(it: Result<RecommendMusicTypeEntity>) {
        val banner = mBinding.banner
        val recommendMusicTypeEntity = it.getOrNull()
        if (recommendMusicTypeEntity != null) {
            val recommendMusicTypeData = recommendMusicTypeEntity.data
            val list = recommendMusicTypeData.list
            Logger.i("setMusicType--->>>$list")
            if (list.isNotEmpty()) {
                banner.visibility = View.VISIBLE
                banner.setAdapter(MineBannerImgAdapter(list))
                    .addBannerLifecycleObserver(this) //添加生命周期观察者
                    //.setIndicator(CircleIndicator(mActivity))
                    .setBannerGalleryEffect(10, 10).setIndicatorHeight(20).setIndicatorHeight(20)
                    .setIndicatorNormalColorRes(R.color.text_normal_color)
                    .setIndicatorSelectedColorRes(R.color.text_select_color).setIndicatorSpace(15)
                    .setIndicatorGravity(IndicatorConfig.Direction.CENTER)
                    .setOnBannerListener(bannerItemListener)

                val random = Random()
                val i = random.nextInt(list.size - 1)
                Logger.i("setMusicType--->>>i=$i,pid=" + list[i].id)
                requestRecommendMusic(list[i].id)
            }
        }
    }

    private val bannerItemListener = OnBannerListener<RecommendMusicType> { data, _ ->
        Logger.i("setMusicType--->>>${data.id}")
        LoadingDialog.show(
            GlobalData.COMMON_LOADING_TAG,
            mActivity as FragmentActivity,
            "获取中...",
            true
        )
        requestRecommendMusic(data.id)
    }

    private fun requestRecommendMusic(pid: String) {
        MusicRepository.getRecommendMusic(pid).observe(this) {
            LoadingDialog.hide(GlobalData.COMMON_LOADING_TAG)
            refreshLayout.finishRefresh()
            setContent(it)
        }
    }

    private fun requestPropType() {
        MusicRepository.getPopByType().observe(this) {
            refreshLayout.finishRefresh()
            setPropType(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setPropType(it: Result<PropType>?) {
        if (it != null) {
            Logger.i("setPropType--->>>$it")
            val propType = it.getOrNull()
            if (propType != null) {
                val data = propType.data
                val popImgUrl = data.popImgUrl
                if (!TextUtils.isEmpty(popImgUrl)) {
                    rlAd.visibility = View.VISIBLE
                }
                CommonTools.loadImage(popImgUrl, ivRecommendCover)
                val onlineTime = data.onlineTime
                val offlineTime = data.offlineTime
                val onlineTimeStr: String = CommonTools.parseTime(onlineTime)
                val offlineTimeStr: String = CommonTools.parseTime(offlineTime)
                Logger.i("setPropType--->>>onlineTimeStr=$onlineTimeStr  offlineTimeStr=$offlineTimeStr")
                tvLimit.text = "$onlineTimeStr-$offlineTimeStr"
                jumpUrlStr = data.jumpUrl
            }
        }
    }

    private fun setContent(it: Result<MusicRecommendEntity>?) {
        val musicRecommendEntity = it?.getOrNull()
        if (musicRecommendEntity != null) {
            val data = musicRecommendEntity.data

            rlReCommendPlay.visibility = View.VISIBLE
            val name = data.name
            if (!TextUtils.isEmpty(name)) {
                tvTitle.text = name
            }
            val musicList = CommonTools.filterMusicList(data.musicList)
            musicList?.let { it1 -> rvContent.refreshData(it1) }
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

    override fun onRefresh(refreshLayout: RefreshLayout) {
        TaskUtil.runOnUiThread({
            if (refreshLayout.isRefreshing) {
                refreshLayout.finishRefresh()
            }
        }, 2000)
        initData()
    }
}