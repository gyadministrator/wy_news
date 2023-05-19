package com.android.lyric.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.IntDef;

import com.android.lyric.ILrcView;
import com.android.lyric.ILrcViewListener;
import com.android.lyric.LrcRowUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * 自定义LrcView,可以同步显示歌词，拖动歌词，缩放歌词
 */
public class LrcView extends View implements ILrcView {

    public final static String TAG = "LrcView";

    /**
     * 正常歌词模式
     */
    public final static int DISPLAY_MODE_NORMAL = 0;
    /**
     * 拖动歌词模式
     */
    public final static int DISPLAY_MODE_SEEK = 1;
    /**
     * 缩放歌词模式
     */
    public final static int DISPLAY_MODE_SCALE = 2;
    /**
     * 歌词的当前展示模式
     */
    private int mDisplayMode = DISPLAY_MODE_NORMAL;

    /**
     * 歌词集合，包含所有行的歌词
     */
    private List<LrcRow> mLrcRows;
    /**
     * 最小移动的距离，当拖动歌词时如果小于该距离不做处理
     */
    private final static int mMinSeekFiredOffset = 15;

    /**
     * 当前高亮歌词的行数
     */
    private int mHighLightRow = 0;
    /**
     * 当前高亮歌词的字体颜色为黄色
     */
    private int mHighLightRowColor = Color.RED;
    /**
     * 不高亮歌词的字体颜色为白色
     */
    private int mNormalRowColor = Color.BLACK;

    /**
     * 拖动歌词时，在当前高亮歌词下面的一条直线的字体颜色
     **/
    private int mSeekLineColor = Color.BLUE;
    /**
     * 拖动歌词时，展示当前高亮歌词的时间的字体颜色
     **/
    private int mSeekLineTextColor = Color.BLUE;
    /**
     * 拖动歌词时，展示当前高亮歌词的时间的字体大小默认值
     **/
    private int mSeekLineTextSize = LrcRowUtil.dp2px(getContext(), 14);
    /**
     * 拖动歌词时，展示当前高亮歌词的时间的字体大小最小值
     **/
    private int mMinSeekLineTextSize = LrcRowUtil.dp2px(getContext(), 13);
    /**
     * 拖动歌词时，展示当前高亮歌词的时间的字体大小最大值
     **/
    private int mMaxSeekLineTextSize = LrcRowUtil.dp2px(getContext(), 16);

    /**
     * 歌词字体大小默认值
     **/
    private int mLrcFontSize = LrcRowUtil.dp2px(getContext(), 16);

    /**
     * 高亮歌词字体大小
     **/
    private int mLrcFontSelectSize = LrcRowUtil.dp2px(getContext(), 20);
    /**
     * 歌词字体大小最小值
     **/
    private int mMinLrcFontSize = LrcRowUtil.dp2px(getContext(), 16);
    /**
     * 歌词字体大小最大值
     **/
    private int mMaxLrcFontSize = LrcRowUtil.dp2px(getContext(), 20);

    /**
     * 两行歌词之间的间距
     **/
    private int mPaddingY = LrcRowUtil.dp2px(getContext(), 15);
    /**
     * 拖动歌词时，在当前高亮歌词下面的一条直线的起始位置
     **/
    private final static int mSeekLinePaddingX = 0;

    /**
     * 拖动歌词的监听类，回调LrcViewListener类的onLrcSought方法
     **/
    private ILrcViewListener mLrcViewListener;

    /**
     * 当没有歌词的时候展示的内容
     **/
    private String mLoadingLrcTip = "歌词加载中...";

    private final TextPaint mPaint;

    /**
     * 当前播放的时间
     */
    long currentMillis;

    /**
     * 歌词高亮的模式 正常高亮模式
     */
    public static final int MODE_HIGH_LIGHT_NORMAL = 0;
    /**
     * 歌词高亮的模式 卡拉OK模式
     */
    public static final int MODE_HIGH_LIGHT_KARAOKE = 1;

    /**
     * 歌词高亮的模式   卡拉OK模式和正常高亮模式
     */
    private int mode = MODE_HIGH_LIGHT_KARAOKE;

