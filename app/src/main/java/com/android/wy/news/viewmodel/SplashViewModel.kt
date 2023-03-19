package com.android.wy.news.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.entity.NewsTitleEntity
import com.android.wy.news.utils.ThreadExecutorManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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