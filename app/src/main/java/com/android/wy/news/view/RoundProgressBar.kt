package com.android.wy.news.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.android.wy.news.R


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/28 13:04
  * @Version:        1.0
  * @Description:    
 */
class RoundProgressBar : View {
    private var paint: Paint? = null //画笔对象的引用

    private var roundColor = 0 //圆环的颜色

    private var roundProgressColor = 0 //圆环进度的颜色

    private var innerRoundColor = 0 //圆环内部圆颜色

    private var roundWidth = 0f //圆环的宽度

    private var textColor = 0 //中间进度百分比字符串的颜色

    private var textSize = 0f //中间进度百分比字符串的字体

    private var max = 0 //最大进度

    private var progress = 0 //当前进度

    private var isDisplayText = false //是否显示中间百分比进度字符串

    private var style = 0 //进度条的风格：空心圆环或者实心圆环


    companion object {
        private const val STROKE = 0 //空心
        private const val FILL = 1 //实心
    }


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        paint = Paint()
        //从attrs.xml中获取自定义属性和默认值
        //从attrs.xml中获取自定义属性和默认值
        val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar)
        roundColor =
            typedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.WHITE) //外边框的颜色

        roundProgressColor = typedArray.getColor(
            R.styleable.RoundProgressBar_roundProgressColor,
            Color.WHITE
        ) //加载进度颜色

        innerRoundColor = typedArray.getColor(
            R.styleable.RoundProgressBar_innerRoundColor,
            Color.parseColor("#00000000")
        ) //内部加载框颜色

        roundWidth = typedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5f) //圆环宽度

        textColor = typedArray.getColor(
            R.styleable.RoundProgressBar_textColor,
            Color.parseColor("#FD7500")
        ) //字体颜色

        textSize = typedArray.getDimension(R.styleable.RoundProgressBar_textSize, 18f) //字体大小

        max = typedArray.getInteger(R.styleable.RoundProgressBar_max, 100) //最大

        style = typedArray.getInt(R.styleable.RoundProgressBar_style, FILL) //0空心，1实心

        isDisplayText =
            typedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true)
        typedArray.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //画最外层大圆环
        //画最外层大圆环
        val centerX = width / 2 //获取中心点X坐标

        val centerY = height / 2 //获取中心点Y坐标

        val radius = (centerX - roundWidth / 2).toInt() //圆环的半径

        paint?.color = roundColor
        paint?.style = Paint.Style.STROKE //设置空心

        paint?.strokeWidth = roundWidth //设置圆环宽度

        paint?.isAntiAlias = true //消除锯齿

        paint?.let {
            canvas?.drawCircle(
                centerX.toFloat(), centerY.toFloat(), radius.toFloat(),
                it
            )
        } //绘制圆环

        //绘制圆环内部圆
        paint?.color = innerRoundColor
        paint?.style = Paint.Style.FILL
        paint?.isAntiAlias = true
        canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), radius - roundWidth / 2, paint!!)

        //画进度
        paint?.strokeWidth = roundWidth //设置圆环宽度

        paint?.color = roundProgressColor //设置进度颜色

        val oval = RectF(
            (centerX - radius).toFloat(), (centerX - radius).toFloat(), (centerX
                    + radius).toFloat(), (centerX + radius).toFloat()
        ) //用于定义的圆弧的形状和大小的界限

        when (style) {
            STROKE -> {
                paint?.style = Paint.Style.STROKE
                canvas?.drawArc(
                    oval,
                    0f,
                    (360 * progress / max).toFloat(),
                    false,
                    paint!!
                ) // 根据进度画圆弧
            }

            FILL -> {
                paint?.style = Paint.Style.FILL
                if (progress != 0) canvas?.drawArc(
                    oval, -90f, (360 * progress / max).toFloat(), true,
                    paint!!
                ) // 根据进度画圆弧
            }
        }
        //画中间进度百分比字符串
        //画中间进度百分比字符串
        paint?.strokeWidth = 0f
        paint?.color = textColor
        paint?.textSize = textSize
        //paint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        //paint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        val percent = (progress.toFloat() / max.toFloat() * 100).toInt() //计算百分比

        val textWidth = paint?.measureText("$percent%") //测量字体宽度，需要居中显示


        if (isDisplayText && style == STROKE && percent != 0) {
            if (textWidth != null) {
                canvas?.drawText(
                    "$percent%", centerX - textWidth / 2, centerX + textSize / 2,
                    paint!!
                )
            }
        }

    }

    fun getPaint(): Paint? {
        return paint
    }

    fun setPaint(paint: Paint?) {
        this.paint = paint
    }

    fun getRoundColor(): Int {
        return roundColor
    }

    fun setRoundColor(roundColor: Int) {
        this.roundColor = roundColor
    }

    fun getRoundProgressColor(): Int {
        return roundProgressColor
    }

    fun setRoundProgressColor(roundProgressColor: Int) {
        this.roundProgressColor = roundProgressColor
    }

    fun getRoundWidth(): Float {
        return roundWidth
    }

    fun setRoundWidth(roundWidth: Float) {
        this.roundWidth = roundWidth
    }

    fun getTextColor(): Int {
        return textColor
    }

    fun setTextColor(textColor: Int) {
        this.textColor = textColor
    }

    fun getTextSize(): Float {
        return textSize
    }

    fun setTextSize(textSize: Float) {
        this.textSize = textSize
    }

    @Synchronized
    fun getMax(): Int {
        return max
    }

    @Synchronized
    fun setMax(max: Int) {
        require(max >= 0) { "max must more than 0" }
        this.max = max
    }

    @Synchronized
    fun getProgress(): Int {
        return progress
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @author caizhiming
     */
    @Synchronized
    fun setProgress(progress: Int) {
        require(progress >= 0) { "progress must more than 0" }
        if (progress > max) {
            this.progress = progress
        }
        if (progress <= max) {
            this.progress = progress
            postInvalidate()
        }
    }

    fun isDisplayText(): Boolean {
        return isDisplayText
    }

    fun setDisplayText(isDisplayText: Boolean) {
        this.isDisplayText = isDisplayText
    }

    fun getStyle(): Int {
        return style
    }

    fun setStyle(style: Int) {
        this.style = style
    }
}