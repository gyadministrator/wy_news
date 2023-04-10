package com.android.wy.news.app

import android.app.Application
import android.widget.Toast
import com.amap.api.location.AMapLocationClient
import com.android.wy.news.BuildConfig
import com.android.wy.news.common.Constants
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SkinType
import com.android.wy.news.common.SpTools
import com.android.wy.news.http.NewsHttpService
import com.android.wy.news.permission.PermissionHelper
import com.android.wy.news.skin.UiModeManager
import com.xuexiang.xupdate.XUpdate
import com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION
import com.xuexiang.xupdate.utils.UpdateUtils


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 19:28
  * @Version:        1.0
  * @Description:    
 */
class App : Application() {

    companion object {
        lateinit var app: App
    }

    init {
        app = this
    }

    override fun onCreate() {
        super.onCreate()
        AMapLocationClient.setApiKey(Constants.LOCATION_KEY);
        if (BuildConfig.isShowLog) {
            CrashHandler.mInstance.init(this)
            Logger.setDebug(true)
        }
        initUpdate()
        initSkin()
    }

    private fun initSkin() {
        val i = SpTools.getInt(SkinType.SKIN_TYPE)
        if (i != null) {
            UiModeManager.setCurrentUiMode(i)
        } else {
            UiModeManager.setCurrentUiMode(SkinType.SKIN_TYPE_LIGHT)
        }
    }

    private fun initUpdate() {
        XUpdate.get().debug(true).isWifiOnly(true).isGet(true).isAutoMode(false).param(
            "versionCode", UpdateUtils.getVersionCode(this)
        ).param("appKey", packageName).setOnUpdateFailureListener { error ->
            if (error.code != CHECK_NO_NEW_VERSION) {
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
            }
        }.supportSilentInstall(true).setIUpdateHttpService(NewsHttpService()).init(this)
    }
}