package com.android.wy.news.refresh

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.android.wy.news.R
import com.android.wy.news.util.AppUtil
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import com.wang.avi.AVLoadingIndicatorView


class CustomRefreshFooter : LinearLayout, RefreshFooter {
    private var mImage: AVLoadingIndicatorView? = null
    private var tvTip: TextView? = null
    private var tvFooterTip: TextView? = null
    private var llLoading: LinearLayout? = null
    private var textFooterTipColor: Int? = null


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        val view = inflate(context, R.layout.layout_custom_refresh_footer, this)
        mImage = view.findViewById(R.id.iv_refresh_footer)
        tvTip = view.findViewById(R.id.tv_tip)
        llLoading = view.findViewById(R.id.ll_loading)
        tvFooterTip = view.findViewById(R.id.tv_footer_tip)

        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.CustomRefreshFooter)
        textFooterTipColor = context?.resources?.getColor(R.color.main_title)?.let {
            typedArray?.getColor(
                R.styleable.CustomRefreshFooter_textFooterTipColor,
                it
            )
        }
        textFooterTipColor?.let { tvFooterTip?.setTextColor(it) }
        textFooterTipColor?.let { mImage?.setIndicatorColor(it) }
        typedArray?.recycle()
    }

    @SuppressLint("RestrictedApi")
    override fun onStateChanged(
        refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState
    ) {
        when (newState) {
            RefreshState.None, RefreshState.PullUpToLoad -> {
                mImage?.show()
            }

            RefreshState.Loading, RefreshState.LoadReleased -> {}
            RefreshState.ReleaseToLoad -> {}
            else -> {}
        }
    }

    override fun getView(): View {
        return this
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }

    @SuppressLint("RestrictedApi")
    override fun setPrimaryColors(vararg colors: Int) {
    }

    @SuppressLint("RestrictedApi")
    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
        //控制是否稍微上滑动就刷新
        kernel.refreshLayout.setEnableAutoLoadMore(false)
    }

    @SuppressLint("RestrictedApi")
    override fun onMoving(
        isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int
    ) {
    }

    @SuppressLint("RestrictedApi")
    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    @SuppressLint("RestrictedApi")
    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    @SuppressLint("RestrictedApi")
    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        mImage?.hide()
        return 0
    }

    @SuppressLint("RestrictedApi")
    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    @SuppressLint("RestrictedApi")
    override fun setNoMoreData(noMoreData: Boolean): Boolean {
        if (noMoreData) {
            tvTip?.text = "哎呀，到底了呀"
            tvTip?.visibility = View.VISIBLE
            mImage?.hide()
            llLoading?.visibility = View.GONE
        } else {
            tvTip?.visibility = View.GONE
            mImage?.show()
            llLoading?.visibility = View.VISIBLE
        }
        return true
    }
}