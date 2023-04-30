package com.android.wy.news.music.lrc

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
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
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.MusicState
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import jp.wasabeef.glide.transformations.BlurTransformation


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
    private var mMediaHelper: MediaPlayerHelper? = null
    private var lrcView: LrcView? = null

    companion object {
        private const val POSITION_KEY = "position_key"
        private const val MUSIC_INFO_KEY = "music_info_key"
        const val TAG = "LrcFragment"

        fun newInstance(position: Int, musicInfoJson: String): LrcFragment {
            val fragment = LrcFragment()
            val args = Bundle()
            args.putInt(POSITION_KEY, position)
            args.putString(MUSIC_INFO_KEY, musicInfoJson)
            fragment.arguments = args
            return fragment
        }
    }

    private fun checkState(state: Int) {
        when (state) {
            MusicState.STATE_PREPARE -> {

            }

            MusicState.STATE_PLAY -> {
                mFlPlayMusic?.animation = mPlayMusicAnim
                mIvNeedle?.animation = mPlayNeedleAnim
            }

            MusicState.STATE_PAUSE -> {
                mFlPlayMusic?.clearAnimation()
                mIvNeedle?.animation = mStopNeedleAnim
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mContentView = inflater.inflate(R.layout.lrc_fragment, container, false)
        return mContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initData()
    }

    private fun initViews() {
        ivBg = mContentView?.findViewById(R.id.iv_bg)
        ivCover = mContentView?.findViewById(R.id.iv_cover)
        tvTitle = mContentView?.findViewById(R.id.tv_title)
        tvDesc = mContentView?.findViewById(R.id.tv_desc)
        rlDown = mContentView?.findViewById(R.id.rl_down)
        mFlPlayMusic = mContentView?.findViewById(R.id.fl_play_music)
        mIvNeedle = mContentView?.findViewById(R.id.iv_needle)
        lrcView = mContentView?.findViewById(R.id.lrc_view)
        rlDown?.setOnClickListener {
            dismiss()
        }
        mPlayMusicAnim = AnimationUtils.loadAnimation(context, R.anim.play_music_anim);
        mPlayNeedleAnim = AnimationUtils.loadAnimation(context, R.anim.play_needle_anim);
        mStopNeedleAnim = AnimationUtils.loadAnimation(context, R.anim.stop_needle_anim);
    }

    private fun initData() {
        mMediaHelper = context?.let { MediaPlayerHelper.getInstance(it) }
        val args = arguments
        if (args != null) {
            currentPosition = args.getInt(POSITION_KEY)
            val s = args.getString(MUSIC_INFO_KEY)
            if (!TextUtils.isEmpty(s)) {
                val gson = Gson()
                currentMusicInfo = gson.fromJson(s, MusicInfo::class.java)
            }
        }
        setMusic()
    }

    private fun setMusic() {
        ivBg?.let {
            Glide.with(this).load(this.currentMusicInfo?.pic)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 10)))
                .into(it)
        }
        this.currentMusicInfo?.pic?.let { ivCover?.let { it1 -> CommonTools.loadImage(it, it1) } }
        tvTitle?.text = this.currentMusicInfo?.artist
        tvDesc?.text = this.currentMusicInfo?.album
        if (mMediaHelper!!.isPlaying()) {
            checkState(MusicState.STATE_PLAY)
        } else {
            checkState(MusicState.STATE_PAUSE)
        }
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
        val mImmersionBar = ImmersionBar.with(this)
        mImmersionBar.hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
        mImmersionBar.fullScreen(true)
        mImmersionBar.init()
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