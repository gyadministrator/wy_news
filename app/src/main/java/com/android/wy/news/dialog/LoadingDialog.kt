package com.android.wy.news.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.android.wy.news.app.App
import com.android.wy.news.common.Logger
import com.android.wy.news.util.AppUtil

object LoadingDialog {
    private val dialogList = HashMap<String, LoadingDialogFragment>()

    @SuppressLint("InflateParams")
    fun show(tag: String, activity: FragmentActivity, title: String, isTouch: Boolean) {
        val background = AppUtil.isBackground(activity)
        if (background) return
        Logger.i("LoadingDialog--->>>show:$tag")
        val loadingDialogFragment = LoadingDialogFragment()
        if (!dialogList.containsKey(tag)) {
            dialogList[tag] = loadingDialogFragment
        }
        val bundle = Bundle()
        bundle.putString(LoadingDialogFragment.TITLE_KEY, title)
        bundle.putBoolean(LoadingDialogFragment.IS_TOUCH, isTouch)
        loadingDialogFragment.arguments = bundle
        val supportFragmentManager = activity.supportFragmentManager
        if (!supportFragmentManager.isDestroyed) {
            loadingDialogFragment.show(
                supportFragmentManager,
                "loading_dialog"
            )
        }
    }

    fun hide(tag: String) {
        Logger.i("LoadingDialog--->>>hide:$tag")
        val loadingDialogFragment = dialogList[tag]
        if (loadingDialogFragment != null) {
            loadingDialogFragment.dismiss()
            dialogList.remove(tag)
        }
    }
}