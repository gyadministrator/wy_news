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
interface RecordMusicController {
    @Insert
    fun addRecordMusic(recordMusicEntity: RecordMusicEntity)

    @Delete
    fun deleteRecordMusic(recordMusicEntity: RecordMusicEntity)

    @Delete
    fun deleteAllRecordMusic(recordMusicEntityList: ArrayList<RecordMusicEntity>)

    @Query("SELECT * FROM recordMusicEntity WHERE mId  = :mid LIMIT 1")
    fun findRecordMusic(mid: String): RecordMusicEntity?

    @Query("SELECT * FROM recordMusicEntity")
    fun getRecordMusic(): List<RecordMusicEntity>
}