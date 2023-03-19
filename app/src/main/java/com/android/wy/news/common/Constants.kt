package com.android.wy.news.common

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 19:18
  * @Version:        1.0
  * @Description:    
 */
class Constants {
    companion object {
        const val BASE_URL = "https://3g.163.com"
        const val BASE_HEAD_URL = "http://c.m.163.com"
        val map = hashMapOf<String, String>()
        val mTitleList = arrayListOf<String>()
        fun initUrl() {
            map.clear()
            mTitleList.clear()
            //0-10，表示从0开始，取10条，下一组数据可以拼 10-10
            ///touch/reconstruct/article/list/BBM54PGAwangning/0-10.html
            map["新闻"] = "BBM54PGAwangning"
            map["娱乐"] = "BA10TA81wangning"
            map["体育"] = "BA8E6OEOwangning"
            map["财经"] = "BA8EE5GMwangning"
            map["军事"] = "BAI67OGGwangning"
            map["科技"] = "BA8D4A3Rwangning"
            map["手机"] = "BAI6I0O5wangning"
            map["数码"] = "BAI6JOD9wangning"
            map["时尚"] = "BA8F6ICNwangning"
            map["游戏"] = "BAI6RHDKwangning"
            map["教育"] = "BA8FF5PRwangning"
            map["健康"] = "BDC4QSV3wangning"
            map["旅游"] = "BEO4GINLwangning"
            //map["视频"] = "/touch/nc/api/video/recommend/Video_Recom/0-10.do?callback=videoList"

            val mutableSet = map.keys
            val iterator = mutableSet.iterator()
            while (iterator.hasNext()) {
                mTitleList.add(iterator.next())
            }
        }
    }
}