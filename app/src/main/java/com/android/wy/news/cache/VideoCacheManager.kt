package com.android.wy.news.cache

import android.content.Context
import android.text.TextUtils
import com.android.wy.news.common.Constants
import com.android.wy.news.common.SpTools
import com.danikula.videocache.HttpProxyCacheServer

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/27 14:39
  * @Version:        1.0
  * @Description:    
 */
class VideoCacheManager {
    companion object {
        private var proxy: HttpProxyCacheServer? = null

        fun getProxyUrl(context: Context, videoNetUrl: String?): String? {
            var proxyUrl: String? = videoNetUrl
            val isCacheVideo = SpTools.getBoolean(Constants.CACHE_VIDEO)
            if (isCacheVideo != null && isCacheVideo == true) {
                getProxy(context)
                if (!TextUtils.isEmpty(videoNetUrl)) {
                    proxyUrl = proxy?.getProxyUrl(videoNetUrl)
                }
            }
            return proxyUrl
        }

        private fun getProxy(context: Context) {
            if (proxy == null) {
                proxy = newProxy(context.applicationContext)
            }
        }

        private fun newProxy(context: Context): HttpProxyCacheServer {
            return HttpProxyCacheServer.Builder(context)
                .maxCacheSize(1024 * 1024 * 1024)//缓存空间
                .maxCacheFilesCount(10)//最大文件数
                .build()
        }
    }
}