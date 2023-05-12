package com.android.wy.news.util

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.android.wy.news.R
import com.android.wy.news.app.App
import com.android.wy.news.databinding.LayoutToastBinding

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/10 19:31
  * @Version:        1.0
  * @Description:    
 */
object ToastUtil {
    fun show(msg: String) {
        show(msg, -1)
    }

    @SuppressLint("InflateParams")
    fun show(msg: String, icon: Int) {
        val view = LayoutInflater.from(App.app).inflate(R.layout.layout_toast, null)
        val binding = LayoutToastBinding.bind(view)
        val ivIcon = binding.ivIcon
        if (icon != -1) {
            ivIcon.setImageResource(icon)
        }
        val tvTip = binding.tvTip
        tvTip.text = msg
        val toast = Toast(App.app)
        toast.view = view
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }
}