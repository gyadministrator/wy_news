package com.android.wy.news.http.repository

import com.android.wy.news.common.GlobalData
import com.android.wy.news.dialog.LoadingDialog
import com.android.wy.news.http.NetworkRequest
import kotlinx.coroutines.Dispatchers

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 16:20
  * @Version:        1.0
  * @Description:    
 */
object MusicRepository : BaseRepository() {
    private val params = HashMap<String, Any>()

    private fun addParam() {
        params.clear()
        params.putAll(GlobalData.musicCommonRequestParams)
    }

    fun getMusicList(bangId: Int, pn: Int) = getData(Dispatchers.IO) {
        addParam()
        params["bangId"] = bangId
        params["pn"] = pn
        params["rn"] = GlobalData.PAGE_SIZE
        val musicListEntity = NetworkRequest.getMusicList(params)
        if (musicListEntity.code == GlobalData.RESPONSE_SUCCESS_CODE) Result.success(musicListEntity)
        else Result.failure(RuntimeException("getMusicList is error"))
    }

    fun getMusicUrl(tag: String, mid: String) = getData(Dispatchers.IO) {
        addParam()
        params["type"] = "music"
        params["mid"] = mid
        val musicUrlEntity = NetworkRequest.getMusicUrl(params)
        if (musicUrlEntity.code == GlobalData.RESPONSE_SUCCESS_CODE) Result.success(musicUrlEntity)
        else {
            LoadingDialog.hide(tag)
            Result.failure(RuntimeException("getMusicUrl is error"))
        }
    }

    fun getMusicMv(mid: String) = getData(Dispatchers.IO) {
        addParam()
        params["type"] = "mv"
        params["mid"] = mid
        val musicUrlEntity = NetworkRequest.getMusicMv(params)
        if (musicUrlEntity.code == GlobalData.RESPONSE_SUCCESS_CODE) Result.success(musicUrlEntity)
        else Result.failure(RuntimeException("getMusicMv is error"))
    }

    fun getRecommendMusic() = getData(Dispatchers.IO) {
        addParam()
        params["pid"] = "1082685104"
        params["pn"] = 1
        params["rn"] = GlobalData.PAGE_SIZE
        val musicRecommendEntity = NetworkRequest.getRecommendMusic(params)
        if (musicRecommendEntity.code == GlobalData.RESPONSE_SUCCESS_CODE) Result.success(
            musicRecommendEntity
        )
        else Result.failure(RuntimeException("getRecommendMusic is error"))
    }

    fun getMusicByKey(key: String) = getData(Dispatchers.IO) {
        addParam()
        params["key"] = key
        params["pn"] = 1
        params["rn"] = GlobalData.PAGE_SIZE
        val searchMusicEntity = NetworkRequest.getMusicByKey(params)
        if (searchMusicEntity.code == GlobalData.RESPONSE_SUCCESS_CODE) Result.success(
            searchMusicEntity
        )
        else Result.failure(RuntimeException("getMusicByKey is error"))
    }

    fun getMusicLrc(musicId: String) = getData(Dispatchers.IO) {
        addParam()
        params["musicId"] = musicId
        val musicLrcEntity = NetworkRequest.getMusicLrc(params)
        if (musicLrcEntity.data != null) Result.success(musicLrcEntity)
        else Result.failure(RuntimeException("getMusicLrc is error"))
    }

    fun getArtistMusic(artistId: String, pn: Int) = getData(Dispatchers.IO) {
        addParam()
        params["artistid"] = artistId
        params["pn"] = pn
        params["rn"] = GlobalData.PAGE_SIZE
        val artistMusicEntity = NetworkRequest.getArtistMusic(params)
        if (artistMusicEntity.code == GlobalData.RESPONSE_SUCCESS_CODE) Result.success(
            artistMusicEntity
        )
        else Result.failure(RuntimeException("getArtistMusic is error"))
    }

    fun getArtistAlbum(artistId: String, pn: Int) = getData(Dispatchers.IO) {
        addParam()
        params["artistid"] = artistId
        params["pn"] = pn
        params["rn"] = GlobalData.PAGE_SIZE
        val artistAlbumEntity = NetworkRequest.getArtistAlbum(params)
        if (artistAlbumEntity.code == GlobalData.RESPONSE_SUCCESS_CODE) Result.success(
            artistAlbumEntity
        )
        else Result.failure(RuntimeException("getArtistAlbum is error"))
    }

    fun getArtistMv(artistId: String, pn: Int) = getData(Dispatchers.IO) {
        addParam()
        params["artistid"] = artistId
        params["pn"] = pn
        params["rn"] = GlobalData.PAGE_SIZE
        val artistMvEntity = NetworkRequest.getArtistMv(params)
        if (artistMvEntity.code == GlobalData.RESPONSE_SUCCESS_CODE) Result.success(artistMvEntity)
        else Result.failure(RuntimeException("getArtistMv is error"))
    }

    fun getAlbumInfo(albumId: String, pn: Int) = getData(Dispatchers.IO) {
        addParam()
        params["albumId"] = albumId
        params["pn"] = pn
        params["rn"] = GlobalData.PAGE_SIZE
        val artistMvEntity = NetworkRequest.getAlbumInfo(params)
        if (artistMvEntity.code == GlobalData.RESPONSE_SUCCESS_CODE) Result.success(artistMvEntity)
        else Result.failure(RuntimeException("getAlbumInfo is error"))
    }
}