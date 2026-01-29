package com.android.wy.news.entity.music


/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/6/16 13:14
  * @Version:        1.0
  * @Description:    
 */
data class ArtistAlbumEntity(
    val code: Int,
    val curTime: Long,
    val `data`: ArtistAlbumData,
    val msg: String,
    val profileId: String,
    val reqId: String,
    val tId: String
)

data class ArtistAlbumData(
    val albumList: ArrayList<Album>,
    val total: String
)

data class Album(
    val album: String,
    val albumid: Int,
    val albuminfo: String,
    val artist: String,
    val artistid: Int,
    val content_type: String,
    val isstar: Int,
    val lang: String,
    val pay: Int,
    val pic: String,
    val releaseDate: String
)