    //相对行间距，相对字体大小，0.5f表示行间距为0.5倍的字体高度
    private static final float spacingMult = 1.0f;

    //在基础行距上添加多少
    private static final float spacingAdd = 0f;

    @Retention(RetentionPolicy.CLASS)
    @IntDef({MODE_HIGH_LIGHT_NORMAL, MODE_HIGH_LIGHT_KARAOKE})
    public @interface LrcMode {

    }

    /*----------------------------------------公开方法---------------------------------------------------*/
    public LrcView setMode(@LrcMode int mode) {
        this.mode = mode;
        return this;
    }

    public LrcView setLoadingLrcTip(String loadingLrcTip) {
        this.mLoadingLrcTip = loadingLrcTip;
        return this;
    }

    public LrcView setLineSpace(int lineSpace) {
        this.mPaddingY = LrcRowUtil.dp2px(getContext(), lineSpace);
        return this;
    }

    public LrcView setMaxLrcSize(int maxLrcSize) {
        this.mMaxLrcFontSize = LrcRowUtil.dp2px(getContext(), maxLrcSize);
        return this;
    }

    public LrcView setMinLrcSize(int minLrcSize) {
        this.mMinLrcFontSize = LrcRowUtil.dp2px(getContext(), minLrcSize);
        return this;
    }

    public LrcView setLrcSize(int lrcSize) {
        this.mLrcFontSize = LrcRowUtil.dp2px(getContext(), lrcSize);
        return this;
    }

    public LrcView setLrcSelectSize(int lrcSelectSize) {
        this.mLrcFontSelectSize = LrcRowUtil.dp2px(getContext(), lrcSelectSize);
        return this;
    }

    public LrcView setMaxSeekLineSize(int maxSeekLineSize) {
        this.mMaxSeekLineTextSize = LrcRowUtil.dp2px(getContext(), maxSeekLineSize);
        return this;
    }

    public LrcView setMinSeekLineSize(int minSeekLineSize) {
        this.mMinSeekLineTextSize = LrcRowUtil.dp2px(getContext(), minSeekLineSize);
        return this;
    }

    public LrcView setSeekLineSize(int seekLineSize) {
        this.mSeekLineTextSize = LrcRowUtil.dp2px(getContext(), seekLineSize);
        return this;
    }

    public LrcView setSeekLineColor(int seekLineColor) {
        this.mSeekLineColor = seekLineColor;
        return this;
    }

    public LrcView setSeekLineLrcColor(int seekLineLrcColor) {
        this.mSeekLineTextColor = seekLineLrcColor;
        return this;
    }

    public LrcView setNormalLrcColor(int normalLrcColor) {
        this.mNormalRowColor = normalLrcColor;
        return this;
    }

    public LrcView setSelectLrcColor(int selectLrcColor) {
        this.mHighLightRowColor = selectLrcColor;
        return this;
    }

    @Override
    public void setLrcViewListener(ILrcViewListener listener) {
        mLrcViewListener = listener;
    }
    /*----------------------------------------公开方法结束---------------------------------------------------*/

    public LrcView(Context context, AttributeSet attr) {
        super(context, attr);
        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mLrcFontSize);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        final int height = getHeight();
        final int width = getWidth();
        //当没有歌词的时候
        if (mLrcRows == null || mLrcRows.size() == 0) {
            if (mLoadingLrcTip != null) {
                mPaint.setColor(mHighLightRowColor);
                mPaint.setTextSize(mLrcFontSize);
                mPaint.setTextAlign(Align.CENTER);
                canvas.drawText(mLoadingLrcTip, width / 2f, height / 2f - mLrcFontSize, mPaint);
            }
            return;
        }

        int rowY;
        final int rowX = width / 2;
        int rowNum;
        /*
         * 分以下三步来绘制歌词：
         *
         *         第1步：高亮地画出正在播放的那句歌词
         *        第2步：画出正在播放的那句歌词的上面可以展示出来的歌词
         *        第3步：画出正在播放的那句歌词的下面的可以展示出来的歌词
         */

