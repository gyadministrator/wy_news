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
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.app.App
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SpTools
import com.android.wy.news.dialog.LoadingDialog
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.entity.music.MusicLrcEntity
import com.android.wy.news.entity.music.MusicUrlEntity
import com.android.wy.news.event.LrcChangeEvent
import com.android.wy.news.event.MusicInfoEvent
import com.android.wy.news.event.MusicUrlEvent
import com.android.wy.news.event.PlayFinishEvent
import com.android.wy.news.http.HttpController
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
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
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Response

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/30 9:14
  * @Version:        1.0
  * @Description:    
 */
object PlayMusicManager {
    private var mServiceIntent: Intent? = null
    private var currentPosition = -1
    private var currentMusicInfo = MutableLiveData<MusicInfo?>()
    private var currentDownloadMusicInfo: MusicInfo? = null
    private var currentPlayUrl: String? = ""
    private var lifecycleOwner: LifecycleOwner? = null
    private var musicAdapter: MusicAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var musicReceiver: MusicReceiver? = null
    private var activity: FragmentActivity? = null

    fun initMusicInfo(
        activity: Activity,
        recyclerView: RecyclerView,
        lifecycleOwner: LifecycleOwner,
        musicAdapter: MusicAdapter
    ) {
        if (activity is FragmentActivity) {
            this.activity = activity
        }
        this.recyclerView = recyclerView
        this.lifecycleOwner = lifecycleOwner
        this.musicAdapter = musicAdapter
        val s = SpTools.getString(GlobalData.SpKey.LAST_PLAY_MUSIC_KEY)
        val musicInfo = JsonUtil.parseJsonToObject(s, MusicInfo::class.java)
        this.currentMusicInfo.postValue(musicInfo)
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

                this.currentMusicInfo.postValue(musicInfo)
                val listenFee = musicInfo.isListenFee
                if (listenFee) {
                    ToastUtil.show("目前VIP歌曲暂不支持免费播放")
                    return
                }

                musicInfo.state = MusicState.STATE_PREPARE

                val json = JsonUtil.parseObjectToJson(musicInfo)
                val musicInfoEvent = MusicInfoEvent(json)
                EventBus.getDefault().postSticky(musicInfoEvent)

                musicAdapter?.setSelectedIndex(currentPosition)

                requestMusicInfo(musicInfo)

                SpTools.putString(
                    GlobalData.SpKey.LAST_PLAY_MUSIC_KEY, json
                )

                TaskUtil.runOnThread {
                    val recordMusicRepository = RecordMusicRepository(App.app.applicationContext)
                    val mid = musicInfo.musicrid
                    val entity = recordMusicRepository.getRecordMusicByMid(mid)
                    if (entity == null) {
                        val recordMusicEntity = RecordMusicEntity(0, mid, json)
                        recordMusicRepository.addRecordMusic(recordMusicEntity)
                    }
                }
            }
        }
    }

    fun requestMusicInfo(musicInfo: MusicInfo) {
        activity?.let { LoadingDialog.show(GlobalData.MUSIC_LOADING_TAG, it, "加载歌曲...",false) }
        val musicId = musicInfo.musicrid
        if (musicId.contains("_")) {
            val mid = musicId.substring(musicId.indexOf("_") + 1, musicId.length)
            if (AppUtil.isBackground(App.app)) {
                requestMusicInfoWithBack(mid)
            } else {
                lifecycleOwner?.let { it ->
                    MusicRepository.getMusicUrl(GlobalData.MUSIC_LOADING_TAG, mid).observe(it) {
                        val musicUrlEntity = it.getOrNull()
                        parseMusicUrl(mid, musicUrlEntity)
                    }
                }
            }
        }
    }

    private fun parseMusicUrl(mid: String, musicUrlEntity: MusicUrlEntity?) {
        Logger.i("mid:$mid---->>>musicUrlEntity:$musicUrlEntity")
        if (musicUrlEntity != null) {
            val musicUrlData = musicUrlEntity.data
            if (musicUrlEntity.code == -1) {
                ToastUtil.show("该歌曲为付费歌曲,暂时不能免费播放")
            } else {
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

    private fun requestMusicInfoWithBack(mid: String) {
        val params = HashMap<String, Any>()
        params.putAll(GlobalData.musicCommonRequestParams)
        params["type"] = "music"
        params["mid"] = mid
        val apiService = HttpManager.mInstance.getApiService(
            GlobalConstant.MUSIC_BASE_URL, IApiService::class.java
        )
        val observable = apiService.getMusicUrlWithResponseBody(params)
        HttpController.startRequest(this::class.java.name,
            observable,
            object : HttpController.OnHttpListener {
                override fun onRequestSuccess(response: Response<ResponseBody>) {
                    val body = response.body()
                    val musicUrlEntity =
                        JsonUtil.parseJsonToObject(body?.string(), MusicUrlEntity::class.java)
                    parseMusicUrl(mid, musicUrlEntity)
                }

                override fun onRequestError(t: Throwable) {

                }
            })
    }

    fun requestDownloadMusicInfo(musicInfo: MusicInfo) {
        this.currentDownloadMusicInfo = musicInfo
        activity?.let { LoadingDialog.show(GlobalData.COMMON_LOADING_TAG, it, "下载歌曲...",true) }
        val musicId = musicInfo.musicrid
        if (musicId.contains("_")) {
            val mid = musicId.substring(musicId.indexOf("_") + 1, musicId.length)
            lifecycleOwner?.let { it ->
                MusicRepository.getMusicUrl(GlobalData.COMMON_LOADING_TAG, mid).observe(it) {
                    val musicUrlEntity = it.getOrNull()
                    Logger.i("mid:$mid---->>>musicUrlEntity:$musicUrlEntity")
                    if (musicUrlEntity != null) {
                        val musicUrlData = musicUrlEntity.data
                        if (musicUrlEntity.code == -1) {
                            ToastUtil.show("该歌曲为付费歌曲,暂时不能免费下载")
                        } else {
                            val url = musicUrlData?.url
                            Logger.i("mid:$mid---->>>url:$url")
                            url?.let { it1 -> startDownload(it1) }
                        }
                    }
                }
            }
        }
    }

    fun playMusic(url: String?) {
        Logger.i("playMusic--->>>$url")
        this.currentPlayUrl = url
        GlobalData.playUrlChange.postValue(url)
        startMusicService()
        getLrc()
    }

    fun startMusicService() {
        if (mServiceIntent == null) {
            mServiceIntent = Intent(App.app, MusicNotifyService::class.java)
        }
        mServiceIntent?.action = MusicNotifyService.MUSIC_PREPARE_ACTION
        mServiceIntent?.putExtra(MusicNotifyService.MUSIC_INFO_KEY,
            this.currentMusicInfo.value?.let { JsonUtil.parseObjectToJson(it) })
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
        val musicId = this.currentMusicInfo.value?.musicrid
        if (musicId != null && musicId.contains("_")) {
            val mid = musicId.substring(musicId.indexOf("_") + 1, musicId.length)
            if (AppUtil.isBackground(App.app)) {
                getLrcWithBack(mid)
            } else {
                lifecycleOwner?.let { it ->
                    MusicRepository.getMusicLrc(mid).observe(it) {
                        val musicLrcEntity = it.getOrNull()
                        parseMusicLrc(musicLrcEntity)
                    }
                }
            }
        }
    }

    private fun parseMusicLrc(musicLrcEntity: MusicLrcEntity?) {
        if (musicLrcEntity != null) {
            val musicLrcData = musicLrcEntity.data
            if (musicLrcData != null) {
                val lrcList = musicLrcData.lrclist
                if (!lrcList.isNullOrEmpty()) {
                    val currentLrcList = CommonTools.parseLrc(lrcList)
                    GlobalData.currentLrcData.clear()
                    GlobalData.currentLrcData.addAll(currentLrcList)
                    EventBus.getDefault().postSticky(LrcChangeEvent())
                }
            }
        }
    }

    private fun getLrcWithBack(mid: String) {
        val params = HashMap<String, Any>()
        params.putAll(GlobalData.musicCommonRequestParams)
        params["musicId"] = mid
        val apiService = HttpManager.mInstance.getApiService(
            GlobalConstant.MUSIC_BASE_URL, IApiService::class.java
        )
        val observable = apiService.getMusicLrcWithBack(params)
        HttpController.startRequest(this::class.java.name,
            observable,
            object : HttpController.OnHttpListener {
                override fun onRequestSuccess(response: Response<ResponseBody>) {
                    val body = response.body()
                    val musicLrcEntity =
                        JsonUtil.parseJsonToObject(body?.string(), MusicLrcEntity::class.java)
                    parseMusicLrc(musicLrcEntity)
                }

                override fun onRequestError(t: Throwable) {

                }

            })
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

                    }

                    MusicNotifyService.MUSIC_PLAY_ACTION -> {
                        GlobalData.isPlaying.postValue(true)
                        currentMusicInfo.value?.state = MusicState.STATE_PLAY
                        musicAdapter?.setSelectedIndex(currentPosition)
                    }

                    MusicNotifyService.MUSIC_PAUSE_ACTION -> {
                        currentMusicInfo.value?.state = MusicState.STATE_PAUSE
                        GlobalData.isPlaying.postValue(false)
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

    fun getPlayMusicInfo(): MutableLiveData<MusicInfo?> {
        return currentMusicInfo
    }
}