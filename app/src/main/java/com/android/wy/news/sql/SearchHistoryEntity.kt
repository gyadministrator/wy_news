package com.android.wy.news.sql

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/4/3 14:48
  * @Version:        1.0
  * @Description:    
 */
@Entity
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "title")
    var title: String
)