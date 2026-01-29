package com.android.wy.news.sql

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/5/31 9:22
  * @Version:        1.0
  * @Description:    
 */
@Dao
interface DownloadMusicController {
    @Insert
    fun addDownloadMusic(downloadMusicEntity: DownloadMusicEntity)

    @Delete
    fun deleteDownloadMusic(downloadMusicEntity: DownloadMusicEntity)

    @Delete
    fun deleteAllDownloadMusic(downloadMusicEntityList: ArrayList<DownloadMusicEntity>)

    @Query("SELECT * FROM downloadMusicEntity WHERE localPath  = :localPath LIMIT 1")
    fun findDownloadMusic(localPath: String): DownloadMusicEntity?

    @Query("SELECT * FROM downloadMusicEntity")
    fun getSearchDownloadMusic(): List<DownloadMusicEntity>
}