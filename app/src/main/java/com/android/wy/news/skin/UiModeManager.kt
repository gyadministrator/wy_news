package com.android.wy.news.skin

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/4 17:34
  * @Version:        1.0
  * @Description:    
 */
class UiModeManager {
    companion object {
        /**
         * 1. MODE_NIGHT_AUTO_BATTERY 低电量模式自动开启深色模式。
         *
         * 2. MODE_NIGHT_FOLLOW_SYSTEM 跟随系统开启和关闭深色模式（默认）。
         *
         * 3. MODE_NIGHT_NO 强制使用notnight资源，表示非深色模式。
         *
         * 4. MODE_NIGHT_YES 强制使用night资源。
         *
         * 5. MODE_NIGHT_UNSPECIFIED 配合 setLocalNightMode(int)) 使用，表示由Activity通过AppCompactActivity.getDelegate()来单独设置页面的深色模式，不设置全局模式。
         */
        fun getNightMode(): Int {
            return AppCompatDelegate.getDefaultNightMode()
        }

        /**
         * 深色模式设置可以从三个层级设置，分别是系统层、Application层以及Activity层。底层的设置会覆盖上层的设置，例如系统设置了深色模式，但是Application设置了浅色模式，那么应用会显示浅色主题
         * 当深色模式改变时，Activity会重建，如果不希望Activity重建，可以在AndroidManifest.xml中对对应的Activity设置android:configChanges="uiMode"，不过设置之后页面的颜色改变需要Activity在中通过监听onConfigurationChanged来动态改变
         */
        fun setDefaultNightMode(@AppCompatDelegate.NightMode mode: Int) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        /**
         * 判断当前是否深色模式
         *Configuration.uiMode 有三种NIGHT的模式：
         *
         * 1. UI_MODE_NIGHT_NO 表示当前使用的是notnight模式资源。
         *
         * 2. UI_MODE_NIGHT_YES 表示当前使用的是night模式资源。
         *
         * 3. UI_MODE_NIGHT_UNDEFINED 表示当前没有设置模式。
         * @return 深色模式返回 true，否则返回false
         */
        fun isNightMode(context: Context): Boolean {
            return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> true
                else -> false
            }
        }
    }
}