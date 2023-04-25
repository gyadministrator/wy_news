package com.android.wy.news.http.repository

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
}