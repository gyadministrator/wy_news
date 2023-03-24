package com.android.wy.news.entity

data class CityNewsEntity(
    val house: ArrayList<House>,
    val status: String
)

data class House(
    val addata: Any,
    val category: String,
    val channel: String,
    val digest: Any,
    val docid: String,
    val imgsrc3gtype: Int,
    val imgsrcFrom: Any,
    val isTop: Any,
    val link: String,
    val liveInfo: Any,
    val picInfo: List<PicInfo>,
    val ptime: String,
    val source: String,
    val tag: Any,
    val tcount: Int,
    val title: String,
    val type: String,
    val typeid: Any,
    val unlikeReason: Any,
    val videoInfo: Any
)

data class PicInfo(
    val height: Any,
    val ref: Any,
    val url: String,
    val width: Any
)