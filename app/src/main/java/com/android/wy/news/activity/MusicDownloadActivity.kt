package com.android.wy.news.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityMusicDownloadBinding
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.viewmodel.MusicDownloadViewModel

@Route(path = RouteManager.PATH_ACTIVITY_MUSIC_DOWNLOAD)
class MusicDownloadActivity : BaseActivity<ActivityMusicDownloadBinding, MusicDownloadViewModel>() {
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

    override fun getViewBinding(): ActivityMusicDownloadBinding {
        return ActivityMusicDownloadBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): MusicDownloadViewModel {
        return CommonTools.getViewModel(this, MusicDownloadViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}