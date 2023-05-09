package com.android.wy.news.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.android.wy.news.dialog.TipDialogFragment
import java.util.Locale


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/9 9:23
  * @Version:        1.0
  * @Description:    自启动设置
 */
class AutoStartUtil {
    companion object {

        /**
         * 获取自启动管理页面的Intent
         * @param context context
         * @return 返回自启动管理页面的Intent
         * 找到自启动页面执行以下命令查看自启动activity
         * adb shell dumpsys activity top
         */
        fun getAutostartSettingIntent(context: Context) {
            var componentName: ComponentName? = null
            val brand = Build.MANUFACTURER
            var intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                when (brand.lowercase(Locale.getDefault())) {
                    "samsung" ->
                        componentName = ComponentName(
                            "com.samsung.android.sm",
                            "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity"
                        )

                    "huawei" ->
                        //荣耀V8，EMUI 8.0.0，Android 8.0上，以下两者效果一样
                        componentName = ComponentName(
                            "com.huawei.systemmanager",
                            "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity"
                        )

                    "xiaomi" ->
                        componentName = ComponentName(
                            "com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity"
                        )

                    "vivo" ->
                        //componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.safaguard.PurviewTabActivity");
                        componentName =
                            ComponentName(
                                "com.iqoo.secure",
                                "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                            )
                    /*componentName =
                        ComponentName(
                            "com.vivo.permissionmanager",
                            "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                        )*/

                    "oppo" ->
                        //componentName = new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity");
                        componentName = ComponentName(
                            "com.coloros.oppoguardelf",
                            "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity"
                        )

                    "yulong", "360" ->
                        componentName = ComponentName(
                            "com.yulong.android.coolsafe",
                            "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity"
                        )

                    "meizu" ->
                        componentName =
                            ComponentName(
                                "com.meizu.safe",
                                "com.meizu.safe.permission.SmartBGActivity"
                            )

                    "oneplus" ->
                        componentName = ComponentName(
                            "com.oneplus.security",
                            "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
                        )

                    "letv" -> {
                        intent.action = "com.letv.android.permissionautoboot"
                        intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                        intent.data = Uri.fromParts("package", context.packageName, null)
                    }

                    else -> {
                        intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                        intent.data = Uri.fromParts("package", context.packageName, null)
                    }
                }
                intent.component = componentName
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                val dialogFragment = TipDialogFragment.newInstance(
                    "温馨提示",
                    "为了您更好体验听歌服务,请设置App自启动,便于音乐播放期间,锁屏展示音乐歌曲信息。",
                    "确定"
                )
                val activity = context as AppCompatActivity
                dialogFragment.show(activity.supportFragmentManager, "tip_dialog")
                dialogFragment.addListener(object : TipDialogFragment.OnDialogFragmentListener {
                    override fun onClickSure() {
                        intent = Intent(Settings.ACTION_SETTINGS)
                        context.startActivity(intent)
                    }
                })
            }
        }
    }
}