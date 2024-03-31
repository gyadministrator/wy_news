package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.common.Logger
import com.android.wy.news.entity.RecommendVideoData
import com.android.wy.news.entity.RecommendVideoEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoTabViewModel : BaseViewModel() {
    val dataList = MutableLiveData<ArrayList<RecommendVideoData>>()

    fun getRecommendVideoList() {
        val apiService =
            HttpManager.mInstance.getApiGsonService(
                GlobalConstant.VIDEO_URL,
                IApiService::class.java
            )
        val observable = apiService.getRecommendVideo()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                val s = response.body()?.string()
                val videoData = CommonTools.parseRecommendVideoData(s)
                Logger.i("getRecommendVideoList--->>>$videoData")
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