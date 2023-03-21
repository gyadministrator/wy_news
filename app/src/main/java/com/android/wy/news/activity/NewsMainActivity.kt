package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.view.KeyEvent
import android.widget.Toast
import com.android.bottombar.activity.GYBottomActivity
import com.android.bottombar.model.GYBarItem
import com.android.bottombar.view.GYBottomBarView
import com.android.wy.news.fragment.ChannelFragment
import com.android.wy.news.fragment.LiveFragment
import com.android.wy.news.fragment.HeaderFragment
import com.android.wy.news.fragment.VideoFragment
import com.android.wy.news.R
import com.gyf.immersionbar.ImmersionBar


class NewsMainActivity : GYBottomActivity(), GYBottomBarView.IGYBottomBarChangeListener {
    private lateinit var bottomView: GYBottomBarView
    private var firstTime: Long = 0

    override fun initBarItems() {
        barItems.add(GYBarItem("头条", R.mipmap.header))
        barItems.add(GYBarItem("频道", R.mipmap.channel))
        barItems.add(GYBarItem("视频", R.mipmap.video))
        barItems.add(GYBarItem("直播", R.mipmap.live))
    }

    override fun initFragment() {
        fragments.add(HeaderFragment.newInstance())
        fragments.add(ChannelFragment.newInstance())
        fragments.add(VideoFragment.newInstance())
        fragments.add(LiveFragment.newInstance())
    }

    override fun initSelectIcons() {
        icons.add(R.mipmap.header_selected)
        icons.add(R.mipmap.channel_selected)
        icons.add(R.mipmap.video_selected)
        icons.add(R.mipmap.live_selected)
    }

    override fun initContentView(): Int {
        return R.layout.activity_news_main
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
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;//竖屏
        ImmersionBar.with(this).statusBarColor(R.color.main_bg_color)
            .navigationBarColor(R.color.main_bg_color).statusBarDarkFont(true).init()
        bottomView = findViewById(R.id.bottomView)
    }

    override fun onSelected(position: Int) {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                val secondTime = System.currentTimeMillis()
                if (secondTime - firstTime > 2000) {
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
                    firstTime = secondTime
                    return true
                } else {
                    finish()
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}