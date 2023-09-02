package com.android.wy.news.fragment

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.databinding.FragmentMusicBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.event.MusicListEvent
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.listener.IMusicItemChangeListener
import com.android.wy.news.manager.PlayMusicManager
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


/**
 * {
 *     "title": "畅听榜",
 *     "id": 145
 *   }
 */
class MusicFragment : BaseFragment<FragmentMusicBinding, MusicViewModel>(), OnRefreshListener,
    OnLoadMoreListener, IMusicItemChangeListener {
    private var pageStart = 1
    private var categoryId: Int = 0
    private lateinit var rvContent: MusicRecyclerView
    private lateinit var shimmerRecyclerView: ShimmerRecyclerView
    private var musicAdapter: MusicAdapter? = null
    private var isRefresh = false
    private var isLoading = false
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var playBarView: PlayBarView
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
        val parentFragment = parentFragment
        if (parentFragment is MusicTabFragment) {
            playBarView = parentFragment.getPlayBarView()
        }
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
        musicAdapter = rvContent.getMusicAdapter()

        musicAdapter?.let { it1 ->
            PlayMusicManager.initMusicInfo(
                mActivity, rvContent,
                this, it1
            )
        }
        PlayMusicManager.getLrc()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PlayMusicManager.unRegisterMusicReceiver()
        PlayMusicManager.stopMusicService()
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

    override fun onItemClick(view: View, data: MusicInfo) {
        val checkState = playBarView.checkState(data)
        if (!checkState) return
        val i = view.tag as Int
        PlayMusicManager.prepareMusic(i)
        startAnim(view, data.pic)
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
    }
}