package com.android.wy.news.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.ActivityMusicMvBinding
import com.android.wy.news.dialog.LoadingDialog
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.view.ScreenVideoView
import com.android.wy.news.viewmodel.MusicMvViewModel
import com.gyf.immersionbar.ImmersionBar

@Route(path = RouteManager.PATH_ACTIVITY_MUSIC_MV)
class MusicMvActivity : BaseActivity<ActivityMusicMvBinding, MusicMvViewModel>() {
    private lateinit var rlBack: RelativeLayout
    private lateinit var screenVideoView: ScreenVideoView
    private var musicId = ""
    private var pic = ""

    companion object {
        private const val MUSIC_TITLE_KEY = "music_title_key"
        private const val MUSIC_PIC_KEY = "music_pic_key"
        private const val MUSIC_RID_KEY = "music_rid_key"

        fun startMv(context: Context, title: String, pic: String, musicRid: String) {
            val intent = Intent(context, MusicMvActivity::class.java)
            intent.putExtra(MUSIC_TITLE_KEY, title)
            intent.putExtra(MUSIC_PIC_KEY, pic)
            intent.putExtra(MUSIC_RID_KEY, musicRid)
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
        return true
    }

    override fun initView() {
        ImmersionBar.with(this).statusBarColor(R.color.black).navigationBarColor(R.color.black)
            .statusBarDarkFont(false).init()
        rlBack = mBinding.rlBack
        screenVideoView = mBinding.screenView
    }

    override fun initData() {
        val title = intent.getStringExtra(MUSIC_TITLE_KEY)
        pic = intent.getStringExtra(MUSIC_PIC_KEY).toString()
        musicId = intent.getStringExtra(MUSIC_RID_KEY).toString()
        title?.let { screenVideoView.setTitle(it) }
        getMvInfo()
    }

    private fun getMvInfo() {
        LoadingDialog.show(this, "获取MV地址中...")
        if (musicId.contains("_")) {
            musicId = musicId.substring(musicId.indexOf("_") + 1, musicId.length)
        }
        MusicRepository.getMusicMv(musicId).observe(this) {
            LoadingDialog.hide()
            val musicUrlEntity = it.getOrNull()
            val data = musicUrlEntity?.data
            val url = data?.url
            if (url != null) {
                screenVideoView.setUp(url, pic, true)
            }
            Logger.i("MV url:$url")
            screenVideoView.play()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val title = intent?.getStringExtra(MUSIC_TITLE_KEY)
        pic = intent?.getStringExtra(MUSIC_PIC_KEY).toString()
        musicId = intent?.getStringExtra(MUSIC_RID_KEY).toString()
        title?.let { screenVideoView.setTitle(it) }
        getMvInfo()
    }

    override fun initEvent() {
        rlBack.setOnClickListener {
            finish()
        }
    }

    override fun getViewBinding(): ActivityMusicMvBinding {
        return ActivityMusicMvBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): MusicMvViewModel {
        return CommonTools.getViewModel(this, MusicMvViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }
}