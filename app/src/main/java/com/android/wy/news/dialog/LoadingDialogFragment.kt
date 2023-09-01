package com.android.wy.news.dialog

import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutLoadingDialogBinding
import com.android.wy.news.view.LoadingView

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/30 11:10
  * @Version:        1.0
  * @Description:    
 */
class LoadingDialogFragment : BaseDialogFragment<LayoutLoadingDialogBinding>() {
    private var loadingView: LoadingView? = null
    private var tvTitle: TextView? = null
    private var title:String?=null

    companion object {
        const val TITLE_KEY = "title_key"
    }

    override fun initView() {
        loadingView = mBinding.loadingView
        tvTitle = mBinding.tvTitle

        if (!TextUtils.isEmpty(title)) {
            tvTitle?.text = title
        }
        loadingView?.startLoadingAnim()
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    override fun getViewBinding(): LayoutLoadingDialogBinding {
        return LayoutLoadingDialogBinding.inflate(layoutInflater)
    }

    override fun onClear() {
        loadingView?.stopLoadingAnim()
    }

    override fun initIntent() {
        title = arguments?.getString(TITLE_KEY)
    }

    override fun getLayoutHeight(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun getLayoutWidth(): Int {
        return (CommonTools.getScreenWidth() * 0.5).toInt()
    }

    override fun setFragmentStyle() {

    }

    override fun getGravityLocation(): Int {
        return Gravity.CENTER
    }

    override fun isTouchDismiss(): Boolean {
        return true
    }
}