package com.android.wy.news.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.wy.news.R
import com.android.wy.news.app.App
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.LayoutDesktopLrcBinding
import com.android.wy.news.service.MusicNotifyService
import com.android.wy.news.util.AppUtil
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.util.ToastUtil
import java.lang.ref.WeakReference


/*
  * @Author:         gao_yun
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
    private var hasAddView = false
    private var mActivity: WeakReference<Activity>? = null


    /**
     * 应用Window，对应Activity，层级范围是 1~99
     * 子Window，对应Dialog和PopupWindow，层级范围是 1000~1999
     * 系统Window，对应Toast层级范围是 2000~2999
     */
    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    fun showDesktopLrc(activity: Activity, time: Long) {
        val background = AppUtil.isBackground(activity)
        val isShowDesktop = SpTools.getBoolean(GlobalData.SpKey.IS_SHOW_DESKTOP_LRC)
        if (!background || isShowDesktop == null || isShowDesktop == false || GlobalData.isPlaying.value==false) return
        if (hasAddView) {
            val tvCurrentLrc = mContentView?.get()?.findViewById<TextView>(R.id.tv_current_lrc)
            val tvNextLrc = mContentView?.get()?.findViewById<TextView>(R.id.tv_next_lrc)
            updateLrc(tvCurrentLrc, tvNextLrc, time)
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
            windowManager.addView(view, mParams)
            hasAddView = true
            initView(binding, time)
            Logger.i("showDesktopLrc--->>>add")
        }
    }

    private fun updateLrc(tvCurrentLrc: TextView?, tvNextLrc: TextView?, time: Long) {
        val lrcTextList = CommonTools.getLrcTextList(GlobalData.currentLrcData, time)
        tvCurrentLrc?.visibility = View.VISIBLE
        tvCurrentLrc?.text = lrcTextList[0]
        tvNextLrc?.text = lrcTextList[1]
        mParams?.let { mContentView?.get()?.let { it1 -> updateView(it1, it) } }
        Logger.i("showDesktopLrc---->>>updateLrc--->>>lrc:$lrcTextList")
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

    private fun hideBg(
        llContent: LinearLayout,
        ivIcon: ImageView,
        ivLock: ImageView,
        ivClose: ImageView,
        delay: Long
    ) {
        TaskUtil.runOnUiThread({
            llContent.setBackgroundColor(Color.TRANSPARENT)
            ivIcon.visibility = View.GONE
            ivLock.visibility = View.GONE
            ivClose.visibility = View.GONE
        }, delay)
    }

    private fun showBg(
        llContent: LinearLayout,
        ivIcon: ImageView,
        ivLock: ImageView,
        ivClose: ImageView
    ) {
        llContent.setBackgroundResource(R.drawable.bg_over)
        ivIcon.visibility = View.VISIBLE
        ivLock.visibility = View.VISIBLE
        ivClose.visibility = View.VISIBLE
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(binding: LayoutDesktopLrcBinding, time: Long) {
        val ivLock = binding.ivLock
        val ivClose = binding.ivClose
        val tvCurrentLrc = binding.tvCurrentLrc
        val tvNextLrc = binding.tvNextLrc
        val llContent = binding.llContent
        val ivIcon = binding.ivIcon

        updateLrc(tvCurrentLrc, tvNextLrc, time)
        if (GlobalData.isLock) {
            hideBg(llContent, ivIcon, ivLock, ivClose, 0)
        } else {
            hideBg(llContent, ivIcon, ivLock, ivClose, 3000)
        }
        setLockState(ivLock)

        ivLock.setOnClickListener {
            if (GlobalData.isLock) {
                ivLock.setImageResource(R.mipmap.unlock)
                ToastUtil.show("桌面歌词已解锁")
            } else {
                ivLock.setImageResource(R.mipmap.lock)
                ToastUtil.show("桌面歌词已锁定,如需解锁,请下拉通知栏再打开")
            }
            val receiverIntent = Intent()
            receiverIntent.action = MusicNotifyService.MUSIC_LOCK_ACTION
            AppUtil.sendBroadCast(receiverIntent)
        }
        ivClose.setOnClickListener {
            removeView()
            SpTools.putBoolean(GlobalData.SpKey.IS_SHOW_DESKTOP_LRC, false)
            ToastUtil.show("桌面歌词已关闭,如需要,请在设置中再打开")
        }
        binding.root.setOnTouchListener { _, event ->
            if (!GlobalData.isLock) {
                if (mTitleHeight == 0) {
                    mTitleHeight = mActivity?.get()?.let { getTitleHeight(it) }!!
                }
                mRawX = event?.rawX!!
                mRawY = event.rawY - mTitleHeight
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        mStartX = event.x
                        mStartY = event.y
                        showBg(llContent, ivIcon, ivLock, ivClose)
                        setLockState(ivLock)
                    }

                    MotionEvent.ACTION_MOVE -> {
                        updateLrcWindow()
                    }

                    MotionEvent.ACTION_UP -> {
                        updateLrcWindow()
                        hideBg(llContent, ivIcon, ivLock, ivClose, 3000)
                    }
                }
            }
            true
        }
    }

    private fun setLockState(ivLock: ImageView) {
        if (GlobalData.isLock) {
            ivLock.setImageResource(R.mipmap.lock)
        } else {
            ivLock.setImageResource(R.mipmap.unlock)
        }
    }
}