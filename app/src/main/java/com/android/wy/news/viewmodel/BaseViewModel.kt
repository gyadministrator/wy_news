package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 18:37
  * @Version:        1.0
  * @Description:    
 */
abstract class BaseViewModel : ViewModel() {
    protected val msg = MutableLiveData<String>()

    abstract fun clear()
    override fun onCleared() {
        super.onCleared()
        clear()
    }
}