package com.android.wy.news.permission

import android.app.Activity
import com.android.wy.news.common.Logger
import com.hjq.permissions.IPermissionInterceptor
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/10 11:20
  * @Version:        1.0
  * @Description:    
 */
class PermissionHelper {
    companion object {
        private val permissionList = arrayOf(
            Permission.READ_MEDIA_IMAGES,
            Permission.READ_MEDIA_VIDEO,
            Permission.READ_MEDIA_AUDIO,
            /*当 targetSdkVersion >= 33 申请 READ_EXTERNAL_STORAGE 或者 WRITE_EXTERNAL_STORAGE 会被系统直接拒绝，不会弹出任何授权框*/
            /*Permission.READ_EXTERNAL_STORAGE,
            Permission.WRITE_EXTERNAL_STORAGE,*/
            Permission.ACCESS_FINE_LOCATION,
            Permission.ACCESS_COARSE_LOCATION
        )

        fun initPermission(activity: Activity) {
            XXPermissions.with(activity)
                .permission(permissionList)
                //.unchecked()
                .interceptor(object : IPermissionInterceptor {
                    override fun launchPermissionRequest(
                        activity: Activity,
                        allPermissions: MutableList<String>,
                        callback: OnPermissionCallback?
                    ) {
                        super.launchPermissionRequest(activity, allPermissions, callback)
                    }

                    override fun grantedPermissionRequest(
                        activity: Activity,
                        allPermissions: MutableList<String>,
                        grantedPermissions: MutableList<String>,
                        allGranted: Boolean,
                        callback: OnPermissionCallback?
                    ) {
                        super.grantedPermissionRequest(
                            activity,
                            allPermissions,
                            grantedPermissions,
                            allGranted,
                            callback
                        )
                    }

                    override fun deniedPermissionRequest(
                        activity: Activity,
                        allPermissions: MutableList<String>,
                        deniedPermissions: MutableList<String>,
                        doNotAskAgain: Boolean,
                        callback: OnPermissionCallback?
                    ) {
                        super.deniedPermissionRequest(
                            activity,
                            allPermissions,
                            deniedPermissions,
                            doNotAskAgain,
                            callback
                        )
                    }

                    override fun finishPermissionRequest(
                        activity: Activity,
                        allPermissions: MutableList<String>,
                        skipRequest: Boolean,
                        callback: OnPermissionCallback?
                    ) {
                        super.finishPermissionRequest(
                            activity,
                            allPermissions,
                            skipRequest,
                            callback
                        )
                    }
                })
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        if (!allGranted) {
                            Logger.i("获取部分权限成功，但部分权限未正常授予")
                        }
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean
                    ) {
                        super.onDenied(permissions, doNotAskAgain)
                        if (doNotAskAgain) {
                            Logger.i("被永久拒绝授权，请手动授予权限")
                        }
                    }
                })
        }

        fun checkPermission(activity: Activity, permission: String): Boolean {
            return XXPermissions.isGranted(activity, permission)
        }

        fun checkSpecialPermission(permission: String): Boolean {
            return XXPermissions.isSpecial(permission)
        }
    }
}