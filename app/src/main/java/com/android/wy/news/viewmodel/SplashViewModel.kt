package com.android.wy.news.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.entity.LiveHeaderEntity
import com.android.wy.news.entity.NewsTitleEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.android.wy.news.utils.ThreadExecutorManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 19:31
  * @Version:        1.0
  * @Description:    
 */
class SplashViewModel : BaseViewModel() {
    var isReadFinish = MutableLiveData(false)

    fun init(context: Context) {
        ThreadExecutorManager.mInstance.startExecute { readNewsTitle(context) }
        ThreadExecutorManager.mInstance.startExecute { getLiveHeader() }
    }

    private fun getLiveHeader() {
        val apiService =
            HttpManager.mInstance.getApiService(Constants.LIVE_URL, IApiService::class.java)
        val observable = apiService.getLiveHeader()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val gson = Gson()
                val dataList = gson.fromJson<ArrayList<LiveHeaderEntity>>(
                    s, object : TypeToken<ArrayList<LiveHeaderEntity>>() {}.type
                )
                Constants.mNewsLiveTitleList.clear()
                Constants.mNewsLiveTitleList.addAll(dataList)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    private fun readNewsTitle(context: Context) {
        val content = CommonTools.getAssertContent(context, "title.json")
        val gson = Gson()
        val dataList = gson.fromJson<ArrayList<NewsTitleEntity>>(
            content, object : TypeToken<ArrayList<NewsTitleEntity>>() {}.type
        )
        Constants.mNewsTitleList.clear()
        Constants.mNewsTitleList.addAll(dataList)
        isReadFinish.postValue(true)
    }

    override fun clear() {

    }
}