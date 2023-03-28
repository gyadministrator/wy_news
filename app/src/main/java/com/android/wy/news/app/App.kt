package com.android.wy.news.app

import android.app.Application
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.SpTools
import com.danikula.videocache.HttpProxyCacheServer

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 19:28
  * @Version:        1.0
  * @Description:    
 */
class App : Application() {

    companion object {
        lateinit var app: App
    }

    init {
        app = this
    }

    override fun onCreate() {
        super.onCreate()
        CrashHandler.mInstance.init(this)
    }
}