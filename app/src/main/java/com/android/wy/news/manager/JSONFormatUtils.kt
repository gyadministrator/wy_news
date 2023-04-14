package com.android.wy.news.manager

import com.google.gson.GsonBuilder
import java.io.FileWriter
import java.io.IOException


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/14 11:24
  * @Version:        1.0
  * @Description:    
 */
class JSONFormatUtils {
    companion object {
        fun <T> jsonWriter(data: T, filePath: String?) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            try {
                FileWriter(filePath).use { writer -> gson.toJson(data, writer) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun <T> jsonWriter(data: List<T>?, filePath: String?) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            try {
                FileWriter(filePath).use { writer -> gson.toJson(data, writer) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}