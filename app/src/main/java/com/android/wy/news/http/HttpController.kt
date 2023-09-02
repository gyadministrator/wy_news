package com.android.wy.news.http

import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.dialog.LoadingDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object HttpController {
    private val requestList = HashMap<String, Call<ResponseBody>>()

    @Synchronized
    fun addRequest(requestTag: String, call: Call<ResponseBody>) {
        if (requestList.containsKey(requestTag)) {
            removeRequest(requestTag)
        }
        Logger.i("addRequest--->>>$requestTag")
        requestList[requestTag] = call
    }

    @Synchronized
    fun removeRequest(requestTag: String) {
        val anyCall = requestList[requestTag]
        if (anyCall != null && !anyCall.isCanceled) {
            Logger.i("removeRequest--->>>$requestTag")
            anyCall.cancel()
        }
    }

    fun removeAllRequest() {
        for ((k, _) in requestList) {
            removeRequest(k)
        }
    }

    fun startRequest(requestTag: String, call: Call<ResponseBody>, httpListener: OnHttpListener) {
        addRequest(requestTag, call)
        callBack.setHttpListener(httpListener)
        call.enqueue(callBack)
    }

    private class RequestCallBack : Callback<ResponseBody> {
        private var httpListener: OnHttpListener? = null

        fun setHttpListener(httpListener: OnHttpListener) {
            this.httpListener = httpListener
        }

        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            LoadingDialog.hide(GlobalData.COMMON_LOADING_TAG)
            httpListener?.onRequestSuccess(response)
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            LoadingDialog.hide(GlobalData.COMMON_LOADING_TAG)
            httpListener?.onRequestError(t)
        }
    }

    private val callBack = RequestCallBack()

    interface OnHttpListener {
        fun onRequestSuccess(response: Response<ResponseBody>)
        fun onRequestError(t: Throwable)
    }
}