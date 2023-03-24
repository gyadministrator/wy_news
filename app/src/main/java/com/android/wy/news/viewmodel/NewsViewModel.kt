package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.entity.NewsEntity
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * /* observable.subscribeOn(Schedulers.io())
.observeOn(AndroidSchedulers.mainThread())
.subscribe(object : Observer<Any> {
override fun onSubscribe(d: Disposable) {

}

override fun onNext(t: Any) {
Log.e("gy", "onNext: $t")
}

override fun onError(e: Throwable) {
Log.e("gy", "onError: " + e.message)
}

override fun onComplete() {

}

})*/
 */

class NewsViewModel : BaseViewModel() {
    val dataList = MutableLiveData<ArrayList<NewsEntity>>()

    fun getNewsList(tid: String, pageStart: Int) {
        val apiService =
            HttpManager.mInstance.getApiService(Constants.BASE_URL, IApiService::class.java)
        val observable = apiService.getNewsList(tid, pageStart)
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val s = response.body()?.string()
                val list = CommonTools.parseData(s)
                dataList.postValue(list)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { msg.postValue(it) }
            }

        })
    }

    override fun clear() {
        dataList.value?.clear()
    }
}