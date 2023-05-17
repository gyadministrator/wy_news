package com.android.wy.news.bottombar.model

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/17 10:52
  * @Version:        1.0
  * @Description:    
 */
data class BarItem(
    var fragmentClass: Class<*>,
    var title: String,
    var normalIcon: Int,
    var selectIcon: Int
)