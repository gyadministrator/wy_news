package com.android.wy.news.viewmodel

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SpTools
import com.android.wy.news.dialog.LoadingDialog
import com.android.wy.news.entity.HeaderEntity
import com.android.wy.news.entity.LiveClassifyEntity
import com.android.wy.news.entity.NewsClassifyEntity
import com.android.wy.news.entity.SplashEntity
import com.android.wy.news.entity.UpdateEntity
import com.android.wy.news.entity.music.MusicTypeEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.android.wy.news.manager.JsoupManager
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.util.TaskUtil
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
        TaskUtil.runOnThread { readNewsTitle(context) }
        TaskUtil.runOnThread { getLiveClassify() }
        TaskUtil.runOnThread { JsoupManager.getCityInfo() }
        TaskUtil.runOnThread { getSplash() }
        TaskUtil.runOnUiThread { getMusicHeader() }
        //TaskUtil.runOnUiThread { testMusicList() }
    }

    private fun getMusicHeader() {
        val apiService =
            HttpManager.mInstance.getApiService(
                GlobalConstant.APP_UPDATE_BASE_URL,
                IApiService::class.java
            )
        val observable = apiService.requestMusicHeader()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                val body = response.body()
                val bytes = body?.bytes()
                val s = bytes?.let { String(bytes = it) }
                Logger.i("getMusicHeader--->>>content:$s")
                parseHeader(s)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Logger.i("getMusicHeader--->>>${t.message}")
            }
        })
    }

    private fun parseHeader(s: String?) {
        if (!TextUtils.isEmpty(s)) {
            val dataList = JsonUtil.parseJsonToList<HeaderEntity>(s)
            Logger.i("parseHeader--->>>dataList:" + JsonUtil.parseObjectToJson(dataList))
            if (dataList.size > 0) {
                GlobalData.musicHeader.clear()
                for (i in 0 until dataList.size) {
                    val headerEntity = dataList[i]
                    GlobalData.musicHeader[headerEntity.key] = headerEntity.value
                }
            }
        }
        Logger.i("parseHeader--->>>GlobalData.musicHeader:" + GlobalData.musicHeader)
    }

    private fun getSplash() {
        val apiService =
            HttpManager.mInstance.getApiService(GlobalConstant.SPLASH_URL, IApiService::class.java)
        val observable = apiService.getSplash()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                if (!TextUtils.isEmpty(s)) {
                    val splashEntity = JsonUtil.parseJsonToObject(s, SplashEntity::class.java)
                    val data = splashEntity?.images
                    if (!data.isNullOrEmpty()) {
                        val image = data[0]
                        val url = GlobalConstant.SPLASH_URL + image.url
                        SpTools.putString(GlobalData.SpKey.SPLASH_AD, url)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }


    private fun getLiveClassify() {
        val apiService =
            HttpManager.mInstance.getApiService(GlobalConstant.LIVE_URL, IApiService::class.java)
        val observable = apiService.getLiveClassify()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val dataList = JsonUtil.parseJsonToList<LiveClassifyEntity>(s)
                GlobalData.mNewsLiveTitleList.clear()
                GlobalData.mNewsLiveTitleList.addAll(dataList)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    private fun testMusicList() {
        val apiService =
            HttpManager.mInstance.getTestApiService(
                GlobalConstant.MUSIC_BASE_URL,
                IApiService::class.java
            )
        val params = HashMap<String, Any>()
        params["bangId"] = 17
        params["pn"] = 1
        params["rn"] = 20
        params.putAll(GlobalData.musicCommonRequestParams)
        val observable = apiService.getTestMusicList(params)
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                Logger.i("testMusicList--->>>$s")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Logger.i("testMusicList--->>>" + t.message)
                t.message?.let { msg.postValue(it) }
            }
        })
    }

    private fun readNewsTitle(context: Context) {
        val content = CommonTools.getAssertContent(context, "title.json")
        val dataList = JsonUtil.parseJsonToList<NewsClassifyEntity>(content)
        GlobalData.mNewsTitleList.clear()
        GlobalData.mNewsTitleList.addAll(dataList)
        readMusicTitle(context)
    }

    private fun readMusicTitle(context: Context) {
        val content = CommonTools.getAssertContent(context, "music_type.json")
        val dataList = JsonUtil.parseJsonToList<MusicTypeEntity>(content)
        GlobalData.mMusicTitleList.clear()
        GlobalData.mMusicTitleList.addAll(dataList)
        isReadFinish.postValue(true)
    }

    override fun clear() {

    }
}