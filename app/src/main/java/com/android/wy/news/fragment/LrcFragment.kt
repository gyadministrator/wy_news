package com.android.wy.news.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.LrcFragmentBinding
import com.android.wy.news.dialog.LoadingDialog
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.event.MusicEvent
import com.android.wy.news.event.MusicInfoEvent
import com.android.wy.news.event.MusicListEvent
import com.android.wy.news.event.PlayEvent
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.dialog.MusicListDialog
import com.android.wy.news.music.MusicPlayMode
import com.android.wy.news.music.MusicState
import com.android.wy.news.music.lrc.LrcHelper
import com.android.wy.news.music.lrc.LrcView
import com.android.wy.news.service.MusicNotifyService
import com.android.wy.news.service.MusicPlayService
import com.android.wy.news.view.RoundProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import jp.wasabeef.glide.transformations.BlurTransformation
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class LrcFragment : DialogFragment() {
    private var mContentView: View? = null
    private var currentPosition = -1
    private var currentMusicInfo: MusicInfo? = null
    private var height = 0
    private var width = 0
    private var ivCover: ImageView? = null
    private var ivBg: ImageView? = null
    private var tvTitle: TextView? = null
    private var tvDesc: TextView? = null
    private var rlDown: RelativeLayout? = null
    private var mIvNeedle: ImageView? = null
    private var mFlPlayMusic: FrameLayout? = null
    private var mAnimStyle: Int =
        com.android.wy.news.locationselect.R.style.DefaultCityPickerAnimation
    private var mPlayMusicAnim: Animation? = null
    private var mPlayNeedleAnim: Animation? = null
    private var mStopNeedleAnim: Animation? = null
    private var mediaHelper: MediaPlayerHelper? = null
    private var lrcView: LrcView? = null
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

    companion object {
        private const val POSITION_KEY = "position_key"
        private const val MUSIC_INFO_KEY = "music_info_key"
        private const val MUSIC_URL_KEY = "music_url_key"
        const val TAG = "LrcFragment"

        fun newInstance(position: Int, musicInfoJson: String, url: String?): LrcFragment {
            val fragment = LrcFragment()
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
                lrcView?.pause()
                ivPlay?.setImageResource(R.mipmap.music_pause)
            }

            MusicState.STATE_ERROR -> {

            }

            else -> {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.android.wy.news.locationselect.R.style.CityPickerStyle)
        musicListDialog = context?.let { MusicListDialog(it, R.style.BottomSheetDialog) }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(o: Any) {
        Logger.i("LrcFragment--->>>onEvent--->>>o:$o")
        if (o is MusicEvent) {
            Logger.i("onEvent--->>>time:${o.time}")
            roundProgressBar?.setProgress(o.time)
            lrcView?.updateTime(o.time.toLong())
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mContentView = inflater.inflate(R.layout.lrc_fragment, container, false)
        return mContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = mContentView?.let { LrcFragmentBinding.bind(it) }
        initView(binding)
        initData()
    }

    private fun initView(binding: LrcFragmentBinding?) {
        val playMusicBinding = binding?.playMusic
        ivBg = binding?.ivBg
        ivCover = playMusicBinding?.ivCover
        tvTitle = binding?.tvTitle
        tvDesc = binding?.tvDesc
        rlDown = binding?.rlDown
        mFlPlayMusic = playMusicBinding?.flPlayMusic
        mIvNeedle = playMusicBinding?.ivNeedle
        lrcView = binding?.lrcView
        sbMusic = binding?.sbMusic
        tvStart = binding?.tvStart
        tvEnd = binding?.tvEnd
        ivPre = binding?.ivPre
        ivNext = binding?.ivNext
        ivMusicMode = binding?.ivMusicMode
        ivMusicList = binding?.ivMusicList

        ivPlay = binding?.ivPlay
        rlPlay = binding?.rlPlay
        roundProgressBar = binding?.roundProgressBar

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
        rlDown?.setOnClickListener {
            dismiss()
        }

        mPlayMusicAnim = AnimationUtils.loadAnimation(context, R.anim.play_music_anim)
        mPlayNeedleAnim = AnimationUtils.loadAnimation(context, R.anim.play_needle_anim)
        mStopNeedleAnim = AnimationUtils.loadAnimation(context, R.anim.stop_needle_anim)

        lrcView?.setOnPlayIndicatorLineListener(object : LrcView.OnPlayIndicatorLineListener {
            override fun onPlay(time: Float, content: String?) {
                mediaHelper?.seekTo(time.toInt())
            }
        })
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
        Toast.makeText(context, "已切换到$s", Toast.LENGTH_SHORT).show()
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

    private fun initData() {
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
        getLrc()
        roundProgressBar?.setMax(this.currentMusicInfo?.duration?.times(1000)!!)
        sbMusic?.max = (this.currentMusicInfo?.duration)?.times(1000)!!
        tvEnd?.text =
            LrcHelper.formatTime((this.currentMusicInfo?.duration)?.times(1000)!!.toFloat())
        ivBg?.let {
            Glide.with(this).load(this.currentMusicInfo?.pic)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 10))).into(it)
        }
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
                            lrcView?.setLrcData(realLrcList)
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        hideNavigationBar()
        val dialog = dialog
        dialog?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss()
            }
            false
        }
        measure()
        val window = dialog?.window
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            window.setGravity(Gravity.BOTTOM)
            window.setLayout(width, height /*- ScreenUtil.getStatusBarHeight(requireActivity())*/)
            window.setWindowAnimations(mAnimStyle)
        }
    }

    private fun hideNavigationBar() {
        val mImmersionBar = ImmersionBar.with(this)
        mImmersionBar.hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
        mImmersionBar.init()
    }

    //测量宽高
    private fun measure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val dm = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getRealMetrics(dm)
            height = dm.heightPixels
            width = dm.widthPixels
        } else {
            val dm = resources.displayMetrics
            height = dm.heightPixels
            width = dm.widthPixels
        }
    }
}