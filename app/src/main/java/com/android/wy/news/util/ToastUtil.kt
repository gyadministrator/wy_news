package com.android.wy.news.util

import android.annotation.SuppressLint
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import com.android.wy.news.R
import com.android.wy.news.activity.HomeActivity
import com.android.wy.news.app.App
import com.android.wy.news.databinding.LayoutToastBinding
import java.lang.reflect.Field


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/5/10 19:31
  * @Version:        1.0
  * @Description:    
 */
object ToastUtil {
    fun show(msg: String) {
        TaskUtil.runOnUiThread {
            val background = AppUtil.isBackground(App.app)
            if (background) {
                Toast.makeText(App.app.applicationContext, msg, Toast.LENGTH_SHORT).show()
            } else {
                show(msg, -1)
            }
        }
    }

    @SuppressLint("InflateParams")
    fun show(msg: String, icon: Int) {
        val view =
            LayoutInflater.from(App.app.applicationContext).inflate(R.layout.layout_toast, null)
        val binding = LayoutToastBinding.bind(view)
        val ivIcon = binding.ivIcon
        if (icon != -1) {
            ivIcon.setImageResource(icon)
        }
        val tvTip = binding.tvTip
        tvTip.text = msg
        val toast = Toast(App.app.applicationContext)
        setToastParams(toast)
        toast.view = view
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun setToastParams(toast: Toast) {
        val windowParams: WindowManager.LayoutParams? = getWindowParams(toast)
        val privateFlags: Field?
        val privateFlagConstField: Field?
        try {
            privateFlagConstField =
                WindowManager.LayoutParams::class.java.getDeclaredField("SYSTEM_FLAG_SHOW_FOR_ALL_USERS")
            privateFlagConstField.isAccessible = true
            val privateFlagConst: Int =
                privateFlagConstField.getInt(WindowManager.LayoutParams::class.java)
            privateFlags = WindowManager.LayoutParams::class.java.getDeclaredField("privateFlags")
            privateFlags.isAccessible = true
            var privateFlagsConst: Int = privateFlags.getInt(WindowManager.LayoutParams::class.java)
            privateFlagsConst = privateFlagsConst or privateFlagConst
            privateFlags.setInt(windowParams, privateFlagsConst)
            windowParams?.let { setTrustedOverlay(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getWindowParams(toast: Toast): WindowManager.LayoutParams? {
        return try {
            val getWindowParams = toast.javaClass.getMethod("getWindowParams") ?: return null
            getWindowParams.isAccessible = true
            getWindowParams.invoke(toast) as WindowManager.LayoutParams
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun setTrustedOverlay(windowParams: WindowManager.LayoutParams) {
        try {
            val method = windowParams.javaClass.getMethod("setTrustedOverlay")
            method.isAccessible = true
            method.invoke(windowParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}