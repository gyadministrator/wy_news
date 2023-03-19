package com.android.wy.news.common

import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.android.wy.news.entity.NewsEntity
import com.android.wy.news.entity.NewsHeaderEntity
import com.android.wy.news.viewmodel.BaseViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 15:01
  * @Version:        1.0
  * @Description:    
 */
class CommonTools {
    companion object {
        fun <T : View> fd(@IdRes id: Int, view: View): T {
            return view.findViewById(id)
        }

        fun <T : BaseViewModel> getViewModel(owner: ViewModelStoreOwner, clazz: Class<T>): T {
            return ViewModelProvider(owner).get(clazz)
        }

        fun getScreenWidth(): Int {
            return Resources.getSystem().displayMetrics.widthPixels//屏幕宽度
        }

        fun getScreenHeight(): Int {
            return Resources.getSystem().displayMetrics.heightPixels//屏幕高度
        }

        fun parseData(data: String?): ArrayList<NewsEntity> {
            var dataList = ArrayList<NewsEntity>()
            if (data != null && !TextUtils.isEmpty(data)) {
                if (data.contains("(") && data.endsWith(")")) {
                    val content = data.substring(data.indexOf("(") + 1, data.length - 1)
                    if (content.contains("[") && content.endsWith("]}")) {
                        val realContent =
                            content.substring(content.indexOf("["), content.length - 1)
                        val gson = Gson()
                        dataList = gson.fromJson(
                            realContent, object : TypeToken<ArrayList<NewsEntity>>() {}.type
                        )
                    }
                }
            }
            return dataList
        }

        fun parseHeaderData(data: String?): ArrayList<NewsHeaderEntity> {
            var dataList = ArrayList<NewsHeaderEntity>()
            if (data != null && !TextUtils.isEmpty(data)) {
                if (data.contains("[") && data.endsWith("]}")) {
                    val realContent = data.substring(data.indexOf("["), data.length - 1)
                    val gson = Gson()
                    dataList = gson.fromJson(
                        realContent, object : TypeToken<ArrayList<NewsHeaderEntity>>() {}.type
                    )
                }
            }
            return dataList
        }

        fun getAssertContent(context: Context, json: String): String {
            val stringBuilder = StringBuilder()
            val inputStream: InputStream
            try {
                inputStream = context.resources.assets.open(json)
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var jsonLine = bufferedReader.readLine()
                while (jsonLine != null) {
                    stringBuilder.append(jsonLine)
                    jsonLine = bufferedReader.readLine()
                }
                bufferedReader.close()
                inputStreamReader.close()
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return stringBuilder.toString()
        }
    }
}