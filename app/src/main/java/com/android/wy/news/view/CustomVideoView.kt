package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.VideoView
import com.android.wy.news.R
import com.android.wy.news.cache.VideoCacheManager

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/28 16:57
  * @Version:        1.0
  * @Description:    
 */
class CustomVideoView : FrameLayout, View.OnClickListener {
    private lateinit var tvTitle: TextView
    private lateinit var clVideo: VideoView
    private lateinit var ivCover: ImageView
    private lateinit var ivPlay: ImageView
    private lateinit var clSeekBar: SeekBar
    private var isPause = false
    private var mUrl: String = ""

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_custom_video, this)
        initView(view)
    }

    private fun initView(view: View) {
        tvTitle = view.findViewById(R.id.tv_title)
        clVideo = view.findViewById(R.id.cl_video)
        ivCover = view.findViewById(R.id.iv_cover)
        ivPlay = view.findViewById(R.id.iv_play)
        clSeekBar = view.findViewById(R.id.cl_seekBar)
        ivPlay.setOnClickListener(this)
    }

    fun getCover(): ImageView {
        return ivCover
    }

    fun play(url: String) {
        mUrl = url
        val proxyUrl = VideoCacheManager.getProxyUrl(context, url)
        clVideo.setVideoPath(proxyUrl)
        clVideo.start()
        isPause = false
    }

    fun stop() {
        if (clVideo.isPlaying) {
            clVideo.stopPlayback()
        }
    }

    fun pause() {
        if (clVideo.isPlaying) {
            clVideo.pause()
            isPause = true
        }
    }

    fun resume() {
        clVideo.resume()
        isPause = false
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            val id = p0.id
            if (id == R.id.iv_play) {
                if (!clVideo.isPlaying) {
                    play(mUrl)
                    return
                }
                if (isPause) {
                    resume()
                } else {
                    pause()
                }
            }
        }
    }
}