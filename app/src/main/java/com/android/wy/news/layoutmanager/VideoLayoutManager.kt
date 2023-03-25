package com.android.wy.news.layoutmanager

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.listener.OnViewPagerListener

class VideoLayoutManager : LinearLayoutManager {
    private lateinit var pagerSnapHelper: PagerSnapHelper
    private var recyclerView: RecyclerView? = null
    private var mDrift: Int = 0//位移，用来判断移动方向
    private var mOnViewPagerListener: OnViewPagerListener? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context, orientation, reverseLayout
    ) {
        init()
    }

    constructor(
        context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        pagerSnapHelper = PagerSnapHelper()
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        pagerSnapHelper.attachToRecyclerView(view)
        recyclerView = view
        recyclerView?.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
    }

    /**
     * * 滑动状态的改变
        * 缓慢拖拽-> SCROLL_STATE_DRAGGING
        * 快速滚动-> SCROLL_STATE_SETTLING
        * 空闲状态-> SCROLL_STATE_IDLE
     */
    override fun onScrollStateChanged(state: Int) {
        val snapView = pagerSnapHelper.findSnapView(this)
        when (state) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                val position = snapView?.let { getPosition(it) }
                if (mOnViewPagerListener != null && childCount == 1) {
                    position?.let {
                        mOnViewPagerListener?.onPageSelected(
                            it, position == itemCount - 1
                        )
                    }
                }
            }
            RecyclerView.SCROLL_STATE_DRAGGING -> {
                snapView?.let { getPosition(it) }
            }
            RecyclerView.SCROLL_STATE_SETTLING -> {
                snapView?.let { getPosition(it) }
            }
        }
        super.onScrollStateChanged(state)
    }

    override fun scrollVerticallyBy(
        dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?
    ): Int {
        this.mDrift = dy
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    override fun scrollHorizontallyBy(
        dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?
    ): Int {
        this.mDrift = dx
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    fun setOnViewPagerListener(listener: OnViewPagerListener) {
        mOnViewPagerListener = listener
    }

    private var mChildAttachStateChangeListener =
        object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                if (mOnViewPagerListener != null && childCount == 1) {
                    mOnViewPagerListener?.onInitComplete()
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                if (mDrift >= 0) {
                    if (mOnViewPagerListener != null) {
                        mOnViewPagerListener?.onPageRelease(true, getPosition(view))
                    }
                } else {
                    if (mOnViewPagerListener != null) {
                        mOnViewPagerListener?.onPageRelease(false, getPosition(view))
                    }
                }
            }

        }
}