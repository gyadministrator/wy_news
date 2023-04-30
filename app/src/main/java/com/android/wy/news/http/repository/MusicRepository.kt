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

    fun getMusicUrl(mid: String) = getData(Dispatchers.IO) {
        val musicUrlEntity = NetworkRequest.getMusicUrl(mid)
        if (musicUrlEntity.code == 200) Result.success(musicUrlEntity)
        else Result.failure(RuntimeException("getMusicUrl is error"))
    }

    fun getMusicLrc(musicId: String) = getData(Dispatchers.IO) {
        val musicLrcEntity = NetworkRequest.getMusicLrc(musicId)
        if (musicLrcEntity.data != null) Result.success(musicLrcEntity)
        else Result.failure(RuntimeException("getMusicLrc is error"))
    }
}