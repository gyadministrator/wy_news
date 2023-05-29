package com.android.wy.news.util

import com.android.wy.news.app.App
import com.android.wy.news.common.Logger
import com.android.wy.news.entity.DownloadEntity
import com.android.wy.news.notification.NotificationHelper
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.util.FileDownloadUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.FileOutputStream


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/29 15:30
  * @Version:        1.0
  * @Description:    
 */
object DownloadFileUtil {
    private val fileDownloader = FileDownloader.getImpl()
    private val saveFolder =
        FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "downloads"
    val taskList = ArrayList<Int>()

    /**
     * 文件下载
     */
    suspend fun download(downloadEntity: DownloadEntity) = coroutineScope {
        async(Dispatchers.IO) {
            try {
                val filepath = File(downloadEntity.filePath)
                if (!filepath.exists()) {
                    filepath.mkdirs()
                }
                val file = File(filepath.canonicalPath, downloadEntity.fileName)
                if (file.exists()) {
                    file.delete()
                }
                try {
                    val buffer = ByteArray(1024)
                    val contentLength: Long = downloadEntity.body.contentLength()
                    var lastProgress = 0
                    downloadEntity.body.byteStream().use { input ->
                        FileOutputStream(file).use { fos ->
                            var length: Int
                            var sum: Long = 0
                            while (input.read(buffer).also { length = it } != -1) {
                                fos.write(buffer, 0, length)
                                sum += length.toLong()
                                val progress = (sum * 100 / contentLength).toInt()
                                if (progress > lastProgress) {
                                    lastProgress = progress
                                    downloadEntity.callback.onProgress(progress)
                                }
                            }
                            fos.flush()
                        }
                    }
                    downloadEntity.callback.onSuccess(file)
                    Logger.i("DownloadFileUtil.download filepath: ${file.path}")
                } catch (e: Exception) {
                    if (file.exists()) {
                        file.delete()
                    }
                    downloadEntity.callback.onFailure(e)
                }
            } catch (e: Exception) {
                downloadEntity.callback.onFailure(e)
            }
        }
    }.await()

    fun download(fileName: String, url: String) {
        Logger.i("startDownload--->>>$url")
        val path =
            saveFolder + File.separator + fileName + ".mp3"
        val taskId = fileDownloader.create(url)
            .setPath(path, false)
            .setCallbackProgressTimes(300)
            .setMinIntervalUpdateSpeed(400)
            .setListener(object : FileDownloadListener() {
                override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    Logger.i("startDownload--->>>pending  soFarBytes:$soFarBytes  totalBytes:$totalBytes")
                    ToastUtil.show("开始下载...")
                    NotificationHelper.sendProgressNotification(
                        App.app,
                        fileName,
                        0,
                        soFarBytes == totalBytes
                    )
                }

                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    Logger.i("startDownload--->>>progress  soFarBytes:$soFarBytes  totalBytes:$totalBytes")
                    val fl = soFarBytes.toFloat() / totalBytes.toFloat()
                    val p = (fl * 100).toInt()
                    Logger.i("startDownload--->>>progress p:$p")
                    NotificationHelper.sendProgressNotification(
                        App.app,
                        fileName,
                        p,
                        soFarBytes == totalBytes
                    )
                }

                override fun completed(task: BaseDownloadTask?) {
                    Logger.i("startDownload--->>>completed: " + task?.targetFilePath)
                    ToastUtil.show("下载成功,保存到 " + task?.targetFilePath)
                    NotificationHelper.sendProgressNotification(
                        App.app,
                        fileName,
                        100,
                        true
                    )
                }

                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    Logger.i("startDownload--->>>paused  soFarBytes:$soFarBytes  totalBytes:$totalBytes")
                    ToastUtil.show("暂停下载")
                }

                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    Logger.i("startDownload--->>>error  e:${e?.message}")
                    ToastUtil.show("下载失败: " + e?.message)
                }

                override fun warn(task: BaseDownloadTask?) {
                    Logger.i("startDownload--->>>warn")
                }

            }).start()
        taskList.add(taskId)
    }

    fun pauseTask(taskId: Int) {
        fileDownloader.pause(taskId)
    }

    fun deleteFile(taskId: Int, filePath: String) {
        val deleteData = fileDownloader.clear(taskId, saveFolder)
        val targetFile = File(filePath)
        var delete = false
        if (targetFile.exists()) {
            delete = targetFile.delete()
        }
        Logger.i("delete_single file,deleteDataBase:$deleteData,mSinglePath:$filePath,delete:$delete")
        File(FileDownloadUtils.getTempPath(filePath)).delete()
    }

    fun deleteAllFile() {
        //清除所有的下载任务
        fileDownloader.clearAllTaskData()
        //清除所有下载的文件
        var count = 0
        val file = File(FileDownloadUtils.getDefaultSaveRootPath())
        do {
            if (!file.exists()) {
                break
            }
            if (!file.isDirectory) {
                break
            }
            val files = file.listFiles() ?: break
            for (file1 in files) {
                count++
                file1.delete()
            }
        } while (false)

        ToastUtil.show(String.format("Complete delete %d files", count))

    }
}