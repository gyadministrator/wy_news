package com.android.wy.news.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar


abstract class BaseBottomSheetFragment<V : ViewBinding> : BottomSheetDialogFragment() {
    protected lateinit var mBinding: V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //使底部栏颜色更改
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = getViewBinding()
        initView()
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNavigationBar()
        initData()
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
                bottomSheetBehavior.peekHeight=CommonTools.getScreenHeight()
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

    private fun hideNavigationBar() {
        ImmersionBar.with(this).statusBarColor(R.color.default_status_bar_color)
            .navigationBarColor(R.color.default_status_bar_color)
            .statusBarDarkFont(false).keyboardEnable(false).init()
    }


    abstract fun initView()

    abstract fun getViewBinding(): V

    abstract fun initData()
}