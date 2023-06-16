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
import com.gyf.immersionbar.ImmersionBar


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
        if (isTouchDismiss()) {
            dialog?.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss()
                }
                false
            }
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
        //Android 全屏界面 DialogFragment 弹出时底部导航栏闪烁
        //这样弹出来的DialogFragment就不会获取到系统焦点,也就不会显示底部导航拉了
        //但是这会引起一个问题, 就是点击DialogFragment的周围,他不会自动dismiss消失
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        //在DialogFragment显示后,去掉这个FLAG_NOT_FOCUSABLE.去掉这个标志后,
        // DialogFragment重新获取到焦点,所以点击周围可以关闭掉他.
        // 同时,底部导航栏这个时候又会显示出来, 所以要在DialogFragment显示的onShow()的时候,
        // 再次调用设置全屏的方法,设置为全屏.
        dialog?.setOnShowListener {
            dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            //清除FLAG后,部分手机会再次显示底部导航栏,所以需要再次设置为全屏
            ImmersionBar.with(this).fullScreen(true).init()
        }
        initData()
        initEvent()
    }
}