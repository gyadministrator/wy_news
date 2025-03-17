package com.android.wy.news.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import cn.jzvd.Jzvd
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.LayoutScreenVideoBinding

class ScreenVideoView : FrameLayout, CustomVideoPlayer.OnVideoListener, View.OnClickListener {
    private lateinit var tvTitle: TextView
    private lateinit var tvPlay: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvSource: TextView
    private lateinit var videoPlayer: CustomVideoPlayer
    private lateinit var ivUser: ImageView
    private lateinit var tvUser: TextView
    private lateinit var tvUserSource: TextView
    private lateinit var llContent: LinearLayout
    private lateinit var rlBottom: RelativeLayout
    private lateinit var ivPlay: ImageView
    private lateinit var clUser: ConstraintLayout
    private lateinit var tvUserTitle: TextView
    private var screenVideoListener: OnScreenVideoListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_screen_video, this)
        val binding = LayoutScreenVideoBinding.bind(view)
        initView(binding)
    }

    fun play() {
        videoPlayer.startButton.performClick()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(binding: LayoutScreenVideoBinding) {
        tvTitle = binding.tvTitle
        tvPlay = binding.tvPlay
        tvTime = binding.tvTime
        tvSource = binding.tvSource
        videoPlayer = binding.videoPlayer
        ivUser = binding.ivUser
        tvUser = binding.tvUser
        tvUserSource = binding.tvUserSource
        llContent = binding.llContent
        ivPlay = binding.ivPlay
        clUser = binding.clUser
        tvUserTitle = binding.tvUserTitle
        rlBottom = binding.rlBottom
        //rlBottom.setOnTouchListener(this)
        llContent.setOnClickListener(this)
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

    @SuppressLint("SetTextI18n")
    fun setUser(tName: String): ScreenVideoView {
        tvUser.text = tName
        tvUserTitle.text = "@$tName"
        return this
    }

    fun setUserSource(userSource: String): ScreenVideoView {
        tvUserSource.text = userSource
        return this
    }

    fun setUserCover(userCover: String): ScreenVideoView {
        if (TextUtils.isEmpty(userCover)) {
            clUser.visibility = View.GONE
            tvSource.visibility = View.GONE
            tvUserTitle.visibility = View.VISIBLE
        } else {
            CommonTools.loadImage(userCover, ivUser)
        }
        return this
    }

    fun setUp(url: String, videoCover: String, isShowCover: Boolean): ScreenVideoView {
        Logger.i("setUp--->>>$url")
        videoPlayer.setUp(url, "")
        if (isShowCover) {
            val thumbImageView = videoPlayer.posterImageView
            thumbImageView.scaleType = ImageView.ScaleType.FIT_CENTER
            CommonTools.loadImage(videoCover, thumbImageView)
        }
        return this
    }

    fun setUp(url: String): ScreenVideoView {
        videoPlayer.setUp(url, "")
        return this
    }

    fun addOnScreenVideoListener(onScreenVideoListener: OnScreenVideoListener) {
        this.screenVideoListener = onScreenVideoListener
    }

    interface OnScreenVideoListener {
        fun onVideoFinish()
    }

    override fun onVideoFinish() {
        screenVideoListener?.onVideoFinish()
    }

    override fun onPlayState() {
        val state = videoPlayer.state
        if (state == Jzvd.STATE_PLAYING) {
            ivPlay.visibility = GONE
        } else if (state == Jzvd.STATE_PAUSE) {
            ivPlay.visibility = VISIBLE
        }
    }

    fun setPlayState(isPlaying: Boolean) {
        if (isPlaying) {
            ivPlay.visibility = GONE
        }
    }

    override fun onClick(p0: View?) {
        videoPlayer.startButton.performClick()
        //清除界面UI
        //videoPlayer.changeUiToPlayingClear()
        onPlayState()
    }
}