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
    fun getMusicList(bangId: Int, pn: Int) = getData(Dispatchers.IO) {
        val musicListEntity = NetworkRequest.getMusicList(bangId, pn)
        if (musicListEntity.code == 200) Result.success(musicListEntity)
        else Result.failure(RuntimeException("getMusicList is error"))
    }
}