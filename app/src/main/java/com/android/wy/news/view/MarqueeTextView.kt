package com.android.wy.news.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.android.wy.news.R
import com.android.wy.news.util.TaskUtil

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/24 10:06
  * @Version:        1.0
  * @Description:    
 */
class MarqueeTextView : LinearLayout {
    private var mBannerTV1: TextView
    private var mBannerTV2: TextView
    private var isShow = false
    private var startY1 = 0
    private var endY1 = 0
    private var startY2 = 0
    private var endY2 = 0
    private lateinit var runnable: Runnable
    private lateinit var list: ArrayList<String>
    private var position = 0
    private var offsetY = 100
    private var hasPostRunnable = false

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.widget_scroll_text_layout, this)
        mBannerTV1 = view.findViewById(R.id.tv_banner1)
        mBannerTV2 = view.findViewById(R.id.tv_banner2)
        runnable = Runnable {
            isShow = !isShow
            if (position == list.size - 1) {
                position = 0
            }
            if (isShow) {
                mBannerTV1.text = list[position++]
                mBannerTV2.text = list[position]
            } else {
                mBannerTV2.text = list[position++]
                mBannerTV1.text = list[position]
            }
            startY1 = if (isShow) 0 else offsetY
            endY1 = if (isShow) -offsetY else 0
            ObjectAnimator.ofFloat(mBannerTV1, "translationY", startY1.toFloat(), endY1.toFloat())
                .setDuration(300).start()
            startY2 = if (isShow) offsetY else 0
            endY2 = if (isShow) 0 else -offsetY
            ObjectAnimator.ofFloat(mBannerTV2, "translationY", startY2.toFloat(), endY2.toFloat())
                .setDuration(300).start()
            TaskUtil.runOnUiThread(runnable, 3000)
        }
    }

    fun setList(list: ArrayList<String>) {
        this.list = list
        //处理最后一条数据切换到第一条数据 太快的问题
        if (list.size > 1) {
            list.add(list[0])
        }
    }

    fun getShowText(): String {
        return list[position]
    }

    fun startScroll() {
        mBannerTV1.text = list[0]
        if (list.size > 1) {
            if (!hasPostRunnable) {
                hasPostRunnable = true
                //处理第一次进入 第一条数据切换第二条 太快的问题
                TaskUtil.runOnUiThread(runnable, 3000)
            }
        } else {
            //只有一条数据不进行滚动
            hasPostRunnable = false
        }
    }

    fun stopScroll() {
        TaskUtil.removeUiThreadCallback(runnable)
        hasPostRunnable = false
    }
}