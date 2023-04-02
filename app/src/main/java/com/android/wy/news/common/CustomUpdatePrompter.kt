package com.android.wy.news.common

import android.content.Context
import android.content.DialogInterface
import com.xuexiang.xupdate.entity.PromptEntity
import com.xuexiang.xupdate.entity.UpdateEntity
import com.xuexiang.xupdate.proxy.IUpdatePrompter
import com.xuexiang.xupdate.proxy.IUpdateProxy
import com.xuexiang.xupdate.service.OnFileDownloadListener
import com.xuexiang.xupdate.utils.UpdateUtils
import java.io.File


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/1 16:17
  * @Version:        1.0
  * @Description:    
 */
class CustomUpdatePrompter(context: Context?) : IUpdatePrompter {
    private var mContext: Context? = context

    override fun showPrompt(
        updateEntity: UpdateEntity,
        updateProxy: IUpdateProxy,
        promptEntity: PromptEntity
    ) {
        showUpdatePrompt(updateEntity, updateProxy)
    }

    /**
     * Show custom version update prompter
     *
     * @param updateEntity
     * @param updateProxy
     */
    private fun showUpdatePrompt(updateEntity: UpdateEntity, updateProxy: IUpdateProxy) {
       /* val updateInfo = UpdateUtils.getDisplayUpdateInfo(mContext, updateEntity)
        Builder(mContext)
            .setTitle(String.format("是否升级到%s版本？", updateEntity.versionName))
            .setMessage(updateInfo)
            .setPositiveButton("升级", DialogInterface.OnClickListener { dialog, which ->
                updateProxy.startDownload(updateEntity, object : OnFileDownloadListener {
                    override fun onStart() {
                        HProgressDialogUtils.showHorizontalProgressDialog(
                            mContext,
                            "下载进度",
                            false
                        )
                    }

                    override fun onProgress(progress: Float, total: Long) {
                        HProgressDialogUtils.setProgress(Math.round(progress * 100))
                    }

                    override fun onCompleted(file: File): Boolean {
                        HProgressDialogUtils.cancel()
                        return true
                    }

                    override fun onError(throwable: Throwable) {
                        HProgressDialogUtils.cancel()
                    }
                })
            })
            .setNegativeButton("暂不升级", null)
            .setCancelable(false)
            .create()
            .show()*/
    }
}