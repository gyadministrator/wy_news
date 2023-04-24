package com.android.wy.news.http.repository

import com.android.wy.news.http.NetworkRequest
import kotlinx.coroutines.Dispatchers

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 10:21
  * @Version:        1.0
  * @Description:    
 */
object HotRepository : BaseRepository() {
    /**
     * 这里我们调用父类的getData()函数，
     * 将liveData()函数的线程参数类型指定成了Dispatchers.IO，
     * 这样的代码块中的所有代码都是运行在子线程中，如果请求状态码是200，则表示成功，
     * 那么就使用Kotlin内置的Result.success()方法来包装获取的数据，
     * 然后就调用Result.failure()方法来包装一个异常信息。
     */
    fun getHotData() = getData(Dispatchers.IO) {
        val hotEntity = NetworkRequest.getHot()
        if (hotEntity.code == 0 || hotEntity.data != null) Result.success(hotEntity)
        else Result.failure(RuntimeException("getHot is error"))
    }
}