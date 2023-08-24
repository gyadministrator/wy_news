package com.android.wy.news.http.repository

import com.android.wy.news.common.GlobalData
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
        val params = HashMap<String, Any>()
        params["bangId"] = bangId
        params["pn"] = pn
        params["rn"] = 20
        params.putAll(GlobalData.musicCommonRequestParams)
        val musicListEntity = NetworkRequest.getMusicList(params)
        if (musicListEntity.code == 200) Result.success(musicListEntity)
        else Result.failure(RuntimeException("getMusicList is error"))
    }

    fun getMusicUrl(mid: String) = getData(Dispatchers.IO) {
        val params = HashMap<String, Any>()
        params["type"] = "music"
        params["mid"] = mid
        params.putAll(GlobalData.musicCommonRequestParams)
        val musicUrlEntity = NetworkRequest.getMusicUrl(params)
        if (musicUrlEntity.code == 200) Result.success(musicUrlEntity)
        else Result.failure(RuntimeException("getMusicUrl is error"))
    }

    fun getMusicMv(mid: String) = getData(Dispatchers.IO) {
        val params = HashMap<String, Any>()
        params["type"] = "mv"
        params["mid"] = mid
        params.putAll(GlobalData.musicCommonRequestParams)
        val musicUrlEntity = NetworkRequest.getMusicMv(params)
        if (musicUrlEntity.code == 200) Result.success(musicUrlEntity)
        else Result.failure(RuntimeException("getMusicMv is error"))
    }

    fun getRecommendMusic() = getData(Dispatchers.IO) {
        val params = HashMap<String, Any>()
        params["pid"] = "1082685104"
        params["pn"] = 1
        params["rn"] = 20
        params.putAll(GlobalData.musicCommonRequestParams)
        val musicRecommendEntity = NetworkRequest.getRecommendMusic(params)
        if (musicRecommendEntity.code == 200) Result.success(musicRecommendEntity)
        else Result.failure(RuntimeException("getRecommendMusic is error"))
    }

    fun getMusicByKey(key: String) = getData(Dispatchers.IO) {
        val params = HashMap<String, Any>()
        params["key"] = key
        params["pn"] = 1
        params["rn"] = 20
        params.putAll(GlobalData.musicCommonRequestParams)
        val searchMusicEntity = NetworkRequest.getMusicByKey(params)
        if (searchMusicEntity.code == 200) Result.success(searchMusicEntity)
        else Result.failure(RuntimeException("getMusicByKey is error"))
    }

    fun getMusicLrc(musicId: String) = getData(Dispatchers.IO) {
        val params = HashMap<String, Any>()
        params["musicId"] = musicId
        params.putAll(GlobalData.musicCommonRequestParams)
        val musicLrcEntity = NetworkRequest.getMusicLrc(params)
        if (musicLrcEntity.data != null) Result.success(musicLrcEntity)
        else Result.failure(RuntimeException("getMusicLrc is error"))
    }

    fun getArtistMusic(artistId: String, pn: Int) = getData(Dispatchers.IO) {
        val params = HashMap<String, Any>()
        params["artistid"] = artistId
        params["pn"] = pn
        params["rn"] = 20
        params.putAll(GlobalData.musicCommonRequestParams)
        val artistMusicEntity = NetworkRequest.getArtistMusic(params)
        if (artistMusicEntity.code == 200) Result.success(artistMusicEntity)
        else Result.failure(RuntimeException("getArtistMusic is error"))
    }

    fun getArtistAlbum(artistId: String, pn: Int) = getData(Dispatchers.IO) {
        val params = HashMap<String, Any>()
        params["artistid"] = artistId
        params["pn"] = pn
        params["rn"] = 20
        params.putAll(GlobalData.musicCommonRequestParams)
        val artistAlbumEntity = NetworkRequest.getArtistAlbum(params)
        if (artistAlbumEntity.code == 200) Result.success(artistAlbumEntity)
        else Result.failure(RuntimeException("getArtistAlbum is error"))
    }

    fun getArtistMv(artistId: String, pn: Int) = getData(Dispatchers.IO) {
        val params = HashMap<String, Any>()
        params["artistid"] = artistId
        params["pn"] = pn
        params["rn"] = 20
        params.putAll(GlobalData.musicCommonRequestParams)
        val artistMvEntity = NetworkRequest.getArtistMv(params)
        if (artistMvEntity.code == 200) Result.success(artistMvEntity)
        else Result.failure(RuntimeException("getArtistMv is error"))
    }

    fun getAlbumInfo(albumId: String, pn: Int) = getData(Dispatchers.IO) {
        val params = HashMap<String, Any>()
        params["albumId"] = albumId
        params["pn"] = pn
        params["rn"] = 20
        params.putAll(GlobalData.musicCommonRequestParams)
        val artistMvEntity = NetworkRequest.getAlbumInfo(params)
        if (artistMvEntity.code == 200) Result.success(artistMvEntity)
        else Result.failure(RuntimeException("getAlbumInfo is error"))
    }
}