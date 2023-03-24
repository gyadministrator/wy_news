package com.android.wy.news.entity

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/23 19:28
  * @Version:        1.0
  * @Description:    
 */
data class HotWordEntity(
    val code: Int,
    val `data`: Data,
    val message: String
)

data class Data(
    val requestId: String,
    val rollHotWordList: ArrayList<RollHotWord>
)

data class RollHotWord(
    val hotWord: String,
    val searchWord: String,
    val source: String,
    val tag: String
)