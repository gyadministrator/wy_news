package com.android.wy.news.bottombar.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/17 10:29
  * @Version:        1.0
  * @Description:    
 */
open class BottomBar : View {
    private var context: Context? = null
    private var containerId = 0

    private var fragmentClassList: ArrayList<Class<*>> = ArrayList()
    private var titleList = ArrayList<String>()
    private var iconResBeforeList = ArrayList<Int>()
    private var iconResAfterList = ArrayList<Int>()

    private var fragmentList = ArrayList<Fragment?>()

    private var itemCount = 0

    private var paint = Paint()

    private var iconBitmapBeforeList = ArrayList<Bitmap?>()
    private var iconBitmapAfterList = ArrayList<Bitmap?>()
    private var iconRectList = ArrayList<Rect>()

    private var currentCheckedIndex = 0
    private var firstCheckedIndex = 0

    private var titleColorBefore = Color.parseColor("#999999")
    private var titleColorAfter = Color.parseColor("#ff5d5e")

    private var titleSizeInDp = 10
    private var iconWidth = 20
    private var iconHeight = 20
    private var titleIconMargin = 5

    private var titleBaseLine = 0
    private var titleXList = ArrayList<Int>()

    private var parentItemWidth = 0
    private var target = -1
    private var currentFragment: Fragment? = null
    private var onBottomBarSelectListener: OnBottomBarSelectListener? = null


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.context = context
    }

    fun setContainer(containerId: Int): BottomBar {
        this.containerId = containerId
        return this
    }

    /**
     * 支持"#333333"这种形式
     */
    fun setTitleBeforeAndAfterColor(
        beforeResCode: String?,
        afterResCode: String?
    ): BottomBar {
        titleColorBefore = Color.parseColor(beforeResCode)
        titleColorAfter = Color.parseColor(afterResCode)
        return this
    }

    fun setTitleBeforeAndAfterColor(
        beforeResCode: Int,
        afterResCode: Int
    ): BottomBar {
        titleColorBefore = beforeResCode
        titleColorAfter = afterResCode
        return this
    }

    fun setTitleSize(titleSizeInDp: Int): BottomBar {
        this.titleSizeInDp = titleSizeInDp
        return this
    }

    fun setIconWidth(iconWidth: Int): BottomBar {
        this.iconWidth = iconWidth
        return this
    }

    fun setTitleIconMargin(titleIconMargin: Int): BottomBar {
        this.titleIconMargin = titleIconMargin
        return this
    }

    fun setIconHeight(iconHeight: Int): BottomBar {
        this.iconHeight = iconHeight
        return this
    }

    fun setSelectListener(onBottomBarSelectListener: OnBottomBarSelectListener): BottomBar {
        this.onBottomBarSelectListener = onBottomBarSelectListener
        return this
    }

    fun addItem(
        fragmentClass: Class<*>,
        title: String,
        iconResBefore: Int,
        iconResAfter: Int
    ): BottomBar {
        fragmentClassList.add(fragmentClass)
        titleList.add(title)
        iconResBeforeList.add(iconResBefore)
        iconResAfterList.add(iconResAfter)
        return this
    }

    fun setFirstChecked(firstCheckedIndex: Int): BottomBar { //从0开始
        this.firstCheckedIndex = firstCheckedIndex
        return this
    }

    fun build() {
        itemCount = fragmentClassList.size
        //预创建bitmap的Rect并缓存
        //预创建icon的Rect并缓存
        for (i in 0 until itemCount) {
            val beforeBitmap = getBitmap(iconResBeforeList[i])
            iconBitmapBeforeList.add(beforeBitmap)
            val afterBitmap = getBitmap(iconResAfterList[i])
            iconBitmapAfterList.add(afterBitmap)
            val rect = Rect()
            iconRectList.add(rect)
            val clx = fragmentClassList[i]
            try {
                val fragment = clx.newInstance() as Fragment
                fragmentList.add(fragment)
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        currentCheckedIndex = firstCheckedIndex
        switchFragment(currentCheckedIndex)
        invalidate()
    }

    private fun getBitmap(resId: Int): Bitmap? {
        val bitmapDrawable = context?.let { ContextCompat.getDrawable(it, resId) } as BitmapDrawable
        return bitmapDrawable.bitmap
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        initParam()
    }

    private fun initParam() {
        if (itemCount != 0) {
            //单个item宽高
            parentItemWidth = width / itemCount
            val parentItemHeight = height

            //图标边长
            val iconWidth = dp2px(iconWidth.toFloat()) //先指定20dp
            val iconHeight = dp2px(iconHeight.toFloat())

            //图标文字margin
            val textIconMargin =
                dp2px(titleIconMargin.toFloat() / 2) //先指定5dp，这里除以一半才是正常的margin，不知道为啥，可能是图片的原因

            //标题高度
            val titleSize = dp2px(titleSizeInDp.toFloat()) //这里先指定10dp
            paint.textSize = titleSize.toFloat()
            val rect = Rect()
            paint.getTextBounds(titleList[0], 0, titleList[0].length, rect)
            val titleHeight = rect.height()

            //从而计算得出图标的起始top坐标、文本的baseLine
            val iconTop = (parentItemHeight - iconHeight - textIconMargin - titleHeight) / 2
            titleBaseLine = parentItemHeight - iconTop

            //对icon的rect的参数进行赋值
            val firstRectX = (parentItemWidth - iconWidth) / 2 //第一个icon的左
            for (i in 0 until itemCount) {
                val rectX = i * parentItemWidth + firstRectX
                val temp = iconRectList[i]
                temp.left = rectX
                temp.top = iconTop
                temp.right = rectX + iconWidth
                temp.bottom = iconTop + iconHeight
            }

            //标题（单位是个问题）
            for (i in 0 until itemCount) {
                val title = titleList[i]
                paint.getTextBounds(title, 0, title.length, rect)
                titleXList.add((parentItemWidth - rect.width()) / 2 + parentItemWidth * i)
            }
        }
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = context?.resources?.displayMetrics?.density
        return (dpValue * scale!! + 0.5f).toInt()
    }

    /**
     * 修改bitmap的颜色
     */
    private fun makeTintBitmap(inputBitmap: Bitmap, tintColor: Int): Bitmap? {
        val outputBitmap =
            Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)
        val canvas = Canvas(outputBitmap)
        val paint = Paint()
        paint.colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(inputBitmap, 0f, 0f, paint)
        return outputBitmap
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //这里让view自身替我们画背景 如果指定的话
        if (itemCount != 0) {
            //画背景
            paint.isAntiAlias = false
            for (i in 0 until itemCount) {
                val bitmap: Bitmap? = if (i == currentCheckedIndex) {
                    iconBitmapAfterList[i]
                } else {
                    iconBitmapBeforeList[i]
                }
                val rect = iconRectList[i]
                val tintBitmap: Bitmap? = if (i == currentCheckedIndex) {
                    bitmap?.let { makeTintBitmap(it, titleColorAfter) }
                } else {
                    bitmap?.let { makeTintBitmap(it, titleColorBefore) }
                }
                tintBitmap?.let { canvas.drawBitmap(it, null, rect, paint) } //null代表bitmap全部画出
            }

            //画文字
            paint.isAntiAlias = true
            for (i in 0 until itemCount) {
                val title = titleList[i]
                if (i == currentCheckedIndex) {
                    paint.color = titleColorAfter
                } else {
                    paint.color = titleColorBefore
                }
                val x = titleXList[i]
                canvas.drawText(title, x.toFloat(), titleBaseLine.toFloat(), paint)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> target = withinWhichArea(event.x.toInt())
            MotionEvent.ACTION_UP -> {
                if (event.y > 0) {
                    if (target == withinWhichArea(event.x.toInt())) {
                        //这里触发点击事件
                        switchFragment(target)
                        currentCheckedIndex = target
                        invalidate()
                        onBottomBarSelectListener?.onItemSelect(currentCheckedIndex)
                    }
                    target = -1
                }
            }
        }
        return true
        //这里return super为什么up执行不到？是因为return super的值，全部取决于你是否
        //clickable，当你down事件来临，不可点击，所以return false，也就是说，而且你没
        //有设置onTouchListener，并且控件是ENABLE的，所以dispatchTouchEvent的返回值
        //也是false，所以在view group的dispatchTransformedTouchEvent也是返回false，
        //这样一来，view group中的first touch target就是空的，所以intercept标记位
        //果断为false，然后就再也进不到循环取孩子的步骤了，直接调用dispatch-
        // TransformedTouchEvent并传孩子为null，所以直接调用view group自身的dispatch-
        // TouchEvent了
    }

    /**
     * 从0开始
     */
    private fun withinWhichArea(x: Int): Int {
        return x / parentItemWidth
    }

    /**
     * 注意 这里是只支持AppCompatActivity
     * 需要支持其他版的自行修改
     */
    private fun switchFragment(whichFragment: Int) {
        val fragment = fragmentList[whichFragment]
        val frameLayoutId = containerId
        if (fragment != null) {
            val transaction: FragmentTransaction =
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            if (fragment.isAdded) {
                if (currentFragment != null) {
                    transaction.hide(currentFragment!!).show(fragment)
                } else {
                    transaction.show(fragment)
                }
            } else {
                if (currentFragment != null) {
                    transaction.hide(currentFragment!!).add(frameLayoutId, fragment)
                } else {
                    transaction.add(frameLayoutId, fragment)
                }
            }
            currentFragment = fragment
            transaction.commit()
        }
    }

    interface OnBottomBarSelectListener {
        fun onItemSelect(position: Int)
    }
}