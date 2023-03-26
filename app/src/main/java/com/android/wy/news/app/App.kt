package com.android.wy.news.app

import android.app.Application
import com.android.wy.news.common.SpTools

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 19:28
  * @Version:        1.0
  * @Description:    
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        SpTools.init(this)
    }
}