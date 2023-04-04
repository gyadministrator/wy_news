package com.android.wy.news.view

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.wy.news.R
import com.android.wy.news.databinding.LayoutTitleBarBinding

class TitleBarView : RelativeLayout {
    private lateinit var tvTitle: TextView
    private var title: String? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_title_bar, this)
        val binding = LayoutTitleBarBinding.bind(view)
        initView(binding)

        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.TitleBarView)
        title = typedArray?.getString(R.styleable.TitleBarView_title)
        if (!TextUtils.isEmpty(title)) {
            tvTitle.text = title
        }
        typedArray?.recycle()
    }

    private fun initView(binding: LayoutTitleBarBinding) {
        val rlBack = binding.rlBack
        tvTitle = binding.tvTitle
        rlBack.setOnClickListener {
            val context = context
            if (context is Activity) {
                context.finish()
            }
        }
    }

    fun setTitle(title: String) {
        tvTitle.text = title
    }

    fun getTitle(): String {
        return tvTitle.text.toString()
    }
}