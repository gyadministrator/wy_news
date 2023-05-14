package com.android.wy.news.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import com.android.wy.news.R
import com.android.wy.news.app.App
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.LayoutDesktopLrcBinding
import com.android.wy.news.util.AppUtil
import com.android.wy.news.util.ToastUtil
import java.lang.ref.WeakReference


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/10 16:40
  * @Version:        1.0
  * @Description:    
 */
object LrcDesktopManager {
    private var mContentView: WeakReference<View>? = null
    private var mParams: WindowManager.LayoutParams? = null
    private var mRawX = 0f
    private var mRawY = 0f
    private var mStartX = 0f
    private var mStartY = 0f
    private var mTitleHeight = 0
    private var isLock = false
    private var hasAddView = false
    private var mActivity: WeakReference<Activity>? = null
    private var currentLrc: String? = null


    /**
     * 应用Window，对应Activity，层级范围是 1~99
     * 子Window，对应Dialog和PopupWindow，层级范围是 1000~1999
     * 系统Window，对应Toast层级范围是 2000~2999
     */
    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    fun showDesktopLrc(activity: Activity, time: Long) {
        val background = AppUtil.isBackground(activity)
        if (!background) return
        currentLrc = CommonTools.getLrcText(Constants.currentLrcData, time)
        if (hasAddView) {
            val tvLrc = mContentView?.get()?.findViewById<TextView>(R.id.tv_lrc)
            tvLrc?.text = currentLrc
            mParams?.let { mContentView?.get()?.let { it1 -> updateView(it1, it) } }
            Logger.i("showDesktopLrc---->>>update--->>>lrc:$currentLrc")
        } else {
            val view =
                LayoutInflater.from(App.app).inflate(R.layout.layout_desktop_lrc, null)
            val binding = LayoutDesktopLrcBinding.bind(view)
            val windowManager =
                activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            mParams = WindowManager.LayoutParams()
            val screenWidth = CommonTools.getScreenWidth()
            mParams?.width = screenWidth * 9 / 10
            mParams?.height =
                CommonTools.dip2px(activity, 150f)/*ViewGroup.LayoutParams.WRAP_CONTENT*/
            //窗口位置
            mParams?.gravity = Gravity.CENTER_VERTICAL
            //位图格式 半透明
            mParams?.format = PixelFormat.TRANSPARENT
            //窗口的层级关系
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mParams?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                mParams?.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            }
            //窗口的模式
            mParams?.flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            mContentView = WeakReference(view)
            mActivity = WeakReference(activity)
            initView(binding)
            windowManager.addView(view, mParams)
            hasAddView = true
            Logger.i("showDesktopLrc--->>>add")
        }
    }

    private fun getTitleHeight(activity: Activity): Int {
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        return frame.top
    }

    private fun updateLrcWindow() {
        mParams?.x = (mRawX - mStartX).toInt()
        mParams?.y = (mRawY - mStartY).toInt()
        mParams?.gravity = Gravity.START or Gravity.TOP
        mContentView?.get()?.let { mParams?.let { it1 -> updateView(it, it1) } }
    }

    fun removeView() {
        val windowManager =
            App.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val view: View? = mContentView?.get()
        if (view != null) {
            windowManager.removeView(view)
            hasAddView = false
            mContentView = null
        }
    }

    private fun updateView(view: View, params: ViewGroup.LayoutParams) {
        val windowManager =
            App.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.updateViewLayout(view, params)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(binding: LayoutDesktopLrcBinding) {
        val ivLock = binding.ivLock
        val ivClose = binding.ivClose
        val tvLrc = binding.tvLrc
        tvLrc.text = currentLrc
        ivLock.setOnClickListener {
            isLock = !isLock
            if (isLock) {
                ivLock.setImageResource(R.mipmap.lock)
            } else {
                ivLock.setImageResource(R.mipmap.unlock)
            }
        }
        ivClose.setOnClickListener {
            removeView()
            SpTools.putBoolean(Constants.IS_SHOW_DESKTOP_LRC, false)
            ToastUtil.show("桌面歌词已关闭,如需要,请在设置中再打开")
        }
        binding.root.setOnTouchListener { _, event ->
            if (!isLock) {
                if (mTitleHeight == 0) {
                    mTitleHeight = mActivity?.get()?.let { getTitleHeight(it) }!!
                }
                mRawX = event?.rawX!!
                mRawY = event.rawY - mTitleHeight
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mStartX = event.x
                        mStartY = event.y
                    }

                    MotionEvent.ACTION_MOVE -> {
                        updateLrcWindow()
                    }

                    MotionEvent.ACTION_UP -> {
                        updateLrcWindow()
                    }
                }
            }
            true
        }
    }
}