package com.android.wy.news.dialog

import android.R
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.RingTypeDialogBinding
import com.android.wy.news.entity.music.MusicInfo


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/5 15:19
  * @Version:        1.0
  * @Description:    
 */
@SuppressLint("InflateParams")
class RingTypeDialog(private var musicInfo: MusicInfo) :
    BaseBottomSheetFragment<RingTypeDialogBinding>() {
    private var tvTitle: TextView? = null
    private var rlClose: RelativeLayout? = null
    private var rlCallRing: RelativeLayout? = null
    private var rlAlarmRing: RelativeLayout? = null
    private var rlNotifyRing: RelativeLayout? = null

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
        rlCallRing = mBinding.rlCall
        rlAlarmRing = mBinding.rlAlarm
        rlNotifyRing = mBinding.rlNotice
        rlClose?.setOnClickListener {
            dismiss()
        }
        rlCallRing?.setOnClickListener {
            dismiss()
            setCall()
        }
        rlAlarmRing?.setOnClickListener {
            dismiss()
            setAlarm()
        }
        rlNotifyRing?.setOnClickListener {
            dismiss()
            setNotice()
        }
    }

    private fun setNotice() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置通知铃声")
        startActivityForResult(intent, 3)
    }

    private fun setAlarm() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置闹钟铃声")
        startActivityForResult(intent, 2)
    }

    private fun setCall() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置来电铃声")
        startActivityForResult(intent, 1)
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onActivityResult(requestCode, resultCode, data)")
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        } else {
            val uri: Uri? = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (uri != null) {
                when (requestCode) {
                    1 -> RingtoneManager.setActualDefaultRingtoneUri(
                        activity,
                        RingtoneManager.TYPE_RINGTONE,
                        uri
                    )

                    2 -> RingtoneManager.setActualDefaultRingtoneUri(
                        activity,
                        RingtoneManager.TYPE_ALARM,
                        uri
                    )

                    3 -> RingtoneManager.setActualDefaultRingtoneUri(
                        activity,
                        RingtoneManager.TYPE_NOTIFICATION,
                        uri
                    )

                    else -> {}
                }
            }
        }
    }

    override fun getViewBinding(): RingTypeDialogBinding {
        return RingTypeDialogBinding.inflate(layoutInflater)
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