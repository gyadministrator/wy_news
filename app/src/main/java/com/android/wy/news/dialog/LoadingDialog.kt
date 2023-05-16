package com.android.wy.news.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutLoadingDialogBinding

object LoadingDialog {
    private var dialog: AlertDialog? = null

    @SuppressLint("InflateParams")
    fun show(context: Context, title: String) {
        val builder = AlertDialog.Builder(context)
        dialog = builder.create()
        val view = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null)
        val binding = LayoutLoadingDialogBinding.bind(view)
        val loadingView = binding.loadingView
        val tvTitle = binding.tvTitle
        tvTitle.text = title
        dialog?.show()
        dialog?.setContentView(view)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        //设置dialog背景透明
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val screenWidth = CommonTools.getScreenWidth()
        //设置布局宽高
        dialog?.window?.setLayout(screenWidth * 2 / 5, screenWidth * 2 / 5)
        loadingView.startLoadingAnim()
    }

    fun hide() {
        if (dialog != null && dialog!!.isShowing) {
            dialog?.dismiss()
            dialog = null
        }
    }
}