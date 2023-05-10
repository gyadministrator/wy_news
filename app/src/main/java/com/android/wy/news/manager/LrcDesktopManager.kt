package com.android.wy.news.manager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.android.wy.news.R
import com.android.wy.news.app.App
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.LayoutDesktopLrcBinding
import com.android.wy.news.util.AppUtil
import java.lang.ref.WeakReference

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/10 16:40
  * @Version:        1.0
  * @Description:    
 */
class LrcDesktopManager {
    companion object {
        private var contentView: WeakReference<View>? = null

        /**
         * 应用Window，对应Activity，层级范围是 1~99
         * 子Window，对应Dialog和PopupWindow，层级范围是 1000~1999
         * 系统Window，对应Toast层级范围是 2000~2999
         */
        @SuppressLint("InflateParams")
        fun showDesktopLrc() {
            val background = AppUtil.isBackground(App.app)
            if (background) {
                Logger.i("showDesktopLrc: ")
                val windowManager =
                    App.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                //窗口位置
                layoutParams.gravity = Gravity.CENTER_VERTICAL
                //位图格式
                layoutParams.format = PixelFormat.TRANSPARENT
                //窗口的层级关系
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                }
                //窗口的模式
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                val view =
                    LayoutInflater.from(App.app).inflate(R.layout.layout_desktop_lrc, null)
                contentView = WeakReference(view)
                val binding = LayoutDesktopLrcBinding.bind(view)
                initView(binding)
                windowManager.addView(view, layoutParams)
            }
        }

        fun removeView() {
            val background = AppUtil.isBackground(App.app)
            if (background) {
                val windowManager =
                    App.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val view = contentView?.get()
                windowManager.removeView(view)
            }
        }

        fun updateView(view: View, params: ViewGroup.LayoutParams) {
            val background = AppUtil.isBackground(App.app)
            if (background) {
                val windowManager =
                    App.app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                windowManager.updateViewLayout(view, params)
            }
        }

        private fun initView(binding: LayoutDesktopLrcBinding) {

        }
    }
}