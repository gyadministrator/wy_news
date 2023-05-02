package com.android.wy.news.fragment

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
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
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.MusicState
import com.android.wy.news.music.lrc.LrcFragment
import com.android.wy.news.service.MusicService
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
    private var isBind = false
    private var mServiceIntent: Intent? = null
    private var musicBinder: MusicService.MusicBinder? = null
    private var musicService: MusicService? = null
    private lateinit var refreshLayout: SmartRefreshLayout
    private var playBarView: PlayBarView? = null
    private var currentPosition = -1
    private var currentMusicInfo: MusicInfo? = null
    private var currentPlayUrl: String? = ""
    private var musicReceiver: MusicReceiver? = null
    private var mediaHelper: MediaPlayerHelper? = null

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
            playBarView?.setPlay(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(o: Any) {
        if (o is MusicEvent) {
            Logger.i("onEvent--->>>time:${o.time}")
            playBarView?.setPlay(true)
            playBarView?.updateProgress(o.time)
        }
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
        startMusicService()
    }

    private fun registerMusicReceiver() {
        musicReceiver = MusicReceiver(this)
        val filter = IntentFilter()
        filter.addAction(MusicService.MUSIC_PLAY_ACTION)
        filter.addAction(MusicService.MUSIC_PAUSE_ACTION)
        filter.addAction(MusicService.MUSIC_NEXT_ACTION)
        filter.addAction(MusicService.MUSIC_PRE_ACTION)
        filter.addAction(MusicService.MUSIC_COMPLETE_ACTION)
        mActivity.registerReceiver(musicReceiver, filter)
    }

    private fun unRegisterMusicReceiver() {
        if (musicReceiver != null) {
            mActivity.unregisterReceiver(musicReceiver)
        }
    }

    private fun startMusicService() {
        if (mServiceIntent == null) {
            mServiceIntent = Intent(mActivity, MusicService::class.java)
        }
        unBind()
        if (!isBind) {
            mActivity.bindService(mServiceIntent, connection, Context.BIND_AUTO_CREATE)
            isBind = true
        }
        registerMusicReceiver()
    }

    private fun unBind() {
        if (isBind) {
            mActivity.unbindService(connection)
            unRegisterMusicReceiver()
            isBind = false
        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            musicBinder = service as (MusicService.MusicBinder)
            musicService = musicBinder?.getService()
            this@MusicFragment.currentMusicInfo?.let {
                this@MusicFragment.currentPlayUrl?.let { it1 ->
                    musicBinder?.setMusic(
                        musicInfo = it, it1
                    )
                }
            }
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
                playBarView?.setPlay(false)
                this.currentMusicInfo?.state = MusicState.STATE_PAUSE
                musicAdapter.setSelectedIndex(position)
            } else {
                if (!TextUtils.isEmpty(currentPlayUrl)) {
                    mediaHelper?.start()
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
            playBarView?.visibility = View.VISIBLE
            currentMusicInfo?.pic?.let {
                currentMusicInfo?.duration?.let { it1 ->
                    playBarView?.setCover(it)?.setTitle(stringBuilder.toString())?.setPlay(true)
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
                    MusicService.MUSIC_PLAY_ACTION -> {
                        this.musicFragment?.playBarView?.showLoading(false)
                        this.musicFragment?.currentMusicInfo?.state = MusicState.STATE_PLAY
                        this.musicFragment?.currentPosition?.let {
                            this.musicFragment?.musicAdapter?.setSelectedIndex(
                                it
                            )
                        }
                        this.musicFragment?.showPlayBar()
                    }

                    MusicService.MUSIC_PAUSE_ACTION -> {
                        this.musicFragment?.currentMusicInfo?.state = MusicState.STATE_PAUSE
                        this.musicFragment?.currentPosition?.let {
                            this.musicFragment?.musicAdapter?.setSelectedIndex(
                                it
                            )
                        }
                    }

                    MusicService.MUSIC_PRE_ACTION -> {

                    }

                    MusicService.MUSIC_NEXT_ACTION -> {

                    }

                    MusicService.MUSIC_COMPLETE_ACTION -> {
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