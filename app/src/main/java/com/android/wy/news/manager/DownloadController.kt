package com.android.wy.news.manager

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.android.wy.news.common.Logger
import java.io.File


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/4/19 14:42
  * @Version:        1.0
  * @Description:    
 */
object DownloadController {
    private var downloadId: Long = -1
    private var downloadManager: DownloadManager? = null
    private const val APK_NAME = "wy_news.apk"
    private var broadcastReceiver: BroadcastReceiver? = null

    fun download(context: Context, url: String?, titleStr: String?, contentStr: String?) {
        //创建下载任务
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setTitle(titleStr)
        request.setDescription(contentStr)
        //设置下载的路径
        val file =
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), APK_NAME)
        request.setDestinationUri(Uri.fromFile(file))
        file.absolutePath
        //获取DownloadManager
        downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        //将下载请求放入队列
        downloadId = downloadManager!!.enqueue(request)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun registerReceiver(context: Context) {
        // 注册广播监听系统的下载完成事件。
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val thisDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (thisDownloadId != -1L && downloadId != -1L) {
                    if (thisDownloadId == downloadId) {
                        //下载完成，检查下载状态
                        checkStatus(context)
                    }
                }
            }
        }
        context.registerReceiver(broadcastReceiver, intentFilter)
    }

    fun unRegisterReceiver(context: Context) {
        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver)
        }
    }

    @SuppressLint("Range")
    private fun checkStatus(context: Context) {
        val query = DownloadManager.Query()
        // 执行查询, 返回一个 Cursor (相当于查询数据库)
        val cursor = downloadManager!!.query(query)
        if (!cursor.moveToFirst()) {
            cursor.close()
        }
        val id = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_ID))
        //通过下载的id查找
        query.setFilterById(id.toLong())

        // 获取下载好的 apk 路径
        val localFilename: String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
        } else {
            cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME))
        }
        if (cursor.moveToFirst()) {
            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_PAUSED ->
                    //下载暂停
                    Logger.i("下载暂停")

                DownloadManager.STATUS_PENDING ->
                    //下载延迟
                    Logger.i("下载延迟")

                DownloadManager.STATUS_RUNNING ->
                    //正在下载
                    Logger.i("正在下载")

                DownloadManager.STATUS_SUCCESSFUL -> {
                    //下载完成安装APK
                    localFilename?.let { installApk(context, it) }
                    cursor.close()
                }

                DownloadManager.STATUS_FAILED -> {
                    //下载失败
                    Logger.i("下载失败")
                    cursor.close()
                }

                else -> {}
            }
        }
    }

    private fun installApk(context: Context, path: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val file = File(Uri.parse(path).path)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val uri =
                FileProvider.getUriForFile(context, "com.android.wy.news.fileprovider", file)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        context.startActivity(intent)
    }
}