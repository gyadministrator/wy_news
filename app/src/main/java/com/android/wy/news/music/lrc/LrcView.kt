package com.android.wy.news.music.lrc

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Looper
import android.text.BidiFormatter
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import android.widget.OverScroller
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.android.wy.news.R
import kotlin.math.abs


class LrcView : View {
    companion object {
        private const val DEFAULT_CONTENT = "暂无歌词"
    }

    private var mLrcData: ArrayList<Lrc>? = null
    private var mTextPaint: TextPaint? = null
    private var mDefaultContent: String? = null
    private var mCurrentLine = 0
    private var mOffset = 0f
    private var mLastMotionX = 0f
    private var mLastMotionY = 0f
    private var mScaledTouchSlop = 0
    private var mOverScroller: OverScroller? = null
    private var mVelocityTracker: VelocityTracker? = null
    private var mMaximumFlingVelocity = 0
    private var mMinimumFlingVelocity = 0
    private var mLrcTextSize = 0f
    private var mLrcLineSpaceHeight = 0f
    private var mTouchDelay = 0
    private var mNormalColor = 0
    private var mCurrentPlayLineColor = 0
    private var mNoLrcTextSize = 0f
    private var mNoLrcTextColor = 0

    //是否拖拽中，否的话响应onClick事件
    private var isDragging = false

