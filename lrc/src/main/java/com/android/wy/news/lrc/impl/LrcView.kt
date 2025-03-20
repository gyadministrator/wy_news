package com.android.wy.news.lrc.impl

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.PointF
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.IntDef
import com.android.wy.news.lrc.listener.ILrcView
import com.android.wy.news.lrc.listener.ILrcViewListener
import com.android.wy.news.lrc.util.LrcRowUtil
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/22 10:42
  * @Version:        1.0
  * @Description:    
 */
@Suppress("DEPRECATION")
class LrcView : View, ILrcView {
    companion object {
        const val TAG = "LrcView"

        /**
         * 正常歌词模式
         */
        const val DISPLAY_MODE_NORMAL = 0

        /**
         * 拖动歌词模式
         */
        const val DISPLAY_MODE_SEEK = 1

        /**
         * 缩放歌词模式
         */
        const val DISPLAY_MODE_SCALE = 2

        /**
         * 歌词高亮的模式 正常高亮模式
         */
        const val MODE_HIGH_LIGHT_NORMAL = 0

        /**
         * 歌词高亮的模式 卡拉OK模式
         */
        const val MODE_HIGH_LIGHT_KARAOKE = 1
    }

    /**
     * 歌词的当前展示模式
     */
    private var mDisplayMode: Int = DISPLAY_MODE_NORMAL

    /**
     * 歌词集合，包含所有行的歌词
     */
    private var mLrcRows: ArrayList<LrcRow>? = null

    /**
     * 最小移动的距离，当拖动歌词时如果小于该距离不做处理
     */
    private val mMinSeekFiredOffset = 15

    /**
     * 当前高亮歌词的行数
     */
    private var mHighLightRow = 0

    /**
     * 当前高亮歌词的字体颜色为黄色
     */
    private var mHighLightRowColor = Color.RED

    /**
     * 不高亮歌词的字体颜色为白色
     */
    private var mNormalRowColor = Color.BLACK

    /**
     * 拖动歌词时，在当前高亮歌词下面的一条直线的字体颜色
     */
    private var mSeekLineColor = Color.BLUE

    /**
     * 拖动歌词时，展示当前高亮歌词的时间的字体颜色
     */
    private var mSeekLineTextColor = Color.BLUE

    /**
     * 拖动歌词时，展示当前高亮歌词的时间的字体大小默认值
     */
    private var mSeekLineTextSize: Int = LrcRowUtil.dp2px(context, 14f)

    /**
     * 拖动歌词时，展示当前高亮歌词的时间的字体大小最小值
     */
    private var mMinSeekLineTextSize: Int = LrcRowUtil.dp2px(context, 13f)

    /**
     * 拖动歌词时，展示当前高亮歌词的时间的字体大小最大值
     */
    private var mMaxSeekLineTextSize: Int = LrcRowUtil.dp2px(context, 16f)

    /**
     * 歌词字体大小默认值
     */
    private var mLrcFontSize: Int = LrcRowUtil.dp2px(context, 16f)

    /**
     * 高亮歌词字体大小
     */
    private var mLrcFontSelectSize: Int = LrcRowUtil.dp2px(context, 20f)

    /**
     * 歌词字体大小最小值
     */
    private var mMinLrcFontSize: Int = LrcRowUtil.dp2px(context, 16f)

    /**
     * 歌词字体大小最大值
     */
    private var mMaxLrcFontSize: Int = LrcRowUtil.dp2px(context, 20f)

    /**
     * 两行歌词之间的间距
     */
    private var mPaddingY: Int = LrcRowUtil.dp2px(context, 15f)

    /**
     * 拖动歌词时，在当前高亮歌词下面的一条直线的起始位置
     */
    private val mSeekLinePaddingX = 0

    /**
     * 拖动歌词的监听类，回调LrcViewListener类的onLrcSought方法
     */
    private var mLrcViewListener: ILrcViewListener? = null

