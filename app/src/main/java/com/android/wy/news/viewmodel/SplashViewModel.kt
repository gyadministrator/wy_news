package com.android.wy.news.viewmodel

import com.android.wy.news.common.Constants
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.android.wy.news.utils.ThreadExecutorManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 19:31
  * @Version:        1.0
  * @Description:    
 */
class SplashViewModel : BaseViewModel() {

    fun init() {
        ThreadExecutorManager.mInstance.startExecute { Constants.initUrl() }
    }

    fun getHeaderNews(url:String){
        val apiService =
            HttpManager.mInstance.getApiService(Constants.BASE_HEAD_URL, IApiService::class.java)
        val headerNews = apiService.getHeaderNews()
        headerNews.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    override fun clear() {

    }
}