package com.android.wy.news.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.ScreenVideoAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.ActivityVideoFullBinding
import com.android.wy.news.entity.ScreenVideoEntity
import com.android.wy.news.listener.OnViewPagerListener
import com.android.wy.news.manager.VideoLayoutManager
import com.android.wy.news.viewmodel.VideoFullViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener

class VideoFullActivity : BaseActivity<ActivityVideoFullBinding, VideoFullViewModel>(),
    OnLoadMoreListener, BaseNewsAdapter.OnItemAdapterListener<ScreenVideoEntity>,
    OnViewPagerListener, ScreenVideoAdapter.OnScreenVideoListener {
    private var currentPage = -1
    private lateinit var rvContent: RecyclerView
    private lateinit var rlBack: RelativeLayout
    private var isLoading = false
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var layoutManager: VideoLayoutManager
    private lateinit var screenVideoAdapter: ScreenVideoAdapter
    private var currentPosition = 0

    companion object {
        const val PAGE = "video_page"
        const val VIDEO_LIST = "video_list"

        fun startFullScreen(
            page: Int, videoInfoList: ArrayList<ScreenVideoEntity>, context: Context
        ) {
            val intent = Intent(context, VideoFullActivity::class.java)
            intent.putExtra(PAGE, page)
            val gson = Gson()
            intent.putExtra(VIDEO_LIST, gson.toJson(videoInfoList))
            context.startActivity(intent)
            val activity = context as Activity
            activity.overridePendingTransition(R.anim.zoomin, R.anim.zoomout)
        }
    }

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
        return false
    }

    override fun initView() {
        ImmersionBar.with(this).statusBarColor(R.color.black).navigationBarColor(R.color.black)
            .statusBarDarkFont(false).init()
        rvContent = mBinding.rvContent
        rlBack = mBinding.rlBack
        refreshLayout = mBinding.refreshLayout
        refreshLayout.setOnLoadMoreListener(this)
        refreshLayout.setEnableRefresh(false)

        screenVideoAdapter = ScreenVideoAdapter(this, this)
        layoutManager = VideoLayoutManager(mActivity, OrientationHelper.VERTICAL, false)
        rvContent.layoutManager = layoutManager
        rvContent.adapter = screenVideoAdapter
        layoutManager.setOnViewPagerListener(this)
    }

    override fun initData() {
        val gson = Gson()
        val intent = intent
        currentPage = intent.getIntExtra(PAGE, -1)
        val videoListJson = intent.getStringExtra(VIDEO_LIST)
        val dataList = gson.fromJson<ArrayList<ScreenVideoEntity>>(
            videoListJson, object : TypeToken<ArrayList<ScreenVideoEntity>>() {}.type
        )
        screenVideoAdapter.refreshData(dataList)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val gson = Gson()
            currentPage = intent.getIntExtra(PAGE, -1)
            val videoListJson = intent.getStringExtra(VIDEO_LIST)
            val dataList = gson.fromJson<ArrayList<ScreenVideoEntity>>(
                videoListJson, object : TypeToken<ArrayList<ScreenVideoEntity>>() {}.type
            )
            screenVideoAdapter.refreshData(dataList)
        }

    }

    override fun initEvent() {
        rlBack.setOnClickListener {
            finish()
        }
    }

    override fun getViewBinding(): ActivityVideoFullBinding {
        return ActivityVideoFullBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): VideoFullViewModel {
        return CommonTools.getViewModel(this, VideoFullViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {
        mViewModel.topNewsList.observe(this) {
            if (isLoading) {
                refreshLayout.finishLoadMore()
            }
            if (it.size == 0) {
                if (isLoading) {
                    Logger.e("当前[$currentPage]页无数据,为你加载下一页")
                    Toast.makeText(
                        this, "当前[$currentPage]页无数据,为你加载下一页", Toast.LENGTH_SHORT
                    ).show()
                    getVideoData()
                }
            } else {
                val i = screenVideoAdapter.loadMoreData(it)
                if (isLoading) {
                    //加载完成，直接滑动到新加载的第一条数据
                    rvContent.scrollToPosition(i + 1)
                    playVideo(i + 1)
                }
            }
            isLoading = false
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        isLoading = true
        getVideoData()
    }

    private fun getVideoData() {
        currentPage++
        mViewModel.getTopNews(currentPage)
    }

    override fun onItemClickListener(view: View, data: ScreenVideoEntity) {

    }

    override fun onInitComplete() {
        playVideo(0)
    }

    override fun onPageRelease(isNext: Boolean, position: Int) {
        releaseVideo(position)
    }

    private fun releaseVideo(position: Int) {

    }

    override fun onPageSelected(position: Int, isBottom: Boolean) {
        currentPosition = position
        val canScrollVertically = rvContent.canScrollVertically(-1)
        if (!canScrollVertically) {
            //滑动到顶部
            finish()
            return
        }
        playVideo(position)
    }

    private fun playVideo(position: Int) {
        Handler(Looper.getMainLooper()).post {
            val holder = rvContent.findViewHolderForAdapterPosition(position)
            if (holder is ScreenVideoAdapter.ScreenViewHolder) {
                holder.playVideo.play()
            }
        }
    }

    override fun onPlayFinish() {
        if (currentPosition + 1 > screenVideoAdapter.itemCount) {
            //加载更多
            getVideoData()
        } else {
            if (!isLoading) {
                currentPosition += 1
                layoutManager.scrollToPosition(currentPosition)
                playVideo(currentPosition)
            }
        }
    }
}