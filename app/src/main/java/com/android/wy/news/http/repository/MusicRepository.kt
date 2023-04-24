package com.android.wy.news.http.repository

import android.util.Log
import com.android.wy.news.http.NetworkRequest
import kotlinx.coroutines.Dispatchers

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 16:20
  * @Version:        1.0
  * @Description:    
 */
object MusicRepository : BaseRepository() {
    fun getMusicCateGory() = getData(Dispatchers.IO) {
        val musicCategoryEntity = NetworkRequest.getMusicCateGory()
        if (musicCategoryEntity.errno == 22000) Result.success(musicCategoryEntity)
        else Result.failure(RuntimeException("getMusicCateGory is error"))
    }

    fun getMusicList(subCateId: String?) = getData(Dispatchers.IO) {
        val musicListEntity = NetworkRequest.getMusicList(subCateId)
        if (musicListEntity.errno == 22000) Result.success(musicListEntity)
        else Result.failure(RuntimeException("getMusicList is error"))
    }
}