    /**
     * 当没有歌词的时候展示的内容
     */
    private var mLoadingLrcTip: String? = "歌词加载中..."

    private var mPaint: TextPaint? = null

    /**
     * 当前播放的时间
     */
    private var currentMillis: Long = 0

    private var mLastMotionY = 0f

    /**
     * 第一个手指的坐标
     */
    private val mPointerOneLastMotion = PointF()

    /**
     * 第二个手指的坐标
     */
    private val mPointerTwoLastMotion = PointF()

    /**
     * 是否是第一次移动，当一个手指按下后开始移动的时候，设置为true,
     * 当第二个手指按下的时候，即两个手指同时移动的时候，设置为false
     */
    private var mIsFirstMove = false

    /**
     * 歌词高亮的模式   卡拉OK模式和正常高亮模式
     */
    private var mode = MODE_HIGH_LIGHT_KARAOKE

    //相对行间距，相对字体大小，0.5f表示行间距为0.5倍的字体高度
    private val spacingMult = 1.0f

    //在基础行距上添加多少
    private val spacingAdd = 0f

    @Retention(AnnotationRetention.BINARY)
    @IntDef(MODE_HIGH_LIGHT_NORMAL, MODE_HIGH_LIGHT_KARAOKE)
    annotation class LrcMode {}

    /*----------------------------------------公开方法---------------------------------------------------*/
    fun setMode(@LrcMode mode: Int): LrcView {
        this.mode = mode
        return this
    }

    fun setLoadingLrcTip(loadingLrcTip: String?): LrcView {
        this.mLoadingLrcTip = loadingLrcTip!!
        return this
    }

    fun setLineSpace(lineSpace: Int): LrcView {
        this.mPaddingY = LrcRowUtil.dp2px(context, lineSpace.toFloat())
        return this
    }

    fun setMaxLrcSize(maxLrcSize: Int): LrcView {
        this.mMaxLrcFontSize = LrcRowUtil.dp2px(context, maxLrcSize.toFloat())
        return this
    }

    fun setMinLrcSize(minLrcSize: Int): LrcView {
        this.mMinLrcFontSize = LrcRowUtil.dp2px(context, minLrcSize.toFloat())
        return this
    }

    fun setLrcSize(lrcSize: Int): LrcView {
        this.mLrcFontSize = LrcRowUtil.dp2px(context, lrcSize.toFloat())
        return this
    }

    fun setLrcSelectSize(lrcSelectSize: Int): LrcView {
        this.mLrcFontSelectSize = LrcRowUtil.dp2px(context, lrcSelectSize.toFloat())
        return this
    }

    fun setMaxSeekLineSize(maxSeekLineSize: Int): LrcView {
        this.mMaxSeekLineTextSize = LrcRowUtil.dp2px(context, maxSeekLineSize.toFloat())
        return this
    }

    fun setMinSeekLineSize(minSeekLineSize: Int): LrcView {
        this.mMinSeekLineTextSize = LrcRowUtil.dp2px(context, minSeekLineSize.toFloat())
        return this
    }

    fun setSeekLineSize(seekLineSize: Int): LrcView {
        this.mSeekLineTextSize = LrcRowUtil.dp2px(context, seekLineSize.toFloat())
        return this
    }

    fun setSeekLineColor(seekLineColor: Int): LrcView {
        this.mSeekLineColor = seekLineColor
        return this
    }

    fun setSeekLineLrcColor(seekLineLrcColor: Int): LrcView {
        this.mSeekLineTextColor = seekLineLrcColor
        return this
    }

    fun setNormalLrcColor(normalLrcColor: Int): LrcView {
        this.mNormalRowColor = normalLrcColor
        return this
    }

    fun setSelectLrcColor(selectLrcColor: Int): LrcView {
        this.mHighLightRowColor = selectLrcColor
        return this
    }

