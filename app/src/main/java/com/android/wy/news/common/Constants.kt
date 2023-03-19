package com.android.wy.news.common

import com.android.wy.news.entity.NewsTitleEntity

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 19:18
  * @Version:        1.0
  * @Description:
  * //map["视频"] = "/touch/nc/api/video/recommend/Video_Recom/0-10.do?callback=videoList"
  * //0-10，表示从0开始，取10条，下一组数据可以拼 10-10
    //touch/reconstruct/article/list/BBM54PGAwangning/0-10.html
 */
class Constants {
    companion object {
        const val BASE_URL = "https://3g.163.com"
        const val BASE_HEAD_URL = "http://c.m.163.com"
        val mNewsTitleList = arrayListOf<NewsTitleEntity>()
    }
}