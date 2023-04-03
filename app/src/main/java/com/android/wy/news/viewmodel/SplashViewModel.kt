package com.android.wy.news.viewmodel

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.common.SpTools
import com.android.wy.news.entity.IpEntity
import com.android.wy.news.entity.LiveClassifyEntity
import com.android.wy.news.entity.NewsClassifyEntity
import com.android.wy.news.entity.SplashEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.android.wy.news.manager.ThreadExecutorManager
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
        ThreadExecutorManager.mInstance.startExecute { getLiveClassify() }
        ThreadExecutorManager.mInstance.startExecute { getIpInfo() }
        ThreadExecutorManager.mInstance.startExecute { getSplash() }
    }

    private fun getSplash() {
        val apiService =
            HttpManager.mInstance.getApiService(Constants.SPLASH_URL, IApiService::class.java)
        val observable = apiService.getSplash()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                if (!TextUtils.isEmpty(s)) {
                    val gson = Gson()
                    val splashEntity = gson.fromJson(s, SplashEntity::class.java)
                    val data = splashEntity.images
                    if (data != null && data.isNotEmpty()) {
                        val image = data[0]
                        val url = Constants.SPLASH_URL + image.url
                        SpTools.put(Constants.SPLASH_AD, url)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    private fun getAdInfo() {
        val apiService =
            HttpManager.mInstance.getApiService(Constants.AD_URL, IApiService::class.java)
        val observable = apiService.getAdInfo()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val adData = CommonTools.parseAdData(s)
                if (adData != null) {
                    val ads = adData.ads
                    if (ads != null && ads.isNotEmpty()) {
                        val adItem = ads[0]
                        if (adItem != null) {
                            val resources = adItem.resources
                            if (resources != null && resources.isNotEmpty()) {
                                val resource = resources[0]
                                if (resource != null) {
                                    val urls = resource.urls
                                    if (urls != null && urls.isNotEmpty()) {
                                        val adUrl = urls[0]
                                        adUrl?.let { SpTools.put(Constants.SPLASH_AD, it) }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    private fun getIpInfo() {
        val apiService =
            HttpManager.mInstance.getApiService(Constants.IP_INFO_URL, IApiService::class.java)
        val observable = apiService.getCityByIp()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val gson = Gson()
                val ipEntity = gson.fromJson(s, IpEntity::class.java)
                val result = ipEntity.result
                Constants.currentCity = result.city
                CommonTools.getAddress(result.areaLat.toDouble(), result.areaLng.toDouble())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }


    private fun getLiveClassify() {
        val apiService =
            HttpManager.mInstance.getApiService(Constants.LIVE_URL, IApiService::class.java)
        val observable = apiService.getLiveClassify()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val gson = Gson()
                val dataList = gson.fromJson<ArrayList<LiveClassifyEntity>>(
                    s, object : TypeToken<ArrayList<LiveClassifyEntity>>() {}.type
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
        val dataList = gson.fromJson<ArrayList<NewsClassifyEntity>>(
            content, object : TypeToken<ArrayList<NewsClassifyEntity>>() {}.type
        )
        Constants.mNewsTitleList.clear()
        Constants.mNewsTitleList.addAll(dataList)
        isReadFinish.postValue(true)
    }

    override fun clear() {

    }
}