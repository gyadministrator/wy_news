package com.android.wy.news.entity

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/14 11:05
  * @Version:        1.0
  * @Description:    
 */

data class CityInfo(
    val cityList: ArrayList<City>,
    val code: String,
    val name: String
)

data class City(
    val areaList: ArrayList<Area>,
    val code: String,
    val name: String
)

data class Area(
    val code: String,
    val name: String
)