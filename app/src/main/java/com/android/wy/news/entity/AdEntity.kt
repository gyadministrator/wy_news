package com.android.wy.news.entity

data class AdEntity(
    val ads: List<AdItem?>?, val result: Int
)

data class AdItem(
    val adid: String,
    val category: String,
    val content: String,
    val expire: Long,
    val ext_param: ExtParam,
    val from: Int,
    val hasHtmlResource: Int,
    val htmlResource: String,
    val location: String,
    val monitor: List<Monitor>,
    val position: Int,
    val relatedActionLinks: List<RelatedActionLink>,
    val requestTime: Long,
    val resources: List<Resource?>?,
    val source: String,
    val style: String,
    val title: String,
    val visibility: List<Visibility>
)

class ExtParam

data class Monitor(
    val action: Int, val url: String
)

data class RelatedActionLink(
    val type: String, val url: String
)

data class Resource(
    val type: Int, val urls: List<String?>?
)

data class Visibility(
    val duration: Int, val rate_height: String, val type: String
)