package com.android.wy.news.manager

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.android.wy.news.receiver.DeviceReceiver
import com.android.wy.news.util.ToastUtil
import java.lang.ref.WeakReference


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/6/14 13:33
  * @Version:        1.0
  * @Description:    DeviceReceiver 继承自 DeviceAdminReceiver
  * 获取设备管理服务
 */
class DeviceManager
private constructor(context: Context) {
    private var devicePolicyManager: DevicePolicyManager? = null
    private var componentName: ComponentName? = null
    private var mContext: WeakReference<Context>? = null

    companion object {
        private var instance: DeviceManager? = null

        fun getInstance(context: Context): DeviceManager? {
            if (instance == null) {
                synchronized(DeviceManager::class.java) {
                    if (instance == null) {
                        instance = DeviceManager(context)
                    }
                }
            }
            return instance
        }
    }

    init {
        mContext = WeakReference(context)
        devicePolicyManager =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(context, DeviceReceiver::class.java)
    }

    /**
     * 激活程序
     */
    fun onActivate() {
        ToastUtil.show("激活")
        //判断是否激活  如果没有就启动激活设备
        if (!devicePolicyManager!!.isAdminActive(componentName!!)) {
            val intent = Intent(
                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN
            )
            intent.putExtra(
                DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                componentName
            )
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "提示文字")
            mContext?.get()?.startActivity(intent)
        } else {
            ToastUtil.show("设备已经激活,请勿重复激活")
        }
    }

    /**
     * 移除程序 如果不移除程序 APP无法被卸载
     */
    fun onRemoveActivate() {
        componentName?.let { devicePolicyManager?.removeActiveAdmin(it) }
    }

    /**
     * 设置解锁方式 不需要激活就可以运行
     */
    fun startLockMethod() {
        val intent = Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD)
        mContext?.get()?.startActivity(intent)
    }

    /**
     * 设置解锁方式
     */
    fun setLockMethod() {
        // PASSWORD_QUALITY_ALPHABETIC
        // 用户输入的密码必须要有字母（或者其他字符）。
        // PASSWORD_QUALITY_ALPHANUMERIC
        // 用户输入的密码必须要有字母和数字。
        // PASSWORD_QUALITY_NUMERIC
        // 用户输入的密码必须要有数字
        // PASSWORD_QUALITY_SOMETHING
        // 由设计人员决定的。
        // PASSWORD_QUALITY_UNSPECIFIED
        // 对密码没有要求。
        if (devicePolicyManager!!.isAdminActive(componentName!!)) {
            val intent = Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD)
            devicePolicyManager?.setPasswordQuality(
                componentName!!,
                DevicePolicyManager.PASSWORD_QUALITY_NUMERIC
            )
            mContext?.get()?.startActivity(intent)
        } else {
            ToastUtil.show("请先激活设备")
        }
    }

    /**
     * 立刻锁屏
     */
    fun lockNow() {
        if (devicePolicyManager!!.isAdminActive(componentName!!)) {
            devicePolicyManager?.lockNow()
        } else {
            ToastUtil.show("请先激活设备")
        }
    }

    /**
     * 设置多长时间后锁屏
     * @param time time
     */
    fun lockByTime(time: Long) {
        if (devicePolicyManager!!.isAdminActive(componentName!!)) {
            devicePolicyManager?.setMaximumTimeToLock(componentName!!, time)
        } else {
            ToastUtil.show("请先激活设备")
        }
    }

    /**
     * 恢复出厂设置
     */
    fun wipeData() {
        if (devicePolicyManager!!.isAdminActive(componentName!!)) {
            devicePolicyManager?.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE)
        } else {
            ToastUtil.show("请先激活设备")
        }
    }

    /**
     * 设置密码锁
     * @param password password
     */
    fun setPassword(password: String?) {
        if (devicePolicyManager!!.isAdminActive(componentName!!)) {
            devicePolicyManager?.resetPassword(
                password,
                DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY
            )
        } else {
            ToastUtil.show("请先激活设备")
        }
    }
}