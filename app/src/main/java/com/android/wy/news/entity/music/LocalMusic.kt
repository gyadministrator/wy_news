package com.android.wy.news.entity.music


/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/6/8 15:14
  * @Version:        1.0
  * @Description:    
 */
data class LocalMusic(
    var song: String,
    var singer: String,
    var size: Long,
    var duration: Int,
    var path: String,
    var albumId: Long,
    var id: Long,
    var state: Int
)