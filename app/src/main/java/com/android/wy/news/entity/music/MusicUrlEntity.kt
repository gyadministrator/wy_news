package com.android.wy.news.entity.music

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/28 9:38
  * @Version:        1.0
  * @Description:    
 */
data class MusicUrlEntity(
    val code: Int,
    val curTime: Long,
    val `data`: MusicUrlData?,
    val msg: String,
    val profileId: String,
    val reqId: String,
    val success: Boolean
)

data class MusicUrlData(
    val url: String?
)