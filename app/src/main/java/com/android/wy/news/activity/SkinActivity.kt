package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.SwitchCompat
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.SkinType
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.ActivitySkinBinding
import com.android.wy.news.skin.UiModeManager
import com.android.wy.news.viewmodel.SkinViewModel

class SkinActivity : BaseActivity<ActivitySkinBinding, SkinViewModel>(), View.OnClickListener {
    private lateinit var scSkin: SwitchCompat
    private lateinit var rlLight: RelativeLayout
    private lateinit var ivLight: ImageView
    private lateinit var rlDark: RelativeLayout
    private lateinit var llContent: LinearLayout
    private lateinit var ivDark: ImageView

    companion object {
        fun startSkinActivity(context: Context) {
            val intent = Intent(context, SkinActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun setDefaultImmersionBar(): Boolean {
        return true
    }

    override fun hideStatusBar(): Boolean {
        return false
    }

    override fun hideNavigationBar(): Boolean {
        return false
    }

    override fun isFollowNightMode(): Boolean {
        return true
    }

    override fun initView() {
        scSkin = mBinding.scSkin
        rlLight = mBinding.rlLight
        ivLight = mBinding.ivLight
        rlDark = mBinding.rlDark
        ivDark = mBinding.ivDark
        llContent = mBinding.llContent

        scSkin.setOnCheckedChangeListener { p0, p1 ->
            if (p0 != null) {
                if (p1) {
                    ivLight.visibility = View.GONE
                    ivDark.visibility = View.GONE
                    llContent.visibility = View.GONE

                    UiModeManager.setCurrentUiMode(SkinType.SKIN_TYPE_SYSTEM)
                } else {
                    rlLight.visibility = View.VISIBLE
                    rlDark.visibility = View.VISIBLE
                    llContent.visibility = View.VISIBLE

                    UiModeManager.setCurrentUiMode(SkinType.SKIN_TYPE_LIGHT)
                    initData()
                }
            }
        }

        rlLight.setOnClickListener(this)
        rlDark.setOnClickListener(this)
    }

    override fun initData() {
        when (SpTools.getInt(SkinType.SKIN_TYPE)) {
            SkinType.SKIN_TYPE_SYSTEM -> {
                scSkin.isChecked = true
                ivLight.visibility = View.GONE
                ivDark.visibility = View.GONE
                llContent.visibility = View.GONE
            }

            SkinType.SKIN_TYPE_LIGHT -> {
                ivLight.visibility = View.VISIBLE
                ivDark.visibility = View.GONE
            }

            SkinType.SKIN_TYPE_DARK -> {
                ivLight.visibility = View.GONE
                ivDark.visibility = View.VISIBLE
            }

            else -> {
                ivLight.visibility = View.VISIBLE
                ivDark.visibility = View.GONE
            }
        }
    }

    override fun initEvent() {
    }

    override fun getViewBinding(): ActivitySkinBinding {
        return ActivitySkinBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): SkinViewModel {
        return CommonTools.getViewModel(this, SkinViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            if (p0 == rlLight) {
                ivLight.visibility = View.VISIBLE
                ivDark.visibility = View.GONE
                scSkin.isChecked = false

                UiModeManager.setCurrentUiMode(SkinType.SKIN_TYPE_LIGHT)
            } else if (p0 == rlDark) {
                ivLight.visibility = View.GONE
                ivDark.visibility = View.VISIBLE
                scSkin.isChecked = false

                UiModeManager.setCurrentUiMode(SkinType.SKIN_TYPE_DARK)
            }
        }
    }

}