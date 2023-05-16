package com.android.wy.news.common

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/16 9:36
  * @Version:        1.0
  * @Description:
  * * //map["视频"] = "/touch/nc/api/video/recommend/Video_Recom/0-10.do?callback=videoList"
  * //0-10，表示从0开始，取10条，下一组数据可以拼 10-10
    //touch/reconstruct/article/list/BBM54PGAwangning/0-10.html
    * {
    "title": "新闻",
    "tid": "BBM54PGAwangning"
  }
  * 高德定位信息
  * key:7f0d79866211bd1622b599d4ffae5a73
  * sha1:1A:6B:A9:8E:FE:F1:B6:38:B9:35:BF:AD:C6:4A:04:CC:69:FA:12:D4
  * 接口地址相关
 */
object GlobalConstant {
    /*-----------------新闻地址----------------------*/
    const val BASE_URL = "https://3g.163.com"
    const val BASE_HEAD_URL = "http://c.m.163.com"
    const val WEB_URL = "https://3g.163.com/news/article/"
    const val LIVE_WEB_URL = "https://live.163.com/room/"
    const val LIVE_URL = "http://data.live.126.net"
    const val HOT_WORD_URL = "https://gw.m.163.com/search/"
    const val CITY_URL = "https://xf.house.163.com"
    const val VIDEO_URL = "http://c.m.163.com"
    const val HOT_NEWS_URL = "https://m.163.com"
    const val SEARCH_URL = "https://gw.m.163.com"
    const val SPLASH_URL = "http://www.bing.com"

    /*-----------------音乐地址----------------------*/
    const val MUSIC_BASE_URL = "http://www.kuwo.cn"

    /*-----------------其它地址----------------------*/
    const val privacyUrl = "file:///android_asset/privacy_policy.html"
    const val userUrl = "file:///android_asset/user_agreement.html"
    const val AUTHOR_URL = "https://github.com/gyadministrator"
    const val REPOSITORY_URL = "https://github.com/gyadministrator/wy_news"
    const val APP_UPDATE_BASE_URL = "https://github.com"

    /*-----------------测试地址----------------------*/
    const val TEST_APK_URL = "https://static.ws.126.net/163/apk/newsapp/newsreader_sps_article.apk"
    const val APP_UPDATE_INFO_URL =
        "https://github.com/gyadministrator/wy_news/releases/download/v1.0/updateInfo.json"
    const val APP_DOWNLOAD_URL =
        "https://github.com/gyadministrator/wy_news/releases/download/v1.0/release-v1.0-2023.05.07.apk"
}