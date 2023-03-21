package com.android.wy.news.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.entity.NewsEntity
import com.android.wy.news.entity.VideoEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoViewModel : BaseViewModel() {
    val dataList = MutableLiveData<ArrayList<VideoEntity>>()
    fun getVideoList(pageStart: Int) {
        val apiService =
            HttpManager.mInstance.getApiService(Constants.BASE_URL, IApiService::class.java)
        val observable = apiService.getVideoList(pageStart)
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val videoData = CommonTools.parseVideoData(s)
                dataList.postValue(videoData)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    override fun clear() {
    }
}