        // 1、 高亮地画出正在要高亮的的那句歌词
        int highlightRowY = height / 2 - mLrcFontSize;

        if (mode == MODE_HIGH_LIGHT_KARAOKE) {
            // 卡拉OK模式 逐字高亮
            drawKaraokeHighLightLrcRow(canvas, width, rowX, highlightRowY);
        } else {
            // 正常高亮
            drawHighLrcRow(canvas, rowX, highlightRowY);
        }

        // 上下拖动歌词的时候 画出拖动要高亮的那句歌词的时间 和 高亮的那句歌词下面的一条直线
        if (mDisplayMode == DISPLAY_MODE_SEEK) {
            // 画出高亮的那句歌词下面的一条直线
            mPaint.setColor(mSeekLineColor);
            //该直线的x坐标从0到屏幕宽度  y坐标为高亮歌词和下一行歌词中间
            canvas.drawLine(mSeekLinePaddingX, highlightRowY + mPaddingY, width - mSeekLinePaddingX, highlightRowY + mPaddingY, mPaint);

            // 画出高亮的那句歌词的时间
            mPaint.setColor(mSeekLineTextColor);
            mPaint.setTextSize(mSeekLineTextSize);
            mPaint.setTextAlign(Align.LEFT);
            canvas.drawText(mLrcRows.get(mHighLightRow).startTimeString, 0, highlightRowY, mPaint);
        }

        // 2、画出正在播放的那句歌词的上面可以展示出来的歌词
        mPaint.setColor(mNormalRowColor);
        mPaint.setTextSize(mLrcFontSize);
        mPaint.setTextAlign(Align.CENTER);
        rowNum = mHighLightRow - 1;
        rowY = highlightRowY - mPaddingY - mLrcFontSize;

        //只画出正在播放的那句歌词的上一句歌词
//        if (rowY > -mLrcFontSize && rowNum >= 0) {
//            String text = mLrcRows.get(rowNum).content;
//            canvas.drawText(text, rowX, rowY, mPaint);
//        }

        //画出正在播放的那句歌词的上面所有的歌词
        while (rowY > -mLrcFontSize && rowNum >= 0) {
            String text = mLrcRows.get(rowNum).content;
            canvas.drawText(text, rowX, rowY, mPaint);
            rowY -= (mPaddingY + mLrcFontSize);
            rowNum--;
        }

        // 3、画出正在播放的那句歌词的下面的可以展示出来的歌词
        rowNum = mHighLightRow + 1;
        rowY = highlightRowY + mPaddingY + mLrcFontSize;

        //只画出正在播放的那句歌词的下一句歌词
//        if (rowY < height && rowNum < mLrcRows.size()) {
//            String text2 = mLrcRows.get(rowNum).content;
//            canvas.drawText(text2, rowX, rowY, mPaint);
//        }

