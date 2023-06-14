package com.android.wy.news.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import com.android.wy.news.manager.DeviceManager
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.util.ToastUtil


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/6/14 13:47
  * @Version:        1.0
  * @Description:    
 */
class DeviceReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        // 设备管理：可用
        ToastUtil.show("设备管理：可用")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        // 设备管理：不可用
        ToastUtil.show("设备管理：不可用")
        //如果取消了激活就再次提示激活
        TaskUtil.runOnUiThread({
            DeviceManager.getInstance(context.applicationContext)?.onActivate()
        }, 3000)
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        /* // 这里处理 不可编辑设备。这里可以造成死机状态
                  Intent intent2 = new Intent(context, NoticeSetting.class);
                  intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  context.startActivity(intent2);
                  context.stopService(intent);// 是否可以停止*/
        return "这是一个可选的消息，警告有关禁止用户的请求"
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "Toast.makeText(context, \"设备管理：密码己经改变\", Toast.LENGTH_SHORT).show()",
            "android.widget.Toast",
            "android.widget.Toast"
        )
    )
    override fun onPasswordChanged(context: Context, intent: Intent) {
        // 设备管理：密码己经改变
        ToastUtil.show("设备管理：密码己经改变")
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "Toast.makeText(context, \"设备管理：改变密码失败\", Toast.LENGTH_SHORT).show()",
            "android.widget.Toast",
            "android.widget.Toast"
        )
    )
    override fun onPasswordFailed(context: Context, intent: Intent) {
        // 设备管理：改变密码失败
        ToastUtil.show("设备管理：改变密码失败")
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "Toast.makeText(context, \"设备管理：改变密码成功\", Toast.LENGTH_SHORT).show()",
            "android.widget.Toast",
            "android.widget.Toast"
        )
    )
    override fun onPasswordSucceeded(context: Context, intent: Intent) {
        // 设备管理：改变密码成功
        ToastUtil.show("设备管理：改变密码成功")
    }
}
