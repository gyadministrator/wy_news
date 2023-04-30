package com.android.wy.news.entity.music

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/28 11:14
  * @Version:        1.0
  * @Description:    
 */
data class MusicLrcEntity(
    val `data`: MusicLrcData?,
    val msg: String,
    val msgs: Any,
    val profileid: String,
    val reqid: String,
    val status: Int
)

data class MusicLrcData(
    val lrclist: List<Lrclist>,
    val simpl: Simpl,
    val songinfo: Songinfo
)

data class Lrclist(
    val lineLyric: String,
    val time: String
)

data class Simpl(
    val musiclist: List<Any>,
    val playlist: List<Playlist>
)

data class Songinfo(
    val album: String,
    val albumId: String,
    val artist: String,
    val artistId: String,
    val contentType: String,
    val coopFormats: List<String>,
    val copyRight: String,
    val duration: String,
    val formats: String,
    val hasEcho: Any,
    val hasMv: String,
    val id: String,
    val isExt: Any,
    val isNew: Any,
    val isPoint: String,
    val isbatch: Any,
    val isdownload: String,
    val isstar: String,
    val mkvNsig1: String,
    val mkvNsig2: String,
    val mkvRid: String,
    val mp3Nsig1: String,
    val mp3Nsig2: String,
    val mp3Rid: String,
    val mp3Size: String,
    val mp4sig1: String,
    val mp4sig2: String,
    val musicrId: String,
    val mutiVer: String,
    val mvpayinfo: Any,
    val mvpic: Any,
    val nsig1: String,
    val nsig2: String,
    val online: String,
    val params: Any,
    val pay: String,
    val pic: String,
    val playCnt: String,
    val rankChange: Any,
    val reason: Any,
    val score: Any,
    val score100: String,
    val songName: String,
    val songTimeMinutes: String,
    val tpay: Any,
    val trend: Any,
    val upTime: String,
    val uploader: String
)

data class Playlist(
    val digest: String,
    val disname: String,
    val extend: String,
    val info: String,
    val isnew: String,
    val name: String,
    val newcount: String,
    val nodeid: String,
    val pic: String,
    val playcnt: String,
    val source: String,
    val sourceid: String,
    val tag: String
)