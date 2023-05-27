package com.android.wy.news.entity.music

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/27 14:46
  * @Version:        1.0
  * @Description:    
 */
data class SearchMusicEntity(
    val code: Int,
    val curTime: Long,
    val `data`: SearchMusicData,
    val msg: String,
    val profileId: String,
    val reqId: String,
    val tId: String
)

data class SearchMusicData(
    val list: ArrayList<SearchMusicItem>,
    val total: String
)

data class SearchMusicItem(
    val ad_subtype: String,
    val ad_type: String,
    val album: String,
    val albumid: Int,
    val albumpic: String,
    val artist: String,
    val artistid: Int,
    val barrage: String,
    val content_type: String,
    val duration: Int,
    val hasLossless: Boolean,
    val hasmv: Int,
    val isListenFee: Boolean,
    val isstar: Int,
    val musicrid: String,
    val mvpayinfo: SearchMusicMvPayInfo,
    val name: String,
    val nationid: String,
    val online: Int,
    val originalsongtype: Int,
    val pay: String,
    val payInfo: SearchMusicPayInfo,
    val pic: String,
    val pic120: String,
    val releaseDate: String,
    val rid: Int,
    val score100: String,
    val songTimeMinutes: String,
    val tme_musician_adtype: String,
    val track: Int
)

data class SearchMusicMvPayInfo(
    val down: Int,
    val play: Int,
    val vid: Int
)

data class SearchMusicPayInfo(
    val cannotDownload: Int,
    val cannotOnlinePlay: Int,
    val down: String,
    val download: String,
    val feeType: SearchMusicFeeType,
    val limitfree: Int,
    val listen_fragment: String,
    val local_encrypt: String,
    val ndown: String,
    val nplay: String,
    val overseas_ndown: String,
    val overseas_nplay: String,
    val paytagindex: SearchMusicPayTagIndex,
    val play: String,
    val refrain_end: Int,
    val refrain_start: Int
)

data class SearchMusicFeeType(
    val song: String,
    val vip: String
)

data class SearchMusicPayTagIndex(
    val AR501: Int,
    val DB: Int,
    val F: Int,
    val H: Int,
    val HR: Int,
    val L: Int,
    val S: Int,
    val ZP: Int,
    val ZPGA201: Int,
    val ZPGA501: Int,
    val ZPLY: Int
)