package com.android.wy.news.fragment

import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.wy.news.R
import com.android.wy.news.activity.WebFragmentActivity
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.app.App
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.FragmentPlayMusicSongBinding
import com.android.wy.news.dialog.CommonOperationDialog
import com.android.wy.news.dialog.LoadingDialog
import com.android.wy.news.dialog.MusicListDialog
import com.android.wy.news.entity.OperationItemEntity
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.event.MusicEvent
import com.android.wy.news.event.MusicInfoEvent
import com.android.wy.news.event.MusicListEvent
import com.android.wy.news.event.PlayEvent
import com.android.wy.news.listener.IPageChangeListener
import com.android.wy.news.manager.LrcDesktopManager
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.MusicPlayMode
import com.android.wy.news.music.MusicState
import com.android.wy.news.music.lrc.LrcHelper
import com.android.wy.news.service.MusicNotifyService
import com.android.wy.news.service.MusicPlayService
import com.android.wy.news.util.AppUtil
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.util.ToastUtil
import com.android.wy.news.view.RoundProgressBar
import com.android.wy.news.viewmodel.PlayMusicSongViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PlayMusicSongFragment : BaseFragment<FragmentPlayMusicSongBinding, PlayMusicSongViewModel>() {
    private var currentPosition = -1
    private var currentMusicInfo: MusicInfo? = null
    private var ivCover: ImageView? = null
    private var tvTitle: TextView? = null
    private var tvDesc: TextView? = null
    private var tvCurrentLrc: TextView? = null
    private var tvNextLrc: TextView? = null
    private var mIvNeedle: ImageView? = null
    private var mFlPlayMusic: FrameLayout? = null
    private var mPlayMusicAnim: Animation? = null
    private var mPlayNeedleAnim: Animation? = null
    private var mStopNeedleAnim: Animation? = null
    private var mediaHelper: MediaPlayerHelper? = null
    private var sbMusic: SeekBar? = null
    private var tvStart: TextView? = null
    private var tvEnd: TextView? = null
    private var ivPre: ImageView? = null
    private var ivNext: ImageView? = null
    private var ivMusicMode: ImageView? = null
    private var ivMusicList: ImageView? = null
    private var llLrcContent: LinearLayout? = null
    private var isDragSeek = false

    private var ivPlay: ImageView? = null
    private var rlPlay: RelativeLayout? = null
    private var rlDownload: RelativeLayout? = null
    private var rlSinger: RelativeLayout? = null
    private var rlRing: RelativeLayout? = null
    private var rlAlbum: RelativeLayout? = null
    private var roundProgressBar: RoundProgressBar? = null
    private var index = 0
    private var currentPlayUrl: String? = null
    private var pageChangeListener: IPageChangeListener? = null
    private var currentDataList = ArrayList<MusicInfo>()

    fun setPageListener(pageChangeListener: IPageChangeListener) {
        this.pageChangeListener = pageChangeListener
    }

    companion object {
        private const val POSITION_KEY = "position_key"
        private const val MUSIC_INFO_KEY = "music_info_key"
        private const val MUSIC_URL_KEY = "music_url_key"

        fun newInstance(
            position: Int,
            musicInfoJson: String,
            url: String?,
            pageChangeListener: IPageChangeListener
        ): PlayMusicSongFragment {
            val fragment = PlayMusicSongFragment()
            fragment.setPageListener(pageChangeListener)
            val args = Bundle()
            args.putInt(POSITION_KEY, position)
            args.putString(MUSIC_INFO_KEY, musicInfoJson)
            args.putString(MUSIC_URL_KEY, url)
            fragment.arguments = args
            return fragment
        }
    }

    private fun checkState(state: Int) {
        when (state) {
            MusicState.STATE_PREPARE -> {

            }

            MusicState.STATE_PLAY -> {
                mIvNeedle?.startAnimation(mPlayNeedleAnim)
                mFlPlayMusic?.startAnimation(mPlayMusicAnim)
                ivPlay?.setImageResource(R.mipmap.music_play)
            }

            MusicState.STATE_PAUSE -> {
                mFlPlayMusic?.clearAnimation()
                mIvNeedle?.startAnimation(mStopNeedleAnim)
                ivPlay?.setImageResource(R.mipmap.music_pause)
            }

            MusicState.STATE_ERROR -> {

            }

            else -> {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(o: Any) {
        Logger.i("PlayMusicSongFragment--->>>onEvent--->>>o:$o")
        if (o is MusicEvent) {
            Logger.i("onEvent--->>>time:${o.time}")
            roundProgressBar?.setProgress(o.time)
            activity?.let { LrcDesktopManager.showDesktopLrc(it, o.time.toLong()) }
            setLrcText(o.time.toLong())
            if (!isDragSeek) {
                sbMusic?.progress = o.time
            }
            tvStart?.text = LrcHelper.formatTime(o.time.toFloat())
        } else if (o is MusicInfoEvent) {
            currentMusicInfo = JsonUtil.parseJsonToObject(o.musicJson, MusicInfo::class.java)
            setMusic()
        } else if (o is PlayEvent) {
            if (TextUtils.isEmpty(currentPlayUrl)) {
                LoadingDialog.hide()
            }
            if (mediaHelper!!.isPlaying()) {
                checkState(MusicState.STATE_PLAY)
            } else {
                checkState(MusicState.STATE_PAUSE)
            }
        } else if (o is MusicListEvent) {
            val dataList = o.dataList
            Logger.i("onEvent--->>>MusicListEvent.dataList:$dataList")
            currentDataList.addAll(dataList)
        }
    }

    private fun setLrcText(time: Long) {
        val lrcTextList = CommonTools.getLrcTextList(GlobalData.currentLrcData, time)
        if (lrcTextList.size > 0) {
            tvCurrentLrc?.visibility = View.VISIBLE
            tvCurrentLrc?.text = lrcTextList[0]
            tvNextLrc?.text = lrcTextList[1]
        }
    }

    override fun initView() {
        val playMusicBinding = mBinding.playMusic
        ivCover = playMusicBinding.ivCover
        tvTitle = mBinding.tvTitle
        tvDesc = mBinding.tvDesc
        mFlPlayMusic = playMusicBinding.flPlayMusic
        mIvNeedle = playMusicBinding.ivNeedle
        sbMusic = mBinding.sbMusic
        tvStart = mBinding.tvStart
        tvEnd = mBinding.tvEnd
        ivPre = mBinding.ivPre
        ivNext = mBinding.ivNext
        ivMusicMode = mBinding.ivMusicMode
        ivMusicList = mBinding.ivMusicList
        tvCurrentLrc = mBinding.tvCurrentLrc
        tvNextLrc = mBinding.tvNextLrc
        llLrcContent = mBinding.llLrcContent

        ivPlay = mBinding.ivPlay
        rlPlay = mBinding.rlPlay
        rlDownload = mBinding.rlDownload
        rlSinger = mBinding.rlSinger
        rlRing = mBinding.rlRing
        rlAlbum = mBinding.rlAlbum
        roundProgressBar = mBinding.roundProgressBar

        llLrcContent?.setOnClickListener {
            pageChangeListener?.changePage(1)
        }
        ivMusicList?.setOnClickListener {
            showMusicList()
        }
        ivMusicMode?.setOnClickListener {
            setMusicMode()
        }
        rlPlay?.setOnClickListener {
            play()
        }
        ivPre?.setOnClickListener {
            playPre()
        }
        ivNext?.setOnClickListener {
            playNext()
        }
        rlSinger?.setOnClickListener {
            val artistId = this.currentMusicInfo?.artistid
            WebFragmentActivity.startActivity(mActivity, artistId.toString())
        }
        rlDownload?.setOnClickListener {
            this.currentMusicInfo?.let { it1 -> PlayMusicManager.setLongClickMusicInfo(it1) }
            PlayMusicManager.getDownloadMusicInfo()
                ?.let { PlayMusicManager.requestMusicInfo(it) }
        }
        rlRing?.setOnClickListener {
            this.currentMusicInfo?.let { it1 -> PlayMusicManager.setLongClickMusicInfo(it1) }
            val stringBuilder = StringBuilder()
            val album = this.currentMusicInfo?.name
            val artist = this.currentMusicInfo?.artist
            stringBuilder.append(artist)
            if (!TextUtils.isEmpty(album)) {
                stringBuilder.append("-$album")
            }
            val activity = mActivity as AppCompatActivity
            val list = arrayListOf(
                OperationItemEntity(R.mipmap.call, AppUtil.getString(App.app, R.string.call_ring)),
                OperationItemEntity(
                    R.mipmap.alarm,
                    AppUtil.getString(App.app, R.string.alarm_ring)
                ),
                OperationItemEntity(
                    R.mipmap.notice,
                    AppUtil.getString(App.app, R.string.notify_ring)
                )
            )
            CommonOperationDialog.show(
                activity,
                stringBuilder.toString(),
                list,
                object : BaseNewsAdapter.OnItemAdapterListener<OperationItemEntity> {
                    override fun onItemClickListener(view: View, data: OperationItemEntity) {
                        val tag = view.tag
                        if (tag is Int) {
                            when (tag) {
                                0 -> {
                                    setCall()
                                }

                                1 -> {
                                    setAlarm()
                                }

                                2 -> {
                                    setNotice()
                                }

                                else -> {

                                }
                            }
                        }
                    }

                    override fun onItemLongClickListener(view: View, data: OperationItemEntity) {

                    }
                })
        }

        mPlayMusicAnim = AnimationUtils.loadAnimation(context, R.anim.play_music_anim)
        mPlayNeedleAnim = AnimationUtils.loadAnimation(context, R.anim.play_needle_anim)
        mStopNeedleAnim = AnimationUtils.loadAnimation(context, R.anim.stop_needle_anim)

        sbMusic?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                isDragSeek = true
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                isDragSeek = false
                p0?.progress?.let { mediaHelper?.seekTo(it) }
            }
        })
    }

    private fun showMusicList() {
        val musicListDialog = MusicListDialog()
        val bundle = Bundle()
        bundle.putString(
            MusicListDialog.MUSIC_LIST_KEY,
            JsonUtil.parseObjectToJson(currentDataList)
        )
        musicListDialog.arguments = bundle
        val supportFragmentManager = (context as AppCompatActivity).supportFragmentManager
        if (!supportFragmentManager.isDestroyed) {
            musicListDialog.show(
                supportFragmentManager,
                "music_list_dialog"
            )
        }
    }

    private fun setMusicMode() {
        val map = MusicPlayMode.map
        index++
        if (index > 2) {
            index = 0
        }
        val s = map[index]
        MusicPlayMode.setMode(index)
        ToastUtil.show("已切换到$s")
        initMusicMode()
    }

    private fun setNotice() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置通知铃声")
        startActivityForResult(intent, 3)
    }

    private fun setAlarm() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置闹钟铃声")
        startActivityForResult(intent, 2)
    }

    private fun setCall() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置来电铃声")
        startActivityForResult(intent, 1)
    }

    private fun initMusicMode() {
        val mode = MusicPlayMode.getMode()
        if (mode != null) {
            index = mode
        }
        when (mode) {
            MusicPlayMode.STATE_TYPE_NORMAL -> {
                ivMusicMode?.setImageResource(R.mipmap.state_repeat)
            }

            MusicPlayMode.STATE_TYPE_RANDOM -> {
                ivMusicMode?.setImageResource(R.mipmap.state_random)
            }

            MusicPlayMode.STATE_TYPE_ONE -> {
                ivMusicMode?.setImageResource(R.mipmap.state_one)
            }

            else -> {
                ivMusicMode?.setImageResource(R.mipmap.state_repeat)
            }
        }
    }

    private fun play() {
        if (TextUtils.isEmpty(currentPlayUrl)) {
            LoadingDialog.show(mActivity as AppCompatActivity, "请稍等...")
        }
        val intent = Intent(context, MusicPlayService::class.java)
        intent.action = MusicNotifyService.MUSIC_STATE_ACTION
        context?.startService(intent)
    }

    private fun playNext() {
        checkState(MusicState.STATE_PAUSE)
        val intent = Intent(context, MusicPlayService::class.java)
        intent.action = MusicNotifyService.MUSIC_NEXT_ACTION
        context?.startService(intent)
    }

    private fun playPre() {
        checkState(MusicState.STATE_PAUSE)
        val intent = Intent(context, MusicPlayService::class.java)
        intent.action = MusicNotifyService.MUSIC_PRE_ACTION
        context?.startService(intent)
    }

    override fun initData() {
        mediaHelper = context?.let { MediaPlayerHelper.getInstance(it) }
        val args = arguments
        if (args != null) {
            currentPosition = args.getInt(POSITION_KEY)
            currentPlayUrl = args.getString(MUSIC_URL_KEY)
            val s = args.getString(MUSIC_INFO_KEY)
            if (!TextUtils.isEmpty(s)) {
                currentMusicInfo = JsonUtil.parseJsonToObject(s, MusicInfo::class.java)
                setLrcText(0)
            }
        }
        setMusic()
        initMusicMode()
    }

    private fun setMusic() {
        roundProgressBar?.setMax(this.currentMusicInfo?.duration?.times(1000)!!)
        sbMusic?.max = (this.currentMusicInfo?.duration)?.times(1000)!!
        tvEnd?.text =
            LrcHelper.formatTime((this.currentMusicInfo?.duration)?.times(1000)!!.toFloat())
        this.currentMusicInfo?.pic?.let { ivCover?.let { it1 -> CommonTools.loadImage(it, it1) } }
        tvTitle?.text = this.currentMusicInfo?.artist
        tvDesc?.text = this.currentMusicInfo?.name

        if (mediaHelper!!.isPlaying()) {
            checkState(MusicState.STATE_PLAY)
        } else {
            checkState(MusicState.STATE_PAUSE)
        }
    }

    override fun initEvent() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun getViewBinding(): FragmentPlayMusicSongBinding {
        return FragmentPlayMusicSongBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): PlayMusicSongViewModel {
        return CommonTools.getViewModel(this, PlayMusicSongViewModel::class.java)
    }

    override fun onClear() {
    }

    override fun onNotifyDataChanged() {
        GlobalData.playUrlChange.observe(this) {
            currentPlayUrl = it
            LoadingDialog.hide()
        }
    }
}