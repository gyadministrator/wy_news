package com.android.wy.news.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.customview.widget.ViewDragHelper
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/2/28 15:57
  * @Version:        1.0
  * @Description:    
 */
class FlowLayout : ViewGroup, View.OnClickListener {
    private var lineNum: Int = 6
    private var dividerMargin: Float = 10f
    private var viewDragHelper: ViewDragHelper? = null
    private var inflater: LayoutInflater
    private lateinit var itemListener: OnFlowItemListener
    private val max_width = 100

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("InflateParams")
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        inflater = LayoutInflater.from(context)
        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.FlowLayout)
        if (typedArray != null) {
            lineNum = typedArray.getInteger(R.styleable.FlowLayout_lineNum, 6)
            dividerMargin = typedArray.getDimension(R.styleable.FlowLayout_dividerMargin, 10f)
        }
        typedArray?.recycle()
    }

    @SuppressLint("InflateParams")
    fun setData(dataList: ArrayList<String>) {
        post {
            if (dataList.size > 0) {
                for (i in 0 until dataList.size) {
                    val itemView = inflater.inflate(R.layout.item_label_layout, null)
                    val tvName = itemView.findViewById<TextView>(R.id.tv_name)
                    tvName.text = dataList[i]
                    addView(itemView)
                    itemView.tag = i
                    itemView.setOnClickListener(this)
                }
            }
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        if (viewDragHelper?.continueSettling(true) == true) {
            invalidate()
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper?.shouldInterceptTouchEvent(ev) == true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper?.processTouchEvent(event)
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val itemMargin = dividerMargin.toInt()
        val childCount = childCount
        //实际的宽度
        var realWidth = 0
        //实际的高度
        var realHeight = 0
        //每一行子view的长度之和
        var childLineSumWidth = 0
        //每一行的最大高度
        var maxLineHeight = 0
        //是否是每一行的开始
        var isLineStart: Boolean
        //是否是每一行的结束
        var isLineEnd: Boolean
        //测量子view的宽高
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        //循环计算实际的宽高
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childMeasuredWidth = child.measuredWidth
            val childMeasuredHeight = child.measuredHeight

            isLineStart = ((i + 1) % lineNum == 1)
            //(子view的宽度+右边距)
            childLineSumWidth += childMeasuredWidth + itemMargin
            if (isLineStart) {
                //每行的开始(左边距+子view的宽度+右边距)
                childLineSumWidth += itemMargin
            }
            //realWidth取每一行的宽度的最大值
            if (realWidth < childLineSumWidth) {
                realWidth = childLineSumWidth
            }
            if (maxLineHeight < childMeasuredHeight) {
                maxLineHeight = childMeasuredHeight
            }
            isLineEnd = (((i + 1) % lineNum == 0) || i == (childCount - 1))
            if (isLineEnd) {
                realHeight += maxLineHeight + itemMargin
                if (i == (childCount - 1)) {
                    realHeight += itemMargin
                }
                childLineSumWidth = 0
                maxLineHeight = 0
            }
        }
        val screenWidth = CommonTools.getScreenWidth()
        if (realWidth > screenWidth) {
            realWidth = screenWidth
        }
        setMeasuredDimension(realWidth, realHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = childCount
        var childWidth = 0
        var childHeight = 0
        var childLineHeight = 0
        //是否是每一行的开始
        var isLineStart: Boolean
        //是否是每一行的结束
        var isLineEnd: Boolean
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childMeasuredWidth = child.measuredWidth
            Log.e("gy", "onLayout: $childMeasuredWidth")
            val childMeasuredHeight = child.measuredHeight

            if (childLineHeight < childMeasuredHeight) {
                childLineHeight = childMeasuredHeight
            }
            isLineStart = ((i + 1) % lineNum == 1)
            isLineEnd = (((i + 1) % lineNum == 0) || i == (childCount - 1))
            childWidth += dividerMargin.toInt()
            if (isLineStart) {
                childHeight += dividerMargin.toInt()
            }
            child.layout(
                childWidth,
                childHeight,
                childWidth + childMeasuredWidth,
                childHeight + childMeasuredHeight
            )
            if (isLineEnd) {
                childWidth = 0
                childHeight += childLineHeight
            } else {
                childWidth += childMeasuredWidth
            }
        }
    }

    fun addFlowListener(itemListener: OnFlowItemListener?) {
        if (itemListener != null) {
            this.itemListener = itemListener
        }
    }

    interface OnFlowItemListener {
        fun onClickItemListener(view: View?, text: String)
    }

    override fun onClick(p0: View?) {
        val tag = p0?.tag
        if (tag != null && p0 is TextView) {
            itemListener.onClickItemListener(p0, p0.text.toString())
        }
    }
}