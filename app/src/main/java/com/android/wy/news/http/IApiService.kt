package com.android.wy.news.http

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
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

    @GET("/locate/api/getLocByIp")
    fun getCityByIp(): Call<ResponseBody>

    @GET("/bj/api/indexCmsNews?")
    fun getCurrentCityNewsList(@Query("city") city: String): Call<ResponseBody>

    @GET("/q?c=homepage&t=wap_float&l=1&app=314EA67F&nt=4G&os=null&location=100&source=other&usergroup=2&cb=callback_1679819101384")
    fun getAdInfo(): Call<ResponseBody>

    @GET("/nc/api/v1/pc-wap/search?size=20&from=wap&needPcUrl=true")
    fun getFirstSearch(@Query("query") query: String): Call<ResponseBody>

    @GET("/nc/api/v1/pc-wap/search?queryId=7285539010898925&size=20&from=wap&needPcUrl=true")
    fun getPageSearch(@Query("query") query: String, @Query("page") page: Int): Call<ResponseBody>

    @GET("api/v1/pc-wap/hot-word")
    fun getHot(): Call<ResponseBody>

    @GET("/HPImageArchive.aspx?format=js&idx=0&n=1")
    fun getSplash(): Call<ResponseBody>

    @GET("/touch/nc/api/video/recommend/Video_Recom/{pageStart}-20.do?callback=videoList")
    fun getRecommendVideo(@Path("pageStart") pageStart: Int): Call<ResponseBody>

    //添加这个注解用来下载大文件
    @Streaming
    @GET
    fun downloadVideo(@Url fileUrl: String): Call<ResponseBody>
}