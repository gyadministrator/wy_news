package com.android.wy.news.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutCommonConfirmDialogBinding


/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/6/15 13:22
  * @Version:        1.0
  * @Description:    
 */
class CommonConfirmDialogFragment private constructor() :
    BaseDialogFragment<LayoutCommonConfirmDialogBinding>() {
    private var tvTitle: TextView? = null
    private var tvContent: TextView? = null
    private var tvSure: TextView? = null
    private var tvCancel: TextView? = null
    private var dialogFragmentListener: OnDialogFragmentListener? = null

    private var titleText: String? = null
    private var contentText: String? = null
    private var sureText: String? = null
    private var cancelText: String? = null
    private var isSingleBtn: Boolean = false

    companion object {
        private const val DIALOG_SINGLE_BTN = "dialog_single_btn"
        private const val DIALOG_TITLE_TEXT = "dialog_title_text"
        private const val DIALOG_CONTENT_TEXT = "dialog_content_text"
        private const val DIALOG_SURE_TEXT = "dialog_sure_text"
        private const val DIALOG_CANCEL_TEXT = "dialog_cancel_text"

        fun newInstance(
            isSingleBtn: Boolean,
            titleText: String,
            contentText: String,
            sureText: String,
            cancelText: String
        ): CommonConfirmDialogFragment {
            val commonConfirmDialogFragment = CommonConfirmDialogFragment()
            val bundle = Bundle()
            bundle.putBoolean(DIALOG_SINGLE_BTN, isSingleBtn)
            bundle.putString(DIALOG_TITLE_TEXT, titleText)
            bundle.putString(DIALOG_CONTENT_TEXT, contentText)
            bundle.putString(DIALOG_SURE_TEXT, sureText)
            bundle.putString(DIALOG_CANCEL_TEXT, cancelText)
            commonConfirmDialogFragment.arguments = bundle
            return commonConfirmDialogFragment
        }
    }

    override fun initView() {
        tvTitle = mBinding.tvTitle
        tvContent = mBinding.tvContent
        tvSure = mBinding.tvSure
        tvCancel = mBinding.tvCancel
        setTitleText(titleText)
        setContentText(contentText)
        setSureText(sureText)
        setCancelText(cancelText)
        if (isSingleBtn) {
            tvCancel?.visibility = View.GONE
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        tvSure?.setOnClickListener {
            this.dismiss()
            dialogFragmentListener?.onClickBtn(it, true)
        }

        tvCancel?.setOnClickListener {
            this.dismiss()
            dialogFragmentListener?.onClickBtn(it, false)
        }
    }

    override fun getViewBinding(): LayoutCommonConfirmDialogBinding {
        return LayoutCommonConfirmDialogBinding.inflate(layoutInflater)
    }

    override fun onClear() {

    }

    override fun initIntent() {
        val arguments = arguments
        if (arguments != null) {
            isSingleBtn = arguments.getBoolean(DIALOG_SINGLE_BTN, false)
            titleText = arguments.getString(DIALOG_TITLE_TEXT, "")
            contentText = arguments.getString(DIALOG_CONTENT_TEXT, "")
            sureText = arguments.getString(DIALOG_SURE_TEXT, "确定")
            cancelText = arguments.getString(DIALOG_CANCEL_TEXT, "取消")
        }
    }

    override fun getLayoutHeight(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun getLayoutWidth(): Int {
        return (CommonTools.getScreenWidth() * 0.9).toInt()
    }

    override fun setFragmentStyle() {

    }

    override fun getGravityLocation(): Int {
        return Gravity.BOTTOM
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

    private fun setCancelText(cancelText: String?) {
        tvCancel?.text = cancelText
    }

    override fun isTouchDismiss(): Boolean {
        return false
    }

    fun addListener(onDialogFragmentListener: OnDialogFragmentListener) {
        this.dialogFragmentListener = onDialogFragmentListener
    }

    interface OnDialogFragmentListener {
        fun onClickBtn(view: View, isClickSure: Boolean)
    }
}