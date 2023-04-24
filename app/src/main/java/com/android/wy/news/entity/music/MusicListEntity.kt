package com.android.wy.news.entity.music

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 16:10
  * @Version:        1.0
  * @Description:    
 */
data class MusicListEntity(
    val `data`: MusicListData,
    val elapsed_time: String,
    val errmsg: String,
    val errno: Int,
    val ip: String,
    val state: Boolean
)

data class MusicListData(
    val haveMore: Int,
    val result: ArrayList<MusicListResult>,
    val total: Int
)

data class MusicListResult(
    val addDate: String,
    val cateList: List<Int>,
    val desc: String,
    val id: Int,
    val isFavorite: Int,
    val menu: List<Any>,
    val pic: String,
    val resourceType: Int,
    val tagList: List<String>,
    val title: String,
    val trackCount: Int
)