package com.android.wy.news.common

import com.android.wy.news.entity.LiveClassifyEntity
import com.android.wy.news.entity.NewsClassifyEntity

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 19:18
  * @Version:        1.0
  * @Description:
  * //map["视频"] = "/touch/nc/api/video/recommend/Video_Recom/0-10.do?callback=videoList"
  * //0-10，表示从0开始，取10条，下一组数据可以拼 10-10
    //touch/reconstruct/article/list/BBM54PGAwangning/0-10.html
    * {
    "title": "新闻",
    "tid": "BBM54PGAwangning"
  }
 */
class Constants {
    companion object {
        const val BASE_URL = "https://3g.163.com"
        const val BASE_HEAD_URL = "http://c.m.163.com"
        const val WEB_URL = "https://3g.163.com/news/article/"
        const val LIVE_WEB_URL = "https://live.163.com/room/"
        const val LIVE_URL = "http://data.live.126.net"
        const val HOT_WORD_URL = "https://gw.m.163.com/search/"
        const val IP_INFO_URL = "https://ipservice.ws.126.net"
        const val CITY_URL = "https://xf.house.163.com"
        const val VIDEO_URL = "http://c.m.163.com"
        const val HOT_NEWS_URL = "https://m.163.com"
        const val AD_URL = "https://nex.163.com"
        const val SEARCH_URL = "https://gw.m.163.com"
        const val SPLASH_URL = "http://www.bing.com"
        const val SPLASH_AD = "splash_ad"
        var currentCity: String = ""
        val mNewsTitleList = arrayListOf<NewsClassifyEntity>()
        val mNewsLiveTitleList = arrayListOf<LiveClassifyEntity>()
    }
}