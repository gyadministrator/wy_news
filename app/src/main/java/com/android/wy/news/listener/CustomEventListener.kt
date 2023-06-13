package com.android.wy.news.listener

import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.android.wy.news.common.Logger


/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/6/13 13:19
  * @Version:        1.0
  * @Description:    
 */
class CustomEventListener : CustomActivityOnCrash.EventListener {
    override fun onLaunchErrorActivity() {
        Logger.i("onLaunchErrorActivity--->>>")
    }

    override fun onRestartAppFromErrorActivity() {
        Logger.i("onRestartAppFromErrorActivity--->>>")
    }

    override fun onCloseAppFromErrorActivity() {
        Logger.i("onCloseAppFromErrorActivity--->>>")
    }
}