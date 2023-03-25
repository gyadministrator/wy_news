package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.bottombar.activity.GYBottomActivity
import com.android.bottombar.model.GYBarItem
import com.android.bottombar.view.GYBottomBarView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.fragment.ClassifyTabFragment
import com.android.wy.news.fragment.LiveTabFragment
import com.android.wy.news.fragment.TopTabFragment
import com.android.wy.news.fragment.VideoTabFragment
import com.android.wy.news.view.MarqueeTextView
import com.android.wy.news.viewmodel.NewsMainViewModel
import com.gyf.immersionbar.ImmersionBar
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import java.util.*


class HomeActivity : GYBottomActivity(), GYBottomBarView.IGYBottomBarChangeListener {
    private lateinit var bottomView: GYBottomBarView
    private var firstTime: Long = 0
    private lateinit var mViewModel: NewsMainViewModel
    private lateinit var marqueeTextView: MarqueeTextView
    private val list = ArrayList<String>()

    override fun initBarItems() {
        barItems.add(GYBarItem("头条", R.mipmap.header))
        barItems.add(GYBarItem("频道", R.mipmap.channel))
        barItems.add(GYBarItem("视频", R.mipmap.video))
        barItems.add(GYBarItem("直播", R.mipmap.live))
    }

    override fun onDestroy() {
        super.onDestroy()
        marqueeTextView.stopScroll()
    }

    override fun initFragment() {
        fragments.add(TopTabFragment.newInstance())
        fragments.add(ClassifyTabFragment.newInstance())
        fragments.add(VideoTabFragment.newInstance())
        fragments.add(LiveTabFragment.newInstance())
        mViewModel = CommonTools.getViewModel(this, NewsMainViewModel::class.java)

        mViewModel.dataList.observe(this) {
            if (it != null) {
                list.clear()
                marqueeTextView.stopScroll()
                for (i in 0 until it.size) {
                    val rollHotWord = it[i]
                    val hotWord = rollHotWord.hotWord
                    list.add(hotWord)
                }
            }
            marqueeTextView.setList(list)
            marqueeTextView.startScroll()
        }
        mViewModel.getHotWord()
    }

    override fun initSelectIcons() {
        icons.add(R.mipmap.header_selected)
        icons.add(R.mipmap.channel_selected)
        icons.add(R.mipmap.video_selected)
        icons.add(R.mipmap.live_selected)
    }

    override fun initContentView(): Int {
        return R.layout.activity_home
    }

    override fun getBottomBarView(): GYBottomBarView {
        return bottomView
    }

    override fun initContainerId(): Int {
        return R.id.fl_container
    }

    override fun initChangeListener(): GYBottomBarView.IGYBottomBarChangeListener {
        return this
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun initView() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//竖屏
        ImmersionBar.with(this).statusBarColor(R.color.status_bar_color)
            .navigationBarColor(R.color.main_bg_color).statusBarDarkFont(false).init()
        bottomView = findViewById(R.id.bottomView)
        marqueeTextView = findViewById(R.id.marqueeTextView)
        list.add("热词加载中...")
        marqueeTextView.setList(list)
        marqueeTextView.startScroll()
    }

    override fun onSelected(position: Int) {
        if (position == 2) {
            hideSearch()
        } else {
            showSearch()
        }
    }

    @SuppressLint("RestrictedApi")
    fun getShowFragment(): Fragment? {
        val fragments: List<Fragment> = supportFragmentManager.fragments
        var fragment: Fragment? = null
        for (i in fragments.indices) {
            fragment = fragments[i]
            if (fragment.isAdded && fragment.isMenuVisible) {
                break
            }
        }
        return fragment
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return
        }
        val secondTime = System.currentTimeMillis()
        if (secondTime - firstTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
            firstTime = secondTime
        } else {
            finish()
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        JCVideoPlayer.releaseAllVideos()
    }

    fun hideSearch() {
        val rlSearch = findViewById<RelativeLayout>(R.id.rl_search)
        rlSearch.visibility = View.GONE
    }

    fun showSearch() {
        val rlSearch = findViewById<RelativeLayout>(R.id.rl_search)
        rlSearch.visibility = View.VISIBLE
    }
}