package com.android.wy.news.sql

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/4/3 14:51
  * @Version:        1.0
  * @Description:    
 */
@Dao
interface SearchHistoryController {
    @Insert
    fun addSearchHistory(searchHistoryEntity: SearchHistoryEntity)

    @Delete
    fun deleteSearchHistory(searchHistoryEntity: SearchHistoryEntity)

    @Delete
    fun deleteAllSearchHistory(searchHistoryEntityList: ArrayList<SearchHistoryEntity>)

    @Query("SELECT * FROM searchHistoryEntity WHERE title  = :title LIMIT 1")
    fun findSearchHistory(title: String): SearchHistoryEntity

    @Query("SELECT * FROM searchHistoryEntity")
    fun getSearchHistoryList(): List<SearchHistoryEntity>
}