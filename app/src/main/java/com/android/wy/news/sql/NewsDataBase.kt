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
    entities = [SearchHistoryEntity::class, DownloadMusicEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDataBase : RoomDatabase() {
    abstract fun getSearchHistoryDao(): SearchHistoryController
    abstract fun getDownloadMusicDao(): DownloadMusicController

    companion object {
        @Volatile
        private var INSTANCE: NewsDataBase? = null

        fun getInstance(context: Context): NewsDataBase {
            if (INSTANCE != null) {
                return INSTANCE!!
            }
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        NewsDataBase::class.java,
                        "wy_news_db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}