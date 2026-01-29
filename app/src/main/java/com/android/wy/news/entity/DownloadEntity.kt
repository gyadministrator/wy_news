package com.android.wy.news.entity
import okhttp3.ResponseBody
import java.io.File

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/5/29 15:29
  * @Version:        1.0
  * @Description:    
 */

/**
 * 下载参数
 */
data class DownloadEntity (
    val fileName: String,
    val filePath: String,
    val body: ResponseBody,
    val callback: DownloadCallback
) {
    // 下载回调接口，用来返回下载情况
    interface DownloadCallback {
        fun onSuccess(file: File)
        fun onProgress(progress: Int)
        fun onFailure(e: Exception)
    }
}