        //画出正在播放的那句歌词的所有下面的可以展示出来的歌词
        while (rowY < height && rowNum < mLrcRows.size()) {
            String text = mLrcRows.get(rowNum).content;
            //canvas.drawText(text, rowX, rowY, mPaint);
            /*-----------使用StaticLayout处理文字换行----------*/
            StaticLayout staticLayout;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                staticLayout = StaticLayout.Builder.obtain(text, 0, text.length(), mPaint, getWidth()).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(spacingAdd, spacingMult).setIncludePad(false).build();
            } else {
                staticLayout = new StaticLayout(text, mPaint, getWidth(), Layout.Alignment.ALIGN_NORMAL, spacingMult, spacingAdd, false);
            }
            canvas.save();
            canvas.translate(rowX, rowY);
            staticLayout.draw(canvas);
            canvas.restore();
            /*-----------使用StaticLayout处理文字换行结束----------*/
            rowY += (mPaddingY + mLrcFontSize);
            rowNum++;
        }

    }

    private void drawKaraokeHighLightLrcRow(Canvas canvas, int width, int rowX, int highlightRowY) {
        LrcRow highLrcRow = mLrcRows.get(mHighLightRow);
        String highlightText = highLrcRow.content;

        // 先画一层普通颜色的
        mPaint.setColor(mNormalRowColor);
        mPaint.setTextSize(mLrcFontSelectSize);
        mPaint.setTextAlign(Align.CENTER);
        canvas.drawText(highlightText, rowX, highlightRowY, mPaint);
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
        int highLineWidth = (int) mPaint.measureText(highlightText);
        int leftOffset = (width - highLineWidth) / 2;
        long start = highLrcRow.getStartTime();
        long end = highLrcRow.getEndTime();
        // 高亮的宽度
        int highWidth = (int) ((currentMillis - start) * 1.0f / (end - start) * highLineWidth);
        if (highWidth > 0) {
            //画一个 高亮的bitmap
            mPaint.setColor(mHighLightRowColor);
            Bitmap textBitmap = Bitmap.createBitmap(highWidth, highlightRowY + mPaddingY, Bitmap.Config.ARGB_8888);
            Canvas textCanvas = new Canvas(textBitmap);
            textCanvas.drawText(highlightText, highLineWidth / 2f, highlightRowY, mPaint);
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
            /*-----------使用StaticLayout处理文字换行结束----------*/
            canvas.drawBitmap(textBitmap, leftOffset, 0, mPaint);
        }
    }

    private void drawHighLrcRow(Canvas canvas, int rowX, int highlightRowY) {
        String highlightText = mLrcRows.get(mHighLightRow).content;
        mPaint.setColor(mHighLightRowColor);
        mPaint.setTextSize(mLrcFontSelectSize);
        mPaint.setTextAlign(Align.CENTER);
        //canvas.drawText(highlightText, rowX, highlightRowY, mPaint);
        /*-----------使用StaticLayout处理文字换行----------*/
        StaticLayout staticLayout;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            staticLayout = StaticLayout.Builder.obtain(highlightText, 0, highlightText.length(), mPaint, getWidth()).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(spacingAdd, spacingMult).setIncludePad(false).build();
        } else {
            staticLayout = new StaticLayout(highlightText, mPaint, getWidth(), Layout.Alignment.ALIGN_NORMAL, spacingMult, spacingAdd, false);
        }
        canvas.save();
        canvas.translate(rowX, highlightRowY);
        staticLayout.draw(canvas);
        canvas.restore();
        /*-----------使用StaticLayout处理文字换行结束----------*/
    }

    /**
     * 设置要高亮的歌词为第几行歌词
     *
     * @param position 要高亮的歌词行数
     * @param cb       是否是手指拖动后要高亮的歌词
     */
    public void seekLrc(int position, boolean cb) {
        if (mLrcRows == null || position < 0 || position > mLrcRows.size()) {
            return;
        }
        LrcRow lrcRow = mLrcRows.get(position);
        mHighLightRow = position;
        invalidate();
        //如果是手指拖动歌词后
        if (mLrcViewListener != null && cb) {
            //回调onLrcSought方法，将音乐播放器播放的位置移动到高亮歌词的位置
            mLrcViewListener.onLrcSought(position, lrcRow);
        }
    }

    private float mLastMotionY;
    /**
     * 第一个手指的坐标
     **/
    private final PointF mPointerOneLastMotion = new PointF();
    /**
     * 第二个手指的坐标
     **/
    private final PointF mPointerTwoLastMotion = new PointF();
    /**
     * 是否是第一次移动，当一个手指按下后开始移动的时候，设置为true,
     * 当第二个手指按下的时候，即两个手指同时移动的时候，设置为false
     */
    private boolean mIsFirstMove = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLrcRows == null || mLrcRows.size() == 0) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            //手指按下
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "down,mLastMotionY:" + mLastMotionY);
                mLastMotionY = event.getY();
                mIsFirstMove = true;
                invalidate();
                break;
            //手指移动
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {
                    Log.d(TAG, "two move");
                    doScale(event);
                    return true;
                }
                Log.d(TAG, "one move");
                // single pointer mode ,seek
                //如果是双指同时按下，进行歌词大小缩放，抬起其中一个手指，另外一个手指不离开屏幕地移动的话，不做任何处理
                if (mDisplayMode == DISPLAY_MODE_SCALE) {
                    //if scaling but pointer become not two ,do nothing.
                    return true;
                }
                //如果一个手指按下，在屏幕上移动的话，拖动歌词上下
                doSeek(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                //手指抬起
            case MotionEvent.ACTION_UP:
                if (mDisplayMode == DISPLAY_MODE_SEEK) {
                    //高亮手指抬起时的歌词并播放从该句歌词开始播放
                    seekLrc(mHighLightRow, true);
                }
                mDisplayMode = DISPLAY_MODE_NORMAL;
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 处理双指在屏幕移动时的，歌词大小缩放
     */
    private void doScale(MotionEvent event) {
        //如果歌词的模式为：拖动歌词模式
        if (mDisplayMode == DISPLAY_MODE_SEEK) {
            //如果是单指按下，在进行歌词上下滚动，然后按下另外一个手指，则把歌词模式从 拖动歌词模式 变为 缩放歌词模式
            mDisplayMode = DISPLAY_MODE_SCALE;
            Log.d(TAG, "change mode from DISPLAY_MODE_SEEK to DISPLAY_MODE_SCALE");
            return;
        }
        // two pointer mode , scale font
        if (mIsFirstMove) {
            mDisplayMode = DISPLAY_MODE_SCALE;
            invalidate();
            mIsFirstMove = false;
            //两个手指的x坐标和y坐标
            setTwoPointerLocation(event);
        }
        //获取歌词大小要缩放的比例
        int scaleSize = getScale(event);
        Log.d(TAG, "scaleSize:" + scaleSize);
        //如果缩放大小不等于0，进行缩放，重绘LrcView
        if (scaleSize != 0) {
            setNewFontSize(scaleSize);
            invalidate();
        }
        setTwoPointerLocation(event);
    }

    /**
     * 处理单指在屏幕移动时，歌词上下滚动
     */
    private void doSeek(MotionEvent event) {
        float y = event.getY();//手指当前位置的y坐标
        float offsetY = y - mLastMotionY; //第一次按下的y坐标和目前移动手指位置的y坐标之差
        //如果移动距离小于10，不做任何处理
        if (Math.abs(offsetY) < mMinSeekFiredOffset) {
            return;
        }
        //将模式设置为拖动歌词模式
        mDisplayMode = DISPLAY_MODE_SEEK;
        int rowOffset = Math.abs((int) offsetY / mLrcFontSize); //歌词要滚动的行数

        Log.d(TAG, "move to new hightlightrow : " + mHighLightRow + " offsetY: " + offsetY + " rowOffset:" + rowOffset);

        if (offsetY < 0) {
            //手指向上移动，歌词向下滚动
            mHighLightRow += rowOffset;//设置要高亮的歌词为 当前高亮歌词 向下滚动rowOffset行后的歌词
        } else if (offsetY > 0) {
            //手指向下移动，歌词向上滚动
            mHighLightRow -= rowOffset;//设置要高亮的歌词为 当前高亮歌词 向上滚动rowOffset行后的歌词
        }
        //设置要高亮的歌词为0和mHighLightRow中的较大值，即如果mHighLightRow < 0，mHighLightRow=0
        mHighLightRow = Math.max(0, mHighLightRow);
        //设置要高亮的歌词为0和mHighLightRow中的较小值，即如果mHighlight > rowLrcRows.size()-1，mHighLightRow=mLrcRows.size()-1
        mHighLightRow = Math.min(mHighLightRow, mLrcRows.size() - 1);
        //如果歌词要滚动的行数大于0，则重画LrcView
        if (rowOffset > 0) {
            mLastMotionY = y;
            invalidate();
        }
    }

    /**
     * 设置当前两个手指的x坐标和y坐标
     */
    private void setTwoPointerLocation(MotionEvent event) {
        mPointerOneLastMotion.x = event.getX(0);
        mPointerOneLastMotion.y = event.getY(0);
        mPointerTwoLastMotion.x = event.getX(1);
        mPointerTwoLastMotion.y = event.getY(1);
    }

    /**
     * 设置缩放后的字体大小
     */
    private void setNewFontSize(int scaleSize) {
        //设置歌词缩放后的的最新字体大小
        mLrcFontSize += scaleSize;
        mLrcFontSize = Math.max(mLrcFontSize, mMinLrcFontSize);
        mLrcFontSize = Math.min(mLrcFontSize, mMaxLrcFontSize);

        //设置显示高亮的那句歌词的时间最新字体大小
        mSeekLineTextSize += scaleSize;
        mSeekLineTextSize = Math.max(mSeekLineTextSize, mMinSeekLineTextSize);
        mSeekLineTextSize = Math.min(mSeekLineTextSize, mMaxSeekLineTextSize);
    }

    /**
     * 获取歌词大小要缩放的比例
     */
    private int getScale(MotionEvent event) {
        Log.d(TAG, "scaleSize getScale");
        float x0 = event.getX(0);
        float y0 = event.getY(0);
        float x1 = event.getX(1);
        float y1 = event.getY(1);

        float maxOffset;

        boolean zooMin;
        //第一次双指之间的x坐标的差距
        float oldXOffset = Math.abs(mPointerOneLastMotion.x - mPointerTwoLastMotion.x);
        //第二次双指之间的x坐标的差距
        float newXOffset = Math.abs(x1 - x0);

        //第一次双指之间的y坐标的差距
        float oldYOffset = Math.abs(mPointerOneLastMotion.y - mPointerTwoLastMotion.y);
        //第二次双指之间的y坐标的差距
        float newYOffset = Math.abs(y1 - y0);

        //双指移动之后，判断双指之间移动的最大差距
        maxOffset = Math.max(Math.abs(newXOffset - oldXOffset), Math.abs(newYOffset - oldYOffset));
        //如果x坐标移动的多一些
        if (maxOffset == Math.abs(newXOffset - oldXOffset)) {
            //如果第二次双指之间的x坐标的差距大于第一次双指之间的x坐标的差距则是放大，反之则缩小
            zooMin = newXOffset > oldXOffset;
        }
        //如果y坐标移动的多一些
        else {
            //如果第二次双指之间的y坐标的差距大于第一次双指之间的y坐标的差距则是放大，反之则缩小
            zooMin = newYOffset > oldYOffset;
        }
        Log.d(TAG, "scaleSize maxOffset:" + maxOffset);
        if (zooMin) {
            return (int) (maxOffset / 10);//放大双指之间移动的最大差距的1/10
        } else {
            return -(int) (maxOffset / 10);//缩小双指之间移动的最大差距的1/10
        }
    }

    /**
     * 设置歌词行集合
     *
     * @param lrcRows lrcRows
     */
    public void setLrc(List<LrcRow> lrcRows) {
        mLrcRows = lrcRows;
        invalidate();
    }

    /**
     * 播放的时候调用该方法滚动歌词，高亮正在播放的那句歌词
     *
     * @param time time
     */
    public void seekLrcToTime(long time) {
        if (mLrcRows == null || mLrcRows.size() == 0) {
            return;
        }
        if (mDisplayMode != DISPLAY_MODE_NORMAL) {
            return;
        }

        currentMillis = time;
        Log.d(TAG, "seekLrcToTime:" + time);

        for (int i = 0; i < mLrcRows.size(); i++) {
            LrcRow current = mLrcRows.get(i);
            LrcRow next = i + 1 == mLrcRows.size() ? null : mLrcRows.get(i + 1);
            /*
             *  正在播放的时间大于current行的歌词的时间而小于next行歌词的时间， 设置要高亮的行为current行
             *  正在播放的时间大于current行的歌词，而current行为最后一句歌词时，设置要高亮的行为current行
             */
            if ((time >= current.startTime && next != null && time < next.startTime) || (time > current.startTime && next == null)) {
                seekLrc(i, false);
                return;
            }
        }
    }
}