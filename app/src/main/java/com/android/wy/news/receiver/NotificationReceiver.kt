package com.android.wy.news.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.wy.news.activity.WebActivity
import com.android.wy.news.common.AppUtil
import com.android.wy.news.common.Constants
import com.android.wy.news.common.SpTools
import com.android.wy.news.event.NoticeEvent
import org.greenrobot.eventbus.EventBus

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
                EventBus.getDefault().postSticky(NoticeEvent(url))
                val packageName = p0.packageName
                val uid: Int = AppUtil.getPackageUid(p0, packageName)
                if (uid > 0) {
                    val rstA = AppUtil.isAppRunning(p0, packageName)
                    val rstB = AppUtil.isProcessRunning(p0, uid)
                    if (!(rstA || rstB)) {
                        //指定包名的程序未在运行中
                        SpTools.putBoolean(Constants.NOTICE_STATUS, true)
                        AppUtil.startApp(p0)
                    }
                }
            }
        }
    }
}