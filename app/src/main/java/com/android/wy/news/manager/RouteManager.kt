package com.android.wy.news.manager

import com.alibaba.android.arouter.launcher.ARouter

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/16 10:51
  * @Version:        1.0
  * @Description:    
 */
object RouteManager {
    const val PATH_ACTIVITY_ABOUT = "/activity/AboutActivity"
    const val PATH_ACTIVITY_MAIN = "/activity/MainActivity"
    const val PATH_ACTIVITY_HOME = "/activity/HomeActivity"
    const val PATH_ACTIVITY_INTRODUCE = "/activity/IntroduceActivity"
    const val PATH_ACTIVITY_LAUNCH = "/activity/LaunchActivity"
    const val PATH_ACTIVITY_PERMISSION = "/activity/PermissionActivity"
    const val PATH_ACTIVITY_SEARCH = "/activity/SearchActivity"
    const val PATH_ACTIVITY_SETTING = "/activity/SettingActivity"
    const val PATH_ACTIVITY_SKIN = "/activity/SkinActivity"
    const val PATH_ACTIVITY_SPLASH = "/activity/SplashActivity"
    const val PATH_ACTIVITY_THIRD = "/activity/ThirdActivity"
    const val PATH_ACTIVITY_VIDEO_FULL = "/activity/VideoFullActivity"
    const val PATH_ACTIVITY_MUSIC_MV = "/activity/MusicMvActivity"
    const val PATH_ACTIVITY_WEB = "/activity/WebActivity"

    fun go(path: String) {
        ARouter.getInstance().build(path).navigation()
    }

    fun go(path: String, params: HashMap<String, Any>) {
        val build = ARouter.getInstance().build(path)
        if (params.size > 0) {
            val entries = params.entries
            val iterator = entries.iterator()
            iterator.forEach {
                val key = it.key
                val value = it.value
                if (value is Int) {
                    build.withInt(key, value)
                } else if (value is String) {
                    build.withString(key, value)
                }
            }
        }
        build.navigation()
    }
}