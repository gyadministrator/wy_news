package com.android.wy.news.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.activity.HomeActivity
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.FragmentMusicBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.event.MusicEvent
import com.android.wy.news.event.MusicInfoEvent
import com.android.wy.news.event.MusicListEvent
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.MusicState
import com.android.wy.news.music.lrc.Lrc
import com.android.wy.news.service.MusicNotifyService
import com.android.wy.news.view.PlayBarView
import com.android.wy.news.viewmodel.MusicViewModel
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * {
 *     "title": "畅听榜",
 *     "id": 145
 *   }
 */

class MusicFragment : BaseFragment<FragmentMusicBinding, MusicViewModel>(), OnRefreshListener,
    OnLoadMoreListener, BaseNewsAdapter.OnItemAdapterListener<MusicInfo>,
    PlayBarView.OnPlayBarListener {
    private var pageStart = 1
    private var categoryId: Int = 0
    private lateinit var rvContent: RecyclerView
    private lateinit var shimmerRecyclerView: ShimmerRecyclerView
    private lateinit var musicAdapter: MusicAdapter
    private var isRefresh = false
    private var isLoading = false
    private var isLoadingNext = true
    private var mServiceIntent: Intent? = null
    private lateinit var refreshLayout: SmartRefreshLayout
    private var playBarView: PlayBarView? = null
    private var currentPosition = -1
    private var currentMusicInfo: MusicInfo? = null
    private var currentPlayUrl: String? = ""
    private var musicReceiver: MusicReceiver? = null
    private var mediaHelper: MediaPlayerHelper? = null
    private var currentLrcList: ArrayList<Lrc>? = null

    companion object {
        private const val mKey: String = "category_id"
        fun newInstance(cateGoryId: Int): MusicFragment {
            val musicFragment = MusicFragment()
            val bundle = Bundle()
            bundle.putInt(mKey, cateGoryId)
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
        mediaHelper = MediaPlayerHelper.getInstance(mActivity)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        musicAdapter = MusicAdapter(this)
        rvContent.layoutManager = LinearLayoutManager(mActivity)
        rvContent.adapter = musicAdapter

        val gson = Gson()
        val s = SpTools.getString(Constants.LAST_PLAY_MUSIC_KEY)
        this.currentMusicInfo = gson.fromJson(s, MusicInfo::class.java)
        if (this.currentMusicInfo != null) {
            showPlayBar()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
        unRegisterMusicReceiver()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(o: Any) {
        if (o is MusicEvent) {
            Logger.i("onEvent--->>>time:${o.time}")
            playBarView?.updateProgress(o.time)
            val lrc = currentLrcList?.get(getTimeLinePosition(o.time.toLong()))
            lrc?.let { playBarView?.setTitle(lrc.text) }
        }
    }

    private fun getTimeLinePosition(time: Long): Int {
        //注意 time 单位为ms lrc.time 为s
        var linePos = 0
        val lrcCount = currentLrcList?.size
        for (i in 0 until lrcCount!!) {
            val lrc = currentLrcList!![i]
            if (time >= (lrc.time) * 1000) {
                if (i == lrcCount - 1) {
                    linePos = lrcCount - 1
                } else if (time < (currentLrcList!![i + 1].time) * 1000) {
                    linePos = i
                    break
                }
            }
        }
        return linePos
    }

    override fun initEvent() {
        registerMusicReceiver()
        val arguments = arguments
        if (arguments != null) {
            categoryId = arguments.getInt(mKey)
        }
        if (!TextUtils.isEmpty(Constants.CSRF_TOKEN)) {
            getMusicList()
        } else {
            getCookie()
        }
    }

    private fun getCookie() {
        mViewModel.getCookie()
    }

    private fun getMusicList() {
        MusicRepository.getMusicList(categoryId, pageStart).observe(this) {
            val musicListEntity = it.getOrNull()
            val musicListData = musicListEntity?.data
            val musicList = musicListData?.musicList
            val dataList = CommonTools.filterMusicList(musicList)
            if (isRefresh) {
                refreshLayout.setNoMoreData(false)
                refreshLayout.finishRefresh()
                refreshLayout.setEnableLoadMore(true)
            }
            if (isLoading) {
                refreshLayout.finishLoadMore()
            }
            if (dataList.size == 0) {
                if (isLoading) {
                    refreshLayout.setNoMoreData(true)
                }
            } else {
                if (isRefresh) {
                    musicAdapter.refreshData(dataList)
                } else {
                    musicAdapter.loadMoreData(dataList)
                }
            }

            val musicListEvent = MusicListEvent(musicAdapter.getDataList())
            EventBus.getDefault().postSticky(musicListEvent)

            if (dataList.size < 20 && isLoadingNext) {
                isLoadingNext = false
                //加载下一页补充
                pageStart++
                getMusicList()
            }
            shimmerRecyclerView.hideShimmerAdapter()
            isRefresh = false
            isLoading = false
        }
    }

    override fun getViewBinding(): FragmentMusicBinding {
        return FragmentMusicBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): MusicViewModel {
        return CommonTools.getViewModel(this, MusicViewModel::class.java)
    }

    override fun onNotifyDataChanged() {
        mViewModel.musicUrl.observe(this) {
            if (!TextUtils.isEmpty(it)) {
                playMusic(it)
            }
        }

        mViewModel.isSuccess.observe(this) {
            if (it) {
                getMusicList()
            }
        }

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

    private fun playMusic(it: String?) {
        this.currentPlayUrl = it
        startMusicService()
        getLrc()
    }

    private fun getLrc() {
        val musicId = this.currentMusicInfo?.musicrid
        if (musicId!!.contains("_")) {
            val mid = musicId.substring(musicId.indexOf("_") + 1, musicId.length)
            MusicRepository.getMusicLrc(mid).observe(this) {
                val musicLrcEntity = it.getOrNull()
                if (musicLrcEntity != null) {
                    val musicLrcData = musicLrcEntity.data
                    if (musicLrcData != null) {
                        val lrcList = musicLrcData.lrclist
                        if (lrcList.isNotEmpty()) {
                            currentLrcList = CommonTools.parseLrc(lrcList)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerMusicReceiver() {
        musicReceiver = MusicReceiver(this)
        val filter = IntentFilter()
        filter.addAction(MusicNotifyService.MUSIC_PLAY_ACTION)
        filter.addAction(MusicNotifyService.MUSIC_PAUSE_ACTION)
        filter.addAction(MusicNotifyService.MUSIC_NEXT_ACTION)
        filter.addAction(MusicNotifyService.MUSIC_PRE_ACTION)
        filter.addAction(MusicNotifyService.MUSIC_COMPLETE_ACTION)
        filter.addAction(MusicNotifyService.MUSIC_STATE_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mActivity.registerReceiver(musicReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            mActivity.registerReceiver(musicReceiver, filter)
        }
    }

    private fun unRegisterMusicReceiver() {
        if (musicReceiver != null) {
            mActivity.unregisterReceiver(musicReceiver)
        }
    }

    private fun startMusicService() {
        if (mServiceIntent == null) {
            mServiceIntent = Intent(mActivity, MusicNotifyService::class.java)
        }
        mServiceIntent?.action = MusicNotifyService.MUSIC_PREPARE_ACTION
        val gson = Gson()
        mServiceIntent?.putExtra(
            MusicNotifyService.MUSIC_INFO_KEY,
            gson.toJson(this.currentMusicInfo)
        )
        mServiceIntent?.putExtra(MusicNotifyService.MUSIC_URL_KEY, this.currentPlayUrl)
        mActivity.startService(mServiceIntent)
    }

    private fun playNext() {
        Logger.i("playNext: ")
        val dataList = musicAdapter.getDataList()
        if (currentPosition + 1 > dataList.size - 1) currentPosition = dataList.size - 2
        //滑动到播放的歌曲
        rvContent.scrollToPosition(currentPosition + 1)
        //下一曲
        prepareMusic(currentPosition + 1)
    }

    private fun playPre() {
        Logger.i("playPre: ")
        if (currentPosition - 1 < 0) currentPosition = 1
        //滑动到播放的歌曲
        rvContent.scrollToPosition(currentPosition - 1)
        //上一曲
        prepareMusic(currentPosition - 1)
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

    override fun onItemClickListener(view: View, data: MusicInfo) {
        val i = view.tag as Int
        prepareMusic(i)
    }

    private fun prepareMusic(position: Int) {
        if (currentPosition == position) return
        playBarView?.showLoading(true)
        val dataList = musicAdapter.getDataList()
        if (position < 0) currentPosition = 0
        if (position > dataList.size) currentPosition = dataList.size - 1
        if (position < dataList.size) {
            currentPosition = position
            val musicInfo = dataList[currentPosition]
            this.currentMusicInfo = musicInfo
            this.currentMusicInfo?.state = MusicState.STATE_PREPARE

            val gson = Gson()
            val musicInfoEvent = MusicInfoEvent(gson.toJson(this.currentMusicInfo))
            EventBus.getDefault().postSticky(musicInfoEvent)

            musicAdapter.setSelectedIndex(currentPosition)
            mViewModel.requestMusicUrl(musicInfo)

            SpTools.putString(Constants.LAST_PLAY_MUSIC_KEY, gson.toJson(currentMusicInfo))
        }
    }

    override fun onClickPlay(position: Int) {
        Logger.i("onClickPlay--->>>position:$position")
        if (mediaHelper != null) {
            if (mediaHelper!!.isPlaying()) {
                mediaHelper?.pause()
                this.currentMusicInfo?.state = MusicState.STATE_PAUSE
                musicAdapter.setSelectedIndex(position)
            } else {
                if (!TextUtils.isEmpty(currentPlayUrl)) {
                    mediaHelper?.start()
                    this.currentMusicInfo?.state = MusicState.STATE_PLAY
                    musicAdapter.setSelectedIndex(position)
                    //startMusicService()
                } else {
                    playBarView?.showLoading(true)
                    this.currentMusicInfo?.let { mViewModel.requestMusicUrl(it) }
                }
            }
        }
    }

    override fun onClickPlayBar(position: Int) {
        //显示歌曲页面
        showLrcPage()
    }

    private fun showLrcPage() {
        val fragmentManager = (mActivity as AppCompatActivity).supportFragmentManager
        var ft: FragmentTransaction? = fragmentManager.beginTransaction()
        val prev: Fragment? = fragmentManager.findFragmentByTag(LrcFragment.TAG)
        if (prev != null) {
            ft?.remove(prev)?.commit()
            ft = fragmentManager.beginTransaction()
        }
        ft?.addToBackStack(null)
        val gson = Gson()
        val s = gson.toJson(this.currentMusicInfo)
        val lrcFragment = LrcFragment.newInstance(currentPosition, s)
        ft?.let { lrcFragment.show(ft, LrcFragment.TAG) }
    }

    private fun showPlayBar() {
        val stringBuilder = StringBuilder()
        val album = currentMusicInfo?.album
        val artist = currentMusicInfo?.artist
        stringBuilder.append(artist)
        if (!TextUtils.isEmpty(album)) {
            stringBuilder.append("-$album")
        }
        if (mActivity is HomeActivity) {
            val homeActivity = mActivity as HomeActivity
            playBarView = homeActivity.getPlayBarView()
            val selectPosition = homeActivity.getSelectPosition()
            if (selectPosition != 3) return
            playBarView?.visibility = View.VISIBLE
            currentMusicInfo?.pic?.let {
                currentMusicInfo?.duration?.let { it1 ->
                    playBarView?.setCover(it)?.setTitle(stringBuilder.toString())
                        ?.setPosition(currentPosition)?.setDuration(duration = it1 * 1000)
                        ?.showPlayContainer(true)
                        ?.addListener(this)
                }
            }
        }
    }

    class MusicReceiver(musicFragment: MusicFragment) : BroadcastReceiver() {
        private var musicFragment: MusicFragment? = null

        init {
            this.musicFragment = musicFragment
        }

        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1 != null) {
                val action = p1.action
                Logger.i("onReceive---->>>action:$action")
                when (action) {
                    MusicNotifyService.MUSIC_STATE_ACTION -> {
                        this.musicFragment?.playBarView?.getPlayContainer()?.performClick()
                    }

                    MusicNotifyService.MUSIC_PLAY_ACTION -> {
                        this.musicFragment?.playBarView?.showLoading(false)
                        this.musicFragment?.playBarView?.setPlay(true)
                        this.musicFragment?.currentMusicInfo?.state = MusicState.STATE_PLAY
                        this.musicFragment?.currentPosition?.let {
                            this.musicFragment?.musicAdapter?.setSelectedIndex(
                                it
                            )
                        }
                        this.musicFragment?.showPlayBar()
                    }

                    MusicNotifyService.MUSIC_PAUSE_ACTION -> {
                        this.musicFragment?.currentMusicInfo?.state = MusicState.STATE_PAUSE
                        this.musicFragment?.playBarView?.setPlay(false)
                        this.musicFragment?.currentPosition?.let {
                            this.musicFragment?.musicAdapter?.setSelectedIndex(
                                it
                            )
                        }
                    }

                    MusicNotifyService.MUSIC_PRE_ACTION -> {
                        this.musicFragment?.playPre()
                    }

                    MusicNotifyService.MUSIC_NEXT_ACTION -> {
                        this.musicFragment?.playNext()
                    }

                    MusicNotifyService.MUSIC_COMPLETE_ACTION -> {
                        //最后一首播放完，播放第一首
                        val dataList = this.musicFragment?.musicAdapter?.getDataList()
                        if (dataList != null) {
                            if (this.musicFragment?.currentPosition!! + 1 > dataList.size - 1) {
                                this.musicFragment?.currentPosition = 0
                            }
                        }
                        this.musicFragment?.playNext()
                    }

                    else -> {

                    }
                }
            }
        }
    }
}