package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.android.wy.news.R
import com.android.wy.news.databinding.LayoutEmptyRecyclerViewBinding
import com.bumptech.glide.Glide


/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/6/17 14:00
  * @Version:        1.0
  * @Description:    
 */
class EmptyRecyclerView : FrameLayout {
    private var rvContent: RecyclerView? = null
    private var rlEmpty: RelativeLayout? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.layout_empty_recycler_view, this)
        val binding = LayoutEmptyRecyclerViewBinding.bind(contentView)
        initView(binding)
    }

    private fun initView(binding: LayoutEmptyRecyclerViewBinding) {
        rvContent = binding.rvContent
        rlEmpty = binding.rlEmpty
        initRecycler()
    }

    private fun initRecycler() {
        val itemAnimator = rvContent?.itemAnimator
        if (itemAnimator is SimpleItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
            itemAnimator.changeDuration = 0
        }
        val recycledViewPool = RecyclerView.RecycledViewPool()
        recycledViewPool.setMaxRecycledViews(0, 10)
        rvContent?.setRecycledViewPool(recycledViewPool)
        //addOnScrollListener(LoadScrollListener(mContext))
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        adapter?.registerAdapterDataObserver(emptyObserver)
        emptyObserver.onChanged()
    }

    private val emptyObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            val adapter = rvContent?.adapter
            if (adapter != null) {
                if (adapter.itemCount == 0) {
                    rlEmpty?.visibility = View.VISIBLE
                    rvContent?.visibility = View.GONE
                } else {
                    rlEmpty?.visibility = View.GONE
                    rvContent?.visibility = View.VISIBLE
                }
            }
        }
    }

    private class LoadScrollListener(var context: Context?) : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE ->
                    try {
                        //当屏幕停止滚动，加载图片
                        if (context != null) Glide.with(context!!).resumeRequests()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                RecyclerView.SCROLL_STATE_DRAGGING ->
                    try {
                        //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                        if (context != null) Glide.with(context!!).pauseRequests()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                RecyclerView.SCROLL_STATE_SETTLING ->
                    try {
                        //由于用户的操作，屏幕产生惯性滑动，停止加载图片
                        if (context != null) Glide.with(context!!).pauseRequests()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
            }
        }
    }
}