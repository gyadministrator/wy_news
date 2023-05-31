package com.android.wy.news.sql

import android.content.Context

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/3 15:08
  * @Version:        1.0
  * @Description:    
 */
class SearchHistoryRepository(context: Context) {
    private var searchHistoryController: SearchHistoryController

    init {
        searchHistoryController = NewsDataBase.getInstance(context).getSearchHistoryDao()
    }

    fun getSearchHistoryList(): ArrayList<SearchHistoryEntity> {
        return searchHistoryController.getSearchHistoryList() as ArrayList<SearchHistoryEntity>
    }

    fun getSearchHistoryByTitle(title: String): SearchHistoryEntity {
        return searchHistoryController.findSearchHistory(title)
    }

    fun deleteSearchHistory(searchHistoryEntity: SearchHistoryEntity) {
        searchHistoryController.deleteSearchHistory(searchHistoryEntity)
    }

    fun deleteAllSearchHistory(historyList: ArrayList<SearchHistoryEntity>) {
        searchHistoryController.deleteAllSearchHistory(historyList)
    }

    fun addSearchHistory(searchHistoryEntity: SearchHistoryEntity) {
        searchHistoryController.addSearchHistory(searchHistoryEntity)
    }
}