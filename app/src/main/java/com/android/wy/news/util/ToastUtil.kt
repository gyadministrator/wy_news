package com.android.wy.news.util

import android.widget.Toast
import com.android.wy.news.app.App

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/10 19:31
  * @Version:        1.0
  * @Description:    
 */
object ToastUtil {
    fun show(msg: String) {
        Toast.makeText(App.app, msg, Toast.LENGTH_SHORT).show()
    }
}