package com.android.wy.news.http

import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.util.ToastUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 10:10
  * @Version:        1.0
  * @Description:    这里简化了Retrofit回调的写法，
  * 这里定义了一个await()函数，它是一个挂起函数，
  * 我们给它声明了一个泛型T，并将await()函数定义成了Call< T >的扩展函数，
  * 这样所有返回值是Call类型的Retrofit网络请求接口都可以直接调用await()函数了。
  * 接着，await()函数中使用了suspendCoroutine函数来挂起当前协程，
  * 并且由于扩展函数的原因，我们现在拥有了Call对象的上下文，
  * 那么这里就可以直接调用enqueue()方法让Retrofit发起网络请求。
 */
object NetworkRequest {
    /**
     * 创建服务
     */
    private val service = HttpManager.mInstance.create(IApiService::class.java)

    //通过await()getHot()函数也声明成挂起函数。使用协程
    suspend fun getHot() = service.getSuspendHot().await()

    /*---------------------------------以下是音乐相关----------------------------------*/
    private val musicService = HttpManager.mInstance.createMusic(IApiService::class.java)
    suspend fun getMusicList(map: MutableMap<String, Any>) =
        musicService.getMusicList(map).await()

    suspend fun getMusicUrl(map: MutableMap<String, Any>) =
        musicService.getMusicUrl(map).await()

    suspend fun getMusicMv(map: MutableMap<String, Any>) =
        musicService.getMusicUrl(map).await()

    suspend fun getRecommendMusic(map: MutableMap<String, Any>) =
        musicService.getRecommendMusic(map).await()

    suspend fun getRecommendMusicType(map: MutableMap<String, Any>) =
        musicService.getRecommendMusicType(map).await()

    suspend fun getMusicByKey(map: MutableMap<String, Any>) =
        musicService.getMusicByKey(map).await()

    suspend fun getMusicLrc(map: MutableMap<String, Any>) =
        musicService.getMusicLrc(map).await()

    suspend fun getArtistMusic(map: MutableMap<String, Any>) =
        musicService.getArtistMusic(map).await()

    suspend fun getArtistAlbum(map: MutableMap<String, Any>) =
        musicService.getArtistAlbum(map).await()

    suspend fun getArtistMv(map: MutableMap<String, Any>) =
        musicService.getArtistMv(map).await()

    suspend fun getAlbumInfo(map: MutableMap<String, Any>) =
        musicService.getAlbumInfo(map).await()

    suspend fun getPopByType(map: MutableMap<String, Any>) =
        musicService.getPopByType(map).await()

    /**
     * Retrofit网络返回处理
     */
    private suspend fun <T> Call<T>.await(): T = suspendCoroutine {
        enqueue(object : Callback<T> {
            //正常返回
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                Logger.i("onResponse=$body")
                if (body != null) it.resume(body)
                else it.resumeWithException(RuntimeException("response body is null"))
            }

            //异常返回
            override fun onFailure(call: Call<T>, t: Throwable) {
                it.resumeWithException(t)
                t.message?.let { it1 -> ToastUtil.show(it1) }
                Logger.e("onFailure=" + t.message)
            }
        })
    }
}