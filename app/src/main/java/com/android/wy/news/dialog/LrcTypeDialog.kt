package com.android.wy.news.dialog

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.LrcType
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.LrcTypeDialogBinding


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/5 15:19
  * @Version:        1.0
  * @Description:    
 */
@SuppressLint("InflateParams")
class LrcTypeDialog : BaseBottomSheetFragment<LrcTypeDialogBinding>() {
    private var tvTitle: TextView? = null
    private var ivLine: ImageView? = null
    private var ivWord: ImageView? = null
    private var rlClose: RelativeLayout? = null
    private var rlLine: RelativeLayout? = null
    private var rlWord: RelativeLayout? = null

    override fun getLayoutHeight(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun getLayoutWidth(): Int {
        return CommonTools.getScreenWidth()
    }

    override fun getGravityLocation(): Int {
        return Gravity.BOTTOM
    }

    override fun initView() {
        tvTitle = mBinding.tvTitle
        ivLine = mBinding.ivLine
        ivWord = mBinding.ivWord
        rlClose = mBinding.rlClose
        rlLine = mBinding.rlLine
        rlWord = mBinding.rlWord
        rlClose?.setOnClickListener {
            dismiss()
        }
        rlLine?.setOnClickListener {
            SpTools.putInt(GlobalData.SpKey.LRC_TYPE, LrcType.LRC_TYPE_NORMAL)
            checkType()
            GlobalData.lrcTypeChange.postValue(LrcType.LRC_TYPE_NORMAL)
            dismiss()
        }
        rlWord?.setOnClickListener {
            SpTools.putInt(GlobalData.SpKey.LRC_TYPE, LrcType.LRC_TYPE_KALAOK)
            checkType()
            GlobalData.lrcTypeChange.postValue(LrcType.LRC_TYPE_KALAOK)
            dismiss()
        }
        checkType()
    }

    override fun getViewBinding(): LrcTypeDialogBinding {
        return LrcTypeDialogBinding.inflate(layoutInflater)
    }

    override fun onClear() {

    }

    override fun initIntent() {

    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    private fun checkType() {
        val lrcType = SpTools.getInt(GlobalData.SpKey.LRC_TYPE)
        if (lrcType == LrcType.LRC_TYPE_KALAOK) {
            ivLine?.visibility = View.GONE
            ivWord?.visibility = View.VISIBLE
        } else {
            ivLine?.visibility = View.VISIBLE
            ivWord?.visibility = View.GONE
        }
    }
}