package com.android.wy.news.entity.music

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/5/26 16:24
  * @Version:        1.0
  * @Description:    
 */
data class MusicRecommendEntity(
    val code: Int,
    val curTime: Long,
    val `data`: MusicRecommendData,
    val msg: String,
    val profileId: String,
    val reqId: String,
    val tId: String
)

data class MusicRecommendData(
    val desc: String,
    val id: String,
    val img: String,
    val img300: String,
    val img500: String,
    val img700: String,
    val info: String,
    val isOfficial: Int,
    val listencnt: Int,
    val musicList: ArrayList<MusicInfo>,
    val name: String,
    val tag: String,
    val total: Int,
    val uPic: String,
    val uname: String,
    val userName: String
)

data class Music(
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
    val mvpayinfo: MusicRecommendMvPayInfo,
    val name: String,
    val online: Int,
    val originalsongtype: Int,
    val pay: String,
    val payInfo: MusicRecommendPayInfo,
    val pic: String,
    val pic120: String,
    val releaseDate: String,
    val rid: Int,
    val score100: String,
    val songTimeMinutes: String,
    val tme_musician_adtype: String,
    val track: Int
)

data class MusicRecommendMvPayInfo(
    val down: Int,
    val play: Int,
    val vid: Int
)

data class MusicRecommendPayInfo(
    val cannotDownload: Int,
    val cannotOnlinePlay: Int,
    val down: String,
    val download: String,
    val feeType: MusicRecommendFeeType,
    val limitfree: Int,
    val listen_fragment: String,
    val local_encrypt: String,
    val ndown: String,
    val nplay: String,
    val overseas_ndown: String,
    val overseas_nplay: String,
    val paytagindex: MusicRecommendPayTagIndex,
    val play: String,
    val refrain_end: Int,
    val refrain_start: Int
)

data class MusicRecommendFeeType(
    val song: String,
    val vip: String
)

data class MusicRecommendPayTagIndex(
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