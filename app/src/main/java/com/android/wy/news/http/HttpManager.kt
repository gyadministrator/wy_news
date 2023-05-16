package com.android.wy.news.http

import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.common.GlobalData
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 9:43
  * @Version:        1.0
  * @Description:    
 */
class HttpManager {
    private var builder: OkHttpClient.Builder = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)

    companion object {
        const val TIMEOUT = 60L
        val mInstance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpManager()
        }
    }

    init {
        builder.proxy(Proxy.NO_PROXY)
    }

    fun <T> getMusicApiService(url: String, clazz: Class<T>): T {
        builder.addInterceptor { chain ->
            val request: Request = chain.request()
                .newBuilder()
                .addHeader("csrf", GlobalData.CSRF_TOKEN)
                .addHeader("cookie", "kw_token=" + GlobalData.CSRF_TOKEN)
                .build()
            chain.proceed(request)
        }
        val retrofit = Retrofit.Builder()
            .client(builder.build())
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(clazz)
    }

    fun <T> getApiService(url: String, clazz: Class<T>): T {
        val retrofit = Retrofit.Builder()
            .client(builder.build())
            .baseUrl(url)
            //.addConverterFactory(GsonConverterFactory.create())
            //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(clazz)
    }

    fun <T> getApiService(clazz: Class<T>): T {
        val retrofit = Retrofit.Builder()
            .client(builder.build())
            //.addConverterFactory(GsonConverterFactory.create())
            //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(clazz)
    }

    /*-------------------------------------------以下是协程相关-------------------------------------------------------*/
    private fun getRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(GlobalConstant.HOT_WORD_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun <T> create(serviceClass: Class<T>): T = getRetrofit().create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

    /*------------------------------------------以下是音乐相关-------------------------------------------------------*/
    fun <T> createMusic(serviceClass: Class<T>): T =
        getMusicApiService(GlobalConstant.MUSIC_BASE_URL, serviceClass)
}