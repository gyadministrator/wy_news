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
data class DownloadMusicEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "localPath")
    var localPath: String,
    @ColumnInfo(name = "taskId")
    var taskId: Int,
    @ColumnInfo(name = "musicInfoJson")
    var musicInfoJson: String?
)