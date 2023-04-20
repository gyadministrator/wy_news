package com.android.wy.news.entity

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/20 10:05
  * @Version:        1.0
  * @Description:    
 */
data class RecommendVideoEntity(
    val cover: String,
    val description: String,
    val length: Int,
    val m3u8Hd_url: Any,
    val m3u8_url: String,
    val mp4Hd_url: Any,
    val mp4_url: String,
    val playCount: Int,
    val playersize: Int,
    val ptime: String,
    val replyBoard: String,
    val replyCount: Int,
    val replyid: String,
    val sectiontitle: String,
    val sizeHD: Int,
    val sizeSD: Int,
    val sizeSHD: Int,
    val title: String,
    val topicDesc: String,
    val topicImg: Any,
    val topicName: String,
    val topicSid: String,
    val vid: String,
    val videosource: String,
    val votecount: Int
)