    /*----------------------------------------公开方法结束---------------------------------------------------*/

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.textSize = mLrcFontSize.toFloat()
    }

    /**
     * 设置歌词行集合
     *
     * @param lrcRows lrcRows
     */
    override fun setLrc(lrcRows: ArrayList<LrcRow>?) {
        this.mLrcRows = lrcRows
        invalidate()
    }

    /**
     * 播放的时候调用该方法滚动歌词，高亮正在播放的那句歌词
     *
     * @param time time
     */
    override fun seekLrcToTime(time: Long) {
        if (mLrcRows == null || mLrcRows!!.size == 0) {
            return
        }
        if (mDisplayMode != DISPLAY_MODE_NORMAL) {
            return
        }
        currentMillis = time
        Log.d(TAG, "seekLrcToTime:$time")
        for (i in mLrcRows!!.indices) {
            val current: LrcRow = mLrcRows!![i]
            val next: LrcRow? = if (i + 1 == mLrcRows!!.size) null else mLrcRows!![i + 1]
            /*
             *  正在播放的时间大于current行的歌词的时间而小于next行歌词的时间， 设置要高亮的行为current行
             *  正在播放的时间大于current行的歌词，而current行为最后一句歌词时，设置要高亮的行为current行
             */if (time >= current.startTime && next != null && time < next.startTime || time > current.startTime && next == null) {
                seekLrc(i, false)
                return
            }
        }
    }

    override fun setLrcViewListener(listener: ILrcViewListener?) {
        this.mLrcViewListener = listener
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        val height = height
        val width = width
        //当没有歌词的时候
        if (mLrcRows == null || mLrcRows!!.size == 0) {
            if (mLoadingLrcTip != null) {
                mPaint?.color = mNormalRowColor
                mPaint?.textSize = mLrcFontSize.toFloat()
                mPaint?.textAlign = Align.CENTER
                //mHighLightRow = 0
                canvas?.drawText(mLoadingLrcTip!!, width / 2f, height / 2f - mLrcFontSize, mPaint!!)
            }
            return
        }
        var rowY: Int
        val rowX = width / 2
        /*
         * 分以下三步来绘制歌词：
         *
         *         第1步：高亮地画出正在播放的那句歌词
         *        第2步：画出正在播放的那句歌词的上面可以展示出来的歌词
         *        第3步：画出正在播放的那句歌词的下面的可以展示出来的歌词
         */

        // 1、 高亮地画出正在要高亮的的那句歌词
        /*
         * 分以下三步来绘制歌词：
         *
         *         第1步：高亮地画出正在播放的那句歌词
         *        第2步：画出正在播放的那句歌词的上面可以展示出来的歌词
         *        第3步：画出正在播放的那句歌词的下面的可以展示出来的歌词
         */

        // 1、 高亮地画出正在要高亮的的那句歌词
        val highlightRowY = height / 2 - mLrcFontSize

        if (mode == MODE_HIGH_LIGHT_KARAOKE) {
            // 卡拉OK模式 逐字高亮
            canvas?.let { drawKaraokeHighLightLrcRow(it, width, rowX, highlightRowY) }
        } else {
            // 正常高亮
            canvas?.let { drawHighLrcRow(it, rowX, highlightRowY) }
        }

        // 上下拖动歌词的时候 画出拖动要高亮的那句歌词的时间 和 高亮的那句歌词下面的一条直线
        if (mDisplayMode == DISPLAY_MODE_SEEK) {
            // 画出高亮的那句歌词下面的一条直线
            mPaint?.color = mSeekLineColor
            //该直线的x坐标从0到屏幕宽度  y坐标为高亮歌词和下一行歌词中间
            canvas?.drawLine(
                mSeekLinePaddingX.toFloat(),
                (highlightRowY + mPaddingY).toFloat(),
                (width - mSeekLinePaddingX).toFloat(),
                (highlightRowY + mPaddingY).toFloat(),
                mPaint!!
            )

            // 画出高亮的那句歌词的时间
            mPaint?.color = mSeekLineTextColor
            mPaint?.textSize = mSeekLineTextSize.toFloat()
            mPaint?.textAlign = Align.LEFT
            canvas?.drawText(
                mLrcRows!![mHighLightRow].startTimeString, 0f, highlightRowY.toFloat(),
                mPaint!!
            )
        }

        // 2、画出正在播放的那句歌词的上面可以展示出来的歌词
        mPaint?.color = mNormalRowColor
        mPaint?.textSize = mLrcFontSize.toFloat()
        mPaint?.textAlign = Align.CENTER
        var rowNum: Int = mHighLightRow - 1
        rowY = highlightRowY - mPaddingY - mLrcFontSize

        //只画出正在播放的那句歌词的上一句歌词
        //if (rowY > -mLrcFontSize && rowNum >= 0) {
        //String text = mLrcRows.get(rowNum).content;
        //canvas.drawText(text, rowX, rowY, mPaint);
        //}

        //画出正在播放的那句歌词的上面所有的歌词

        //只画出正在播放的那句歌词的上一句歌词
//        if (rowY > -mLrcFontSize && rowNum >= 0) {
//            String text = mLrcRows.get(rowNum).content;
//            canvas.drawText(text, rowX, rowY, mPaint);
//        }

        //画出正在播放的那句歌词的上面所有的歌词
        while (rowY > -mLrcFontSize && rowNum >= 0) {
            if (rowNum < mLrcRows?.size!!) {
                val text = mLrcRows!![rowNum].content
                canvas!!.drawText(text, rowX.toFloat(), rowY.toFloat(), mPaint!!)
                rowY -= mPaddingY + mLrcFontSize
                rowNum--
            }
        }

        // 3、画出正在播放的那句歌词的下面的可以展示出来的歌词

        // 3、画出正在播放的那句歌词的下面的可以展示出来的歌词
        rowNum = mHighLightRow + 1
        rowY = highlightRowY + mPaddingY + mLrcFontSize

        //只画出正在播放的那句歌词的下一句歌词
        //if (rowY < height && rowNum < mLrcRows.size()) {
        //String text2 = mLrcRows.get(rowNum).content;
        //canvas.drawText(text2, rowX, rowY, mPaint);
        //}

        //画出正在播放的那句歌词的所有下面的可以展示出来的歌词

        //只画出正在播放的那句歌词的下一句歌词
//        if (rowY < height && rowNum < mLrcRows.size()) {
//            String text2 = mLrcRows.get(rowNum).content;
//            canvas.drawText(text2, rowX, rowY, mPaint);
//        }

        //画出正在播放的那句歌词的所有下面的可以展示出来的歌词
        while (rowY < height && rowNum < mLrcRows!!.size) {
            val text = mLrcRows!![rowNum].content
            //canvas.drawText(text, rowX, rowY, mPaint);
            /*-----------使用StaticLayout处理文字换行----------*/
            val staticLayout: StaticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(text, 0, text.length, mPaint!!, getWidth())
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(
                        spacingAdd,
                        spacingMult
                    ).setIncludePad(false).build()
            } else {
                StaticLayout(
                    text,
                    mPaint,
                    getWidth(),
                    Layout.Alignment.ALIGN_NORMAL,
                    spacingMult,
                    spacingAdd,
                    false
                )
            }
            canvas?.save()
            canvas?.translate(rowX.toFloat(), rowY.toFloat())
            staticLayout.draw(canvas)
            canvas?.restore()
            /*-----------使用StaticLayout处理文字换行结束----------*/
            rowY += mPaddingY + mLrcFontSize
            rowNum++
        }
    }

    private fun drawKaraokeHighLightLrcRow(
        canvas: Canvas,
        width: Int,
        rowX: Int,
        highlightRowY: Int
    ) {
        if (mHighLightRow > mLrcRows?.size!! - 1) return
        val highLrcRow: LrcRow = mLrcRows!![mHighLightRow]
        val highlightText: String = highLrcRow.content

        // 先画一层普通颜色的
        mPaint!!.color = mNormalRowColor
        mPaint!!.textSize = mLrcFontSelectSize.toFloat()
        mPaint!!.textAlign = Align.CENTER
        canvas.drawText(highlightText, rowX.toFloat(), highlightRowY.toFloat(), mPaint!!)
        /*-----------使用StaticLayout处理文字换行----------*/
        /*StaticLayout staticLayout;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            staticLayout = StaticLayout.Builder.obtain(highlightText, 0, highlightText.length(), mPaint, getWidth())
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(spacingAdd, spacingMult)
                    .setIncludePad(false)
                    .build();
        } else {
            staticLayout = new StaticLayout(highlightText, mPaint, getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
        canvas.save();
        canvas.translate(rowX, highlightRowY);
        staticLayout.draw(canvas);
        canvas.restore();*/
        /*-----------使用StaticLayout处理文字换行结束----------*/

        // 再画一层高亮颜色的
        val highLineWidth = mPaint!!.measureText(highlightText).toInt()
        val leftOffset = (width - highLineWidth) / 2
        val start: Long = highLrcRow.startTime
        val end: Long = highLrcRow.endTime
        // 高亮的宽度
        val highWidth = ((currentMillis - start) * 1.0f / (end - start) * highLineWidth).toInt()
        if (highWidth > 0) {
            //画一个 高亮的bitmap
            mPaint!!.color = mHighLightRowColor
            val textBitmap =
                Bitmap.createBitmap(highWidth, highlightRowY + mPaddingY, Bitmap.Config.ARGB_8888)
            val textCanvas = Canvas(textBitmap)
            textCanvas.drawText(
                highlightText,
                highLineWidth / 2f,
                highlightRowY.toFloat(),
                mPaint!!
            )
            /*-----------使用StaticLayout处理文字换行----------*/
            /*StaticLayout staticLayout1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                staticLayout1 = StaticLayout.Builder.obtain(highlightText, 0, highlightText.length(), mPaint, getWidth())
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(spacingAdd, spacingMult)
                        .setIncludePad(false)
                        .build();
            } else {
                staticLayout1 = new StaticLayout(highlightText, mPaint, getWidth(), Layout.Alignment.ALIGN_NORMAL, spacingMult, spacingAdd, false);
            }
            canvas.save();
            canvas.translate(highLineWidth / 2f, highlightRowY);
            staticLayout1.draw(textCanvas);
            canvas.restore();*/
            /*-----------使用StaticLayout处理文字换行结束----------*/canvas.drawBitmap(
                textBitmap,
                leftOffset.toFloat(),
                0f,
                mPaint
            )
        }
    }

    private fun drawHighLrcRow(canvas: Canvas, rowX: Int, highlightRowY: Int) {
        if (mHighLightRow > mLrcRows?.size!! - 1) return
        val highlightText = mLrcRows!![mHighLightRow].content
        mPaint!!.color = mHighLightRowColor
        mPaint!!.textSize = mLrcFontSelectSize.toFloat()
        mPaint!!.textAlign = Align.CENTER
        //canvas.drawText(highlightText, rowX, highlightRowY, mPaint);
        /*-----------使用StaticLayout处理文字换行----------*/
        val staticLayout: StaticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(
                highlightText, 0, highlightText.length,
                mPaint!!, width
            ).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(
                spacingAdd,
                spacingMult
            ).setIncludePad(false).build()
        } else {
            StaticLayout(
                highlightText,
                mPaint,
                width,
                Layout.Alignment.ALIGN_NORMAL,
                spacingMult,
                spacingAdd,
                false
            )
        }
        canvas.save()
        canvas.translate(rowX.toFloat(), highlightRowY.toFloat())
        staticLayout.draw(canvas)
        canvas.restore()
        /*-----------使用StaticLayout处理文字换行结束----------*/
    }

    /**
     * 设置要高亮的歌词为第几行歌词
     *
     * @param position 要高亮的歌词行数
     * @param cb       是否是手指拖动后要高亮的歌词
     */
    private fun seekLrc(position: Int, cb: Boolean) {
        if (mLrcRows == null || position < 0 || position > mLrcRows!!.size) {
            return
        }
        val lrcRow: LrcRow = mLrcRows!![position]
        mHighLightRow = position
        invalidate()
        //如果是手指拖动歌词后
        if (mLrcViewListener != null && cb) {
            //回调onLrcSought方法，将音乐播放器播放的位置移动到高亮歌词的位置
            mLrcViewListener?.onLrcDrag(position, lrcRow)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mLrcRows == null || mLrcRows!!.size == 0) {
            return super.onTouchEvent(event)
        }
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "down,mLastMotionY:$mLastMotionY")
                mLastMotionY = event.y
                mIsFirstMove = true
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2) {
                    Log.d(TAG, "two move")
                    doScale(event)
                    return true
                }
                Log.d(TAG, "one move")
                // single pointer mode ,seek
                //如果是双指同时按下，进行歌词大小缩放，抬起其中一个手指，另外一个手指不离开屏幕地移动的话，不做任何处理
                if (mDisplayMode == DISPLAY_MODE_SCALE) {
                    //if scaling but pointer become not two ,do nothing.
                    return true
                }
                //如果一个手指按下，在屏幕上移动的话，拖动歌词上下
                doSeek(event)
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (mDisplayMode == DISPLAY_MODE_SEEK) {
                    //高亮手指抬起时的歌词并播放从该句歌词开始播放
                    seekLrc(mHighLightRow, true)
                }
                mDisplayMode = DISPLAY_MODE_NORMAL
                invalidate()
            }
        }
        return true
    }

    /**
     * 处理双指在屏幕移动时的，歌词大小缩放
     */
    private fun doScale(event: MotionEvent) {
        //如果歌词的模式为：拖动歌词模式
        if (mDisplayMode == DISPLAY_MODE_SEEK) {
            //如果是单指按下，在进行歌词上下滚动，然后按下另外一个手指，则把歌词模式从 拖动歌词模式 变为 缩放歌词模式
            mDisplayMode = DISPLAY_MODE_SCALE
            Log.d(TAG, "change mode from DISPLAY_MODE_SEEK to DISPLAY_MODE_SCALE")
            return
        }
        // two pointer mode , scale font
        if (mIsFirstMove) {
            mDisplayMode = DISPLAY_MODE_SCALE
            invalidate()
            mIsFirstMove = false
            //两个手指的x坐标和y坐标
            setTwoPointerLocation(event)
        }
        //获取歌词大小要缩放的比例
        val scaleSize: Int = getScale(event)
        Log.d(TAG, "scaleSize:$scaleSize")
        //如果缩放大小不等于0，进行缩放，重绘LrcView
        if (scaleSize != 0) {
            setNewFontSize(scaleSize)
            invalidate()
        }
        setTwoPointerLocation(event)
    }

    /**
     * 处理单指在屏幕移动时，歌词上下滚动
     */
    private fun doSeek(event: MotionEvent) {
        val y = event.y //手指当前位置的y坐标
        val offsetY = y - mLastMotionY //第一次按下的y坐标和目前移动手指位置的y坐标之差
        //如果移动距离小于10，不做任何处理
        if (abs(offsetY) < mMinSeekFiredOffset) {
            return
        }
        //将模式设置为拖动歌词模式
        mDisplayMode = DISPLAY_MODE_SEEK
        val rowOffset = abs(offsetY.toInt() / mLrcFontSize) //歌词要滚动的行数
        Log.d(
            TAG,
            "move to new highLightRow : $mHighLightRow offsetY: $offsetY rowOffset:$rowOffset"
        )
        if (offsetY < 0) {
            //手指向上移动，歌词向下滚动
            mHighLightRow += rowOffset //设置要高亮的歌词为 当前高亮歌词 向下滚动rowOffset行后的歌词
        } else if (offsetY > 0) {
            //手指向下移动，歌词向上滚动
            mHighLightRow -= rowOffset //设置要高亮的歌词为 当前高亮歌词 向上滚动rowOffset行后的歌词
        }
        //设置要高亮的歌词为0和mHighLightRow中的较大值，即如果mHighLightRow < 0，mHighLightRow=0
        mHighLightRow = max(0, mHighLightRow)
        //设置要高亮的歌词为0和mHighLightRow中的较小值，即如果mHighlight > rowLrcRows.size()-1，mHighLightRow=mLrcRows.size()-1
        mHighLightRow = min(mHighLightRow, mLrcRows!!.size - 1)
        //如果歌词要滚动的行数大于0，则重画LrcView
        if (rowOffset > 0) {
            mLastMotionY = y
            invalidate()
        }
    }

    /**
     * 设置当前两个手指的x坐标和y坐标
     */
    private fun setTwoPointerLocation(event: MotionEvent) {
        mPointerOneLastMotion.x = event.getX(0)
        mPointerOneLastMotion.y = event.getY(0)
        mPointerTwoLastMotion.x = event.getX(1)
        mPointerTwoLastMotion.y = event.getY(1)
    }

    /**
     * 设置缩放后的字体大小
     */
    private fun setNewFontSize(scaleSize: Int) {
        //设置歌词缩放后的的最新字体大小
        mLrcFontSize += scaleSize
        mLrcFontSize = max(mLrcFontSize, mMinLrcFontSize)
        mLrcFontSize = min(mLrcFontSize, mMaxLrcFontSize)

        //设置显示高亮的那句歌词的时间最新字体大小
        mSeekLineTextSize += scaleSize
        mSeekLineTextSize = max(mSeekLineTextSize, mMinSeekLineTextSize)
        mSeekLineTextSize = min(mSeekLineTextSize, mMaxSeekLineTextSize)
    }

    /**
     * 获取歌词大小要缩放的比例
     */
    private fun getScale(event: MotionEvent): Int {
        Log.d(TAG, "scaleSize getScale")
        val x0 = event.getX(0)
        val y0 = event.getY(0)
        val x1 = event.getX(1)
        val y1 = event.getY(1)
        val maxOffset: Float
        val zooMin: Boolean
        //第一次双指之间的x坐标的差距
        val oldXOffset = abs(mPointerOneLastMotion.x - mPointerTwoLastMotion.x)
        //第二次双指之间的x坐标的差距
        val newXOffset = abs(x1 - x0)

        //第一次双指之间的y坐标的差距
        val oldYOffset = abs(mPointerOneLastMotion.y - mPointerTwoLastMotion.y)
        //第二次双指之间的y坐标的差距
        val newYOffset = abs(y1 - y0)

        //双指移动之后，判断双指之间移动的最大差距
        maxOffset = max(abs(newXOffset - oldXOffset), abs(newYOffset - oldYOffset))
        //如果x坐标移动的多一些
        zooMin = if (maxOffset == abs(newXOffset - oldXOffset)) {
            //如果第二次双指之间的x坐标的差距大于第一次双指之间的x坐标的差距则是放大，反之则缩小
            newXOffset > oldXOffset
        } else {
            //如果第二次双指之间的y坐标的差距大于第一次双指之间的y坐标的差距则是放大，反之则缩小
            newYOffset > oldYOffset
        }
        Log.d(TAG, "scaleSize maxOffset:$maxOffset")
        return if (zooMin) {
            (maxOffset / 10).toInt() //放大双指之间移动的最大差距的1/10
        } else {
            -(maxOffset / 10).toInt() //缩小双指之间移动的最大差距的1/10
        }
    }
}