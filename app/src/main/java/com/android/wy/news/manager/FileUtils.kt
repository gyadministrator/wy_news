package com.android.wy.news.manager

import java.io.File
import java.io.IOException


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/14 11:23
  * @Version:        1.0
  * @Description:    
 */
class FileUtils {
    companion object {
        fun getProjectDir(): String? {
            val directory = File("") // 参数为空
            var courseFile: String? = null
            try {
                courseFile = directory.canonicalPath
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return courseFile
        }
    }
}