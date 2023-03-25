package com.android.wy.news.entity

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/21 16:32
  * @Version:        1.0
  * @Description:    
 */

data class VideoEntity(
    val accoutClassify: Int,
    val autoPlay: Int,
    val category: String,
    val cover: String,
    val description: String,
    val downTimes: Int,
    val firstFrameImg: String,
    val fullSizeImg: String,
    val length: Int,
    val m3u8Hd_url: String,
    val m3u8Shd_url: String,
    val m3u8_url: String,
    val mp4Hd_url: String,
    val mp4Shd_url: String,
    val mp4_url: String,
    val paidLength: Int,
    val paidPreview: Int,
    val playCount: Int,
    val playersize: Int,
    val prompt: String,
    val ptime: String,
    val refreshId: String,
    val replyBoard: String,
    val replyCount: Int,
    val replyid: String,
    val reqId: String,
    val riskLevel: Int,
    val sectiontitle: String,
    val shortV: Boolean,
    val shortVideoImg: String,
    val sizeHD: Int,
    val sizeSD: Int,
    val sizeSHD: Int,
    val skipId: String,
    val tagList: List<Tag>,
    val threadVoteSwitch: String,
    val title: String,
    val topicDesc: String,
    val topicName: String,
    val topicSid: String,
    val unlikeReason: List<String>,
    val verticalVideo: Boolean,
    val vid: String,
    val videoRatio: Double,
    val videoTopic: VideoTopic1?,
    val videosource: String,
    val voteCount: Int
)

data class Tag(
    val level: Int,
    val text: String,
    val type: String
)

data class VideoTopic1(
    val alias: String,
    val certificationImg: String,
    val ename: String,
    val followed: Boolean,
    val tid: String,
    val tname: String,
    val topic_icons: String
)