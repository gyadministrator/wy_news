package com.android.wy.news.entity

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/3/22 17:11
  * @Version:        1.0
  * @Description:    
 */
data class LiveClassifyEntity(
    val defaultCollection: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val sensitive: Int,
    val type: String,
    val visible: Boolean
)