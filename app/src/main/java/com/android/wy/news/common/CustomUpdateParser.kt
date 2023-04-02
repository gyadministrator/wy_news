package com.android.wy.news.common

import com.xuexiang.xupdate.entity.UpdateEntity
import com.xuexiang.xupdate.listener.IUpdateParseCallback
import com.xuexiang.xupdate.proxy.IUpdateParser

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/1 16:13
  * @Version:        1.0
  * @Description:    
 */
class CustomUpdateParser : IUpdateParser {
    override fun parseJson(json: String?): UpdateEntity? {
        /*val result: CustomResult = JsonUtil.fromJson(json, CustomResult::class.java)
        return if (result != null) {
            UpdateEntity()
                .setHasUpdate(result.hasUpdate)
                .setIsIgnorable(result.isIgnorable)
                .setVersionCode(result.versionCode)
                .setVersionName(result.versionName)
                .setUpdateContent(result.updateLog)
                .setDownloadUrl(result.apkUrl)
                .setSize(result.apkSize)
        } else null*/
        return null
    }

    override fun parseJson(json: String?, callback: IUpdateParseCallback?) {

    }

    override fun isAsyncParser(): Boolean {
        return true
    }
}