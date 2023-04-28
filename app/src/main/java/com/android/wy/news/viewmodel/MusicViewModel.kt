package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.manager.JsoupManager
import com.android.wy.news.manager.ThreadExecutorManager

/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 16:59
  * @Version:        1.0
  * @Description:    
 */
class MusicViewModel : BaseViewModel() {
    val isSuccess = MutableLiveData<Boolean>()
    fun getCookie() {
        ThreadExecutorManager.mInstance.startExecute {
            val success = JsoupManager.getCookie()
            isSuccess.postValue(success)
        }
    }


    override fun clear() {

    }
}