package com.android.wy.news.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.wy.news.activity.HomeActivity
import com.android.wy.news.activity.WebActivity

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/14 9:37
  * @Version:        1.0
  * @Description:    
 */
class NotificationReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION = "wy_notification_action"
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1 != null && p0 != null) {
            val action = p1.action
            var url = ""
            if (ACTION == action) {
                if (p1.hasExtra(WebActivity.WEB_URL)) {
                    url = p1.getStringExtra(WebActivity.WEB_URL).toString()
                }
                val mInstance = HomeActivity.mInstance
                if (mInstance == null) {
                    //启动启动页
                } else {
                    //启动Web页面
                    WebActivity.startActivityForTask(p0, url = url)
                }
            }
        }
    }
}