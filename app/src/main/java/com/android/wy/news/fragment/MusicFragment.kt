package com.android.wy.news.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.view.View
import android.widget.Toast
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
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.MusicState
import com.android.wy.news.service.MusicNotifyService
import com.android.wy.news.view.PlayBarView
import com.android.wy.news.viewmodel.MusicViewModel
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import java.util.Timer
import java.util.TimerTask


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
    private var isBind = false
    private var mServiceIntent: Intent? = null
    private var musicBinder: MusicNotifyService.MusicBinder? = null
    private var musicService: MusicNotifyService? = null
    private lateinit var refreshLayout: SmartRefreshLayout
    private var mMediaHelper: MediaPlayerHelper? = null
    private var playBarView: PlayBarView? = null
    private var currentPosition = -1
    private var currentMusicInfo: MusicInfo? = null
    private var timer: Timer? = null
    private var currentPlayUrl: String? = ""

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
        musicAdapter = MusicAdapter(this)
        rvContent.layoutManager = LinearLayoutManager(mActivity)
        rvContent.adapter = musicAdapter
        mMediaHelper = MediaPlayerHelper.getInstance(mActivity)

        val gson = Gson()
        val s = SpTools.getString(Constants.LAST_PLAY_MUSIC_KEY)
        this.currentMusicInfo = gson.fromJson(s, MusicInfo::class.java)
        showPlayBar()
        playBarView?.setPlay(false)
    }

    override fun initEvent() {
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
        mMediaHelper?.setPath(it)
        mMediaHelper?.setOnMediaHelperListener(object :
            MediaPlayerHelper.OnMediaHelperListener {
            override fun onPreparedState(mp: MediaPlayer?) {
                Logger.i("onPreparedState: ")
                mMediaHelper?.start()
            }

            override fun onPauseState() {
                Logger.i("onPauseState: ")
                currentMusicInfo?.state = MusicState.STATE_PAUSE
                musicAdapter.setSelectedIndex(currentPosition)
            }

            override fun onPlayingState() {
                Logger.i("onPlayingState: ")
                playBarView?.showLoading(false)
                currentMusicInfo?.state = MusicState.STATE_PLAY
                musicAdapter.setSelectedIndex(currentPosition)
                timer?.cancel()
                timer = null
                showPlayBar()
                setProgress()
                startMusicService()
            }

            override fun onCompleteState() {
                Logger.i("onCompleteState: ")
                timer?.cancel()
                timer = null
                playNext()
            }

            override fun onBufferState(percent: Int) {
                Logger.i("onBufferState: $percent")
            }

            override fun onErrorState(what: Int, extra: Int) {
                Logger.i("onErrorState: what:$what  extra:$extra")
            }
        })
    }

    private fun startMusicService() {
        if (mServiceIntent == null) {
            mServiceIntent = Intent(mActivity, MusicNotifyService::class.java)
        }
        unBind()
        if (!isBind) {
            mActivity.bindService(mServiceIntent, connection, Context.BIND_AUTO_CREATE)
            isBind = true
        }
    }

    private fun unBind() {
        if (isBind) {
            mActivity.unbindService(connection)
            isBind = false
        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            musicBinder = service as (MusicNotifyService.MusicBinder)
            musicService = musicBinder?.getService()
            this@MusicFragment.currentMusicInfo?.let { musicBinder?.setMusic(musicInfo = it) }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
        }
    }

    private fun playNext() {
        Logger.i("playNext: ")
        playBarView?.setPlay(false)
        //滑动到播放的歌曲
        rvContent.scrollToPosition(currentPosition + 1)
        playBarView?.showLoading(true)
        //下一曲
        prepareMusic(currentPosition + 1)
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
        timer?.cancel()
        timer = null
    }

    override fun onItemClickListener(view: View, data: MusicInfo) {
        val i = view.tag as Int
        playBarView?.showLoading(true)
        prepareMusic(i)
    }

    private fun prepareMusic(position: Int) {
        if (currentPosition == position) return
        val dataList = musicAdapter.getDataList()
        if (position < dataList.size) {
            val musicInfo = dataList[position]
            currentPosition = position
            this.currentMusicInfo = musicInfo
            this.currentMusicInfo?.state = MusicState.STATE_PREPARE
            musicAdapter.setSelectedIndex(currentPosition)
            mViewModel.requestMusicUrl(musicInfo)

            val gson = Gson()
            SpTools.putString(Constants.LAST_PLAY_MUSIC_KEY, gson.toJson(currentMusicInfo))
        }
    }

    private fun setProgress() {
        if (timer == null) {
            //时间监听器
            timer = Timer()
        }
        timer?.schedule(object : TimerTask() {
            override fun run() {
                val time = mMediaHelper?.getCurrentPosition()
                Logger.i("setProgress--->>>time:$time")
                time?.let { playBarView?.updateProgress(it) }
            }
        }, 0, 50)
    }

    override fun onClickPlay(position: Int) {
        Logger.i("onClickPlay--->>>position:$position")
        if (mMediaHelper != null) {
            if (mMediaHelper!!.isPlaying()) {
                mMediaHelper?.pause()
                playBarView?.setPlay(false)
                this.currentMusicInfo?.state = MusicState.STATE_PAUSE
                musicAdapter.setSelectedIndex(position)
            } else {
                if (!TextUtils.isEmpty(currentPlayUrl)) {
                    mMediaHelper?.start()
                    playBarView?.setPlay(true)
                    this.currentMusicInfo?.state = MusicState.STATE_PLAY
                    musicAdapter.setSelectedIndex(position)
                } else {
                    playBarView?.showLoading(true)
                    this.currentMusicInfo?.let { mViewModel.requestMusicUrl(it) }
                }
            }
        }
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
            playBarView?.visibility = View.VISIBLE
            currentMusicInfo?.pic?.let {
                currentMusicInfo?.duration?.let { it1 ->
                    playBarView?.setCover(it)?.setTitle(stringBuilder.toString())?.setPlay(true)
                        ?.setPosition(currentPosition)?.setDuration(duration = it1 * 1000)
                        ?.addListener(this)
                }
            }
        }
    }
}