package com.android.wy.news.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.android.wy.news.R
import com.android.wy.news.cache.VideoCacheManager
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutScreenVideoBinding
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard

class ScreenVideoView : RelativeLayout, SeekBar.OnSeekBarChangeListener {
    private lateinit var tvTitle: TextView
    private lateinit var tvPlay: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvSource: TextView
    private lateinit var playVideo: JCVideoPlayerStandard
    private lateinit var ivUser: ImageView
    private lateinit var tvUser: TextView
    private lateinit var tvUserSource: TextView
    private var screenVideoListener: OnScreenVideoListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_screen_video, this)
        val binding = LayoutScreenVideoBinding.bind(view)
        initView(binding)
        initUiState()
    }

    fun getPlayVideo(): JCVideoPlayerStandard {
        return playVideo
    }

    private fun initUiState() {
    }

    private fun initView(binding: LayoutScreenVideoBinding) {
        tvTitle = binding.tvTitle
        tvPlay = binding.tvPlay
        tvTime = binding.tvTime
        tvSource = binding.tvSource
        playVideo = binding.playVideo
        ivUser = binding.ivUser
        tvUser = binding.tvUser
        tvUserSource = binding.tvUserSource
    }

    fun setTitle(title: String): ScreenVideoView {
        tvTitle.text = title
        return this
    }

    @SuppressLint("SetTextI18n")
    fun setPlayCount(playCount: Long): ScreenVideoView {
        if (playCount > 0) {
            if (playCount > 10000) {
                val fl = playCount / 10000f
                tvPlay.text = "%.1f".format(fl) + "w次播放"
            } else {
                tvPlay.text = playCount.toString() + "次播放"
            }
        }
        return this
    }

    fun setSource(source: String): ScreenVideoView {
        tvSource.text = source
        return this
    }

    fun setTime(pTime: String): ScreenVideoView {
        val time = CommonTools.getTimeDiff(pTime)
        if (TextUtils.isEmpty(time)) {
            tvTime.text = pTime
        } else {
            tvTime.text = time
        }
        return this
    }

    fun setUser(tName: String): ScreenVideoView {
        tvUser.text = tName
        return this
    }

    fun setUserSource(userSource: String): ScreenVideoView {
        tvUserSource.text = userSource
        return this
    }

    fun setUserCover(userCover: String): ScreenVideoView {
        CommonTools.loadImage(userCover, ivUser)
        return this
    }

    fun setUp(url: String, videoCover: String, isShowCover: Boolean): ScreenVideoView {
        val proxyUrl = VideoCacheManager.getProxyUrl(context, url)
        val setUp = playVideo.setUp(proxyUrl, JCVideoPlayer.SCREEN_LAYOUT_LIST, "")
        playVideo.progressBar.setOnSeekBarChangeListener(this)
        if (setUp && isShowCover) {
            val thumbImageView = playVideo.thumbImageView
            thumbImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            CommonTools.loadImage(videoCover, thumbImageView)
        }
        return this
    }

    fun addOnScreenVideoListener(onScreenVideoListener: OnScreenVideoListener) {
        this.screenVideoListener = onScreenVideoListener
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (p1 == 100) {
            screenVideoListener?.onVideoFinish()
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

    interface OnScreenVideoListener {
        fun onVideoFinish()
    }
}