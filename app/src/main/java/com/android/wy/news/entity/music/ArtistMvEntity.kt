package com.android.wy.news.entity.music


/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/6/16 13:15
  * @Version:        1.0
  * @Description:    
 */
data class ArtistMvEntity(
    val code: Int,
    val curTime: Long,
    val `data`: ArtistMvData,
    val msg: String,
    val profileId: String,
    val reqId: String,
    val tId: String
)

data class ArtistMvData(
    val mvlist: ArrayList<Mvlist>,
    val total: String
)

data class Mvlist(
    val artist: String,
    val artistid: Int,
    val duration: Int,
    val id: String,
    val mvPlayCnt: Int,
    val name: String,
    val online: String,
    val pic: String,
    val songTimeMinutes: String
)