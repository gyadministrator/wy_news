package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.view.View
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.android.wy.news.cache.CacheUtils
import com.android.wy.news.common.*
import com.android.wy.news.databinding.ActivitySettingBinding
import com.android.wy.news.viewmodel.SettingViewModel


class SettingActivity : BaseActivity<ActivitySettingBinding, SettingViewModel>(),
    View.OnClickListener {
    private lateinit var tvVersion: TextView
    private lateinit var tvCache: TextView
    private lateinit var tvSkin: TextView
    private lateinit var rlCache: RelativeLayout
    private lateinit var rlSkin: RelativeLayout
    private lateinit var rlPrivacy: RelativeLayout
    private lateinit var rlUser: RelativeLayout
    private lateinit var scPlay: SwitchCompat

    companion object {
        fun startSettingActivity(context: Context) {
            val intent = Intent(context, SettingActivity::class.java)
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

    override fun initView() {
        tvVersion = mBinding.tvVersion
        tvCache = mBinding.tvCache
        tvSkin = mBinding.tvSkin
        rlCache = mBinding.rlCache
        rlSkin = mBinding.rlSkin
        scPlay = mBinding.scPlay
        rlPrivacy = mBinding.rlPrivacy
        rlUser = mBinding.rlUser
    }

    override fun onRestart() {
        super.onRestart()
        getSkinState()
    }

    override fun initData() {
        getVersion()
        getSkinState()
    }

    private fun getSkinState() {
        val play = SpTools.getBoolean(Constants.PLAY_DOWNLOAD)
        val skin = SpTools.getInt(SkinType.SKIN_TYPE)
        if (play != null) {
            scPlay.isChecked = play
        }
        if (skin != null) {
            if (skin == SkinType.SKIN_TYPE_DARK) {
                tvSkin.text = "已打开"
            } else {
                tvSkin.text = "已关闭"
            }
        } else {
            tvSkin.text = "已关闭"
        }
    }

    /*
    * 获取SD卡根目录：Environment.getExternalStorageDirectory().getAbsolutePath();
        外部Cache路径：/mnt/sdcard/android/data/com.xxx.xxx/cache 一般存储缓存数据（注：通过getExternalCacheDir()获取）
        外部File路径：/mnt/sdcard/android/data/com.xxx.xxx/files 存储长时间存在的数据
        （注：通过getExternalFilesDir(String type)获取， type为特定类型，可以是以下任何一种
                    Environment.DIRECTORY_MUSIC,
                    Environment.DIRECTORY_PODCASTS,
                     Environment.DIRECTORY_RINGTONES,
                     Environment.DIRECTORY_ALARMS,
                     Environment.DIRECTORY_NOTIFICATIONS,
                     Environment.DIRECTORY_PICTURES,
                      Environment.DIRECTORY_MOVIES. ）
    * */
    private fun initCacheSize() {
        try {
            Environment.getExternalStorageDirectory().absolutePath
            val outCachePath = externalCacheDir
            val outFilePath = getExternalFilesDir(Environment.DIRECTORY_ALARMS)
            val outCacheSize: String = outCachePath?.let { CacheUtils.getCacheSize(it) }.toString()
            outFilePath?.let { CacheUtils.getCacheSize(it) }.toString()
            tvCache.text = outCacheSize
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getVersion() {
        val versionName = CommonTools.getVersionName(this)
        val versionCode = CommonTools.getVersionCode(this)
        tvVersion.text = versionCode.toString() + "_V" + versionName
    }

    override fun initEvent() {
        rlSkin.setOnClickListener {
            SkinActivity.startSkinActivity(this)
        }
        rlPrivacy.setOnClickListener {
            WebActivity.startActivity(this, Constants.privacyUrl)
        }
        rlUser.setOnClickListener {
            WebActivity.startActivity(this, Constants.userUrl)
        }
        rlCache.setOnClickListener(this)
        scPlay.setOnCheckedChangeListener(object : OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p0 != null) {
                    if (!p0.isPressed) {
                        return
                    }
                    SpTools.putBoolean(Constants.PLAY_DOWNLOAD, p1)
                }
            }
        })
    }

    override fun getViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): SettingViewModel {
        return CommonTools.getViewModel(this, SettingViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            if (p0 == rlCache) {
                clearCache()
            }
        }
    }

    private fun clearCache() {
        CacheUtils.cleanExternalCache(this)
        //重新获取一次缓存大小
        initCacheSize()
    }

}