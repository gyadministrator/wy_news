package com.android.wy.news.update

import androidx.appcompat.app.AppCompatActivity
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.common.Logger
import com.android.wy.news.dialog.LoadingDialog
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UpdateManager {
    fun checkUpdate(activity: AppCompatActivity, onUpdateManagerListener: OnUpdateManagerListener?) {
        LoadingDialog.show(activity, "检查更新中...")
        val apiService =
            HttpManager.mInstance.getApiService(
                GlobalConstant.APP_UPDATE_BASE_URL,
                IApiService::class.java
            )
        val observable = apiService.update()
        observable.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                LoadingDialog.hide()
                val body = response.body()
                val bytes = body?.bytes()
                val s = bytes?.let { String(bytes = it) }
                Logger.i("checkUpdate--->>>content:$s")
                s?.let { onUpdateManagerListener?.onSuccess(it) }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                LoadingDialog.hide()
                Logger.i("checkUpdate--->>>${t.message}")
                onUpdateManagerListener?.onError("连接异常,请稍后重试")
            }
        })
    }
}

interface OnUpdateManagerListener {
    fun onSuccess(s: String)
    fun onError(msg: String)
}