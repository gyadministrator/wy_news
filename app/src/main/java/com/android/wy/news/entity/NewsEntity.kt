package com.android.wy.news.entity

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 10:50
  * @Version:        1.0
  * @Description:    
 */
data class NewsEntity(
    var liveInfo: Any,
    var docid: String,
    var source: String,
    var title: String,
    var priority: Long,
    var hasImg: Int,
    var url: String,
    var skipURL: String,
    var commentCount: Long,
    var imgsrc3gtype: String,
    var stitle: String,
    var digest: String,
    var imgsrc: String,
    var ptime: String,
    var modelmode: String
)