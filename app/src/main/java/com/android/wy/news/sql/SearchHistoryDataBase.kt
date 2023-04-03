package com.android.wy.news.sql

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/3 15:01
  * @Version:        1.0
  * @Description:    
 */
@Database(
    entities = [SearchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SearchHistoryDataBase : RoomDatabase() {
    abstract fun getSearchHistoryDao(): SearchHistoryController

    companion object {
        @Volatile
        private var INSTANCE: SearchHistoryDataBase? = null

        fun getInstance(context: Context): SearchHistoryDataBase {
            if (INSTANCE != null) {
                return INSTANCE!!
            }
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        SearchHistoryDataBase::class.java,
                        "wy_news_db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}