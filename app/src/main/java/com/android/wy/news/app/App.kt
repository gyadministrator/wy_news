package com.android.wy.news.app

import android.app.Activity
import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocationClient
import com.android.wy.news.BuildConfig
import com.android.wy.news.R
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SkinType
import com.android.wy.news.common.SpTools
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
        }
        initSkin()
        initShortCut()
        appFrontBackRegister()
        initRoute()
        initDownload()
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