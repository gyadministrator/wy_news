package com.android.wy.news.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.entity.LiveEntity
import com.android.wy.news.entity.LiveReview
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.android.wy.news.util.JsonUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiveViewModel : BaseViewModel() {
    val dataList = MutableLiveData<ArrayList<LiveReview>>()

    fun getLiveContentList(liveId: Int, pageStart: Int) {
        val apiService =
            HttpManager.mInstance.getApiService(GlobalConstant.LIVE_URL, IApiService::class.java)
        val observable = apiService.getLiveList(liveId, pageStart)
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                if (TextUtils.isEmpty(s)){
                    dataList.postValue(ArrayList())
                }
                val liveEntity = JsonUtil.parseJsonToObject(s, LiveEntity::class.java)
                if (liveEntity != null) {
                    dataList.postValue(liveEntity.live_review)
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