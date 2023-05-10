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
import com.android.wy.news.R
import com.android.wy.news.app.App
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.LayoutDesktopLrcBinding
import java.lang.ref.WeakReference


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/10 16:40
  * @Version:        1.0
  * @Description:    
 */
object LrcDesktopManager {
    private var contentView: WeakReference<View>? = null
    private var mParams: WindowManager.LayoutParams? = null
    private var mRawX = 0f
    private var mRawY = 0f
    private var mStartX = 0f
    private var mStartY = 0f
    private var mTitleHeight = 0
    private var isLock = false


    /**
     * 应用Window，对应Activity，层级范围是 1~99
     * 子Window，对应Dialog和PopupWindow，层级范围是 1000~1999
     * 系统Window，对应Toast层级范围是 2000~2999
     */
    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    fun showDesktopLrc(activity: Activity) {
        Logger.i("showDesktopLrc: ")
        val windowManager =
            activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mParams = WindowManager.LayoutParams()
        mParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        mParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        //窗口位置
        mParams?.gravity = Gravity.CENTER_VERTICAL
        //位图格式 半透明
        mParams?.format = PixelFormat.TRANSPARENT
        //窗口的层级关系
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            mParams?.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        //窗口的模式
        mParams?.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        val view =
            LayoutInflater.from(App.app).inflate(R.layout.layout_desktop_lrc, null)
        view.setOnTouchListener { _, event ->
            if (!isLock) {
                if (mTitleHeight == 0) {
                    mTitleHeight = getTitleHeight(activity)
                }
                mRawX = event?.rawX!!
                mRawY = event.rawY - mTitleHeight
                val action = event.action
                Logger.i("showDesktopLrc--->>>action:$action")
                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        mStartX = event.x
                        mStartY = event.y
                    }

                    MotionEvent.ACTION_MOVE -> {
                        updateFloatWindowPosition()
                    }

                    MotionEvent.ACTION_UP -> {
                        updateFloatWindowPosition()
                    }
                }
            }
            true
        }
        contentView = WeakReference(view)
        val binding = LayoutDesktopLrcBinding.bind(view)
        initView(binding)
        windowManager.addView(view, mParams)
    }

    private fun getTitleHeight(activity: Activity): Int {
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        return frame.top
    }

    private fun updateFloatWindowPosition() {
        mParams?.x = (mRawX - mStartX).toInt()
        mParams?.y = (mRawY - mStartY).toInt()
        mParams?.gravity = Gravity.START or Gravity.TOP
        contentView?.get()?.let { mParams?.let { it1 -> updateView(it, it1) } }
    }

    fun removeView() {
        val windowManager =
            App.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val view = contentView?.get()
        windowManager.removeView(view)
    }

    fun updateView(view: View, params: ViewGroup.LayoutParams) {
        val windowManager =
            App.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.updateViewLayout(view, params)
    }

    private fun initView(binding: LayoutDesktopLrcBinding) {
        val ivLock = binding.ivLock
        ivLock.setOnClickListener {
            isLock = !isLock
            if (isLock) {
                ivLock.setImageResource(R.mipmap.lock)
            } else {
                ivLock.setImageResource(R.mipmap.unlock)
            }
        }
    }
}