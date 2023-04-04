package com.android.wy.news.common

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/4 16:11
  * @Version:        1.0
  * @Description:    
 */
interface SkinType {
    companion object {
        //跟随系统
        const val SKIN_TYPE_SYSTEM = 0

        //浅色
        const val SKIN_TYPE_LIGHT = 1

        //深色
        const val SKIN_TYPE_DARK = 2

        const val SKIN_TYPE = "skin_type"
    }
}