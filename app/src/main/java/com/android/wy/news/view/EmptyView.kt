package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.wy.news.R

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/20 19:50
  * @Version:        1.0
  * @Description:    
 */
class EmptyView : ConstraintLayout, View.OnClickListener {
    private var onEmptyListener: OnEmptyListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_empty, this)
        view.visibility = View.GONE
        val tvReload = view.findViewById<TextView>(R.id.tv_reload)
        tvReload.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        visibility = View.GONE
        onEmptyListener?.onReloadListener()
    }

    fun setListener(emptyListener: OnEmptyListener) {
        onEmptyListener = emptyListener
    }

    interface OnEmptyListener {
        fun onReloadListener()
    }
}