package com.android.wy.news.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityLocalMusicBinding
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.viewmodel.MusicLocalViewModel

@Route(path = RouteManager.PATH_ACTIVITY_MUSIC_LOCAL)
class MusicLocalActivity : BaseActivity<ActivityLocalMusicBinding, MusicLocalViewModel>() {
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
        return true
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivityLocalMusicBinding {
        return ActivityLocalMusicBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): MusicLocalViewModel {
        return CommonTools.getViewModel(this, MusicLocalViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}