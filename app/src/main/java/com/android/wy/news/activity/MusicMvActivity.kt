package com.android.wy.news.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.ActivityMusicMvBinding
import com.android.wy.news.dialog.LoadingDialog
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.view.ScreenVideoView
import com.android.wy.news.viewmodel.MusicMvViewModel
import com.gyf.immersionbar.ImmersionBar

@Route(path = RouteManager.PATH_ACTIVITY_MUSIC_MV)
class MusicMvActivity : BaseActivity<ActivityMusicMvBinding, MusicMvViewModel>() {
    private var currentMusicInfo: MusicInfo? = null
    private lateinit var rlBack: RelativeLayout
    private lateinit var screenVideoView: ScreenVideoView

    companion object {
        const val MUSIC_INFO_KEY = "music_info_key"

        fun startMv(context: Context, musicInfoJson: String) {
            val intent = Intent(context, MusicMvActivity::class.java)
            intent.putExtra(MUSIC_INFO_KEY, musicInfoJson)
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
        val s = intent.getStringExtra(MUSIC_INFO_KEY)
        currentMusicInfo = JsonUtil.parseJsonToObject(s, MusicInfo::class.java)
        val stringBuilder = StringBuilder()
        val name = this.currentMusicInfo?.name
        val artist = this.currentMusicInfo?.artist
        if (!TextUtils.isEmpty(name)) {
            stringBuilder.append(name)
        }
        if (!TextUtils.isEmpty(artist)) {
            stringBuilder.append("-")
            stringBuilder.append(artist)
        }
        screenVideoView.setTitle(stringBuilder.toString())
        getMvInfo()
    }

    private fun getMvInfo() {
        LoadingDialog.show(this, "获取MV地址中...")
        val musicId = this.currentMusicInfo?.musicrid
        if (musicId != null && musicId.contains("_")) {
            val mid = musicId.substring(musicId.indexOf("_") + 1, musicId.length)
            MusicRepository.getMusicMv(mid).observe(this) {
                LoadingDialog.hide()
                val musicUrlEntity = it.getOrNull()
                val data = musicUrlEntity?.data
                val url = data?.url
                if (url != null) {
                    this.currentMusicInfo?.pic?.let { it1 -> screenVideoView.setUp(url, it1, true) }
                }
                Logger.i("MV url:$url")
                screenVideoView.play()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val s = intent?.getStringExtra(MUSIC_INFO_KEY)
        currentMusicInfo = JsonUtil.parseJsonToObject(s, MusicInfo::class.java)
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