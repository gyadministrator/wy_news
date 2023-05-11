package com.android.wy.news.app

import android.app.Activity
import android.app.Application
import com.amap.api.location.AMapLocationClient
import com.android.wy.news.BuildConfig
import com.android.wy.news.R
import com.android.wy.news.common.Constants
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SkinType
import com.android.wy.news.common.SpTools
import com.android.wy.news.manager.LrcDesktopManager
import com.android.wy.news.shortcut.ShortCutHelper
import com.android.wy.news.skin.UiModeManager
import com.android.wy.news.util.AppFrontBack
import com.android.wy.news.util.AppFrontBackListener


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
        AMapLocationClient.setApiKey(Constants.LOCATION_KEY)
        if (BuildConfig.isShowLog) {
            CrashHandler.mInstance.init(this)
            Logger.setDebug(true)
        }
        initSkin()
        initShortCut()
        appFrontBackRegister()
    }

    private fun initShortCut() {
        ShortCutHelper.initShortCut(this)
    }

    private fun initSkin() {
        val i = SpTools.getInt(SkinType.SKIN_TYPE)
        if (i != null) {
            UiModeManager.setCurrentUiMode(i)
        } else {
            UiModeManager.setCurrentUiMode(SkinType.SKIN_TYPE_LIGHT)
        }
    }

    /**
     * 注册APP前后台切换监听
     */
    private fun appFrontBackRegister() {
        AppFrontBack.register(this, object : AppFrontBackListener {
            override fun onBack(activity: Activity?) {
                Logger.i(getString(R.string.app_name) + "app onBack")
                val isShowDesktopLrc = SpTools.getBoolean(Constants.IS_SHOW_DESKTOP_LRC)
                if (isShowDesktopLrc != null && isShowDesktopLrc == true) {
                    activity?.let { LrcDesktopManager.showDesktopLrc(it, 0) }
                }
            }

            override fun onFront(activity: Activity?) {
                Logger.i(getString(R.string.app_name) + "app onFront")
                LrcDesktopManager.removeView()
            }
        })
    }
}