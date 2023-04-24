package com.android.wy.news.entity.music

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 16:07
  * @Version:        1.0
  * @Description:    
 */
data class MusicCategoryEntity(
    val `data`: List<MusicCateGoryData>,
    val elapsed_time: String,
    val errmsg: String,
    val errno: Int,
    val ip: String,
    val state: Boolean
)

data class MusicCateGoryData(
    val categoryName: String,
    val id: String,
    val subCate: List<SubCate>
)

data class SubCate(
    val categoryName: String,
    val count: Int,
    val id: String
)