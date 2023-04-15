package com.android.wy.news.locationselect.model

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/15 13:17
  * @Version:        1.0
  * @Description:    
 */
class LocatedCity(name: String, province: String, code: String) :
 City(name, province, "定位城市", code) {
}