package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.android.wy.news.cache.DataCleanManager
import com.android.wy.news.common.*
import com.android.wy.news.compose.ThirdInfoActivity
import com.android.wy.news.databinding.ActivitySettingBinding
import com.android.wy.news.dialog.ConfirmDialogFragment
import com.android.wy.news.dialog.UpdateDialogFragment
import com.android.wy.news.notification.NotificationHelper
import com.android.wy.news.viewmodel.SettingViewModel
import java.io.IOException

import java.io.InputStream

import java.net.HttpURLConnection

import java.net.URL


class SettingActivity : BaseActivity<ActivitySettingBinding, SettingViewModel>() {
    private lateinit var tvVersion: TextView
    private lateinit var tvVersionInfo: TextView
    private lateinit var tvCache: TextView
    private lateinit var tvSkin: TextView
    private lateinit var rlCache: RelativeLayout
    private lateinit var rlSkin: RelativeLayout
    private lateinit var rlPrivacy: RelativeLayout
    private lateinit var rlUser: RelativeLayout
    private lateinit var rlThird: RelativeLayout
    private lateinit var rlPermission: RelativeLayout
    private lateinit var rlUpdate: RelativeLayout
    private lateinit var rlHelp: RelativeLayout
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

    override fun isFollowNightMode(): Boolean {
        return true
    }

    override fun initView() {
        tvVersion = mBinding.tvVersion
        tvVersionInfo = mBinding.tvVersionInfo
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
        rlHelp = mBinding.rlHelp
    }

    override fun onRestart() {
        super.onRestart()
        getSkinState()
    }

    override fun initData() {
        getCacheSize()
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

    private fun getCacheSize() {
        val cacheSize = DataCleanManager.getCacheSize(this)
        tvCache.text = cacheSize
    }

    @SuppressLint("SetTextI18n")
    private fun getVersion() {
        val versionName = CommonTools.getVersionName(this)
        val versionCode = CommonTools.getVersionCode(this)
        tvVersion.text = "V$versionName"
        tvVersionInfo.text = "Build $versionCode" + "_V$versionName"
    }

    override fun initEvent() {
        rlHelp.setOnClickListener {

        }
        rlUpdate.setOnClickListener {
            showUpdateDialog()
        }
        rlPermission.setOnClickListener {

        }
        rlThird.setOnClickListener {
            ThirdInfoActivity.startThirdLibActivity(this)
        }
        rlSkin.setOnClickListener {
            SkinActivity.startSkinActivity(this)
        }
        rlPrivacy.setOnClickListener {
            WebActivity.startActivity(this, Constants.privacyUrl)
        }
        rlUser.setOnClickListener {
            WebActivity.startActivity(this, Constants.userUrl)
        }
        rlCache.setOnClickListener {
            val dialogFragment = ConfirmDialogFragment.newInstance(
                "温馨提示", "你确定要清除缓存吗？清除缓存后，之前缓存的视频将会被删除", "确定", "取消"
            )
            dialogFragment.show(supportFragmentManager, "wy_cache")
            dialogFragment.addListener(object : ConfirmDialogFragment.OnDialogFragmentListener {
                override fun onClickSure() {
                    clearCache()
                }

                override fun onClickCancel() {

                }

            })
        }
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

    private fun showUpdateDialog() {
        val content =
            "1.布局大调整，使用统一的卡片风格;\n2.适配深色模式，保护眼睛观看;\n3.优化视频播放，加入边下边缓存功能;"
        val dialogFragment =
            UpdateDialogFragment.newInstance("发现新版本", content, "下载", "忽略更新")
        dialogFragment.show(supportFragmentManager, "update_dialog")
        dialogFragment.addListener(object : UpdateDialogFragment.OnDialogFragmentListener {
            override fun onClickSure() {
                NotificationHelper.sendProgressNotification(this@SettingActivity, 0, false)
                goDownload("")
            }

            override fun onClickCancel() {

            }
        })
    }

    private fun goDownload(apkUrl: String) {
        try {
            //设置进度条操作
            val url = URL(apkUrl)
            //打开和URL之间的连接
            val connection = url.openConnection() as HttpURLConnection
            //设置网络请求为get请求
            connection.requestMethod = "GET"
            //开始读取服务器端数据，到了指定时间还没有读到数据，则报超时异常
            connection.readTimeout = 50000
            //建立实际的连接
            connection.connect()
            Thread {
                var totalLength = 0
                try {
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream: InputStream = connection.inputStream
                        //获取文件流大小，更新进度
                        val buffer = ByteArray(1024)
                        var len: Int
                        var progress: Int
                        val fileLength: Long = connection.contentLength.toLong()
                        while (inputStream.read(buffer).also { len = it } != -1) {
                            totalLength += len
                            if (fileLength > 0) {
                                progress =
                                    (totalLength / fileLength.toFloat() * 100).toInt() //进度条传递进度
                                NotificationHelper.sendProgressNotification(this, progress, false)
                            }
                        }
                        NotificationHelper.sendProgressNotification(this, 0, true)
                        //关闭资源
                        inputStream.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    @SuppressLint("SetTextI18n")
    private fun clearCache() {
        DataCleanManager.clearIntExtCache(this)
        //重新获取一次缓存大小
        getCacheSize()
    }

}