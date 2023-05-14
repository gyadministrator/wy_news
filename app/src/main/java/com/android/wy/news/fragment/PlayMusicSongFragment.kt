package com.android.wy.news.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.FragmentPlayMusicSongBinding
import com.android.wy.news.dialog.LoadingDialog
import com.android.wy.news.dialog.MusicListDialog
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.event.MusicEvent
import com.android.wy.news.event.MusicInfoEvent
import com.android.wy.news.event.MusicListEvent
import com.android.wy.news.event.PlayEvent
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.manager.LrcDesktopManager
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.MusicPlayMode
import com.android.wy.news.music.MusicState
import com.android.wy.news.music.lrc.Lrc
import com.android.wy.news.music.lrc.LrcHelper
import com.android.wy.news.service.MusicNotifyService
import com.android.wy.news.service.MusicPlayService
import com.android.wy.news.util.ToastUtil
import com.android.wy.news.view.RoundProgressBar
import com.android.wy.news.viewmodel.PlayMusicSongViewModel
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PlayMusicSongFragment : BaseFragment<FragmentPlayMusicSongBinding, PlayMusicSongViewModel>() {
    private var currentPosition = -1
    private var currentMusicInfo: MusicInfo? = null
    private var ivCover: ImageView? = null
    private var tvTitle: TextView? = null
    private var tvDesc: TextView? = null
    private var tvLrc: TextView? = null
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
    private var isDragSeek = false

    private var ivPlay: ImageView? = null
    private var rlPlay: RelativeLayout? = null
    private var roundProgressBar: RoundProgressBar? = null
    private var index = 0
    private var musicListDialog: MusicListDialog? = null
    private var currentPlayUrl: String? = null
    private var currentLrcList = ArrayList<Lrc>()

    companion object {
        private const val POSITION_KEY = "position_key"
        private const val MUSIC_INFO_KEY = "music_info_key"
        private const val MUSIC_URL_KEY = "music_url_key"

        fun newInstance(position: Int, musicInfoJson: String, url: String?): PlayMusicSongFragment {
            val fragment = PlayMusicSongFragment()
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
            if (currentLrcList.size == 0) {
                getLrc()
            }
            roundProgressBar?.setProgress(o.time)
            activity?.let { LrcDesktopManager.showDesktopLrc(it, o.time.toLong()) }
            val lrcText = CommonTools.getLrcText(currentLrcList, o.time.toLong())
            tvLrc?.text = lrcText
            if (!isDragSeek) {
                sbMusic?.progress = o.time
            }
            tvStart?.text = LrcHelper.formatTime(o.time.toFloat())
        } else if (o is MusicInfoEvent) {
            val gson = Gson()
            currentMusicInfo = gson.fromJson(o.musicJson, MusicInfo::class.java)
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
            musicListDialog?.setData(dataList)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
            getLrc()
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
        tvLrc = mBinding.tvLrc

        ivPlay = mBinding.ivPlay
        rlPlay = mBinding.rlPlay
        roundProgressBar = mBinding.roundProgressBar

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
        musicListDialog?.show()
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
            context?.let { LoadingDialog.show(it, "请稍等...") }
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
                val gson = Gson()
                currentMusicInfo = gson.fromJson(s, MusicInfo::class.java)
            }
        }
        setMusic()
        initMusicMode()
    }

    private fun setMusic() {
        val lrcText = CommonTools.getLrcText(currentLrcList, 0)
        tvLrc?.text = lrcText
        getLrc()
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

    private fun getLrc() {
        val musicId = this.currentMusicInfo?.musicrid
        if (musicId!!.contains("_")) {
            val mid = musicId.substring(musicId.indexOf("_") + 1, musicId.length)
            MusicRepository.getMusicLrc(mid).observe(this) {
                val musicLrcEntity = it.getOrNull()
                if (musicLrcEntity != null) {
                    val musicLrcData = musicLrcEntity.data
                    if (musicLrcData != null) {
                        val lrcList = musicLrcData.lrclist
                        if (lrcList.isNotEmpty()) {
                            val realLrcList = CommonTools.parseLrc(lrcList)
                            currentLrcList.clear()
                            currentLrcList.addAll(realLrcList)
                        }
                    }
                }
            }
        }
    }

    override fun initEvent() {
        musicListDialog = context?.let { MusicListDialog(it, R.style.BottomSheetDialog) }
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
    }
}