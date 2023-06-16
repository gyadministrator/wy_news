package com.android.wy.news.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.activity.HomeActivity
import com.android.wy.news.activity.SingerMusicActivity
import com.android.wy.news.activity.SingerMvActivity
import com.android.wy.news.activity.WebFragmentActivity
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.app.App
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.FragmentMusicBinding
import com.android.wy.news.dialog.CommonOperationDialog
import com.android.wy.news.entity.OperationItemEntity
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.event.MusicEvent
import com.android.wy.news.event.MusicListEvent
import com.android.wy.news.event.MusicUrlEvent
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.listener.IMusicItemChangeListener
import com.android.wy.news.manager.LrcDesktopManager
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.MusicState
import com.android.wy.news.util.AppUtil
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.util.ToastUtil
import com.android.wy.news.view.MusicRecyclerView
import com.android.wy.news.view.PlayBarView
import com.android.wy.news.viewmodel.MusicViewModel
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    OnLoadMoreListener,
    PlayBarView.OnPlayBarListener, IMusicItemChangeListener,
    BaseNewsAdapter.OnItemAdapterListener<OperationItemEntity> {
    private var pageStart = 1
    private var categoryId: Int = 0
    private lateinit var rvContent: MusicRecyclerView
    private lateinit var shimmerRecyclerView: ShimmerRecyclerView
    private var musicAdapter: MusicAdapter? = null
    private var isRefresh = false
    private var isLoading = false
    private lateinit var refreshLayout: SmartRefreshLayout
    private var playBarView: PlayBarView? = null
    private var currentMusicInfo: MusicInfo? = null
    private var mediaHelper: MediaPlayerHelper? = null
    private lateinit var floatingBtn: FloatingActionButton

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
        floatingBtn = mBinding.floatingBtn
        shimmerRecyclerView = mBinding.shimmerRecyclerView
        shimmerRecyclerView.showShimmerAdapter()
        rvContent = mBinding.rvContent
        refreshLayout = mBinding.refreshLayout
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)
        refreshLayout.setEnableFooterFollowWhenNoMoreData(true)
        rvContent.seItemListener(this)
    }

    override fun initData() {
        mediaHelper = MediaPlayerHelper.getInstance(mActivity)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        musicAdapter = rvContent.getMusicAdapter()

        initPlayBar()
        playBarView?.let {
            musicAdapter?.let { it1 ->
                PlayMusicManager.initMusicInfo(
                    mActivity, rvContent,
                    it, this, it1
                )
            }
        }
        PlayMusicManager.getLrc()
    }

    private fun initPlayBar() {
        val s = SpTools.getString(GlobalData.SpKey.LAST_PLAY_MUSIC_KEY)
        this.currentMusicInfo = JsonUtil.parseJsonToObject(s, MusicInfo::class.java)
        if (this.currentMusicInfo != null) {
            showPlayBar()
        } else {
            hidePlayBar()
        }
    }

    private fun hidePlayBar() {
        if (mActivity is HomeActivity) {
            val homeActivity = mActivity as HomeActivity
            playBarView = homeActivity.getPlayBarView()
            playBarView?.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
        PlayMusicManager.unRegisterMusicReceiver()
        PlayMusicManager.stopMusicService()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(o: Any) {
        if (o is MusicEvent) {
            Logger.i("onEvent--->>>time:${o.time}")
            if (GlobalData.currentLrcData.size == 0) {
                PlayMusicManager.getLrc()
            }
            playBarView?.updateProgress(o.time)
            LrcDesktopManager.showDesktopLrc(mActivity, o.time.toLong())
            val position =
                CommonTools.lrcTime2Position(GlobalData.currentLrcData, o.time.toLong())
            val lrc = GlobalData.currentLrcData[position]
            playBarView?.setTitle(lrc.text)
        } else if (o is MusicUrlEvent) {
            PlayMusicManager.playMusic(o.url)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initEvent() {
        PlayMusicManager.registerMusicReceiver()
        val arguments = arguments
        if (arguments != null) {
            categoryId = arguments.getInt(mKey)
        }
        if (!TextUtils.isEmpty(GlobalData.CSRF_TOKEN)) {
            getMusicList()
        } else {
            getCookie()
        }
        /*rvContent.setOnTouchListener { _, p1 ->
            if (p1 != null) {
                when (p1.action) {
                    MotionEvent.ACTION_DOWN -> {
                        TaskUtil.removeUiThreadCallback(runnable)
                        val playPosition = PlayMusicManager.getPlayPosition()
                        if (playPosition >= 0) {
                            floatingBtn.visibility = View.VISIBLE
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        TaskUtil.runOnUiThread({
                            runnable
                        }, 3000)
                    }
                }
            }
            true
        }*/
        rvContent.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val playPosition = PlayMusicManager.getPlayPosition()
                if (playPosition >= 0) {
                    floatingBtn.visibility = View.VISIBLE
                    TaskUtil.runOnUiThread({
                        floatingBtn.visibility = View.GONE
                    }, 3000)
                }
                /*if (!rvContent.canScrollVertically(1)) {
                    //滑动到底部
                    val playPosition = PlayMusicManager.getPlayPosition()
                    if (playPosition >= 0) {
                        floatingBtn.visibility = View.VISIBLE
                    }
                }
                if (!rvContent.canScrollVertically(-1)) {
                    //滑动到顶部
                    floatingBtn.visibility = View.GONE
                }*/
            }
        })
        floatingBtn.setOnClickListener {
            scrollPosition()
        }
    }

    private fun scrollPosition() {
        val playPosition = PlayMusicManager.getPlayPosition()
        if (playPosition >= 0) {
            rvContent.smoothScrollToPosition(playPosition)
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
            if (isRefresh) {
                refreshLayout.setNoMoreData(false)
                refreshLayout.finishRefresh()
                refreshLayout.setEnableLoadMore(true)
            }
            if (isLoading) {
                refreshLayout.finishLoadMore()
            }

            if (musicList != null) {
                if (musicList.size == 0) {
                    if (isLoading) {
                        refreshLayout.setNoMoreData(true)
                    }
                } else {
                    if (isRefresh) {
                        rvContent.refreshData(musicList)
                    } else {
                        rvContent.loadData(musicList)
                    }
                }
            }

            val musicListEvent = musicAdapter?.getDataList()?.let { it1 -> MusicListEvent(it1) }
            EventBus.getDefault().postSticky(musicListEvent)

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
            PlayMusicManager.playMusic(it)
        }

        mViewModel.isSuccess.observe(this) {
            if (it) {
                getMusicList()
            }
        }

        mViewModel.msg.observe(this) {
            ToastUtil.show(it)
            refreshLayout.setEnableLoadMore(false)
            if (isRefresh) {
                refreshLayout.setNoMoreData(false)
                refreshLayout.finishRefresh(false)
            }
            if (isLoading) {
                refreshLayout.finishLoadMore(false)
            }
        }

        GlobalData.indexChange.observe(this) {
            if (it == 3) {
                PlayMusicManager.registerMusicReceiver()
                if (!EventBus.getDefault().isRegistered(this)) {
                    EventBus.getDefault().register(this)
                }
                initPlayBar()
            }
        }
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

    override fun onClickPlay(position: Int) {
        this.currentMusicInfo?.let { PlayMusicManager.setClickMusicInfo(it) }
        Logger.i("onClickPlay--->>>position:$position")
        if (mediaHelper != null) {
            if (mediaHelper!!.isPlaying()) {
                mediaHelper?.pause()
                this.currentMusicInfo?.state = MusicState.STATE_PAUSE
                rvContent.updatePosition(position)
            } else {
                if (!TextUtils.isEmpty(PlayMusicManager.getPlayUrl())) {
                    mediaHelper?.start()
                    this.currentMusicInfo?.state = MusicState.STATE_PLAY
                    rvContent.updatePosition(position)
                } else {
                    currentMusicInfo?.let { PlayMusicManager.requestMusicInfo(it) }
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
        val prev: Fragment? = fragmentManager.findFragmentByTag(PlayMusicFragment.TAG)
        if (prev != null) {
            ft?.remove(prev)?.commit()
            ft = fragmentManager.beginTransaction()
        }
        ft?.addToBackStack(null)
        val s = this.currentMusicInfo?.let { JsonUtil.parseObjectToJson(it) }
        val playMusicFragment =
            s?.let {
                PlayMusicFragment.newInstance(
                    PlayMusicManager.getPlayPosition(),
                    it,
                    PlayMusicManager.getPlayUrl()
                )
            }
        if (playMusicFragment != null) {
            ft?.let { playMusicFragment.show(ft, PlayMusicFragment.TAG) }
        }
    }

    private fun showPlayBar() {
        val stringBuilder = StringBuilder()
        val album = currentMusicInfo?.name
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
                        ?.setPosition(PlayMusicManager.getPlayPosition())
                        ?.setDuration(duration = it1 * 1000)
                        ?.showPlayContainer(true)
                        ?.addListener(this)
                }
            }
        }
    }

    override fun onItemClick(view: View, data: MusicInfo) {
        PlayMusicManager.setClickMusicInfo(data)
        val i = view.tag as Int
        PlayMusicManager.prepareMusic(i)
    }

    override fun onItemLongClick(view: View, data: MusicInfo) {
        PlayMusicManager.setLongClickMusicInfo(data)
        val stringBuilder = StringBuilder()
        val album = data.name
        val artist = data.artist
        stringBuilder.append(artist)
        if (!TextUtils.isEmpty(album)) {
            stringBuilder.append("-$album")
        }
        val activity = mActivity as AppCompatActivity
        val list = arrayListOf(
            OperationItemEntity(R.mipmap.download, AppUtil.getString(App.app, R.string.download)),
            OperationItemEntity(R.mipmap.singer, AppUtil.getString(App.app, R.string.singer)),
            OperationItemEntity(R.mipmap.album, AppUtil.getString(App.app, R.string.album)),
            OperationItemEntity(R.mipmap.state_one, AppUtil.getString(App.app, R.string.single)),
            OperationItemEntity(R.mipmap.video, AppUtil.getString(App.app, R.string.mv)),
        )
        CommonOperationDialog.show(activity, stringBuilder.toString(), list, this)
    }

    override fun onItemClickListener(view: View, data: OperationItemEntity) {
        val tag = view.tag
        val musicInfo = PlayMusicManager.getDownloadMusicInfo()
        val artistId = musicInfo?.artistid
        if (tag is Int) {
            when (tag) {
                0 -> {
                    musicInfo?.let { PlayMusicManager.requestMusicInfo(it) }
                }

                1 -> {
                    WebFragmentActivity.startActivity(mActivity, artistId.toString())
                }

                2 -> {

                }

                3 -> {
                    SingerMusicActivity.startActivity(mActivity, artistId.toString())
                }

                4 -> {
                    SingerMvActivity.startActivity(mActivity, artistId.toString())
                }

                else -> {

                }
            }
        }
    }

    override fun onItemLongClickListener(view: View, data: OperationItemEntity) {

    }
}