package com.android.wy.news.entity.music

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 16:11
  * @Version:        1.0
  * @Description:    
 */
data class MusicLinkEntity(
    val `data`: MusicLinkData,
    val elapsed_time: String,
    val errmsg: String,
    val errno: Int,
    val ip: String,
    val state: Boolean
)

data class MusicLinkData(
    val TSID: String,
    val afReplayGain: Double,
    val albumAssetCode: String,
    val albumTitle: String,
    val allRate: List<String>,
    val artist: List<Artist>,
    val assetId: String,
    val bits: Int,
    val bizList: List<String>,
    val cpId: Int,
    val downTime: String,
    val duration: Int,
    val expireTime: Int,
    val filemd5: String,
    val format: String,
    val genre: String,
    val hashcode: String,
    val id: String,
    val isFavorite: Int,
    val isPaid: Int,
    val isVip: Int,
    val isrc: String,
    val lang: String,
    val lyric: String,
    val maxVolume: Double,
    val meanVolume: Double,
    val pay_model: Int,
    val pic: String,
    val pushTime: String,
    val rate: Int,
    val releaseDate: String,
    val size: Int,
    val sort: Int,
    val title: String,
    val trail_audio_info: TrailAudioInfo
)

data class Artist(
    val artistCode: String,
    val artistType: Int,
    val artistTypeName: String,
    val gender: String,
    val isFavorite: Int,
    val name: String,
    val pic: String,
    val region: String
)

data class TrailAudioInfo(
    val duration: String,
    val expireTime: Int,
    val path: String,
    val rate: Int,
    val start_time: String
)