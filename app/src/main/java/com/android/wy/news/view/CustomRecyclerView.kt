package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/21 16:11
  * @Version:        1.0
  * @Description:    
 */
open class CustomRecyclerView : RecyclerView {
    private var mContext: Context?

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        mContext = context
        overScrollMode = OVER_SCROLL_NEVER
        (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        (itemAnimator as SimpleItemAnimator).changeDuration = 0
        val recycledViewPool = RecycledViewPool()
        recycledViewPool.setMaxRecycledViews(0, 10)
        this.setRecycledViewPool(recycledViewPool)
        initListener()
    }

    private fun initListener() {
        addOnScrollListener(LoadScrollListener(mContext))
    }

    private class LoadScrollListener(var context: Context?) : OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
                SCROLL_STATE_IDLE ->
                    try {
                        //当屏幕停止滚动，加载图片
                        context?.let { Glide.with(it).resumeRequests() }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                SCROLL_STATE_DRAGGING ->
                    try {
                        //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                        context?.let { Glide.with(it).pauseRequests() }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                SCROLL_STATE_SETTLING ->
                    try {
                        //由于用户的操作，屏幕产生惯性滑动，停止加载图片
                        context?.let { Glide.with(it).pauseRequests() }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
            }
        }
    }
}