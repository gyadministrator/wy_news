package com.android.wy.news.entity.music

data class ArtistAlbumInfoEntity(
    val code: Int,
    val curTime: Long,
    val `data`: ArtistAlbumInfoData,
    val msg: String,
    val profileId: String,
    val reqId: String,
    val tId: String
)

data class ArtistAlbumInfoData(
    val album: String,
    val albumid: Int,
    val albuminfo: String,
    val artist: String,
    val artistid: Int,
    val content_type: String,
    val isstar: Int,
    val lang: String,
    val musicList: ArrayList<MusicInfo>,
    val pay: Int,
    val pic: String,
    val playCnt: Int,
    val releaseDate: String,
    val total: Int
)

data class ArtistAlbumInfoMusic(
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
    val mvpayinfo: ArtistAlbumInfoMvPayInfo,
    val name: String,
    val online: Int,
    val originalsongtype: Int,
    val pay: String,
    val payInfo: ArtistAlbumInfoPayInfo,
    val pic: String,
    val pic120: String,
    val releaseDate: String,
    val rid: Int,
    val score100: String,
    val songTimeMinutes: String,
    val tme_musician_adtype: String,
    val track: Int
)

data class ArtistAlbumInfoMvPayInfo(
    val down: Int,
    val play: Int,
    val vid: Int
)

data class ArtistAlbumInfoPayInfo(
    val cannotDownload: Int,
    val cannotOnlinePlay: Int,
    val down: String,
    val download: String,
    val feeType: ArtistAlbumInfoFeeType,
    val limitfree: Int,
    val local_encrypt: String,
    val ndown: String,
    val nplay: String,
    val overseas_ndown: String,
    val overseas_nplay: String,
    val paytagindex: ArtistAlbumInfoPayTagIndex,
    val play: String,
    val refrain_end: Int,
    val refrain_start: Int
)

data class ArtistAlbumInfoFeeType(
    val song: String,
    val vip: String
)

data class ArtistAlbumInfoPayTagIndex(
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