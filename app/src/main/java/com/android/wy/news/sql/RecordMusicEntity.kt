package com.android.wy.news.sql

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/31 9:18
  * @Version:        1.0
  * @Description:    
 */
@Entity
data class RecordMusicEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "mId")
    var mId: String,
    @ColumnInfo(name = "musicInfoJson")
    var musicInfoJson: String?
)