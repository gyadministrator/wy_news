package com.android.wy.news.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Looper
import com.android.wy.news.activity.SplashActivity
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.util.ToastUtil
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.lang.reflect.Field
import java.util.Calendar
import kotlin.system.exitProcess


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/3/28 16:09
  * @Version:        1.0
  * @Description:    
 */
class CrashHandler : Thread.UncaughtExceptionHandler {
    private var mContext: Context? = null
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null
    private val map = HashMap<String, String>()

    companion object {
        val mInstance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            CrashHandler()
        }
    }

    fun init(context: Context) {
        mContext = context
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) return false
        TaskUtil.runOnThread {
            Looper.prepare()
            ToastUtil.show("很抱歉,程序出现异常,即将重启.")
            Looper.loop()
        }
        collectDeviceInfo(mContext)
        saveCrashInfo2File(ex)
        return true
    }

    private fun saveCrashInfo2File(ex: Throwable): String? {
        val sb = StringBuffer()
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH) + 1
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute: Int = calendar.get(Calendar.MINUTE)
        val second: Int = calendar.get(Calendar.SECOND)
        val time = "$year-$month-$day $hour:$minute:$second"
        sb.append("\r\n")
        sb.append("\r\n")
        sb.append("\r\n")
        sb.append("************************************************$time****************************************\r\n")
        sb.append("\r\n")

        val set = map.entries
        val iterator = set.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            sb.append(entry.key + "=" + entry.value + "\n")
        }

        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause: Throwable? = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result: String = writer.toString()
        sb.append(result)
        return try {
            val fileName = "errorLog.txt"
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                //                String path = Environment.getExternalStorageDirectory() + "/errorLog";
                //手机里的路径为：文件管理器 - Android/data/包名/-cach/errorLog
                val path = mContext?.externalCacheDir.toString() + "/errorLog"
                val dir = File(path)
                if (!dir.exists()) {
                    dir.mkdir()
                }
                val file = File(dir, File.separator + System.currentTimeMillis() + "_" + fileName)
                if (!file.exists()) {
                    file.createNewFile()
                }
                val fileWriter = FileWriter(file, true)
                fileWriter.write(sb.toString())
                fileWriter.close()
            }
            fileName
        } catch (e: java.lang.Exception) {
            null
        }
    }

    private fun collectDeviceInfo(ctx: Context?) {
        try {
            val pm: PackageManager? = ctx?.packageManager
            val pi: PackageInfo? =
                pm?.getPackageInfo(ctx.packageName, PackageManager.GET_ACTIVITIES)
            if (pi != null) {
                val versionName = if (pi.versionName == null) "null" else pi.versionName
                val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    pi.longVersionCode.toString() + ""
                } else {
                    pi.versionCode.toString() + ""
                }
                map["versionName"] = versionName
                map["versionCode"] = versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val fields: Array<Field> = Build::class.java.declaredFields
        for (field in fields) {
            try {
                field.isAccessible = true
                map[field.name] = field.get(null)!!.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun uncaughtException(p0: Thread, p1: Throwable) {
        if (!handleException(p1) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler?.uncaughtException(p0, p1)
        } else {
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val intent = Intent(App.app.applicationContext, SplashActivity::class.java)
            val restartIntent = PendingIntent.getActivity(
                App.app.applicationContext, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            //退出程序
            val alarmManager = App.app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            //1秒钟后重启应用
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent)
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(1)
        }
    }
}