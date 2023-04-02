package com.android.wy.news.http

import com.xuexiang.xupdate.proxy.IUpdateHttpService

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/1 15:59
  * @Version:        1.0
  * @Description:    
 */
class NewsHttpService:IUpdateHttpService {
    override fun asyncGet(
        url: String,
        params: MutableMap<String, Any>,
        callBack: IUpdateHttpService.Callback
    ) {

    }

    override fun asyncPost(
        url: String,
        params: MutableMap<String, Any>,
        callBack: IUpdateHttpService.Callback
    ) {

    }

    override fun download(
        url: String,
        path: String,
        fileName: String,
        callback: IUpdateHttpService.DownloadCallback
    ) {

    }

    override fun cancelDownload(url: String) {

    }
}