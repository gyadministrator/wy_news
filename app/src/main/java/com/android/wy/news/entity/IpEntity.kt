package com.android.wy.news.entity

data class IpEntity(
    val message: String,
    val result: Result,
    val status: Int
)

data class Result(
    val administrativeCode: String,
    val areaCode: String,
    val areaLat: String,
    val areaLng: String,
    val city: String,
    val company: String,
    val continentCode: String,
    val country: String,
    val countrySymbol: String,
    val ip: String,
    val network: String,
    val `operator`: String,
    val province: String,
    val timezone: String,
    val utc: String
)