package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutMusicPlayBarBinding

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/25 16:52
  * @Version:        1.0
  * @Description:    
 */
class PlayBarView : LinearLayout, View.OnClickListener {
    private lateinit var tvTitle: TextView
    private lateinit var tvDesc: TextView
    private lateinit var ivCover: ImageView
    private lateinit var ivPre: ImageView
    private lateinit var ivPlay: ImageView
    private lateinit var ivNext: ImageView
    private var onPlayBarListener: OnPlayBarListener? = null

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
        tvDesc = binding.tvDesc
        ivCover = binding.ivCover
        ivPre = binding.ivPre
        ivPlay = binding.ivPlay
        ivNext = binding.ivNext

        ivPre.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.iv_pre -> {
                    onPlayBarListener?.onClickPre()
                }

                R.id.iv_play -> {
                    onPlayBarListener?.onClickPlay()
                }

                R.id.iv_next -> {
                    onPlayBarListener?.onClickNext()
                }

                else -> {

                }
            }
        }
    }

    fun setCover(cover: String): PlayBarView {
        CommonTools.loadImage(cover, ivCover)
        return this
    }

    fun setTitle(title: String): PlayBarView {
        tvTitle.text = title
        return this
    }

    fun setDesc(desc: String): PlayBarView {
        tvDesc.text = desc
        return this
    }

    fun addListener(onPlayBarListener: OnPlayBarListener): PlayBarView {
        this.onPlayBarListener = onPlayBarListener
        return this
    }

    interface OnPlayBarListener {
        fun onClickPre()
        fun onClickPlay()
        fun onClickNext()
    }
}