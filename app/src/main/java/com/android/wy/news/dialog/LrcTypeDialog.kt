package com.android.wy.news.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.wy.news.R
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.LrcType
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.LrcTypeDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gyf.immersionbar.ImmersionBar


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/5 15:19
  * @Version:        1.0
  * @Description:    
 */
@SuppressLint("InflateParams")
class LrcTypeDialog(context: Context, theme: Int) : BottomSheetDialog(context, theme) {
    private var tvTitle: TextView? = null
    private var ivLine: ImageView? = null
    private var ivWord: ImageView? = null
    private var rlClose: RelativeLayout? = null
    private var rlLine: RelativeLayout? = null
    private var rlWord: RelativeLayout? = null

    init {
        val view = layoutInflater.inflate(R.layout.lrc_type_dialog, null)
        val binding = LrcTypeDialogBinding.bind(view)
        initView(binding)
        this.setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }

    private fun initView(binding: LrcTypeDialogBinding) {
        tvTitle = binding.tvTitle
        ivLine = binding.ivLine
        ivWord = binding.ivWord
        rlClose = binding.rlClose
        rlLine = binding.rlLine
        rlWord = binding.rlWord
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

    override fun onStart() {
        super.onStart()
        setBarColor()
    }

    private fun setBarColor() {
        val mImmersionBar = ownerActivity?.let { ImmersionBar.with(it) }
        mImmersionBar?.navigationBarColor(R.color.default_status_bar_color)
        mImmersionBar?.init()
    }
}