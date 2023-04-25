package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.entity.music.MusicResult
import com.android.wy.news.manager.JsoupManager
import com.android.wy.news.manager.ThreadExecutorManager

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 16:59
  * @Version:        1.0
  * @Description:    
 */
class MusicViewModel : BaseViewModel() {
    val dataList = MutableLiveData<ArrayList<MusicResult>>()

    fun getMusicList(categoryId: String, pageNo: Int) {
        ThreadExecutorManager.mInstance.startExecute {
            val list = JsoupManager.getMusicList(categoryId, pageNo)
            dataList.postValue(list)
        }
    }

    override fun clear() {

    }
}