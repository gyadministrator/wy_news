package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.android.wy.news.cache.CacheUtils
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.common.CustomUpdateParser
import com.android.wy.news.common.CustomUpdatePrompter
import com.android.wy.news.common.SkinType
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.ActivitySettingBinding
import com.android.wy.news.viewmodel.SettingViewModel
import com.xuexiang.xupdate.XUpdate
import com.xuexiang.xupdate.entity.PromptEntity
import com.xuexiang.xupdate.entity.UpdateEntity
import com.xuexiang.xupdate.proxy.IPrompterProxy
import com.xuexiang.xupdate.proxy.IUpdateProxy
import com.xuexiang.xupdate.proxy.impl.DefaultUpdateChecker
import com.xuexiang.xupdate.service.OnFileDownloadListener
import com.xuexiang.xupdate.widget.UpdateDialogFragment
import java.io.File


class SettingActivity : BaseActivity<ActivitySettingBinding, SettingViewModel>(),
    View.OnClickListener {
    private lateinit var rlBack: RelativeLayout
    private lateinit var tvVersion: TextView
    private lateinit var tvCache: TextView
    private lateinit var tvSkin: TextView
    private lateinit var rlCache: RelativeLayout
    private lateinit var rlSkin: RelativeLayout
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
        rlBack = mBinding.rlBack
        tvVersion = mBinding.tvVersion
        tvCache = mBinding.tvCache
        tvSkin = mBinding.tvSkin
        rlCache = mBinding.rlCache
        rlSkin = mBinding.rlSkin
        scPlay = mBinding.scPlay
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

    override fun onRestart() {
        super.onRestart()
        getState()
    }

    override fun initData() {
        getUpdate()
        //testUpdate()
        getVersion()
        getState()
    }

    private fun getState() {
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

    private fun testUpdate() {
        val s: String =
            "{\n" + "  \"Code\": 0,\n" + "  \"Msg\": \"\",\n" + "  \"UpdateStatus\": 1,\n" + "  \"VersionCode\": 3,\n" + "  \"VersionName\": \"1.0.2\",\n" + "  \"ModifyContent\": \"1、优化api接口。\\r\\n2、添加使用demo演示。\\r\\n3、新增自定义更新服务API接口。\\r\\n4、优化更新提示界面。\",\n" + "  \"DownloadUrl\": \"https://raw.githubusercontent.com/xuexiangjys/XUpdate/master/apk/xupdate_demo_1.0.2.apk\",\n" + "  \"ApkSize\": 2048,\n" + "  \"ApkMd5\": \"\"\n" + "}"
        val updateEntity = UpdateEntity()
        updateEntity.updateContent =
            "1、优化api接口。\\r\\n2、添加使用demo演示。\\r\\n3、新增自定义更新服务API接口。\\r\\n4、优化更新提示界面。\",\n"
        updateEntity.downloadUrl =
            "https://raw.githubusercontent.com/xuexiangjys/XUpdate/master/apk/xupdate_demo_1.0.2.apk"
        updateEntity.versionCode = 2
        updateEntity.versionName = "1.2.3"
        updateEntity.size = 2048
        updateEntity.md5 = ""
        updateEntity.apkCacheDir = "/test"
        val promptEntity = PromptEntity()
        UpdateDialogFragment.show(supportFragmentManager, updateEntity, object : IPrompterProxy {
            override fun getUrl(): String {
                return ""
            }

            override fun startDownload(
                updateEntity: UpdateEntity, downloadListener: OnFileDownloadListener?
            ) {

            }

            override fun backgroundDownload() {

            }

            override fun cancelDownload() {

            }

            override fun recycle() {

            }

        }, promptEntity)
    }

    private fun getUpdate() {
        XUpdate.newBuild(this).updateUrl("").isAutoMode(true).supportBackgroundUpdate(true)
            //.promptThemeColor(ResUtils.getColor(R.color.update_theme_color))
            .promptButtonTextColor(Color.WHITE)
            //.promptTopResId(R.mipmap.bg_update_top)
            .promptWidthRatio(0.7F).updateParser(CustomUpdateParser())
            .updateChecker(object : DefaultUpdateChecker() {
                override fun onBeforeCheck() {
                    super.onBeforeCheck()
                }

                override fun checkVersion(
                    isGet: Boolean,
                    url: String,
                    params: MutableMap<String, Any>,
                    updateProxy: IUpdateProxy
                ) {
                    super.checkVersion(isGet, url, params, updateProxy)
                }

                override fun onAfterCheck() {
                    super.onAfterCheck()
                }

                override fun processCheckResult(result: String, updateProxy: IUpdateProxy) {
                    super.processCheckResult(result, updateProxy)
                }

                override fun noNewVersion(throwable: Throwable?) {
                    super.noNewVersion(throwable)
                }
            }).updatePrompter(CustomUpdatePrompter(this)).apkCacheDir("").build()
            //.update()
            .download("", object : OnFileDownloadListener {
                override fun onStart() {
                    //HProgressDialogUtils.showHorizontalProgressDialog(getContext(), "下载进度", false);
                }

                override fun onProgress(progress: Float, total: Long) {
                    //HProgressDialogUtils.setProgress(Math.round(progress * 100));
                }

                override fun onCompleted(file: File?): Boolean {/* HProgressDialogUtils.cancel();
                     ToastUtils.toast("apk下载完毕，文件路径：" + file.getPath());*/
                    return false;
                }

                override fun onError(throwable: Throwable?) {
                    //HProgressDialogUtils.cancel();
                }

            })

        //_XUpdate.startInstallApk(this, File(""))
    }

    override fun initEvent() {
        rlBack.setOnClickListener {
            finish()
        }
        rlSkin.setOnClickListener {
            SkinActivity.startSkinActivity(this)
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