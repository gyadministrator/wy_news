package com.android.wy.news.util

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.Intent
import android.os.Process
import android.text.TextUtils
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.android.wy.news.app.App
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/5/9 13:32
  * @Version:        1.0
  * @Description:    
 */
object AppUtil {
    /**
     * 判断进程是否在主进程
     */
    fun isMainProcess(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val packageName = context.applicationContext.packageName
        val processName = getProcessName(context)
        return packageName == processName
    }

    private fun getProcessName(context: Context): String? {
        var processName = getProcessFromFile()
        if (processName == null) {
            // 如果装了xposed一类的框架，上面可能会拿不到，回到遍历迭代的方式
            processName = getProcessNameByAM(context)
        }
        return processName
    }

    private fun getProcessFromFile(): String? {
        var reader: BufferedReader? = null
        return try {
            val pid = Process.myPid()
            val file = "/proc/$pid/cmdline"
            reader = BufferedReader(InputStreamReader(FileInputStream(file), "iso-8859-1"))
            var c: Int
            val processName = StringBuilder()
            while (reader.read().also { c = it } > 0) {
                processName.append(c.toChar())
            }
            processName.toString()
        } catch (e: Exception) {
            null
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getProcessNameByAM(context: Context): String? {
        var processName: String? = null
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        while (true) {
            val plist = am.runningAppProcesses
            if (plist != null) {
                for (info in plist) {
                    if (info.pid == Process.myPid()) {
                        processName = info.processName
                        break
                    }
                }
            }
            if (!TextUtils.isEmpty(processName)) {
                return processName
            }
            try {
                Thread.sleep(100L) // take a rest and again
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }
        }
    }

    /**
     * 判断程序是否在后台
     * @param context context
     * @return 是否在后台
     */
    fun isBackground(context: Context): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        for (appProcess in appProcesses) {
            if (appProcess.processName == context.packageName) {
                return appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
        }
        return false
    }

    fun isApplicationInBackground(context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskList = am.getRunningTasks(1)
        if (taskList != null && taskList.isNotEmpty()) {
            val topActivity = taskList[0].topActivity
            if (topActivity != null && topActivity.packageName != context.packageName) {
                return true
            }
        }
        return false
    }


    /**
     * 方法描述：判断某一应用是否正在运行
     * Created by cafeting on 2017/2/4.
     * @param context   上下文
     * @param packageName 应用的包名
     * @return true 表示正在运行，false 表示没有运行
     */
    fun isAppRunning(context: Context, packageName: String): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list = am.getRunningTasks(100)
        if (list.size <= 0) {
            return false
        }
        for (info in list) {
            if (info.baseActivity!!.packageName == packageName) {
                return true
            }
        }
        return false
    }

    //获取已安装应用的 uid，-1 表示未安装此应用或程序异常
    fun getPackageUid(context: Context, packageName: String?): Int {
        return try {
            val applicationInfo = context.packageManager.getApplicationInfo(
                packageName!!, 0
            )
            applicationInfo.uid
        } catch (e: Exception) {
            -1
        }
    }

    /**
     * 判断某一 uid 的程序是否有正在运行的进程，即是否存活
     * Created by cafeting on 2017/2/4.
     *
     * @param context   上下文
     * @param uid 已安装应用的 uid
     * @return true 表示正在运行，false 表示没有运行
     */
    fun isProcessRunning(context: Context, uid: Int): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServiceInfos = am.getRunningServices(200)
        if (runningServiceInfos.size > 0) {
            for (appProcess in runningServiceInfos) {
                if (uid == appProcess.uid) {
                    return true
                }
            }
        }
        return false
    }

    fun startApp(context: Context) {
        val packageName = context.packageName
        val launchIntent: Intent? =
            context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(launchIntent)
    }

    fun getColor(context: Context, @ColorRes id: Int): Int {
        //return ContextCompat.getColor(App.app, id)
        return context.resources.getColor(id)
    }

    fun getString(context: Context, id: Int): String {
        return context.getString(id)
    }

    fun sendBroadCast(intent: Intent) {
        App.app.sendBroadcast(intent)
    }
}