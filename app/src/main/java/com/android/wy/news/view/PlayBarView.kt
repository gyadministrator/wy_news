package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutMusicPlayBarBinding
import com.wang.avi.AVLoadingIndicatorView

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/25 16:52
  * @Version:        1.0
  * @Description:    
 */
class PlayBarView : LinearLayout, View.OnClickListener {
    private lateinit var tvTitle: TextView
    private lateinit var ivCover: ImageView
    private lateinit var ivPlay: ImageView
    private lateinit var rlPlay: RelativeLayout
    private lateinit var ivStateLoading: AVLoadingIndicatorView
    private lateinit var rlLoading: RelativeLayout
    private lateinit var roundProgressBar: RoundProgressBar
    private var onPlayBarListener: OnPlayBarListener? = null
    private var position = 0
    private var currentProgress = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_music_play_bar, this)
        val binding = LayoutMusicPlayBarBinding.bind(view)
        initView(binding)
    }

    private fun initView(binding: LayoutMusicPlayBarBinding) {
        tvTitle = binding.tvTitle
        ivCover = binding.ivCover
        ivPlay = binding.ivPlay
        rlPlay = binding.rlPlay
        roundProgressBar = binding.roundProgressBar
        ivStateLoading = binding.ivStateLoading
        rlLoading = binding.rlLoading
        rlPlay.setOnClickListener(this)
        ivCover.setOnClickListener(this)
        tvTitle.setOnClickListener(this)
    }

    fun updateProgress(progress: Int): PlayBarView {
        this.currentProgress = progress
        roundProgressBar.setProgress(progress)
        return this
    }

    fun getProgress(): Int {
        return currentProgress
    }

    fun setPosition(position: Int): PlayBarView {
        this.position = position
        return this
    }

    fun showLoading(isShow: Boolean) {
        if (isShow) {
            rlLoading.visibility = View.VISIBLE
            ivStateLoading.show()
            rlPlay.visibility = View.GONE
        } else {
            rlLoading.visibility = View.GONE
            ivStateLoading.hide()
            rlPlay.visibility = View.VISIBLE
        }
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.rl_play -> {
                    onPlayBarListener?.onClickPlay(position = this.position)
                }

                R.id.iv_cover -> {
                    onPlayBarListener?.onClickPlayBar(position = this.position)
                }

                R.id.tv_title -> {
                    onPlayBarListener?.onClickPlayBar(position = this.position)
                }

                else -> {

                }
            }
        }
    }

    fun setDuration(duration: Int): PlayBarView {
        roundProgressBar.setMax(duration)
        return this
    }

    fun setCover(cover: String): PlayBarView {
        CommonTools.loadImage(cover, ivCover)
        return this
    }

    fun setTitle(title: String): PlayBarView {
        tvTitle.text = title
        return this
    }

    fun setPlay(isPlaying: Boolean): PlayBarView {
        if (isPlaying) {
            ivPlay.setImageResource(R.mipmap.music_play)
        } else {
            ivPlay.setImageResource(R.mipmap.music_pause)
        }
        return this
    }

    fun addListener(onPlayBarListener: OnPlayBarListener): PlayBarView {
        this.onPlayBarListener = onPlayBarListener
        return this
    }

    interface OnPlayBarListener {
        fun onClickPlay(position: Int)
        fun onClickPlayBar(position: Int)
    }
}