package com.android.wy.news.app

import android.app.Activity
import android.app.Application
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocationClient
import com.android.wy.news.BuildConfig
import com.android.wy.news.R
import com.android.wy.news.activity.CrashActivity
import com.android.wy.news.activity.SplashActivity
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SkinType
import com.android.wy.news.common.SpTools
import com.android.wy.news.listener.CustomEventListener
import com.android.wy.news.manager.LrcDesktopManager
import com.android.wy.news.shortcut.ShortCutHelper
import com.android.wy.news.skin.UiModeManager
import com.android.wy.news.util.AppFrontBack
import com.android.wy.news.util.AppFrontBackListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection


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
        AMapLocationClient.setApiKey(GlobalData.LOCATION_KEY)
        if (BuildConfig.isShowLog) {
            //CrashHandler.mInstance.init(this)
            Logger.setDebug(true)
            initCrashConfig()
        }
        initSkin()
        initShortCut()
        appFrontBackRegister()
        initRoute()
        initDownload()
    }

    private fun initCrashConfig() {
        /**
        //此方法定义当应用程序在后台崩溃时是否应启动错误活动。共有三种模式：
        //CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM：即使应用程序在后台运行，也会启动错误活动。
        //CaocConfig.BACKGROUND_MODE_CRASH：当应用程序在后台运行时，启动默认系统错误。
        //CaocConfig.BACKGROUND_MODE_SILENT：当应用程序在后台运行时，它会以静默方式崩溃。
        //默认值为CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM。
         */
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
            .enabled(true) //是否启用 default: true
            .showErrorDetails(true) //default: true 隐藏错误活动中的“错误详细信息”按钮
            .showRestartButton(true) //default: true 是否可以重启页面
            .logErrorOnRestart(true) //default: true
            .trackActivities(true) //default: false
            .minTimeBetweenCrashesMs(2000) //default: 3000
            .errorDrawable(cat.ereza.customactivityoncrash.R.drawable.customactivityoncrash_error_image) //default: bug image
            .restartActivity(SplashActivity::class.java) //默认程序崩溃时重启的的activity default: null (your app's launch activity)
            .errorActivity(CrashActivity::class.java) //默认程序崩溃时跳转的activity default: null (default error activity)
            .eventListener(CustomEventListener()) //default: null
            .apply()
    }

    private fun initDownload() {
        FileDownloader.setupOnApplicationOnCreate(this)
            .connectionCreator(
                FileDownloadUrlConnection
                    .Creator(
                        FileDownloadUrlConnection.Configuration()
                            .connectTimeout(15_000)
                            .readTimeout(15_000)
                    )
            )
            .commit()
    }

    private fun initRoute() {
        //这两行必须写在init之前，否则这些配置在init过程中将无效
        if (BuildConfig.DEBUG) {
            //打印日志
            ARouter.openLog()
            //开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug()
        }
        //尽可能早，推荐在Application中初始化
        ARouter.init(this)
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
                val isShowDesktopLrc = SpTools.getBoolean(GlobalData.SpKey.IS_SHOW_DESKTOP_LRC)
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