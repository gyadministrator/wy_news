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
        var CSRF_TOKEN_KEY = "csrf_token_key"
        var LRC_TYPE = "lrc_type"
    }

    /*-----------------全局数据----------------------*/
    const val LOCATION_KEY = "7f0d79866211bd1622b599d4ffae5a73"
    val mNewsTitleList = arrayListOf<NewsClassifyEntity>()
    val mNewsLiveTitleList = arrayListOf<LiveClassifyEntity>()
    val mMusicTitleList = arrayListOf<MusicTypeEntity>()
    val currentLrcData = arrayListOf<Lrc>()
    var CSRF_TOKEN = ""
    val lrcTypeChange = MutableLiveData<Int>()
    val playUrlChange = MutableLiveData<String>()
    val indexChange = MutableLiveData<Int>()
    val doubleClickChange = MutableLiveData<Int>()
}