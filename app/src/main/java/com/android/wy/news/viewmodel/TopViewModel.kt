package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.entity.CityNewsEntity
import com.android.wy.news.entity.HotNewsEntity
import com.android.wy.news.entity.House
import com.android.wy.news.entity.TopEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.android.wy.news.manager.JsoupManager
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.util.TaskUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopViewModel : BaseViewModel() {
    var topNewsList = MutableLiveData<ArrayList<TopEntity>>()
    var cityNewsList = MutableLiveData<ArrayList<House>>()
    var hotNewsList = MutableLiveData<ArrayList<HotNewsEntity>>()

    fun getTopNews(pageStart: Int) {
        val apiService =
            HttpManager.mInstance.getApiService(
                GlobalConstant.BASE_HEAD_URL,
                IApiService::class.java
            )
        val headerNews = apiService.getTopNews(pageStart)
        headerNews.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val headerData = CommonTools.parseTopData(s)
                topNewsList.postValue(headerData)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    fun getCityNews(city: String) {
        val apiService =
            HttpManager.mInstance.getApiService(GlobalConstant.CITY_URL, IApiService::class.java)
        val headerNews = apiService.getCurrentCityNewsList(city)
        headerNews.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val cityNewsEntity = JsonUtil.parseJsonToObject(s, CityNewsEntity::class.java)
                val house = cityNewsEntity?.house
                house.let { cityNewsList.postValue(it) }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }
        })
    }

    fun getTopNewsData() {
        TaskUtil.runOnThread {
            val list = JsoupManager.getTopNews(GlobalConstant.HOT_NEWS_URL)
            hotNewsList.postValue(list)
        }
    }

    override fun clear() {

    }
}