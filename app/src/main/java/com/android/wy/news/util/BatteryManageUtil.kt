package com.android.wy.news.util

import android.content.ComponentName
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/9 9:29
  * @Version:        1.0
  * @Description:    对电池进行管理，使APP避免被电池优化
 */
class BatteryManageUtil {
    companion object {
        /**
         * 忽略电池优化
         */
        fun ignoreBatteryOptimization(context: Context) {
            val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
            val hasIgnored: Boolean
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hasIgnored = powerManager.isIgnoringBatteryOptimizations(context.packageName)
                //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
                if (!hasIgnored) {
                    try { //先调用系统显示 电池优化权限
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                        intent.data = Uri.parse("package:" + context.packageName)
                        context.startActivity(intent)
                    } catch (e: Exception) { //如果失败了则引导用户到电池优化界面
                        try {
                            val intent = Intent(Intent.ACTION_MAIN)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addCategory(Intent.CATEGORY_LAUNCHER)
                            val cn =
                                ComponentName.unflattenFromString("com.android.settings/.Settings\$HighPowerApplicationsActivity")
                            intent.component = cn
                            context.startActivity(intent)
                        } catch (ex: Exception) { //如果全部失败则说明没有电池优化功能
                            ex.printStackTrace()
                            val intent = Intent(Settings.ACTION_SETTINGS)
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}