package com.android.wy.news.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.android.wy.news.common.IBaseDialogFragment

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/29 13:10
  * @Version:        1.0
  * @Description:    
 */
abstract class BaseDialogFragment<V : ViewBinding> : DialogFragment(), IBaseDialogFragment<V> {
    protected lateinit var mBinding: V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentStyle()
        initIntent()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss()
            }
            false
        }
        /*dialog?.window?.setDimAmount(0f) //设置透明度
        val viewRoot: FrameLayout =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        viewRoot.apply {
            layoutParams.width = -1
            layoutParams.height = getLayoutHeight()
        }*/
        val window = dialog?.window
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            window.setGravity(getGravityLocation())
            window.setLayout(getLayoutWidth(), getLayoutHeight())
            window.setWindowAnimations(com.android.wy.news.locationselect.R.style.DefaultCityPickerAnimation)
        }
        dialog?.setCanceledOnTouchOutside(isTouchDismiss())
        dialog?.setCancelable(isTouchDismiss())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //必须设置dialog的window背景为透明颜色，不然圆角无效或者是系统默认的颜色
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mBinding = getViewBinding()
        initView()
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onClear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initEvent()
    }
}