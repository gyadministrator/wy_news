package com.android.wy.news.http

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.Proxy
import java.util.concurrent.TimeUnit

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 9:43
  * @Version:        1.0
  * @Description:    
 */
class HttpManager {
    private var httpClient: OkHttpClient

    companion object {
        const val TIMEOUT = 60L
        val mInstance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpManager()
        }
    }

    init {
        val builder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        builder.proxy(Proxy.NO_PROXY)
        httpClient = builder.build()
    }

    fun <T> getApiService(url: String, clazz: Class<T>): T {
        val retrofit = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(url)
            //.addConverterFactory(GsonConverterFactory.create())
            //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(clazz)
    }
}