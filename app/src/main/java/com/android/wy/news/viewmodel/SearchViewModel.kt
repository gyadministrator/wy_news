package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.entity.HotEntity
import com.android.wy.news.entity.HotWord
import com.android.wy.news.entity.SearchEntity
import com.android.wy.news.entity.SearchResult
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : BaseViewModel() {

    val dataList = MutableLiveData<ArrayList<SearchResult>>()
    val hotList = MutableLiveData<ArrayList<HotWord>>()

    fun getSearchPageList(query: String, pageStart: Int) {
        val apiService =
            HttpManager.mInstance.getApiService(GlobalConstant.SEARCH_URL, IApiService::class.java)
        val observable = apiService.getPageSearch(query = query, pageStart)
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val gson = Gson()
                val searchEntity = gson.fromJson(s, SearchEntity::class.java)
                if (searchEntity != null) {
                    val data = searchEntity.data
                    if (data != null) {
                        val result = data.result
                        dataList.postValue(result)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    fun getRefreshSearchList(query: String) {
        val apiService =
            HttpManager.mInstance.getApiService(GlobalConstant.SEARCH_URL, IApiService::class.java)
        val observable = apiService.getFirstSearch(query = query)
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val gson = Gson()
                val searchEntity = gson.fromJson(s, SearchEntity::class.java)
                if (searchEntity != null) {
                    val data = searchEntity.data
                    if (data != null) {
                        val result = data.result
                        dataList.postValue(result)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    fun getHot() {
        val apiService =
            HttpManager.mInstance.getApiService(GlobalConstant.HOT_WORD_URL, IApiService::class.java)
        val observable = apiService.getHot()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                try {
                    val gson = Gson()
                    val hotEntity = gson.fromJson(s, HotEntity::class.java)
                    if (hotEntity != null) {
                        val data = hotEntity.data
                        val hotWordList = data?.hotWordList
                        hotWordList.let { hotList.postValue(it) }
                    }
                } catch (e: Exception) {
                    msg.postValue(e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    override fun clear() {

    }
}