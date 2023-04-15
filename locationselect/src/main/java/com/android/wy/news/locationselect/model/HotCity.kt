package com.android.wy.news.locationselect.model

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/15 13:13
  * @Version:        1.0
  * @Description:    
 */
class HotCity(name: String, province: String, code: String) :
    City(name, province, "热门城市", code) {
}