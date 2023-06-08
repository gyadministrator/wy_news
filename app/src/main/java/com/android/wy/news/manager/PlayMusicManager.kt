package com.android.wy.news.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.app.App
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SpTools
import com.android.wy.news.dialog.LoadingDialog
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.event.LrcChangeEvent
import com.android.wy.news.event.MusicInfoEvent
import com.android.wy.news.event.MusicUrlEvent
import com.android.wy.news.event.PlayFinishEvent
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.music.MusicState
import com.android.wy.news.notification.NotificationHelper
import com.android.wy.news.service.MusicNotifyService
import com.android.wy.news.sql.RecordMusicEntity
import com.android.wy.news.sql.RecordMusicRepository
import com.android.wy.news.util.AppUtil
import com.android.wy.news.util.DownloadFileUtil
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.util.ToastUtil
import com.android.wy.news.view.PlayBarView
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/30 9:14
  * @Version:        1.0
  * @Description:    
 */
object PlayMusicManager {
    private var mServiceIntent: Intent? = null
    private var currentPosition = -1
    private var currentMusicInfo: MusicInfo? = null
    private var currentDownloadMusicInfo: MusicInfo? = null
    private var currentPlayUrl: String? = ""
    private var isLongClick = false
    private var lifecycleOwner: LifecycleOwner? = null
    private var musicAdapter: MusicAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var playBarView: WeakReference<PlayBarView>? = null
    private var musicReceiver: MusicReceiver? = null
    private var activity: FragmentActivity? = null

    fun initMusicInfo(
        activity: Activity,
        recyclerView: RecyclerView,
        playBarView: PlayBarView?,
        lifecycleOwner: LifecycleOwner,
        musicAdapter: MusicAdapter
    ) {
        if (activity is FragmentActivity) {
            this.activity = activity
        }
        this.recyclerView = recyclerView
        this.playBarView = WeakReference(playBarView)
        this.lifecycleOwner = lifecycleOwner
        this.musicAdapter = musicAdapter
        val s = SpTools.getString(GlobalData.SpKey.LAST_PLAY_MUSIC_KEY)
        this.currentMusicInfo = JsonUtil.parseJsonToObject(s, MusicInfo::class.java)
    }

    fun setLongClickMusicInfo(musicInfo: MusicInfo) {
        this.isLongClick = true
        this.currentDownloadMusicInfo = musicInfo
    }

    fun setClickMusicInfo(musicInfo: MusicInfo) {
        this.isLongClick = false
        this.currentMusicInfo = musicInfo
    }

    fun prepareMusic(
        position: Int
    ) {
        if (currentPosition == position) return
        val dataList = musicAdapter?.getDataList()
        if (position < 0) currentPosition = 0
        if (dataList != null) {
            if (position > dataList.size) currentPosition = dataList.size - 1
        }
        if (dataList != null) {
            if (position < dataList.size) {
                currentPosition = position
                val musicInfo = dataList[currentPosition]

                this.currentMusicInfo = musicInfo
                val listenFee = musicInfo.isListenFee
                if (listenFee) {
                    ToastUtil.show("目前VIP歌曲暂不支持免费播放")
                    return
                }

                this.currentMusicInfo?.state = MusicState.STATE_PREPARE

                val json = JsonUtil.parseObjectToJson(
                    currentMusicInfo!!
                )
                val musicInfoEvent =
                    MusicInfoEvent(json)
                EventBus.getDefault().postSticky(musicInfoEvent)

                musicAdapter?.setSelectedIndex(currentPosition)

                requestMusicInfo(musicInfo)

                SpTools.putString(
                    GlobalData.SpKey.LAST_PLAY_MUSIC_KEY, json
                )

                TaskUtil.runOnThread {
                    val recordMusicRepository = RecordMusicRepository(App.app.applicationContext)
                    val mid = currentMusicInfo?.musicrid
                    val entity = mid?.let { recordMusicRepository.getRecordMusicByMid(it) }
                    if (entity == null) {
                        val recordMusicEntity = mid?.let { RecordMusicEntity(0, it, json) }
                        recordMusicEntity?.let { recordMusicRepository.addRecordMusic(it) }
                    }
                }
            }
        }
    }

