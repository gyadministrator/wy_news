package com.android.wy.news.http

import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.common.GlobalData
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/3/17 9:43
  * @Version:        1.0
  * @Description:    
 */
class HttpManager {
    private var builder: OkHttpClient.Builder = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)//默认重试一次，若需要重试N次，则要实现拦截器。
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

    private fun <T> getMusicApiService(clazz: Class<T>): T {
        val musicHeaders = CommonTools.getMusicHeaders()
        builder.addInterceptor { chain ->
            val request: Request = chain.request()
                .newBuilder()
                .headers(musicHeaders)
                .build()
            chain.proceed(request)
        }
        val retrofit = Retrofit.Builder()
            .client(builder.build())
            .baseUrl(GlobalConstant.MUSIC_BASE_URL)
            .addConverterFactory(NullEmptyConverterFactory())
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

    fun <T> getApiGsonService(url: String, clazz: Class<T>): T {
        val retrofit = Retrofit.Builder()
            .client(builder.build())
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(clazz)
    }

    fun <T> getTestApiService(url: String, clazz: Class<T>): T {
        val musicHeader = GlobalData.musicHeader
        builder.addInterceptor { chain ->
            val header = chain.request()
                .newBuilder()
            //.addHeader("Accept", "application/json, text/plain, */*")
            //.addHeader("Accept-Encoding", "gzip, deflate")
            //.addHeader("Accept-Language", "en-US,en;q=0.9")
            //.addHeader("Connection", "keep-alive")
            /*.addHeader(
                "Cookie",
                "_ga=GA1.2.1097867330.1688692040; _gid=GA1.2.527365140.1692754772; Hm_lvt_cdb524f42f0ce19b169a8071123a4797=1692754772,1692840272; Hm_lpvt_cdb524f42f0ce19b169a8071123a4797=1692844674; _ga_ETPBRPM9ML=GS1.2.1692843852.7.1.1692844675.57.0.0; Hm_Iuvt_cdb524f42f0cer9b268e4v7y734w5esq24=Amw8eXExKca8yPtiknQbXsQwPiMxj2ty"
            )
            //.addHeader("Host", "www.kuwo.cn")
            //.addHeader("Referer", "http://www.kuwo.cn/rankList")
            .addHeader(
                "Secret",
                "61d468f39ca361123093b36890c97bc2718f6d7d6f14203b932c42eb40c41c16027cf490"
            )*/
            for ((k, v) in musicHeader) {
                header.addHeader(k, v)
            }
            //.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
            //.build()
            val request = header.build()
            chain.proceed(request)
        }
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
        getMusicApiService(serviceClass)
}