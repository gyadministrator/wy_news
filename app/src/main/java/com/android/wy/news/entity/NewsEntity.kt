package com.android.wy.news.entity

/*
  * @Author:         gao_yun
  * @CreateDate:     2023/3/17 10:50
  * @Version:        1.0
  * @Description:    
 */
data class NewsEntity(
    val commentCount: Int,
    val digest: String,
    val docid: String,
    val hasImg: Int,
    val imgextra: List<Imgextra>?,
    val imgsrc: String,
    val imgsrc3gtype: String,
    val liveInfo: Any,
    val modelmode: String,
    val photosetID: String,
    val priority: Int,
    val ptime: String,
    val skipType: String,
    val skipURL: String,
    val source: String,
    val stitle: String,
    val title: String,
    val url: String
)

data class Imgextra(
    val imgsrc: String
)