package com.android.wy.news.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.entity.NewsEntity
import com.android.wy.news.entity.NewsTitleEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.android.wy.news.utils.ThreadExecutorManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    var isReadFinish = MutableLiveData(false)
    fun init(context: Context) {
        ThreadExecutorManager.mInstance.startExecute { readNewsTitle(context) }
    }

    private fun readNewsTitle(context: Context) {
        val content = CommonTools.getAssertContent(context, "title.json")
        val gson = Gson()
        val dataList = gson.fromJson<ArrayList<NewsTitleEntity>>(
            content, object : TypeToken<ArrayList<NewsTitleEntity>>() {}.type
        )
        Constants.mNewsTitleList.clear()
        Constants.mNewsTitleList.addAll(dataList)
        isReadFinish.postValue(true)
    }

    fun getHeaderNews(url: String) {
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