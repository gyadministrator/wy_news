package com.android.wy.news.http

import com.android.wy.news.entity.HotEntity
import com.android.wy.news.entity.music.ArtistAlbumEntity
import com.android.wy.news.entity.music.ArtistAlbumInfoEntity
import com.android.wy.news.entity.music.ArtistMusicEntity
import com.android.wy.news.entity.music.ArtistMvEntity
import com.android.wy.news.entity.music.MusicListEntity
import com.android.wy.news.entity.music.MusicLrcEntity
import com.android.wy.news.entity.music.MusicRecommendEntity
import com.android.wy.news.entity.music.MusicUrlEntity
import com.android.wy.news.entity.music.PropType
import com.android.wy.news.entity.music.SearchMusicEntity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Streaming
import retrofit2.http.Url


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 9:59
  * @Version:        1.0
  * @Description:    
 */
interface IApiService {
    @GET("/touch/reconstruct/article/list/{tid}/{pageStart}-10.html")
    fun getNewsList(
        @Path("tid") tid: String, @Path("pageStart") pageStart: Int
    ): Call<ResponseBody>

    @GET("/recommend/getChanListNews?channel=T1457068979049&size=10")
    fun getVideoList(): Call<ResponseBody>

    @GET("/nc/article/headline/T1348647853363/{pageStart}-10.html")
    fun getTopNews(@Path("pageStart") pageStart: Int): Call<ResponseBody>

    @GET("/livechannel/classifylist.json")
    fun getLiveClassify(): Call<ResponseBody>

    @GET("/livechannel/classify/{liveId}/{pageNum}.json")
    fun getLiveList(@Path("liveId") liveId: Int, @Path("pageNum") pageNum: Int): Call<ResponseBody>

    @GET("api/v1/pc-wap/rolling-word")
    fun getRollingWord(): Call<ResponseBody>

    @GET("/bj/api/indexCmsNews?")
    fun getCurrentCityNewsList(@Query("city") city: String): Call<ResponseBody>

    @GET("/nc/api/v1/pc-wap/search?size=20&from=wap&needPcUrl=true")
    fun getFirstSearch(@Query("query") query: String): Call<ResponseBody>

    @GET("/nc/api/v1/pc-wap/search?queryId=7285539010898925&size=20&from=wap&needPcUrl=true")
    fun getPageSearch(@Query("query") query: String, @Query("page") page: Int): Call<ResponseBody>

    @GET("api/v1/pc-wap/hot-word")
    fun getHot(): Call<ResponseBody>

    @GET("api/v1/pc-wap/hot-word")
    fun getSuspendHot(): Call<HotEntity>

    @GET("/HPImageArchive.aspx?format=js&idx=0&n=1")
    fun getSplash(): Call<ResponseBody>

    @GET("/touch/nc/api/video/recommend/Video_Recom/{pageStart}-20.do?callback=videoList")
    fun getRecommendVideo(@Path("pageStart") pageStart: Int): Call<ResponseBody>

    /**@Streaming
     * 添加这个注解用来下载大文件
    文件特别大的时候可以防止内存溢出
     */
    @Streaming
    @GET("/gyadministrator/wy_news/releases/download/v1.0/updateInfo.json")
    fun update(): Call<ResponseBody>

    /**
     * 获取音乐请求Header
     */
    @Streaming
    @GET("/gyadministrator/wy_news/releases/download/v1.0/musicHeader.json")
    fun requestMusicHeader(): Call<ResponseBody>

    /*-------------------------------------------以下是音乐相关---------------------------------------*/
    @GET("/api/www/bang/bang/musicList")
    fun getTestMusicList(
        @QueryMap queryMap: MutableMap<String, Any>
    ): Call<ResponseBody>


    @GET("/api/www/bang/bang/musicList")
    fun getMusicList(
        @QueryMap queryMap: MutableMap<String, Any>
    ): Call<MusicListEntity>

    @GET("/api/v1/www/music/playUrl")
    fun getMusicUrl(
        @QueryMap queryMap: MutableMap<String, Any>
    ): Call<MusicUrlEntity>

    @GET("/api/v1/www/music/playUrl")
    fun getMusicUrlWithResponseBody(
        @QueryMap queryMap: MutableMap<String, Any>
    ): Call<ResponseBody>

    @GET("/newh5/singles/songinfoandlrc")
    fun getMusicLrc(
        @QueryMap queryMap: MutableMap<String, Any>
    ): Call<MusicLrcEntity>

    @GET("/newh5/singles/songinfoandlrc")
    fun getMusicLrcWithBack(
        @QueryMap queryMap: MutableMap<String, Any>
    ): Call<ResponseBody>

    @GET("/api/www/playlist/playListInfo")
    fun getRecommendMusic(
        @QueryMap queryMap: MutableMap<String, Any>
    ): Call<MusicRecommendEntity>

    @GET("/api/www/search/searchMusicBykeyWord")
    fun getMusicByKey(
        @QueryMap queryMap: MutableMap<String, Any>
    ): Call<SearchMusicEntity>

    @GET("/api/www/artist/artistMusic")
    fun getArtistMusic(
        @QueryMap queryMap: MutableMap<String, Any>
    ): Call<ArtistMusicEntity>

    @GET("/api/www/artist/artistAlbum")
    fun getArtistAlbum(
        @QueryMap queryMap: MutableMap<String, Any>
    ): Call<ArtistAlbumEntity>

    @GET("/api/www/artist/artistMv")
    fun getArtistMv(
        @QueryMap queryMap: MutableMap<String, Any>
    ): Call<ArtistMvEntity>

    @GET("/api/www/album/albumInfo")
    fun getAlbumInfo(
        @QueryMap queryMap: MutableMap<String, Any>
    ): Call<ArtistAlbumInfoEntity>

    @GET("/openapi/v2/pc/popConfig/getPopByType")
    fun getPopByType(
        @QueryMap queryMap: MutableMap<String, Any>
    ): Call<PropType>
}