    fun requestMusicInfo(musicInfo: MusicInfo) {
        activity?.let { LoadingDialog.show(it, "请稍等...") }
        val musicId = musicInfo.musicrid
        if (musicId.contains("_")) {
            val mid = musicId.substring(musicId.indexOf("_") + 1, musicId.length)
            lifecycleOwner?.let { it ->
                MusicRepository.getMusicUrl(mid).observe(it) {
                    LoadingDialog.hide()
                    val musicUrlEntity = it.getOrNull()
                    Logger.i("mid:$mid---->>>musicUrlEntity:$musicUrlEntity")
                    if (musicUrlEntity != null) {
                        val musicUrlData = musicUrlEntity.data
                        if (musicUrlEntity.code == -1) {
                            ToastUtil.show("该歌曲为付费歌曲,暂时不能免费播放")
                        }
                        val url = musicUrlData?.url
                        Logger.i("mid:$mid---->>>url:$url")
                        if (AppUtil.isBackground(App.app)) {
                            Logger.i("requestMusicUrl--->>>app onBack")
                            val musicUrlEvent = MusicUrlEvent(url)
                            EventBus.getDefault().postSticky(musicUrlEvent)
                        } else {
                            playMusic(url)
                        }
                    }
                }
            }
        }
    }

    fun playMusic(url: String?) {
        this.currentPlayUrl = url
        if (isLongClick) {
            url?.let { it1 -> startDownload(it1) }
            return
        }
        GlobalData.playUrlChange.postValue(url)
        startMusicService()
        getLrc()
    }

    fun startMusicService() {
        if (mServiceIntent == null) {
            mServiceIntent = Intent(App.app, MusicNotifyService::class.java)
        }
        mServiceIntent?.action = MusicNotifyService.MUSIC_PREPARE_ACTION
        mServiceIntent?.putExtra(
            MusicNotifyService.MUSIC_INFO_KEY,
            this.currentMusicInfo?.let { JsonUtil.parseObjectToJson(it) }
        )
        mServiceIntent?.putExtra(MusicNotifyService.MUSIC_URL_KEY, this.currentPlayUrl)
        App.app.startService(mServiceIntent)
    }

    fun stopMusicService() {
        if (mServiceIntent != null) {
            App.app.stopService(mServiceIntent)
        }
    }

    fun playNext() {
        Logger.i("playNext: ")
        val dataList = musicAdapter?.getDataList()
        if (currentPosition + 1 > dataList!!.size - 1) currentPosition = dataList.size - 2
        //滑动到播放的歌曲
        recyclerView?.scrollToPosition(currentPosition + 1)
        //下一曲
        prepareMusic(currentPosition + 1)
    }

    private fun playPre() {
        Logger.i("playPre: ")
        if (currentPosition - 1 < 0) currentPosition = 1
        //滑动到播放的歌曲
        recyclerView?.scrollToPosition(currentPosition - 1)
        //上一曲
        prepareMusic(currentPosition - 1)
    }

    fun getLrc() {
        val musicId = this.currentMusicInfo?.musicrid
        if (musicId != null && musicId.contains("_")) {
            val mid = musicId.substring(musicId.indexOf("_") + 1, musicId.length)
            lifecycleOwner?.let { it ->
                MusicRepository.getMusicLrc(mid).observe(it) {
                    val musicLrcEntity = it.getOrNull()
                    if (musicLrcEntity != null) {
                        val musicLrcData = musicLrcEntity.data
                        if (musicLrcData != null) {
                            val lrcList = musicLrcData.lrclist
                            if (lrcList.isNotEmpty()) {
                                val currentLrcList = CommonTools.parseLrc(lrcList)
                                GlobalData.currentLrcData.clear()
                                GlobalData.currentLrcData.addAll(currentLrcList)
                                EventBus.getDefault().postSticky(LrcChangeEvent())
                            }
                        }
                    }
                }
            }
        }
    }


