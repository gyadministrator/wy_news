package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.entity.RecommendVideoEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoTabViewModel : BaseViewModel() {
    val dataList = MutableLiveData<ArrayList<RecommendVideoEntity>>()

    fun getRecommendVideoList(pageStart: Int) {
        val apiService =
            HttpManager.mInstance.getApiService(
                GlobalConstant.HOT_NEWS_URL,
                IApiService::class.java
            )
        val observable = apiService.getRecommendVideo(pageStart)
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val videoData = CommonTools.parseRecommendVideoData(s)
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