package com.android.wy.news.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference


/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/6/15 13:21
  * @Version:        1.0
  * @Description:    
 */
object CommonConfirmDialog {
    fun show(
        activity: FragmentActivity,
        isSingleBtn: Boolean,
        titleText: String,
        contentText: String,
        sureText: String,
        cancelText: String,
        listener: CommonConfirmDialogFragment.OnDialogFragmentListener
    ) {
        val confirmDialogFragment = CommonConfirmDialogFragment.newInstance(
            isSingleBtn, titleText, contentText, sureText, cancelText
        )
        confirmDialogFragment.addListener(listener)
        val supportFragmentManager = activity.supportFragmentManager
        if (!supportFragmentManager.isDestroyed) {
            confirmDialogFragment.show(
                supportFragmentManager,
                "common_confirm__dialog"
            )
        }
    }
}