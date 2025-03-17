package com.android.wy.news.refresh

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import com.android.wy.news.R
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle


class CustomRefreshHeader : LinearLayout, RefreshHeader {
    private var textHeaderTipColor: Int? = null
    private var tvTip: TextView? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        val view = inflate(context, R.layout.layout_custom_refresh_header, this)
        tvTip = view.findViewById(R.id.tv_tip)
        context?.withStyledAttributes(attrs, R.styleable.CustomRefreshHeader) {
            textHeaderTipColor = context.resources?.getColor(R.color.main_title)?.let {
                this.getColor(
                    R.styleable.CustomRefreshHeader_textHeaderTipColor,
                    it
                )
            }
            textHeaderTipColor?.let { tvTip?.setTextColor(it) }
        }
    }

    /**
     * 一般可以理解为一下case中的三种状态，在达到相应状态时候开始改变
     * 注意：这三种状态都是初始化的状态
     */
    @SuppressLint("RestrictedApi")
    override fun onStateChanged(
        refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState
    ) {
        when (newState) {
            RefreshState.PullDownToRefresh -> {}
            RefreshState.ReleaseToRefresh -> {

            }

            RefreshState.Refreshing -> {

            }

            else -> {}
        }
    }

    /**
     * 获取真实视图（必须返回，不能为null）一般就是返回当前自定义的view
     */
    override fun getView(): View {
        return this
    }

    /**
     * 获取变换方式（必须指定一个：平移、拉伸、固定、全屏）,Translate指平移，大多数都是平移
     */
    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }

    @SuppressLint("RestrictedApi")
    override fun setPrimaryColors(vararg colors: Int) {
    }

    @SuppressLint("RestrictedApi")
    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
    }

    /**
     * 执行下拉的过程
     */
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

    /**
     * 结束下拉刷新的时候需要关闭动画
     */
    @SuppressLint("RestrictedApi")
    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        return 0
    }

    @SuppressLint("RestrictedApi")
    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }
}