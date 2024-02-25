package com.android.wy.news.entity.music

data class PropType(
    val code: Int,
    val curTime: Long,
    val `data`: Data,
    val msg: String,
    val profileId: String,
    val reqId: String,
    val success: Boolean
)

data class Data(
    val createTime: Long,
    val id: String,
    val jumpUrl: String,
    val offlineTime: Long,
    val onlineTime: Long,
    val popImgUrl: String,
    val popPeriod: Int,
    val popType: String,
    val status: Int
)