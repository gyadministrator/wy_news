package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.cache.DataCleanManager
import com.android.wy.news.common.*
import com.android.wy.news.databinding.ActivitySettingBinding
import com.android.wy.news.dialog.CommonConfirmDialog
import com.android.wy.news.dialog.CommonConfirmDialogFragment
import com.android.wy.news.entity.UpdateEntity
import com.android.wy.news.manager.DownloadController
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.update.OnUpdateManagerListener
import com.android.wy.news.update.UpdateManager
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.util.ToastUtil
import com.android.wy.news.viewmodel.SettingViewModel

@Route(path = RouteManager.PATH_ACTIVITY_SETTING)
class SettingActivity : BaseActivity<ActivitySettingBinding, SettingViewModel>() {
    private lateinit var tvVersion: TextView
    private lateinit var tvCache: TextView
    private lateinit var tvSkin: TextView
    private lateinit var rlCache: RelativeLayout
    private lateinit var rlSkin: RelativeLayout
    private lateinit var rlPrivacy: RelativeLayout
    private lateinit var rlUser: RelativeLayout
    private lateinit var rlThird: RelativeLayout
    private lateinit var rlPermission: RelativeLayout
    private lateinit var rlUpdate: RelativeLayout
    private lateinit var rlAbout: RelativeLayout
    private lateinit var rlAuthor: RelativeLayout
    private lateinit var scPlay: SwitchCompat
    private lateinit var scWifi: SwitchCompat
    private lateinit var scDesktopLrc: SwitchCompat
    private var intentActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var downloadAppUrl: String? = null

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

    override fun isFollowNightMode(): Boolean {
        return true
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
        rlThird = mBinding.rlThird
        rlPermission = mBinding.rlPermission
        rlUpdate = mBinding.rlUpdate
        scWifi = mBinding.scWifi
        rlAbout = mBinding.rlAbout
        rlAuthor = mBinding.rlAuthor
        scDesktopLrc = mBinding.scDesktopLrc
    }

    override fun onRestart() {
        super.onRestart()
        getSkinState()
    }

    override fun initData() {
        initActivityResult()
        getCacheSize()
        getVersion()
        getSkinState()
    }

    private fun getSkinState() {
        val isCacheVideo = SpTools.getBoolean(GlobalData.SpKey.CACHE_VIDEO)
        if (isCacheVideo != null) {
            scPlay.isChecked = isCacheVideo
        }

        val isNoWifiPlay = SpTools.getBoolean(GlobalData.SpKey.NO_WIFI_PLAY)
        if (isNoWifiPlay != null) {
            scWifi.isChecked = isNoWifiPlay
        }

        val skin = SpTools.getInt(SkinType.SKIN_TYPE)
        if (skin != null) {
            if (skin == SkinType.SKIN_TYPE_DARK) {
                tvSkin.text = "已打开"
            } else {
                tvSkin.text = "已关闭"
            }
        } else {
            tvSkin.text = "已关闭"
        }

        val isShowDesktopLrc = SpTools.getBoolean(GlobalData.SpKey.IS_SHOW_DESKTOP_LRC)
        if (isShowDesktopLrc != null) {
            scDesktopLrc.isChecked = isShowDesktopLrc
        }
    }

    private fun getCacheSize() {
        val cacheSize = DataCleanManager.getCacheSize(this)
        tvCache.text = cacheSize
    }

    @SuppressLint("SetTextI18n")
    private fun getVersion() {
        val versionName = CommonTools.getVersionName(this)
        tvVersion.text = "V$versionName"
    }

