package com.android.wy.news.entity

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/22 17:14
  * @Version:        1.0
  * @Description:    
 */
data class LiveEntity(
    val live_review: ArrayList<LiveReview>,
    val nextPage: Int,
    val pageNo: Int
)

data class LiveReview(
    val confirm: Int,
    val endTime: String,
    val image: String,
    val liveStatus: Int,
    val liveType: Int,
    val mutilVideo: Boolean,
    val pano: Boolean,
    val pcImage: String,
    val roomId: Int,
    val roomName: String,
    val source: String,
    val sourceinfo: Sourceinfo,
    val startTime: String,
    val type: Int,
    val userCount: Int,
    val video: Boolean,
    val videos: List<Video>
)

data class Sourceinfo(
    val certificationImg: String,
    val tcount: Int,
    val tid: String,
    val timg: String,
    val tname: String
)

data class Video(
    val flvUrl: String,
    val videoType: Int,
    val videoUrl: String
)