package com.android.wy.news.entity

data class SearchEntity(
    val code: Int,
    val `data`: SearchData?,
    val message: String
)

data class SearchData(
    val has_more: Int,
    val nextCursorMark: String,
    val program: String,
    val qId: String,
    val query_id: String,
    val result: ArrayList<SearchResult>,
    val total: Int
)

data class SearchResult(
    val commentId: String,
    val content: String,
    val dkeys: String,
    val docid: String,
    val imgType: String,
    val imgsum: Int,
    val imgurl: List<String>,
    val pcUrl: String,
    val postid: String,
    val program: String,
    val ptime: String,
    val replyCount: Int,
    val score: Int,
    val skipID: String,
    val skipType: String,
    val source: String,
    val tag: String,
    val title: String,
    val vote: Int
)