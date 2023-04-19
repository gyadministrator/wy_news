package com.android.wy.news.locationselect.adapter.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.locationselect.R
import com.android.wy.news.locationselect.model.City


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/15 13:49
  * @Version:        1.0
  * @Description:    
 */
class SectionItemDecoration(context: Context, data: List<City>?) : RecyclerView.ItemDecoration() {
    private var mData: List<City>? = data
    private var mBgPaint: Paint? = null
    private var mTextPaint: TextPaint? = null
    private var mBounds: Rect? = null

    private var mSectionHeight = 0
    private var mBgColor = 0
    private var mTextColor = 0
    private var mTextSize = 0

    init {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.cpSectionBackground, typedValue, true)
        mBgColor =
            context.resources.getColor(com.android.wy.news.commonui.R.color.bg_search_color/*typedValue.resourceId*/)
        context.theme.resolveAttribute(R.attr.cpSectionHeight, typedValue, true)
        mSectionHeight = context.resources.getDimensionPixelSize(typedValue.resourceId)
        context.theme.resolveAttribute(R.attr.cpSectionTextSize, typedValue, true)
        mTextSize = context.resources.getDimensionPixelSize(typedValue.resourceId)
        context.theme.resolveAttribute(R.attr.cpSectionTextColor, typedValue, true)
        mTextColor =
            context.resources.getColor(com.android.wy.news.commonui.R.color.main_title/*typedValue.resourceId*/)
        mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBgPaint!!.color = mBgColor
        mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint!!.textSize = mTextSize.toFloat()
        mTextPaint!!.color = mTextColor
        mBounds = Rect()
    }

    fun setData(data: List<City>?) {
        mData = data
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state!!)
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val position = params.viewLayoutPosition
            if (mData != null && !mData!!.isEmpty() && position <= mData!!.size - 1 && position > -1) {
                if (position == 0) {
                    drawSection(c, left, right, child, params, position)
                } else {
                    if (mData!![position].getSection() != mData!![position - 1].getSection()
                    ) {
                        drawSection(c, left, right, child, params, position)
                    }
                }
            }
        }
    }

    private fun drawSection(
        c: Canvas, left: Int, right: Int, child: View,
        params: RecyclerView.LayoutParams, position: Int
    ) {
        c.drawRect(
            left.toFloat(),
            (
                    child.top - params.topMargin - mSectionHeight).toFloat(),
            right.toFloat(),
            (
                    child.top - params.topMargin).toFloat(), mBgPaint!!
        )
        mTextPaint!!.getTextBounds(
            mData!![position].getSection(),
            0,
            mData!![position].getSection().length,
            mBounds
        )
        c.drawText(
            mData!![position].getSection(),
            child.paddingLeft.toFloat(),
            (
                    child.top - params.topMargin - (mSectionHeight / 2 - mBounds!!.height() / 2)).toFloat(),
            mTextPaint!!
        )
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val pos = (parent.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
        if (pos < 0) return
        if (mData == null || mData!!.isEmpty()) return
        val section = mData!![pos].getSection()
        val child = parent.findViewHolderForLayoutPosition(pos)!!.itemView
        var flag = false
        if (pos + 1 < mData!!.size) {
            if (null != section && section != mData!![pos + 1].getSection()) {
                if (child.height + child.top < mSectionHeight) {
                    c.save()
                    flag = true
                    c.translate(0f, (child.height + child.top - mSectionHeight).toFloat())
                }
            }
        }
        c.drawRect(
            parent.paddingLeft.toFloat(),
            parent.paddingTop.toFloat(),
            (
                    parent.right - parent.paddingRight).toFloat(),
            (
                    parent.paddingTop + mSectionHeight).toFloat(), mBgPaint!!
        )
        mTextPaint!!.getTextBounds(section, 0, section.length, mBounds)
        c.drawText(
            section,
            child.paddingLeft.toFloat(),
            (
                    parent.paddingTop + mSectionHeight - (mSectionHeight / 2 - mBounds!!.height() / 2)).toFloat(),
            mTextPaint!!
        )
        if (flag) c.restore()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        if (mData != null && mData!!.isNotEmpty() && position <= mData!!.size - 1 && position > -1) {
            if (position == 0) {
                outRect[0, mSectionHeight, 0] = 0
            } else {
                if (mData!![position].getSection() != mData!![position - 1].getSection()
                ) {
                    outRect[0, mSectionHeight, 0] = 0
                }
            }
        }
    }

}