package com.android.wy.news.common

import com.android.wy.news.entity.LiveClassifyEntity
import com.android.wy.news.entity.NewsClassifyEntity
import com.android.wy.news.entity.music.MusicTypeEntity
import com.android.wy.news.music.lrc.Lrc

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
  * 高德定位信息
  * key:7f0d79866211bd1622b599d4ffae5a73
  * sha1:1A:6B:A9:8E:FE:F1:B6:38:B9:35:BF:AD:C6:4A:04:CC:69:FA:12:D4
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
        const val CACHE_VIDEO = "cache_video"
        const val NO_WIFI_PLAY = "no_wifi_play"
        const val IS_WIFI_NOTICE = "is_wifi_notice"
        const val IS_SHOW_DESKTOP_LRC = "is_show_desktop_lrc"
        const val IS_WIFI_NOTICE_DIALOG = "is_wifi_notice_dialog"
        const val PRIVACY_STATUS = "privacy_status"
        const val PRIVACY_STATUS_AGREE = 1
        const val PRIVACY_STATUS_CANCEL = 2
        const val LOCATION_KEY = "7f0d79866211bd1622b599d4ffae5a73"
        const val privacyUrl = "file:///android_asset/privacy_policy.html"
        const val userUrl = "file:///android_asset/user_agreement.html"
        const val NOTICE_STATUS = "notice_status"
        const val AUTHOR_URL = "https://github.com/gyadministrator"
        const val REPOSITORY_URL = "https://github.com/gyadministrator/wy_news"
        const val TEST_APK_URL =
            "https://static.ws.126.net/163/apk/newsapp/newsreader_sps_article.apk"
        val mNewsTitleList = arrayListOf<NewsClassifyEntity>()
        val mNewsLiveTitleList = arrayListOf<LiveClassifyEntity>()
        const val APP_UPDATE_INFO_URL =
            "https://github.com/gyadministrator/wy_news/releases/download/v1.0/updateInfo.json"
        const val APP_UPDATE_BASE_URL =
            "https://github.com"
        const val APP_DOWNLOAD_URL =
            "https://github.com/gyadministrator/wy_news/releases/download/v1.0/release-v1.0-2023.05.07.apk"
        var hasAddView = false

        /*以下是音乐相关*/
        val mMusicTitleList = arrayListOf<MusicTypeEntity>()
        val currentLrcData = arrayListOf<Lrc>()
        var LAST_PLAY_MUSIC_KEY = "last_play_music_key"
        var CSRF_TOKEN_KEY = "csrf_token_key"
        var CSRF_TOKEN = ""
        const val MUSIC_BASE_URL = "http://www.kuwo.cn"
        const val MUSIC_BASE_LRC_URL = "http://m.kuwo.cn"
    }
}