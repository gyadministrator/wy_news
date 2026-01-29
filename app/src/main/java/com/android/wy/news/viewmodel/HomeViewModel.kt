package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.entity.RollHotWord
import com.android.wy.news.entity.RollingWordEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.android.wy.news.util.JsonUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/5/23 13:12
  * @Version:        1.0
  * @Description:    
 */
class HomeViewModel : BaseViewModel() {
    val dataList = MutableLiveData<ArrayList<RollHotWord>>()

    fun getHotWord() {
        val apiService =
            HttpManager.mInstance.getApiService(
                GlobalConstant.HOT_WORD_URL,
                IApiService::class.java
            )
        val observable = apiService.getRollingWord()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                try {
                    val rollingWordEntity =
                        JsonUtil.parseJsonToObject(s, RollingWordEntity::class.java)
                    if (rollingWordEntity != null) {
                        val data = rollingWordEntity.data
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