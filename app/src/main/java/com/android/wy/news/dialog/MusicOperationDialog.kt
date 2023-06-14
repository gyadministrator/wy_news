package com.android.wy.news.dialog

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.MusicOperationDialogBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.listener.IDownloadListener


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/5 15:19
  * @Version:        1.0
  * @Description:    
 */
@SuppressLint("InflateParams")
class MusicOperationDialog(
    private var downloadListener: IDownloadListener,
    private var musicInfo: MusicInfo
) :
    BaseBottomSheetFragment<MusicOperationDialogBinding>() {
    private var tvTitle: TextView? = null
    private var rlClose: RelativeLayout? = null
    private var rlDownload: RelativeLayout? = null
    private var rlCancel: RelativeLayout? = null

    override fun getLayoutHeight(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun getLayoutWidth(): Int {
        return CommonTools.getScreenWidth()
    }

    override fun getGravityLocation(): Int {
        return Gravity.BOTTOM
    }

    override fun isTouchDismiss(): Boolean {
        return true
    }

    override fun initView() {
        tvTitle = mBinding.tvTitle
        rlClose = mBinding.rlClose
        rlDownload = mBinding.rlDownload
        rlCancel = mBinding.rlCancel
        rlClose?.setOnClickListener {
            dismiss()
        }
        rlCancel?.setOnClickListener {
            dismiss()
        }
        rlDownload?.setOnClickListener {
            downloadListener.goDownload(musicInfo)
            dismiss()
        }

        val stringBuilder = StringBuilder()
        val album = musicInfo.name
        val artist = musicInfo.artist
        stringBuilder.append(artist)
        if (!TextUtils.isEmpty(album)) {
            stringBuilder.append("-$album")
        }
        tvTitle?.text = stringBuilder.toString()
    }

    override fun getViewBinding(): MusicOperationDialogBinding {
        return MusicOperationDialogBinding.inflate(layoutInflater)
    }

    override fun onClear() {

    }

    override fun initIntent() {

    }

    override fun initData() {

    }

    override fun initEvent() {

    }
}