    //用户开始操作
    private var isUserScroll = false
    private var isAutoAdjustPosition = true
    private var mPlayDrawable: Drawable? = null
    private var isShowTimeIndicator = false
    private var mPlayRect: Rect? = null
    private var mIndicatorPaint: Paint? = null
    private var mIndicatorLineWidth = 0f
    private var mIndicatorTextSize = 0f
    private var mCurrentIndicateLineTextColor = 0
    private var mIndicatorLineColor = 0
    private var mIndicatorMargin = 0f
    private var mIconLineGap = 0f
    private var mIconWidth = 0f
    private var mIconHeight = 0f
    private var isEnableShowIndicator = true
    private var mIndicatorTextColor = 0
    private var mIndicatorTouchDelay = 0
    private var isCurrentTextBold = false
    private var isLrcIndicatorTextBold = false
    private val mLrcMap = HashMap<String, StaticLayout>()
    private val mStaticLayoutHashMap = HashMap<String, StaticLayout>()


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        context?.let { attrs?.let { it1 -> init(it, it1) } }
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LrcView)
        mLrcTextSize =
            typedArray.getDimension(R.styleable.LrcView_lrcTextSize, sp2px(context, 15f).toFloat())
        mLrcLineSpaceHeight =
            typedArray.getDimension(
                R.styleable.LrcView_lrcLineSpaceSize,
                dp2px(context, 20f).toFloat()
            )
        mTouchDelay = typedArray.getInt(R.styleable.LrcView_lrcTouchDelay, 3500)
        mIndicatorTouchDelay = typedArray.getInt(R.styleable.LrcView_indicatorTouchDelay, 2500)
        mNormalColor = typedArray.getColor(R.styleable.LrcView_lrcNormalTextColor, Color.GRAY)
        mCurrentPlayLineColor =
            typedArray.getColor(R.styleable.LrcView_lrcCurrentTextColor, Color.BLUE)
        mNoLrcTextSize =
            typedArray.getDimension(
                R.styleable.LrcView_noLrcTextSize,
                dp2px(context, 20f).toFloat()
            )
        mNoLrcTextColor = typedArray.getColor(R.styleable.LrcView_noLrcTextColor, Color.BLACK)
        mIndicatorLineWidth =
            typedArray.getDimension(
                R.styleable.LrcView_indicatorLineHeight,
                dp2px(context, 0.5f).toFloat()
            )
        mIndicatorTextSize =
            typedArray.getDimension(
                R.styleable.LrcView_indicatorTextSize,
                sp2px(context, 13f).toFloat()
            )
        mIndicatorTextColor =
            typedArray.getColor(R.styleable.LrcView_indicatorTextColor, Color.GRAY)
        mCurrentIndicateLineTextColor =
            typedArray.getColor(R.styleable.LrcView_currentIndicateLrcColor, Color.GRAY)
        mIndicatorLineColor =
            typedArray.getColor(R.styleable.LrcView_indicatorLineColor, Color.GRAY)
        mIndicatorMargin =
            typedArray.getDimension(
                R.styleable.LrcView_indicatorStartEndMargin,
                dp2px(context, 5f).toFloat()
            )
        mIconLineGap =
            typedArray.getDimension(R.styleable.LrcView_iconLineGap, dp2px(context, 3f).toFloat())
        mIconWidth = typedArray.getDimension(
            R.styleable.LrcView_playIconWidth,
            dp2px(context, 20f).toFloat()
        )
        mIconHeight =
            typedArray.getDimension(
                R.styleable.LrcView_playIconHeight,
                dp2px(context, 20f).toFloat()
            )
        mPlayDrawable = typedArray.getDrawable(R.styleable.LrcView_playIcon)
        mPlayDrawable = if (mPlayDrawable == null) ContextCompat.getDrawable(
            context,
            R.mipmap.play
        ) else mPlayDrawable
        isCurrentTextBold = typedArray.getBoolean(R.styleable.LrcView_isLrcCurrentTextBold, false)
        isLrcIndicatorTextBold =
            typedArray.getBoolean(R.styleable.LrcView_isLrcIndicatorTextBold, false)
        typedArray.recycle()
        setupConfigs(context)
    }

    private fun setupConfigs(context: Context) {
        mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mMaximumFlingVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
        mMinimumFlingVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity
        mOverScroller = OverScroller(context, DecelerateInterpolator())
        mOverScroller!!.setFriction(0.1f)
        //        ViewConfiguration.getScrollFriction();  默认摩擦力 0.015f
        mTextPaint = TextPaint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textAlign = Paint.Align.CENTER
        mTextPaint!!.textSize = mLrcTextSize
        mDefaultContent = DEFAULT_CONTENT
        mIndicatorPaint = Paint()
        mIndicatorPaint?.isAntiAlias = true
        mIndicatorPaint?.strokeWidth = mIndicatorLineWidth
        mIndicatorPaint?.color = mIndicatorLineColor
        mPlayRect = Rect()
        mIndicatorPaint?.textSize = mIndicatorTextSize
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            mPlayRect?.left = mIndicatorMargin.toInt()
            mPlayRect?.top = (height / 2 - mIconHeight / 2).toInt()
            mPlayRect?.right = (mPlayRect!!.left + mIconWidth).toInt()
            mPlayRect?.bottom = (mPlayRect!!.top + mIconHeight).toInt()
            mPlayDrawable?.bounds = mPlayRect!!
        }
    }

    private fun getLrcWidth(): Int {
        return width - paddingLeft - paddingRight
    }

    private fun getLrcHeight(): Int {
        return height
    }

    private fun isLrcEmpty(): Boolean {
        return mLrcData == null || getLrcCount() == 0
    }

    private fun getLrcCount(): Int {
        return mLrcData!!.size
    }

    fun setLrcData(lrcData: ArrayList<Lrc>) {
        resetView(DEFAULT_CONTENT)
        mLrcData = lrcData
        invalidate()
    }

    fun setTypeFace(typeface: Typeface?) {
        if (mTextPaint != null) {
            mTextPaint!!.typeface = typeface
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isLrcEmpty()) {
            canvas?.let { drawEmptyText(it) }
            return
        }
        val indicatePosition: Int = getIndicatePosition()
        mTextPaint!!.textSize = mLrcTextSize
        mTextPaint!!.textAlign = Paint.Align.CENTER
        var y = getLrcHeight() / 2f
        val x = getLrcWidth() / 2f + paddingLeft
        for (i in 0 until getLrcCount()) {
            if (i > 0) {
                y += (getTextHeight(i - 1) + getTextHeight(i)) / 2f + mLrcLineSpaceHeight
            }
            if (mCurrentLine == i) {
                mTextPaint!!.color = mCurrentPlayLineColor
                mTextPaint!!.isFakeBoldText = isCurrentTextBold
            } else if (indicatePosition == i && isShowTimeIndicator) {
                mTextPaint!!.isFakeBoldText = isLrcIndicatorTextBold
                mTextPaint!!.color = mCurrentIndicateLineTextColor
            } else {
                mTextPaint!!.isFakeBoldText = false
                mTextPaint!!.color = mNormalColor
            }
            canvas?.let { drawLrc(it, x, y, i) }
        }

        if (isShowTimeIndicator) {
            canvas?.let { mPlayDrawable?.draw(it) }
            val time = mLrcData!![indicatePosition].time
            val timeWidth = mIndicatorPaint?.measureText(LrcHelper.formatTime(time))
            mIndicatorPaint?.color = mIndicatorLineColor
            if (timeWidth != null) {
                canvas?.drawLine(
                    mPlayRect!!.right + mIconLineGap, height / 2f,
                    width - timeWidth * 1.3f, height / 2f, mIndicatorPaint!!
                )
            }
            val baseX = (width - timeWidth!! * 1.1f).toInt()
            val baseline =
                height / 2f - (mIndicatorPaint!!.descent() - mIndicatorPaint!!.ascent()) / 2 - mIndicatorPaint!!.ascent()
            mIndicatorPaint!!.color = mIndicatorTextColor
            canvas?.drawText(
                LrcHelper.formatTime(time),
                baseX.toFloat(),
                baseline,
                mIndicatorPaint!!
            )
        }
    }

    private fun drawLrc(canvas: Canvas, x: Float, y: Float, i: Int) {
        val text = mLrcData!![i].text
        val builder: BidiFormatter.Builder = BidiFormatter.Builder()
        builder.stereoReset(true)
        val formatter: BidiFormatter = builder.build()
        val formattedText: String = formatter.unicodeWrap(text)
        var staticLayout = mLrcMap[formattedText]
        if (staticLayout == null) {
            mTextPaint!!.textSize = mLrcTextSize
            staticLayout = StaticLayout(
                formattedText, mTextPaint, getLrcWidth(),
                Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false
            )
            mLrcMap[formattedText] = staticLayout
        }
        canvas.save()
        canvas.translate(x, y - staticLayout.height / 2f - mOffset)
        staticLayout.draw(canvas)
        canvas.restore()
    }

    //中间空文字
    private fun drawEmptyText(canvas: Canvas) {
        mTextPaint!!.textAlign = Paint.Align.CENTER
        mTextPaint!!.color = mNoLrcTextColor
        mTextPaint!!.textSize = mNoLrcTextSize
        canvas.save()
        val staticLayout = StaticLayout(
            mDefaultContent, mTextPaint,
            getLrcWidth(), Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false
        )
        canvas.translate(getLrcWidth() / 2f + paddingLeft, getLrcHeight() / 2f)
        staticLayout.draw(canvas)
        canvas.restore()
    }

    fun updateTime(time: Long) {
        if (isLrcEmpty()) {
            return
        }
        val linePosition = getUpdateTimeLinePosition(time)
        if (mCurrentLine != linePosition) {
            mCurrentLine = linePosition
            if (isUserScroll) {
                invalidateView()
                return
            }
            ViewCompat.postOnAnimation(this@LrcView, mScrollRunnable)
        }
    }

    private fun getUpdateTimeLinePosition(time: Long): Int {
        //注意 time 单位为ms lrc.time 为s
        var linePos = 0
        val lrcCount = getLrcCount()
        for (i in 0 until lrcCount) {
            val lrc = mLrcData!![i]
            if (time >= (lrc.time) * 1000) {
                if (i == lrcCount - 1) {
                    linePos = lrcCount - 1
                } else if (time < (mLrcData!![i + 1].time) * 1000) {
                    linePos = i
                    break
                }
            }
        }
        return linePos
    }

    private val mScrollRunnable = Runnable {
        isUserScroll = false
        scrollToPosition(mCurrentLine)
    }

    private val mHideIndicatorRunnable = Runnable {
        isShowTimeIndicator = false
        invalidateView()
    }

    private fun scrollToPosition(linePosition: Int) {
        val scrollY = getItemOffsetY(linePosition)
        val animator = ValueAnimator.ofFloat(mOffset, scrollY)
        animator.addUpdateListener { animation ->
            mOffset = animation.animatedValue as Float
            invalidateView()
        }
        animator.duration = 300
        animator.start()
    }

    private fun getIndicatePosition(): Int {
        var pos = 0
        var min = Float.MAX_VALUE
        //itemOffset 和 mOffset 最小即当前位置
        for (i in mLrcData!!.indices) {
            val offsetY = getItemOffsetY(i)
            val abs = abs(offsetY - mOffset)
            if (abs < min) {
                min = abs
                pos = i
            }
        }
        return pos
    }

    private fun getItemOffsetY(linePosition: Int): Float {
        var tempY = 0f
        for (i in 1..linePosition) {
            tempY += (getTextHeight(i - 1) + getTextHeight(i)) / 2 + mLrcLineSpaceHeight
        }
        return tempY
    }

    private fun getTextHeight(linePosition: Int): Float {
        val text = mLrcData!![linePosition].text
        var staticLayout = mStaticLayoutHashMap[text]
        if (staticLayout == null) {
            mTextPaint!!.textSize = mLrcTextSize
            staticLayout = StaticLayout(
                text, mTextPaint,
                getLrcWidth(), Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false
            )
            mStaticLayoutHashMap[text] = staticLayout
        }
        return staticLayout.height.toFloat()
    }

    private fun overScrolled(): Boolean {
        return mOffset > getItemOffsetY(getLrcCount() - 1) || mOffset < 0
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isLrcEmpty()) {
            return super.onTouchEvent(event)
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                removeCallbacks(mScrollRunnable)
                removeCallbacks(mHideIndicatorRunnable)
                if (!mOverScroller!!.isFinished) {
                    mOverScroller!!.abortAnimation()
                }
                mLastMotionX = event.x
                mLastMotionY = event.y
                isUserScroll = true
                isDragging = false
            }

            MotionEvent.ACTION_MOVE -> {
                var moveY = event.y - mLastMotionY
                if (abs(moveY) > mScaledTouchSlop) {
                    isDragging = true
                    isShowTimeIndicator = isEnableShowIndicator
                }
                if (isDragging) {

//                    if (mOffset < 0) {
//                        mOffset = Math.max(mOffset, -getTextHeight(0) - mLrcLineSpaceHeight);
//                    }
                    val maxHeight = getItemOffsetY(getLrcCount() - 1)
                    //                    if (mOffset > maxHeight) {
//                        mOffset = Math.min(mOffset, maxHeight + getTextHeight(getLrcCount() - 1) + mLrcLineSpaceHeight);
//                    }
                    if (mOffset < 0 || mOffset > maxHeight) {
                        moveY /= 3.5f
                    }
                    mOffset -= moveY
                    mLastMotionY = event.y
                    invalidateView()
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (!isDragging && (!isShowTimeIndicator || !onClickPlayButton(event))) {
                    isShowTimeIndicator = false
                    invalidateView()
                    performClick()
                }
                handleActionUp(event)
            }
        }
