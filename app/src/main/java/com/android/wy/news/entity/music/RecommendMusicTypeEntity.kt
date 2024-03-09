package com.android.wy.news.entity.music

data class RecommendMusicTypeEntity(
    val code: Int,
    val curTime: Long,
    val `data`: RecommendMusicTypeData,
    val msg: String,
    val profileId: String,
    val reqId: String,
    val tId: String
)

data class RecommendMusicTypeData(
    val list: List<RecommendMusicType>
)

data class RecommendMusicType(
    val desc: String,
    val id: String,
    val img: String,
    val img300: String,
    val img500: String,
    val img700: String,
    val info: String,
    val listencnt: Int,
    val musicList: List<Any>,
    val name: String,
    val total: Int,
    val uname: String,
    val userName: String
)