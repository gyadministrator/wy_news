package com.android.wy.news.entity

data class UpdateEntity(
    val content: String,
    val time: String,
    val title: String,
    val versionCode: Int,
    val versionName: String,
    val url: String
)