package com.android.wy.news.view

import android.animation.*
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.android.wy.news.R


class SwitchButton : View, View.OnClickListener {
    private var switchViewStrokeWidth = 0f
    private var switchViewBgColor = Color.BLACK
    private var switchViewBallColor = Color.BLACK
    private var mBallPaint: Paint? = null
    private var mBgPaint: Paint? = null
    private var mViewHeight = 0
    private var mViewWidth = 0
    private var mStrokeRadius = 0
    private var mSolidRadius = 0f
    private var mBgStrokeRectF: RectF? = null
    private var ballRight = 0
    private var greyColor = 0
    private var greenColor = 0
    private var mSwitchBallx = 0f
    private val defaultHeight = 60
    private val defaultWidth = 120
    private var mCurrentState: State? = null
    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null
    private var context: Context? = null


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        this.context = context
        val typedArray: TypedArray? =
            context?.obtainStyledAttributes(attrs, R.styleable.SwitchButton)
        if (typedArray != null) {
            switchViewBgColor =
                typedArray.getColor(R.styleable.SwitchButton_switch_bg_color, Color.BLACK)
            switchViewBallColor =
                typedArray.getColor(R.styleable.SwitchButton_switch_ball_color, Color.BLACK)
        }
        typedArray?.recycle()
        initData()
    }

    private fun initData() {
        greyColor = switchViewBgColor
        greenColor = Color.parseColor("#1AAC19")
        //greenColor = context?.getColor(R.color.text_select_color) ?: Color.parseColor("#1AAC19")
        mBallPaint = createPaint(switchViewBallColor, 0, Paint.Style.FILL, 0)
        mBgPaint = createPaint(switchViewBgColor, 0, Paint.Style.FILL, 0)
        mCurrentState = State.CLOSE
        setOnClickListener(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mViewHeight = h
        mViewWidth = w

        //	默认描边宽度是控件宽度的1/30, 比如控件宽度是120dp, 描边宽度就是4dp

        //	默认描边宽度是控件宽度的1/30, 比如控件宽度是120dp, 描边宽度就是4dp
        switchViewStrokeWidth = w * 1.0f / 30

        mStrokeRadius = mViewHeight / 2
        mSolidRadius = (mViewHeight - 2 * switchViewStrokeWidth) / 2
        ballRight = mViewWidth - mStrokeRadius


        mSwitchBallx = mStrokeRadius.toFloat()
        mBgStrokeRectF = RectF(0f, 0f, mViewWidth.toFloat(), mViewHeight.toFloat())

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var realWidthMeasureSpec: Int = widthMeasureSpec
        var realHeightMeasureSpec: Int = heightMeasureSpec
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val measureWidth: Int
        val measureHeight: Int

        when (widthMode) {
            MeasureSpec.UNSPECIFIED, MeasureSpec.AT_MOST -> {
                measureWidth = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, defaultWidth.toFloat(), resources.displayMetrics
                ).toInt()
                realWidthMeasureSpec =
                    MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.EXACTLY)
            }
            MeasureSpec.EXACTLY -> {}
        }

        when (heightMode) {
            MeasureSpec.UNSPECIFIED, MeasureSpec.AT_MOST -> {
                measureHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, defaultHeight.toFloat(), resources.displayMetrics
                ).toInt()
                realHeightMeasureSpec =
                    MeasureSpec.makeMeasureSpec(measureHeight, MeasureSpec.EXACTLY)
            }
            MeasureSpec.EXACTLY -> {}
        }
        super.onMeasure(realWidthMeasureSpec, realHeightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let { drawSwitchBg(it) }
        canvas?.let { drawSwitchBall(it) }
    }

    private fun drawSwitchBall(canvas: Canvas) {
        canvas.drawCircle(mSwitchBallx, mStrokeRadius.toFloat(), mSolidRadius, mBallPaint!!)
    }

    private fun drawSwitchBg(canvas: Canvas) {
        mBgStrokeRectF?.let {
            mBgPaint?.let { it1 ->
                canvas.drawRoundRect(
                    it, mStrokeRadius.toFloat(), mStrokeRadius.toFloat(), it1
                )
            }
        }
    }

    private fun createPaint(
        paintColor: Int, textSize: Int, style: Paint.Style, lineWidth: Int
    ): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = paintColor
        paint.strokeWidth = lineWidth.toFloat()
        paint.isDither = true //设置防抖动
        paint.textSize = textSize.toFloat()
        paint.style = style
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        return paint
    }

    private enum class State {
        OPEN, CLOSE
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(buttonView: SwitchButton?, isChecked: Boolean)
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        mOnCheckedChangeListener = listener
    }

    fun setChecked(isChecked: Boolean) {
        if (isChecked){
            performClick()
        }
    }

    override fun onClick(v: View?) {
        mCurrentState = if (mCurrentState === State.CLOSE) State.OPEN else State.CLOSE
        //绿色	#1AAC19
        //灰色	#999999
        //绿色	#1AAC19
        //灰色	#999999
        if (mCurrentState === State.CLOSE) {
            animate(ballRight, mStrokeRadius, greenColor, greyColor)
        } else {
            animate(mStrokeRadius, ballRight, greyColor, greenColor)
        }
        if (mOnCheckedChangeListener != null) {
            if (mCurrentState === State.OPEN) {
                mOnCheckedChangeListener?.onCheckedChanged(this, true)
            } else {
                mOnCheckedChangeListener?.onCheckedChanged(this, false)
            }
        }
    }

    private fun animate(from: Int, to: Int, startColor: Int, endColor: Int) {
        val translate = ValueAnimator.ofFloat(from.toFloat(), to.toFloat())
        translate.addUpdateListener { animation ->
            mSwitchBallx = animation.animatedValue as Float
            postInvalidate()
        }
        val color = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor)
        color.addUpdateListener { animation ->
            switchViewBgColor = animation.animatedValue as Int
            mBgPaint?.color = switchViewBgColor
            postInvalidate()
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translate, color)
        animatorSet.duration = 200
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                isClickable = false
            }

            override fun onAnimationEnd(animation: Animator) {
                isClickable = true
            }
        })
        animatorSet.start()
    }
}