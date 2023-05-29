package com.android.wy.news.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


abstract class BaseBottomSheetFragment<V : ViewBinding> : BaseDialogFragment<V>() {

    override fun setFragmentStyle() {
        //使底部栏颜色更改
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.setOnShowListener {
                val bottomSheet =
                    dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                val bottomSheetBehavior: BottomSheetBehavior<*> =
                    BottomSheetBehavior.from(bottomSheet!!)
                //默认展开
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                //禁止向下拖动
                bottomSheetBehavior.isHideable = true
                bottomSheetBehavior.peekHeight = CommonTools.getScreenHeight()
                bottomSheetBehavior.addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        Logger.i("newState:$newState")
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        Logger.i("slideOffset:$slideOffset")
                    }
                })
            }
        }
        return dialog
    }
}