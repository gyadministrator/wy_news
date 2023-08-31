package com.android.wy.news.http.repository

import androidx.lifecycle.liveData
import com.android.wy.news.common.Logger
import com.android.wy.news.util.ToastUtil
import kotlin.coroutines.CoroutineContext

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 10:15
  * @Version:        1.0
  * @Description:    
 */
open class BaseRepository {
    /**
     * 这里的getData()函数，按照liveData()函数的参数接收标准定义的一个高阶函数。
     * 在getData()函数的内部会先调用一下liveData()函数，
     * 然后在liveData()函数的代码块中统一进行try catch处理，
     * 并在try语句中调用传入的Lambda表达式中的代码，最终Lambda表达式的执行结果并调用emit()方法发射出去。
     */
    fun <T> getData(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                e.message?.let { ToastUtil.show(it) }
                Logger.i("getData--->>>e:" + e.message)
                Result.failure(e)
            }
            val t = result.getOrNull();
            Logger.i("getData=$t")
            if (t == null) {
                ToastUtil.show("获取数据失败,请稍后重试")
            }
            //通知数据变化
            emit(result)
        }
}