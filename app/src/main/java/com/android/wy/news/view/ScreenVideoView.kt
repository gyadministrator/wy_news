package com.android.wy.news.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.android.wy.news.R
import com.android.wy.news.cache.VideoCacheManager
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutScreenVideoBinding
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer.CURRENT_STATE_PAUSE
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer.CURRENT_STATE_PLAYING

class ScreenVideoView : FrameLayout, View.OnClickListener, CustomVideoPlayer.OnVideoListener,
    SeekBar.OnSeekBarChangeListener {
    private lateinit var tvTitle: TextView
    private lateinit var tvPlay: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvSource: TextView
    private lateinit var videoPlayer: CustomVideoPlayer
    private lateinit var ivUser: ImageView
    private lateinit var tvUser: TextView
    private lateinit var tvUserSource: TextView
    private lateinit var ivPlay: ImageView
    private lateinit var rlContent: RelativeLayout
    private lateinit var sbVideo: SeekBar
    private var screenVideoListener: OnScreenVideoListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_screen_video, this)
        val binding = LayoutScreenVideoBinding.bind(view)
        initView(binding)
        initUiState()
    }

    fun play() {
        videoPlayer.startPlayLogic()
    }

    private fun initUiState() {
        videoPlayer.setAllControlsVisible(
            View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE, View.GONE
        )
        checkPlayState()
    }

    private fun initView(binding: LayoutScreenVideoBinding) {
        tvTitle = binding.tvTitle
        tvPlay = binding.tvPlay
        tvTime = binding.tvTime
        tvSource = binding.tvSource
        videoPlayer = binding.videoPlayer
        ivUser = binding.ivUser
        tvUser = binding.tvUser
        tvUserSource = binding.tvUserSource
        ivPlay = binding.ivPlay
        rlContent = binding.rlContent
        sbVideo = binding.sbVideo
        sbVideo.setOnSeekBarChangeListener(this)
        rlContent.setOnClickListener(this)
        videoPlayer.addVideoListener(this)
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
        val setUp = videoPlayer.setUp(proxyUrl, JCVideoPlayer.SCREEN_LAYOUT_NORMAL, "")
        if (setUp && isShowCover) {
            val thumbImageView = videoPlayer.thumbImageView
            thumbImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            CommonTools.loadImage(videoCover, thumbImageView)
        }
        return this
    }

    fun addOnScreenVideoListener(onScreenVideoListener: OnScreenVideoListener) {
        this.screenVideoListener = onScreenVideoListener
    }

    interface OnScreenVideoListener {
        fun onVideoFinish()
    }

    override fun onClick(p0: View?) {
        videoPlayer.startButton.performClick()
        //清除界面UI
        videoPlayer.changeUiToPlayingClear()
        checkPlayState()
    }

    private fun checkPlayState() {
        if (videoPlayer.currentState == CURRENT_STATE_PLAYING) {
            ivPlay.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                sbVideo.maxHeight = 4
                sbVideo.minHeight = 4
            } else {
                sbVideo.minimumHeight = 4
            }
            sbVideo.thumb = null
        } else if (videoPlayer.currentState == CURRENT_STATE_PAUSE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                sbVideo.maxHeight = 8
                sbVideo.minHeight = 8
            } else {
                sbVideo.minimumHeight = 8
            }
            sbVideo.thumb = AppCompatResources.getDrawable(context, R.drawable.bg_seek_thumb)
            ivPlay.visibility = View.VISIBLE
        }
    }

    override fun onVideoFinish() {
        screenVideoListener?.onVideoFinish()
    }

    override fun onProgress(progress: Int) {
        sbVideo.progress = progress
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        if (p0 != null) {
            val progress = p0.progress
            //JCMediaManager.instance().mediaPlayer.seekTo(i.toLong())
            //val duration: Int = videoPlayer.duration
            //val progress: Int = i * 100 / if (duration == 0) 1 else duration
            sbVideo.progress = progress
        }
    }
}