    private fun startDownload(url: String) {
        if (TextUtils.isEmpty(url)) {
            ToastUtil.show("下载地址为空")
            return
        }
        this.currentDownloadMusicInfo?.let { DownloadFileUtil.download(it, url) }
    }

    class MusicReceiver : BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1 != null) {
                val action = p1.action
                Logger.i("onReceive---->>>action:$action")
                when (action) {
                    MusicNotifyService.MUSIC_STATE_ACTION -> {
                        playBarView?.get()?.getPlayContainer()?.performClick()
                    }

                    MusicNotifyService.MUSIC_PLAY_ACTION -> {
                        LoadingDialog.hide()
                        playBarView?.get()?.setPlay(true)
                        currentMusicInfo?.state = MusicState.STATE_PLAY
                        musicAdapter?.setSelectedIndex(currentPosition)
                    }

                    MusicNotifyService.MUSIC_PAUSE_ACTION -> {
                        currentMusicInfo?.state = MusicState.STATE_PAUSE
                        playBarView?.get()?.setPlay(false)
                        musicAdapter?.setSelectedIndex(currentPosition)
                    }

                    MusicNotifyService.MUSIC_PRE_ACTION -> {
                        playPre()
                    }

                    MusicNotifyService.MUSIC_NEXT_ACTION -> {
                        playNext()
                    }

                    MusicNotifyService.MUSIC_COMPLETE_ACTION -> {
                        EventBus.getDefault().postSticky(PlayFinishEvent())
                        //最后一首播放完，播放第一首
                        val dataList = musicAdapter?.getDataList()
                        if (dataList != null) {
                            if (currentPosition + 1 > dataList.size - 1) {
                                currentPosition = 0
                            }
                        }
                        playNext()
                    }

                    MusicNotifyService.MUSIC_CLOSE_ACTION -> {
                        val background = AppUtil.isBackground(App.app)
                        if (background) {
                            NotificationHelper.cancelNotification(GlobalData.MUSIC_NOTIFY_ID)
                        }
                        //关闭音乐服务
                        stopMusicService()
                    }

                    MusicNotifyService.MUSIC_LOCK_ACTION -> {
                        GlobalData.isLock = !GlobalData.isLock
                        startMusicService()
                    }

                    else -> {

                    }
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun registerMusicReceiver() {
        if (musicReceiver == null) {
            musicReceiver = MusicReceiver()
            val filter = IntentFilter()
            filter.addAction(MusicNotifyService.MUSIC_PLAY_ACTION)
            filter.addAction(MusicNotifyService.MUSIC_PAUSE_ACTION)
            filter.addAction(MusicNotifyService.MUSIC_NEXT_ACTION)
            filter.addAction(MusicNotifyService.MUSIC_PRE_ACTION)
            filter.addAction(MusicNotifyService.MUSIC_COMPLETE_ACTION)
            filter.addAction(MusicNotifyService.MUSIC_STATE_ACTION)
            filter.addAction(MusicNotifyService.MUSIC_CLOSE_ACTION)
            filter.addAction(MusicNotifyService.MUSIC_LOCK_ACTION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                App.app.registerReceiver(musicReceiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                App.app.registerReceiver(musicReceiver, filter)
            }
        }
    }

    fun unRegisterMusicReceiver() {
        if (musicReceiver != null) {
            App.app.unregisterReceiver(musicReceiver)
            musicReceiver = null
        }
    }

    fun getPlayPosition(): Int {
        Logger.i("getPlayPosition--->>>$currentPosition")
        return currentPosition
    }

    fun getPlayUrl(): String? {
        return currentPlayUrl
    }

    fun getPlayMusicInfo(): MusicInfo? {
        return currentMusicInfo
    }
}