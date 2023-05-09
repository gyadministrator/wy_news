package com.android.wy.news.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutTipDialogBinding


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/11 13:44
  * @Version:        1.0
  * @Description:    
 */
class TipDialogFragment : DialogFragment() {
    private var tvTitle: TextView? = null
    private var tvContent: TextView? = null
    private var tvSure: TextView? = null
    private var dialogFragmentListener: OnDialogFragmentListener? = null

    private var titleText: String? = null
    private var contentText: String? = null
    private var sureText: String? = null

    companion object {
        private const val DIALOG_TITLE = "dialog_title"
        private const val DIALOG_CONTENT = "dialog_content"
        private const val DIALOG_SURE = "dialog_sure"

        fun newInstance(
            titleText: String?, contentText: String, sureText: String?
        ): TipDialogFragment {
            val dialogFragment = TipDialogFragment()
            val bundle = Bundle()
            bundle.putString(DIALOG_TITLE, titleText)
            bundle.putString(DIALOG_CONTENT, contentText)
            bundle.putString(DIALOG_SURE, sureText)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        titleText = bundle?.getString(DIALOG_TITLE)
        contentText = bundle?.getString(DIALOG_CONTENT)
        sureText = bundle?.getString(DIALOG_SURE)
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //必须设置dialog的window背景为透明颜色，不然圆角无效或者是系统默认的颜色
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = layoutInflater.inflate(R.layout.layout_tip_dialog, null)
        val binding = LayoutTipDialogBinding.bind(view)
        initView(binding)
        setTitleText(titleText)
        setContentText(contentText)
        setSureText(sureText)
        return view
    }

    private fun initView(binding: LayoutTipDialogBinding) {
        tvTitle = binding.tvTitle
        tvContent = binding.tvContent
        tvSure = binding.tvSure
        this.dialog?.setCanceledOnTouchOutside(false)
        this.dialog?.setCancelable(false)
        //设置该句使文本的超连接起作用
        tvContent?.movementMethod = LinkMovementMethod.getInstance()

        tvSure?.setOnClickListener {
            this.dismiss()
            dialogFragmentListener?.onClickSure()
        }
    }

    private fun setTitleText(titleText: String?) {
        tvTitle?.text = titleText
    }

    private fun setContentText(contentText: String?) {
        tvContent?.text = contentText
    }

    private fun setSureText(sureText: String?) {
        tvSure?.text = sureText
    }

    fun addListener(onDialogFragmentListener: OnDialogFragmentListener) {
        this.dialogFragmentListener = onDialogFragmentListener
    }

    interface OnDialogFragmentListener {
        fun onClickSure()
    }

    override fun onStart() {
        super.onStart()
        resizeDialogFragment()
    }

    private fun resizeDialogFragment() {
        val dialog = dialog
        if (null != dialog) {
            val window = dialog.window
            val lp = window?.attributes
            val screenWidth = CommonTools.getScreenWidth()
            if (lp != null) {
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                lp.width = (screenWidth * 0.9).toInt()
                window.setLayout(lp.width, lp.height)
            }
        }

    }
}