//        return isDragging || super.onTouchEvent(event);
        //        return isDragging || super.onTouchEvent(event);
        return true
    }

    private fun handleActionUp(event: MotionEvent) {
        if (isEnableShowIndicator) {
            ViewCompat.postOnAnimationDelayed(
                this@LrcView,
                mHideIndicatorRunnable,
                mIndicatorTouchDelay.toLong()
            )
        }
        if (isShowTimeIndicator && mPlayRect != null && onClickPlayButton(event)) {
            isShowTimeIndicator = false
            invalidateView()
            if (mOnPlayIndicatorLineListener != null) {
                mOnPlayIndicatorLineListener?.onPlay(
                    mLrcData!![getIndicatePosition()].time,
                    mLrcData!![getIndicatePosition()].text
                )
            }
        }
        if (overScrolled() && mOffset < 0) {
            scrollToPosition(0)
            if (isAutoAdjustPosition) {
                ViewCompat.postOnAnimationDelayed(
                    this@LrcView,
                    mScrollRunnable,
                    mTouchDelay.toLong()
                )
            }
            return
        }
        if (overScrolled() && mOffset > getItemOffsetY(getLrcCount() - 1)) {
            scrollToPosition(getLrcCount() - 1)
            if (isAutoAdjustPosition) {
                ViewCompat.postOnAnimationDelayed(
                    this@LrcView,
                    mScrollRunnable,
                    mTouchDelay.toLong()
                )
            }
            return
        }
        mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumFlingVelocity.toFloat())
        val yVelocity = mVelocityTracker!!.yVelocity
        val absYVelocity = abs(yVelocity)
        if (absYVelocity > mMinimumFlingVelocity) {
            mOverScroller!!.fling(
                0, mOffset.toInt(), 0, (-yVelocity).toInt(), 0,
                0, 0, getItemOffsetY(getLrcCount() - 1).toInt(),
                0, getTextHeight(0).toInt()
            )
            invalidateView()
        }
        releaseVelocityTracker()
        if (isAutoAdjustPosition) {
            ViewCompat.postOnAnimationDelayed(this@LrcView, mScrollRunnable, mTouchDelay.toLong())
        }
    }

    private fun onClickPlayButton(event: MotionEvent): Boolean {
        val left = mPlayRect!!.left.toFloat()
        val right = mPlayRect!!.right.toFloat()
        val top = mPlayRect!!.top.toFloat()
        val bottom = mPlayRect!!.bottom.toFloat()
        val x = event.x
        val y = event.y
        return mLastMotionX > left && mLastMotionX < right && mLastMotionY > top && mLastMotionY < bottom && x > left && x < right && y > top && y < bottom
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mOverScroller!!.computeScrollOffset()) {
            mOffset = mOverScroller?.currY?.toFloat()!!
            invalidateView();
        }
    }

    private fun releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker!!.clear()
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    private fun resetView(defaultContent: String) {
        if (mLrcData != null) {
            mLrcData?.clear()
        }
        mLrcMap.clear()
        mStaticLayoutHashMap.clear()
        mCurrentLine = 0
        mOffset = 0f
        isUserScroll = false
        isDragging = false
        mDefaultContent = defaultContent
        removeCallbacks(mScrollRunnable)
        invalidate()
    }

    private fun dp2px(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal, context.resources.displayMetrics
        ).toInt()
    }

    private fun sp2px(context: Context, spVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            spVal, context.resources.displayMetrics
        ).toInt()
    }

    /**
     * 暂停（手动滑动歌词后，不再自动回滚至当前播放位置）
     */
    fun pause() {
        isAutoAdjustPosition = false
        invalidateView()
    }

    /**
     * 恢复（继续自动回滚）
     */
    fun resume() {
        isAutoAdjustPosition = true
        ViewCompat.postOnAnimationDelayed(this@LrcView, mScrollRunnable, mTouchDelay.toLong())
        invalidateView()
    }

    private fun invalidateView() {
        if (Looper.getMainLooper().thread === Thread.currentThread()) {
            invalidate()
        } else {
            postInvalidate()
        }
    }


    /*------------------Config-------------------*/

    /*------------------Config-------------------*/
    private var mOnPlayIndicatorLineListener: OnPlayIndicatorLineListener? = null

    fun setOnPlayIndicatorLineListener(onPlayIndicatorLineListener: OnPlayIndicatorLineListener?) {
        mOnPlayIndicatorLineListener = onPlayIndicatorLineListener
    }

    interface OnPlayIndicatorLineListener {
        fun onPlay(time: Float, content: String?)
    }


    fun setEmptyContent(defaultContent: String) {
        mDefaultContent = defaultContent
        invalidateView()
    }

    fun setLrcTextSize(lrcTextSize: Float) {
        mLrcTextSize = lrcTextSize
        invalidateView()
    }

    fun setLrcLineSpaceHeight(lrcLineSpaceHeight: Float) {
        mLrcLineSpaceHeight = lrcLineSpaceHeight
        invalidateView()
    }

    fun setTouchDelay(touchDelay: Int) {
        mTouchDelay = touchDelay
        invalidateView()
    }

    fun setNormalColor(@ColorInt normalColor: Int) {
        mNormalColor = normalColor
        invalidateView()
    }

    fun setCurrentPlayLineColor(@ColorInt currentPlayLineColor: Int) {
        mCurrentPlayLineColor = currentPlayLineColor
        invalidateView()
    }

    fun setNoLrcTextSize(noLrcTextSize: Float) {
        mNoLrcTextSize = noLrcTextSize
        invalidateView()
    }

    fun setNoLrcTextColor(@ColorInt noLrcTextColor: Int) {
        mNoLrcTextColor = noLrcTextColor
        invalidateView()
    }

    fun setIndicatorLineWidth(indicatorLineWidth: Float) {
        mIndicatorLineWidth = indicatorLineWidth
        invalidateView()
    }

    fun setIndicatorTextSize(indicatorTextSize: Float) {
//        mIndicatorTextSize = indicatorTextSize;
        mIndicatorPaint!!.textSize = indicatorTextSize
        invalidateView()
    }

    fun setCurrentIndicateLineTextColor(currentIndicateLineTextColor: Int) {
        mCurrentIndicateLineTextColor = currentIndicateLineTextColor
        invalidateView()
    }

    fun setIndicatorLineColor(indicatorLineColor: Int) {
        mIndicatorLineColor = indicatorLineColor
        invalidateView()
    }

    fun setIndicatorMargin(indicatorMargin: Float) {
        mIndicatorMargin = indicatorMargin
        invalidateView()
    }

    fun setIconLineGap(iconLineGap: Float) {
        mIconLineGap = iconLineGap
        invalidateView()
    }

    fun setIconWidth(iconWidth: Float) {
        mIconWidth = iconWidth
        invalidateView()
    }

    fun setIconHeight(iconHeight: Float) {
        mIconHeight = iconHeight
        invalidateView()
    }

    fun setEnableShowIndicator(enableShowIndicator: Boolean) {
        isEnableShowIndicator = enableShowIndicator
        invalidateView()
    }

    fun getPlayDrawable(): Drawable? {
        return mPlayDrawable
    }

    fun setPlayDrawable(playDrawable: Drawable) {
        mPlayDrawable = playDrawable
        mPlayDrawable!!.bounds = mPlayRect!!
        invalidateView()
    }

    fun setIndicatorTextColor(indicatorTextColor: Int) {
        mIndicatorTextColor = indicatorTextColor
        invalidateView()
    }

    fun setLrcCurrentTextBold(bold: Boolean) {
        isCurrentTextBold = bold
        invalidateView()
    }

    fun setLrcIndicatorTextBold(bold: Boolean) {
        isLrcIndicatorTextBold = bold
        invalidateView()
    }
}