package com.android.wy.news.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import com.android.wy.news.R
import com.android.wy.news.util.AppUtil
import com.android.wy.news.util.TaskUtil

class CustomLoadingView : View {
    private var loadPaint: Paint? = null
    private var paint: Paint? = null
    private var loadingText: String? = null
    private var loadingSize: Float? = 10f
    private var percent = 0f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomLoadingView)
        loadingText = typedArray.getString(R.styleable.CustomLoadingView_loadingText)
        loadingSize = typedArray.getDimension(R.styleable.CustomLoadingView_loadingSize, 10f)
        if (TextUtils.isEmpty(loadingText)) {
            loadingText = resources.getString(R.string.loading_more)
        }
        typedArray.recycle()
        init()
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val bounds = Rect()
        loadingText?.let { paint?.getTextBounds(loadingText, 0, it.length, bounds) }
        setMeasuredDimension(bounds.width(), bounds.height())
    }

    private fun init() {
        paint = Paint()
        paint?.color = AppUtil.getColor(R.color.main_title)
        loadingSize?.let { paint?.textSize = it }

        loadPaint = Paint()
        loadPaint?.style = Paint.Style.FILL
        loadPaint?.color = AppUtil.getColor(R.color.load_color)
    }

    override fun onDraw(canvas: Canvas?) {
        drawText(canvas)
        drawLine(canvas)
    }

    private fun drawLine(canvas: Canvas?) {
        if (null == canvas) {
            return
        }
        canvas.save()
        val bounds = Rect()
        loadingText?.let { paint?.getTextBounds(loadingText, 0, it.length, bounds) }
        val rect = Rect(0, 0, (bounds.width() * percent).toInt(), bounds.height())
        canvas.clipRect(rect)
        loadPaint?.let { canvas.drawRect(rect, it) }
        canvas.restore()
        TaskUtil.runOnUiThread({
            if (percent >= 1.0) {
                percent = 0f
            } else {
                percent += 0.05f
            }
            postInvalidate()
        }, 40)
    }

    private fun drawText(canvas: Canvas?) {
        if (null == canvas) {
            return
        }
        val fm = paint?.fontMetricsInt
        paint?.let {
            if (fm != null) {
                loadingText?.let { it1 ->
                    canvas.drawText(
                        it1,
                        width / 2 - it.measureText(loadingText) / 2,
                        (height / 2 - (fm.bottom + fm.top) / 2).toFloat(),
                        it
                    )
                }
            }
        }
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        if (visibility == VISIBLE) {
            percent += 0.05f
            invalidate()
        }
    }
}