    override fun initEvent() {
        rlAuthor.setOnClickListener {
            WebActivity.startActivity(this, GlobalConstant.AUTHOR_URL)
        }
        rlAbout.setOnClickListener {
            AboutActivity.startAboutActivity(this)
        }
        rlUpdate.setOnClickListener {
            UpdateManager.checkUpdate(mActivity, onUpdateManagerListener)
        }
        rlPermission.setOnClickListener {
            PermissionActivity.startPermissionActivity(this)
        }
        rlThird.setOnClickListener {
            ThirdActivity.startThirdActivity(this)
        }
        rlSkin.setOnClickListener {
            SkinActivity.startSkinActivity(this)
        }
        rlPrivacy.setOnClickListener {
            WebActivity.startActivity(this, GlobalConstant.privacyUrl)
        }
        rlUser.setOnClickListener {
            WebActivity.startActivity(this, GlobalConstant.userUrl)
        }
        rlCache.setOnClickListener {
            CommonConfirmDialog.show(
                this,
                false,
                "温馨提示",
                "你确定要清除缓存吗？清除缓存后，之前缓存的视频将会被删除",
                "确定",
                "取消",
                object : CommonConfirmDialogFragment.OnDialogFragmentListener {
                    override fun onClickBtn(view: View, isClickSure: Boolean) {
                        if (isClickSure) {
                            clearCache()
                        }
                    }
                })
        }
        scPlay.setOnCheckedChangeListener(object : OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p0 != null) {
                    if (!p0.isPressed) {
                        return
                    }
                    SpTools.putBoolean(GlobalData.SpKey.CACHE_VIDEO, p1)
                }
            }
        })
        scWifi.setOnCheckedChangeListener(object : OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p0 != null) {
                    if (!p0.isPressed) {
                        return
                    }
                    SpTools.putBoolean(GlobalData.SpKey.NO_WIFI_PLAY, p1)
                }
            }
        })
        scDesktopLrc.setOnCheckedChangeListener(object : OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p0 != null) {
                    if (!p0.isPressed) {
                        return
                    }
                    SpTools.putBoolean(GlobalData.SpKey.IS_SHOW_DESKTOP_LRC, p1)
                }
            }
        })
    }

    private val onUpdateManagerListener = object : OnUpdateManagerListener {
        override fun onSuccess(s: String) {
            if (!TextUtils.isEmpty(s)) {
                try {
                    val dataList = JsonUtil.parseJsonToList<UpdateEntity>(s)
                    if (dataList.isNotEmpty()) {
                        val updateEntity: UpdateEntity = dataList[0]
                        showUpdateDialog(updateEntity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onError(msg: String) {
            ToastUtil.show(msg)
        }
    }

    private fun showUpdateDialog(updateEntity: UpdateEntity) {
        val versionCode = updateEntity.versionCode
        val code = CommonTools.getVersionCode(mActivity)
        if (versionCode <= code) {
            ToastUtil.show("当前已经是最新版本")
            return
        }
        downloadAppUrl = updateEntity.url
        CommonConfirmDialog.show(
            this,
            false,
            updateEntity.title,
            updateEntity.content,
            "下载",
            "忽略更新",
            object : CommonConfirmDialogFragment.OnDialogFragmentListener {
                override fun onClickBtn(view: View, isClickSure: Boolean) {
                    if (isClickSure) {
                        //NotificationHelper.sendProgressNotification(this@SettingActivity, 0, false)
                        //goDownload(Constants.TEST_APK_URL)
                        openSetting()
                    }
                }
            })
    }

    private fun initActivityResult() {
        intentActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                //开始下载安装
                start()
            }
        }
    }

    fun openSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Android 8.0以上
            if (!packageManager.canRequestPackageInstalls()) {
                //权限没有打开，跳转界面，提示用户去手动打开
                val intent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName")
                )
                intentActivityResultLauncher?.launch(intent)
            } else {
                //已经拥有权限，直接执行下载apk操作
                start()
            }
        } else {
            //开始下载安装
            start()
        }
    }


    private fun start() {
        val titleStr = "每日资讯新版本"
        val contentStr = "正在下载中，请耐心等待"
        //初始化版本控制
        DownloadController.download(this, downloadAppUrl, titleStr, contentStr)
        DownloadController.registerReceiver(this)
    }

    //停止执行版本更新操作
    private fun stop() {
        //初始化版本控制
        DownloadController.unRegisterReceiver(this)
    }

    override fun getViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): SettingViewModel {
        return CommonTools.getViewModel(this, SettingViewModel::class.java)
    }

    override fun onClear() {
        stop()
    }

    override fun onNotifyDataChanged() {
    }

    @SuppressLint("SetTextI18n")
    private fun clearCache() {
        DataCleanManager.clearIntExtCache(this)
        //重新获取一次缓存大小
        getCacheSize()
    }
}