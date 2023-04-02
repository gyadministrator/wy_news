package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.entity.ScreenVideoEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoFullViewModel : BaseViewModel() {
    var topNewsList = MutableLiveData<ArrayList<ScreenVideoEntity>>()

    fun getTopNews(pageStart: Int) {
        val apiService =
            HttpManager.mInstance.getApiService(Constants.BASE_HEAD_URL, IApiService::class.java)
        val headerNews = apiService.getTopNews(pageStart)
        headerNews.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val headerData = CommonTools.parseTopData(s)
                val list =
                    CommonTools.topEntity2ScreenVideoEntity(0, headerData)
                topNewsList.postValue(list)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }


    override fun clear() {

    }
}