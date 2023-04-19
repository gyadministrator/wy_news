package com.android.wy.news.manager

import android.text.TextUtils
import com.android.wy.news.common.Constants
import com.android.wy.news.common.SpTools
import com.android.wy.news.entity.SplashEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/19 14:42
  * @Version:        1.0
  * @Description:    
 */
class DownloadManager {
    companion object {
        fun downloadVideo(url: String) {
            val apiService =
                HttpManager.mInstance.getApiService(IApiService::class.java)
            val observable = apiService.downloadVideo(url)
            observable.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                }

            })
        }
    }
}