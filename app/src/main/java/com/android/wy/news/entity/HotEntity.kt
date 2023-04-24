package com.android.wy.news.entity

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/3 13:31
  * @Version:        1.0
  * @Description:    
 */
data class HotEntity(
    val code: Int,
    val `data`: HotData?,
    val message: String
)

data class HotData(
    val entranceInfo: EntranceInfo,
    val hotWordList: ArrayList<HotWord>,
    val requestId: String
)

data class EntranceInfo(
    val hotWord: String,
    val linkInfo: String,
    val linkUrl: String,
    val logoPic: String,
    val searchWord: String,
    val sloganPic: String
)

data class HotWord(
    val exp: String,
    val hotWord: String,
    val searchWord: String,
    val source: String,
    val tag: String,
    val trend: String
)