package com.android.wy.news.entity.music


/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/6/16 13:11
  * @Version:        1.0
  * @Description:    
 */
data class ArtistMusicEntity(
    val code: Int,
    val curTime: Long,
    val `data`: ArtistMusicData,
    val msg: String,
    val profileId: String,
    val reqId: String,
    val tId: String
)

data class ArtistMusicData(
    val list: ArrayList<MusicInfo>,
    val total: Int
)

data class ArtistMusicItem(
    val album: String,
    val albumid: String,
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
    val mvpayinfo: ArtistMusicMvPayInfo,
    val name: String,
    val online: Int,
    val originalsongtype: Int,
    val pay: String,
    val payInfo: ArtistMusicPayInfo,
    val pic: String,
    val pic120: String,
    val releasedate: String,
    val rid: Int,
    val score100: String,
    val songTimeMinutes: String,
    val track: Int
)

data class ArtistMusicMvPayInfo(
    val download: String,
    val play: String,
    val vid: String
)

data class ArtistMusicPayInfo(
    val cannotDownload: String,
    val cannotOnlinePlay: String,
    val download: String,
    val feeType: ArtistMusicFeeType,
    val limitfree: String,
    val listen_fragment: String,
    val local_encrypt: String,
    val ndown: String,
    val nplay: String,
    val overseas_ndown: String,
    val overseas_nplay: String,
    val paytagindex: ArtistMusicPayTagIndex,
    val play: String,
    val refrain_end: String,
    val refrain_start: String,
    val tips_intercept: String
)

data class ArtistMusicFeeType(
    val album: String,
    val bookvip: String,
    val song: String,
    val vip: String
)

data class ArtistMusicPayTagIndex(
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