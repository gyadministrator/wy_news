package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.entity.HotWordEntity
import com.android.wy.news.entity.RollHotWord
import com.android.wy.news.entity.VideoEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/20 14:01
  * @Version:        1.0
  * @Description:    
 */
class NewsMainViewModel : BaseViewModel() {

    val dataList = MutableLiveData<ArrayList<RollHotWord>>()

    fun getHotWord() {
        val apiService =
            HttpManager.mInstance.getApiService(Constants.HOT_WORD_URL, IApiService::class.java)
        val observable = apiService.getHotWord()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                try {
                    val gson = Gson()
                    val hotWordEntity = gson.fromJson(s, HotWordEntity::class.java)
                    if (hotWordEntity != null) {
                        val data = hotWordEntity.data
                        val rollHotWordList = data.rollHotWordList
                        dataList.postValue(rollHotWordList)
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