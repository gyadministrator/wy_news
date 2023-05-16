package com.android.wy.news.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.android.wy.news.R
import com.android.wy.news.activity.WebActivity
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.databinding.LayoutDialogBinding


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/11 13:44
  * @Version:        1.0
  * @Description:    
 */
class PrivacyDialogFragment : DialogFragment() {
    private var tvTitle: TextView? = null
    private var tvContent: TextView? = null
    private var tvSure: TextView? = null
    private var tvCancel: TextView? = null
    private var dialogFragmentListener: OnDialogFragmentListener? = null

    private var titleText: String? = null
    private var contentText: SpannableStringBuilder? = null
    private var sureText: String? = null
    private var cancelText: String? = null

    companion object {
        fun newInstance(): PrivacyDialogFragment {
            return PrivacyDialogFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val s = activity?.getString(R.string.app_name)
        val spannable =
            SpannableStringBuilder("亲，感谢您对\"${s}\"一直以来的信任！我们依据最新的监管要求更新了\"${s}\"软件\n《隐私政策》和《用户协议》，特向广大用户说明如下信息\n1.为向您提供相关基本功能，我们会收集、使用必要的信息；\n2.基于您的明示授权，我们可能会获取您的位置（为您提供附近城市的新闻、视频及资讯等）等信息，您有权拒绝或取消授权；\n3.我们会采取业界先进的安全措施保护您的信息安全；\n4.未经您同意，我们不会从第三方处获取、共享或向提供您的信息；\n")
        //隐私政策
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(p0: View) {
                activity?.let { WebActivity.startActivity(it, GlobalConstant.privacyUrl) }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }, 42, 49, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(
            ForegroundColorSpan(Color.BLUE), 42, 49, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //用户协议
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(p0: View) {
                activity?.let { WebActivity.startActivity(it, GlobalConstant.userUrl) }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }, 50, 56, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(
            ForegroundColorSpan(Color.BLUE), 50, 56, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        titleText = "温馨提示"
        contentText = spannable
        sureText = "同意"
        cancelText = "不同意"
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //必须设置dialog的window背景为透明颜色，不然圆角无效或者是系统默认的颜色
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = layoutInflater.inflate(R.layout.layout_dialog, null)
        val binding = LayoutDialogBinding.bind(view)
        initView(binding)
        setTitleText(titleText)
        setContentText(contentText)
        setSureText(sureText)
        setCancelText(cancelText)
        return view
    }

    private fun initView(binding: LayoutDialogBinding) {
        tvTitle = binding.tvTitle
        tvContent = binding.tvContent
        tvSure = binding.tvSure
        tvCancel = binding.tvCancel
        this.dialog?.setCanceledOnTouchOutside(false)
        this.dialog?.setCancelable(false)
        //设置该句使文本的超连接起作用
        tvContent?.movementMethod = LinkMovementMethod.getInstance()

        tvSure?.setOnClickListener {
            this.dismiss()
            dialogFragmentListener?.onClickSure()
        }

        tvCancel?.setOnClickListener {
            this.dismiss()
            dialogFragmentListener?.onClickCancel()
        }
    }

    private fun setTitleText(titleText: String?) {
        tvTitle?.text = titleText
    }

    private fun setContentText(contentText: SpannableStringBuilder?) {
        tvContent?.text = contentText
    }

    private fun setSureText(sureText: String?) {
        tvSure?.text = sureText
    }

    private fun setCancelText(cancelText: String?) {
        tvCancel?.text = cancelText
    }

    fun addListener(onDialogFragmentListener: OnDialogFragmentListener) {
        this.dialogFragmentListener = onDialogFragmentListener
    }

    interface OnDialogFragmentListener {
        fun onClickSure()

        fun onClickCancel()
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