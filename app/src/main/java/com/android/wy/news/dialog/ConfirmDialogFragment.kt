package com.android.wy.news.dialog

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutDialogBinding


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/11 13:44
  * @Version:        1.0
  * @Description:    
 */
class ConfirmDialogFragment : BaseDialogFragment<LayoutDialogBinding>() {
    private var tvTitle: TextView? = null
    private var tvContent: TextView? = null
    private var tvSure: TextView? = null
    private var tvCancel: TextView? = null
    private var dialogFragmentListener: OnDialogFragmentListener? = null

    private var titleText: String? = null
    private var contentText: String? = null
    private var sureText: String? = null
    private var cancelText: String? = null

    companion object {
        private const val DIALOG_TITLE = "dialog_title"
        private const val DIALOG_CONTENT = "dialog_content"
        private const val DIALOG_SURE = "dialog_sure"
        private const val DIALOG_CANCEL = "dialog_cancel"

        fun newInstance(
            titleText: String?, contentText: String, sureText: String?, cancelText: String?
        ): ConfirmDialogFragment {
            val dialogFragment = ConfirmDialogFragment()
            val bundle = Bundle()
            bundle.putString(DIALOG_TITLE, titleText)
            bundle.putString(DIALOG_CONTENT, contentText)
            bundle.putString(DIALOG_SURE, sureText)
            bundle.putString(DIALOG_CANCEL, cancelText)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

    override fun initView() {
        tvTitle = mBinding.tvTitle
        tvContent = mBinding.tvContent
        tvSure = mBinding.tvSure
        tvCancel = mBinding.tvCancel

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

        setTitleText(titleText)
        setContentText(contentText)
        setSureText(sureText)
        setCancelText(cancelText)
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    override fun getViewBinding(): LayoutDialogBinding {
        return LayoutDialogBinding.inflate(layoutInflater)
    }

    override fun onClear() {

    }

    override fun initIntent() {
        val bundle = arguments
        titleText = bundle?.getString(DIALOG_TITLE)
        contentText = bundle?.getString(DIALOG_CONTENT)
        sureText = bundle?.getString(DIALOG_SURE)
        cancelText = bundle?.getString(DIALOG_CANCEL)
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

    override fun isTouchDismiss(): Boolean {
        return false
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

    fun addListener(onDialogFragmentListener: OnDialogFragmentListener) {
        this.dialogFragmentListener = onDialogFragmentListener
    }

    interface OnDialogFragmentListener {
        fun onClickSure()

        fun onClickCancel()
    }
}