package com.android.wy.news.http

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

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

    @GET("/touch/nc/api/video/recommend/Video_Recom/{pageStart}-10.do?callback=videoList")
    fun getVideoList(
        @Path("pageStart") pageStart: Int
    ): Call<ResponseBody>

    @GET("/nc/article/headline/T1348647853363/{pageStart}-10.html")
    fun getHeaderNews(@Path("pageStart") pageStart: Int): Call<ResponseBody>
}