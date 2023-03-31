package com.android.wy.news.manager

import com.android.wy.news.common.Constants
import com.android.wy.news.entity.HotNewsEntity
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

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
    }
}