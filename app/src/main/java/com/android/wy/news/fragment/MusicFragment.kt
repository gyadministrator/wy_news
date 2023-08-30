package com.android.wy.news.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.activity.SingerAlbumActivity
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
import com.bumptech.glide.Glide
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
    private lateinit var playBarView: PlayBarView
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
        playBarView = mBinding.playBarView
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
        musicAdapter?.let { it1 ->
            PlayMusicManager.initMusicInfo(
                mActivity, rvContent,
                playBarView, this, it1
            )
        }
        PlayMusicManager.getLrc()
    }

    private fun initPlayBar() {
        val s = SpTools.getString(GlobalData.SpKey.LAST_PLAY_MUSIC_KEY)
        this.currentMusicInfo = JsonUtil.parseJsonToObject(s, MusicInfo::class.java)
        if (this.currentMusicInfo != null) {
            showPlayBar()
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
            playBarView.updateProgress(o.time)
            LrcDesktopManager.showDesktopLrc(mActivity, o.time.toLong())
            val position =
                CommonTools.lrcTime2Position(GlobalData.currentLrcData, o.time.toLong())
            val lrc = GlobalData.currentLrcData[position]
            playBarView.setTitle(lrc.text)
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
        getMusicList()

        rvContent.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val playPosition = PlayMusicManager.getPlayPosition()
                if (playPosition >= 0) {
                    floatingBtn.visibility = View.VISIBLE
                    startFloatAnim(0f, 1f)
                    TaskUtil.runOnUiThread({
                        startFloatAnim(1f, 0f)
                    }, 3000)
                }
            }
        })
        floatingBtn.setOnClickListener {
            scrollPosition()
        }
    }

    private fun startFloatAnim(start: Float, end: Float) {
        val alpha = ObjectAnimator.ofFloat(floatingBtn, "alpha", start, end)
        alpha.duration = 1000
        alpha.interpolator = LinearInterpolator()
        alpha.start()
    }

    private fun scrollPosition() {
        val playPosition = PlayMusicManager.getPlayPosition()
        if (playPosition >= 0) {
            rvContent.smoothScrollToPosition(playPosition)
        }
    }

    private fun getMusicList() {
        MusicRepository.getMusicList(categoryId, pageStart).observe(this) {
            val musicListEntity = it.getOrNull()
            val musicListData = musicListEntity?.data
            var musicList = musicListData?.musicList
            musicList = CommonTools.filterMusicList(musicList)
            if (isRefresh) {
                refreshLayout.setNoMoreData(false)
                refreshLayout.finishRefresh()
                refreshLayout.setEnableLoadMore(true)
            }
            if (isLoading) {
                refreshLayout.finishLoadMore()
            }

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
                initData()
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
        playBarView.visibility = View.VISIBLE
        currentMusicInfo?.pic?.let {
            currentMusicInfo?.duration?.let { it1 ->
                playBarView.setCover(it).setTitle(stringBuilder.toString())
                    .setPosition(PlayMusicManager.getPlayPosition())
                    .setDuration(duration = it1 * 1000)
                    .showPlayContainer(true)
                    .addListener(this)
            }
        }

        val animatorSet = AnimatorSet()
        animatorSet.duration = 1000
        animatorSet.interpolator = LinearInterpolator()
        val alpha = ObjectAnimator.ofFloat(playBarView, "alpha", 0f, 1f)
        val translationY = ObjectAnimator.ofFloat(playBarView, "translationY", 50f, 0f)
        animatorSet.playTogether(alpha, translationY)
        animatorSet.start()
    }

    private fun hidePlayBar() {
        val animatorSet = AnimatorSet()
        animatorSet.duration = 1000
        animatorSet.interpolator = LinearInterpolator()
        val alpha = ObjectAnimator.ofFloat(playBarView, "alpha", 1f, 0f)
        val translationY = ObjectAnimator.ofFloat(playBarView, "translationY", 50f, 0f)
        animatorSet.playTogether(alpha, translationY)
        animatorSet.start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                playBarView.visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
    }

    override fun onItemClick(view: View, data: MusicInfo) {
        PlayMusicManager.setClickMusicInfo(data)
        val i = view.tag as Int
        PlayMusicManager.prepareMusic(i)
        startAnim(view, data.pic)
        playBarView.setCover(data.pic)
    }

    private fun startAnim(view: View, pic: String) {
        val startA = IntArray(2)
        view.getLocationInWindow(startA)
        val change = FloatArray(2)
        val ivAnim = mBinding.ivAnim
        CommonTools.loadImage(pic, ivAnim)
        ivAnim.visibility = View.VISIBLE
        val parentC = IntArray(2)
        mBinding.flContent.getLocationInWindow(parentC)

        val playContainer = playBarView.getPlayContainer()
        val endB = IntArray(2)
        playContainer.getLocationInWindow(endB)

        val startX = startA[0] - parentC[0]
        val startY = startA[1] - parentC[1]
        val toX = endB[0] - parentC[0]
        val toY = endB[1] - parentC[1]
        val path = Path()
        path.moveTo(startX.toFloat(), startY.toFloat())
        path.quadTo(
            (startX + toX) / 2f, startY.toFloat(), toX.toFloat(),
            toY.toFloat()
        )
        val pathMeasure = PathMeasure(path, false)
        val valueAnimator = ValueAnimator.ofFloat(0f, pathMeasure.length)
        valueAnimator.duration = 1000
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { p0 ->
            val i = p0.animatedValue as Float
            pathMeasure.getPosTan(i, change, null)
            ivAnim.translationX = change[0]
            ivAnim.translationY = change[1]
        }
        valueAnimator.start()

        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                ivAnim.visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })
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
                    SingerAlbumActivity.startActivity(mActivity, artistId.toString())
                }

                3 -> {
                    SingerMusicActivity.startActivity(mActivity, artistId.toString(), 0)
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