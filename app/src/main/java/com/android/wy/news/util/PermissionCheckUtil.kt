package com.android.wy.news.util

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Process
import android.provider.Settings
import java.lang.reflect.Field
import java.lang.reflect.Method


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/9 9:33
  * @Version:        1.0
  * @Description:    
 */
object PermissionCheckUtil {
        //判断是否开启悬浮窗权限   context可以用你的Activity.或者this
        fun checkFloatPermission(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return true
            }
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                try {
                    var cls = Class.forName("android.content.Context")
                    val declaredField: Field = cls.getDeclaredField("APP_OPS_SERVICE")
                    declaredField.isAccessible = true
                    var obj: Any? = declaredField.get(cls) as? String ?: return false
                    val str2 = obj as String
                    obj =
                        cls.getMethod("getSystemService", String::class.java).invoke(context, str2)
                    cls = Class.forName("android.app.AppOpsManager")
                    val declaredField2: Field = cls.getDeclaredField("MODE_ALLOWED")
                    declaredField2.isAccessible = true
                    val checkOp: Method = cls.getMethod(
                        "checkOp", Integer.TYPE, Integer.TYPE,
                        String::class.java
                    )
                    val result =
                        checkOp.invoke(obj, 24, Binder.getCallingUid(), context.packageName) as Int
                    result == declaredField2.getInt(cls)
                } catch (e: Exception) {
                    false
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val appOpsMgr =
                        context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                    val mode = appOpsMgr.checkOpNoThrow(
                        "android:system_alert_window",
                        Process.myUid(), context
                            .packageName
                    )
                    mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED
                } else {
                    Settings.canDrawOverlays(context)
                }
            }
        }

        //权限打开
        fun requestSettingCanDrawOverlays(context: Context) {
            val sdkInt = Build.VERSION.SDK_INT
            if (sdkInt >= Build.VERSION_CODES.O) {
                //8.0以上
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                //            startActivityForResult(intent, REQUEST_DIALOG_PERMISSION);
                context.startActivity(intent)
            } else if (sdkInt >= Build.VERSION_CODES.M) {
                //6.0-8.0
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:" + context.packageName)
                //            startActivityForResult(intent, REQUEST_DIALOG_PERMISSION);
                context.startActivity(intent)
            } else {
                //4.4-6.0以下
                //无需处理了
            }
        }
}