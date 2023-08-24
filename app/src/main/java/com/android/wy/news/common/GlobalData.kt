package com.android.wy.news.common

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.entity.LiveClassifyEntity
import com.android.wy.news.entity.NewsClassifyEntity
import com.android.wy.news.entity.music.MusicTypeEntity
import com.android.wy.news.music.lrc.Lrc

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/16 9:36
  * @Version:        1.0
  * @Description:   全局数据相关
 */
object GlobalData {
    /*-----------------sp key----------------------*/
    object SpKey {
        const val SPLASH_AD = "splash_ad"
        const val CACHE_VIDEO = "cache_video"
        const val NO_WIFI_PLAY = "no_wifi_play"
        const val IS_WIFI_NOTICE = "is_wifi_notice"
        const val IS_SHOW_DESKTOP_LRC = "is_show_desktop_lrc"
        const val IS_WIFI_NOTICE_DIALOG = "is_wifi_notice_dialog"
        const val PRIVACY_STATUS = "privacy_status"
        const val NOTICE_STATUS = "notice_status"
        var LAST_PLAY_MUSIC_KEY = "last_play_music_key"
        var LRC_TYPE = "lrc_type"
    }

    /*-----------------全局数据----------------------*/
    var isLock: Boolean = false
    const val MUSIC_NOTIFY_ID = 100
    var isPlaying: Boolean = false
    const val LOCATION_KEY = "7f0d79866211bd1622b599d4ffae5a73"
    val mNewsTitleList = arrayListOf<NewsClassifyEntity>()
    val mNewsLiveTitleList = arrayListOf<LiveClassifyEntity>()
    val mMusicTitleList = arrayListOf<MusicTypeEntity>()
    val currentLrcData = arrayListOf<Lrc>()
    val lrcTypeChange = MutableLiveData<Int>()
    val playUrlChange = MutableLiveData<String>()
    val indexChange = MutableLiveData<Int>()
    val doubleClickChange = MutableLiveData<Int>()
    val cityChange = MutableLiveData<String>()

    /*--------------------音乐请求Header---------------------*/
    private var headerCookie: String =
        "_ga=GA1.2.1097867330.1688692040; _gid=GA1.2.527365140.1692754772; Hm_lvt_cdb524f42f0ce19b169a8071123a4797=1692754772,1692840272; Hm_lpvt_cdb524f42f0ce19b169a8071123a4797=1692844674; _ga_ETPBRPM9ML=GS1.2.1692843852.7.1.1692844675.57.0.0; Hm_Iuvt_cdb524f42f0cer9b268e4v7y734w5esq24=Amw8eXExKca8yPtiknQbXsQwPiMxj2ty"
    private var headerSecret: String =
        "61d468f39ca361123093b36890c97bc2718f6d7d6f14203b932c42eb40c41c16027cf490"
    var musicHeader: HashMap<String, String> =
        hashMapOf("Cookie" to headerCookie, "Secret" to headerSecret)
    val musicCommonRequestParams: Map<String, Any> = mapOf(
        "httpsStatus" to 1, "reqId" to "18c21420-e4d6-11ed-952f-9f227639ff35", "plat" to "web_www",
        "from" to ""
    )
}