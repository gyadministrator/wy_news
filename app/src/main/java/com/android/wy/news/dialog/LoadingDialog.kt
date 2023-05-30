package com.android.wy.news.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutLoadingDialogBinding
import java.lang.ref.WeakReference

object LoadingDialog {
    private var loadingDialog: WeakReference<LoadingDialogFragment>? = null

    @SuppressLint("InflateParams")
    fun show(activity: FragmentActivity, title: String) {
        val loadingDialogFragment = LoadingDialogFragment()
        loadingDialog = WeakReference(loadingDialogFragment)
        val bundle = Bundle()
        bundle.putString(LoadingDialogFragment.TITLE_KEY, title)
        loadingDialogFragment.arguments = bundle
        loadingDialogFragment.show(
            activity.supportFragmentManager,
            "loading_dialog"
        )
    }

    fun hide() {
        if (loadingDialog != null) {
            val loadingDialogFragment = loadingDialog?.get()
            loadingDialogFragment?.dismiss()
            loadingDialog = null
        }
    }
}