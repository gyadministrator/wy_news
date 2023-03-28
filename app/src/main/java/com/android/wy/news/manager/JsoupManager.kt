package com.android.wy.news.manager

import android.util.Log
import com.android.wy.news.entity.BannerEntity
import com.android.wy.news.entity.NewsEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/28 13:33
  * @Version:        1.0
  * @Description:    
 */
class JsoupManager {
    companion object {

        fun getBanner(url: String): ArrayList<BannerEntity> {
            val document = Jsoup.connect(url).get()
            val bannerList = ArrayList<BannerEntity>()
            val element = document.getElementsByClass("mod_netes_origina")
            val s = element.toString()
            if (s.contains("[") && s.contains("]")) {
                val content = s.substring(s.indexOf("["), s.indexOf("]")+1)
                val gson = Gson()
                val dataList = gson.fromJson<ArrayList<BannerEntity>>(
                    content, object : TypeToken<ArrayList<BannerEntity>>() {}.type
                )
                bannerList.addAll(dataList)
            }
            return bannerList
        }
    }
}