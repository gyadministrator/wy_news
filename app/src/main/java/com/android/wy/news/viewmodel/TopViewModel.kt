package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.entity.NewsHeaderEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopViewModel : BaseViewModel() {
    var headDataList = MutableLiveData<ArrayList<NewsHeaderEntity>>()
    fun getHeaderNews(pageStart: Int) {
        val apiService =
            HttpManager.mInstance.getApiService(Constants.BASE_HEAD_URL, IApiService::class.java)
        val headerNews = apiService.getHeaderNews(pageStart)
        headerNews.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val headerData = CommonTools.parseHeaderData(s)
                headDataList.postValue(headerData)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    override fun clear() {

    }
}