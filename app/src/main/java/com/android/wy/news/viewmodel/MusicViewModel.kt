package com.android.wy.news.viewmodel

import android.text.TextUtils
import android.util.Log
import com.android.wy.news.common.Constants
import com.android.wy.news.entity.LiveEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 16:59
  * @Version:        1.0
  * @Description:    
 */
class MusicViewModel : BaseViewModel() {
    fun getMusicList(categoryId: String) {
        val apiService =
            HttpManager.mInstance.getApiService(Constants.MUSIC_BASE_URL, IApiService::class.java)
        val observable = apiService.getMusicList1()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                Log.e("gy", "onResponse: $s")
                /*if (TextUtils.isEmpty(s)){
                    dataList.postValue(ArrayList())
                }
                val gson = Gson()
                val liveEntity = gson.fromJson(s, LiveEntity::class.java)
                if (liveEntity != null) {
                    dataList.postValue(liveEntity.live_review)
                }*/
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    override fun clear() {

    }
}