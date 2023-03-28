package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.entity.BannerEntity
import com.android.wy.news.entity.CityNewsEntity
import com.android.wy.news.entity.House
import com.android.wy.news.entity.TopEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.android.wy.news.manager.JsoupManager
import com.android.wy.news.manager.ThreadExecutorManager
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopViewModel : BaseViewModel() {
    var topNewsList = MutableLiveData<ArrayList<TopEntity>>()
    var cityNewsList = MutableLiveData<ArrayList<House>>()
    var bannerList = MutableLiveData<ArrayList<BannerEntity>>()

    fun getTopNews(pageStart: Int) {
        val apiService =
            HttpManager.mInstance.getApiService(Constants.BASE_HEAD_URL, IApiService::class.java)
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
            HttpManager.mInstance.getApiService(Constants.CITY_URL, IApiService::class.java)
        val headerNews = apiService.getCurrentCityNewsList(city)
        headerNews.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val gson = Gson()
                val cityNewsEntity = gson.fromJson(s, CityNewsEntity::class.java)
                val house = cityNewsEntity.house
                house.let { cityNewsList.postValue(it) }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    fun getBannerData() {
        ThreadExecutorManager.mInstance.startExecute {
            val banner = JsoupManager.getBanner(Constants.BANNER_URL)
            bannerList.postValue(banner)
        }
    }

    override fun clear() {

    }
}