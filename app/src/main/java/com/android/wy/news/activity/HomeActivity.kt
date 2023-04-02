package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.bottombar.activity.GYBottomActivity
import com.android.bottombar.model.GYBarItem
import com.android.bottombar.view.GYBottomBarView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.fragment.ClassifyTabFragment
import com.android.wy.news.fragment.LiveTabFragment
import com.android.wy.news.fragment.TopTabFragment
import com.android.wy.news.fragment.VideoTabFragment
import com.android.wy.news.manager.ThreadExecutorManager
import com.android.wy.news.view.MarqueeTextView
import com.android.wy.news.viewmodel.NewsMainViewModel
import com.gyf.immersionbar.ImmersionBar
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import java.util.*


class HomeActivity : GYBottomActivity(), GYBottomBarView.IGYBottomBarChangeListener {
    private lateinit var bottomView: GYBottomBarView
    private lateinit var tvCity: TextView
    private var firstTime: Long = 0
    private lateinit var mViewModel: NewsMainViewModel
    private lateinit var marqueeTextView: MarqueeTextView
    private lateinit var ivSetting: ImageView
    private val list = ArrayList<String>()

    override fun initBarItems() {
        barItems.add(GYBarItem("头条", R.mipmap.top))
        barItems.add(GYBarItem("频道", R.mipmap.classify))
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
            if (it != null && it.size > 0) {
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
        ThreadExecutorManager.mInstance.startExecute {
            mViewModel.getHotWord()
        }
    }

    override fun initSelectIcons() {
        icons.add(R.mipmap.top_p)
        icons.add(R.mipmap.classify_p)
        icons.add(R.mipmap.video_p)
        icons.add(R.mipmap.live_p)
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
            .navigationBarColor(R.color.white).statusBarDarkFont(false).init()
        bottomView = findViewById(R.id.bottomView)

        tvCity = findViewById(R.id.tv_city)
        tvCity.text = Constants.currentCity

        marqueeTextView = findViewById(R.id.marqueeTextView)
        list.add("热词加载中...")
        marqueeTextView.setList(list)
        marqueeTextView.startScroll()

        ivSetting = findViewById(R.id.iv_setting)
        ivSetting.setOnClickListener {
            SettingActivity.startSettingActivity(this)
        }
    }

    override fun onSelected(position: Int) {
        if (position == 2) {
            hideSearch()
            ImmersionBar.with(this).statusBarColor(R.color.black)
                .navigationBarColor(R.color.black).statusBarDarkFont(false).init()
            bottomView.setBackgroundResource(R.color.black)
        } else {
            showSearch()
            ImmersionBar.with(this).statusBarColor(R.color.status_bar_color)
                .navigationBarColor(R.color.white).statusBarDarkFont(false).init()
            bottomView.setBackgroundResource(R.color.white)
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

    private fun hideSearch() {
        val rlSearch = findViewById<LinearLayout>(R.id.rl_search)
        rlSearch.visibility = View.GONE
    }

    private fun showSearch() {
        val rlSearch = findViewById<LinearLayout>(R.id.rl_search)
        rlSearch.visibility = View.VISIBLE
    }
}