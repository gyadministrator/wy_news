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

    fun getMusicMv(mid: String) = getData(Dispatchers.IO) {
        val musicUrlEntity = NetworkRequest.getMusicMv(mid)
        if (musicUrlEntity.code == 200) Result.success(musicUrlEntity)
        else Result.failure(RuntimeException("getMusicMv is error"))
    }

    fun getRecommendMusic() = getData(Dispatchers.IO) {
        val musicRecommendEntity = NetworkRequest.getRecommendMusic()
        if (musicRecommendEntity.code == 200) Result.success(musicRecommendEntity)
        else Result.failure(RuntimeException("getRecommendMusic is error"))
    }

    fun getMusicByKey(key: String) = getData(Dispatchers.IO) {
        val searchMusicEntity = NetworkRequest.getMusicByKey(key)
        if (searchMusicEntity.code == 200) Result.success(searchMusicEntity)
        else Result.failure(RuntimeException("getMusicByKey is error"))
    }

    fun getMusicLrc(musicId: String) = getData(Dispatchers.IO) {
        val musicLrcEntity = NetworkRequest.getMusicLrc(musicId)
        if (musicLrcEntity.data != null) Result.success(musicLrcEntity)
        else Result.failure(RuntimeException("getMusicLrc is error"))
    }

    fun getArtistMusic(artistId: String, pn: Int) = getData(Dispatchers.IO) {
        val artistMusicEntity = NetworkRequest.getArtistMusic(artistId, pn)
        if (artistMusicEntity.code == 200) Result.success(artistMusicEntity)
        else Result.failure(RuntimeException("getArtistMusic is error"))
    }

    fun getArtistAlbum(artistId: String, pn: Int) = getData(Dispatchers.IO) {
        val artistAlbumEntity = NetworkRequest.getArtistAlbum(artistId, pn)
        if (artistAlbumEntity.code == 200) Result.success(artistAlbumEntity)
        else Result.failure(RuntimeException("getArtistAlbum is error"))
    }

    fun getArtistMv(artistId: String, pn: Int) = getData(Dispatchers.IO) {
        val artistMvEntity = NetworkRequest.getArtistMv(artistId, pn)
        if (artistMvEntity.code == 200) Result.success(artistMvEntity)
        else Result.failure(RuntimeException("getArtistMv is error"))
    }
}