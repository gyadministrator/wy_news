package com.android.wy.news.manager

import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SpTools
import com.android.wy.news.entity.CityInfo
import com.android.wy.news.entity.HotNewsEntity
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.util.concurrent.TimeUnit


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/28 13:33
  * @Version:        1.0
  * @Description:
 */
class JsoupManager {
    companion object {

        fun getTopNews(url: String): ArrayList<HotNewsEntity> {
            val dataList = ArrayList<HotNewsEntity>()
            try {
                val document: Document? = Jsoup.connect(url).get()
                if (document != null) {
                    val root: Elements? = document.body().getElementsByClass("home-topNews")
                    if (root != null && root.size > 0) {
                        for (i in 0 until root.size) {
                            val element: Element? = root[i]
                            if (element != null) {
                                val elementCardsOneImg = element.getElementsByClass("cards-oneImg")
                                for (j in 0 until elementCardsOneImg.size) {
                                    val elementA = elementCardsOneImg[j]
                                    val link = elementA.attr("href")
                                    val title = elementA.getElementsByClass("s-left").first()
                                        .selectFirst("h4").text()
                                    val source = elementA.getElementsByClass("s-left").first()
                                        .getElementsByClass("s-info")
                                        .first().getElementsByClass("s-source").text()
                                    val comment = elementA.getElementsByClass("s-left").first()
                                        .getElementsByClass("s-info")
                                        .first().getElementsByClass("s-replyCount").text()
                                    val hotNewsEntity = HotNewsEntity(
                                        title,
                                        source,
                                        comment,
                                        Constants.HOT_NEWS_URL + link
                                    )
                                    dataList.add(hotNewsEntity)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return dataList
        }

        fun getCityInfo() {
            try {
                //2019年11月中华人民共和国县以上行政区划代码网页
                val doc =
                    Jsoup.connect("http://www.mca.gov.cn/article/sj/xzqh/2019/2019/201912251506.html")
                        .maxBodySize(0).get()
                val elements = doc.getElementsByClass("xl7128029")
                //省和市
                val elementsProAndCity = doc.getElementsByClass("xl7028029")
                val stringListProAndCity = elementsProAndCity.eachText()
                val stringList = elements.eachText()
                val stringName: MutableList<String> = ArrayList()
                val stringCode: MutableList<String> = ArrayList()
                stringListProAndCity.addAll(stringList)
                for (i in stringListProAndCity.indices) {
                    if (i % 2 == 0) {
                        //地区代码
                        stringCode.add(stringListProAndCity[i])
                    } else {
                        //地区名字
                        stringName.add(stringListProAndCity[i])
                    }
                }
                if (stringName.size != stringCode.size) {
                    throw RuntimeException("数据错误")
                }
                val provinceList: List<CityInfo> = CommonTools.processData(stringName, stringCode)
                Logger.i("行政区划代码: $provinceList")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun getVideoUrl(vid: String): String {
            var realUrl = ""
            val url = Constants.HOT_NEWS_URL + "/v/video/" + vid + ".html"
            try {
                val document: Document? = Jsoup.connect(url).get()
                if (document != null) {
                    val root: Elements? = document.body().getElementsByClass("main_video")
                    val element = root?.first()
                    if (element != null) {
                        if (element.childrenSize() > 0) {
                            val child = element.child(0)
                            realUrl = child?.attr("src").toString()
                            Logger.i("vid=$vid 对应的实际url=$realUrl")
                            return realUrl
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return realUrl
        }

        fun getCookie(): Boolean {
            /* val s = SpTools.getString(Constants.CSRF_TOKEN_KEY)
             if (s != null && s.contains("_")) {
                 val saveTime = s.substring(s.indexOf("_") + 1, s.length)
                 val l = saveTime.toLong()
                 Logger.i("saveTime:$saveTime")
                 val diff = System.currentTimeMillis() - l
                 val days = TimeUnit.MILLISECONDS.toDays(diff)
                 Logger.i("saveTime--->>>days:$days")
                 if (days < 1) {
                     return
                 }
             }*/
            try {
                val response = Jsoup.connect("http://www.kuwo.cn/playlists")
                    .execute()
                val cookies = response.cookies()
                if (cookies.contains("kw_token")) {
                    Constants.CSRF_TOKEN = cookies["kw_token"] + ""
                    SpTools.putString(
                        Constants.CSRF_TOKEN_KEY,
                        Constants.CSRF_TOKEN + "_" + System.currentTimeMillis()
                    )
                }
                Logger.i("getCookie: $cookies")
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
    }
}