package com.android.wy.news.locationselect.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import kotlin.math.roundToInt


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/4/15 13:51
  * @Version:        1.0
  * @Description:    
 */
class ScreenUtil {
    companion object {
        private fun getInternalDimensionSize(context: Context, key: String): Int {
            var result = 0
            try {
                val resourceId = context.resources.getIdentifier(key, "dimen", "android")
                if (resourceId > 0) {
                    result = (context.resources.getDimensionPixelSize(resourceId) *
                            Resources.getSystem().displayMetrics.density /
                            context.resources.displayMetrics.density).roundToInt()
                }
            } catch (ignored: Resources.NotFoundException) {
                return 0
            }
            return result
        }

        fun getStatusBarHeight(context: Context): Int {
            return getInternalDimensionSize(context, "status_bar_height")
        }

        fun getNavigationBarHeight(context: Context): Int {
            val mInPortrait =
                context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            val result = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (hasNavBar(context as Activity)) {
                    val key: String = if (mInPortrait) {
                        "navigation_bar_height"
                    } else {
                        "navigation_bar_height_landscape"
                    }
                    return getInternalDimensionSize(context, key)
                }
            }
            return result
        }

        private fun hasNavBar(activity: Activity): Boolean {
            //判断小米手机是否开启了全面屏,开启了，直接返回false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (Settings.Global.getInt(
                        activity.contentResolver,
                        "force_fsg_nav_bar",
                        0
                    ) !== 0
                ) {
                    return false
                }
            }
            //其他手机根据屏幕真实高度与显示高度是否相同来判断
            val windowManager = activity.windowManager
            val d = windowManager.defaultDisplay
            val realDisplayMetrics = DisplayMetrics()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                d.getRealMetrics(realDisplayMetrics)
            }
            val realHeight = realDisplayMetrics.heightPixels
            val realWidth = realDisplayMetrics.widthPixels
            val displayMetrics = DisplayMetrics()
            d.getMetrics(displayMetrics)
            val displayHeight = displayMetrics.heightPixels
            val displayWidth = displayMetrics.widthPixels
            return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
        }
    }
}