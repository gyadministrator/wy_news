package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RelativeLayout
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.CustomUpdateParser
import com.android.wy.news.common.CustomUpdatePrompter
import com.android.wy.news.databinding.ActivitySettingBinding
import com.android.wy.news.viewmodel.SettingViewModel
import com.xuexiang.xupdate.XUpdate
import com.xuexiang.xupdate._XUpdate
import com.xuexiang.xupdate.entity.PromptEntity
import com.xuexiang.xupdate.entity.UpdateEntity
import com.xuexiang.xupdate.proxy.IPrompterProxy
import com.xuexiang.xupdate.proxy.IUpdateProxy
import com.xuexiang.xupdate.proxy.impl.DefaultUpdateChecker
import com.xuexiang.xupdate.service.OnFileDownloadListener
import com.xuexiang.xupdate.widget.UpdateDialogFragment
import java.io.File

class SettingActivity : BaseActivity<ActivitySettingBinding, SettingViewModel>() {
    private lateinit var rlBack: RelativeLayout

    companion object {
        fun startSettingActivity(context: Context) {
            val intent = Intent(context, SettingActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun setDefaultImmersionBar(): Boolean {
        return true
    }

    override fun initView() {
        rlBack = mBinding.rlBack
    }

    override fun initData() {
        //getUpdate()
        val s: String = "{\n" +
                "  \"Code\": 0,\n" +
                "  \"Msg\": \"\",\n" +
                "  \"UpdateStatus\": 1,\n" +
                "  \"VersionCode\": 3,\n" +
                "  \"VersionName\": \"1.0.2\",\n" +
                "  \"ModifyContent\": \"1、优化api接口。\\r\\n2、添加使用demo演示。\\r\\n3、新增自定义更新服务API接口。\\r\\n4、优化更新提示界面。\",\n" +
                "  \"DownloadUrl\": \"https://raw.githubusercontent.com/xuexiangjys/XUpdate/master/apk/xupdate_demo_1.0.2.apk\",\n" +
                "  \"ApkSize\": 2048,\n" +
                "  \"ApkMd5\": \"\"\n" +
                "}"
        val updateEntity = UpdateEntity()
        updateEntity.updateContent="1、优化api接口。\\r\\n2、添加使用demo演示。\\r\\n3、新增自定义更新服务API接口。\\r\\n4、优化更新提示界面。\",\n"
        updateEntity.downloadUrl="https://raw.githubusercontent.com/xuexiangjys/XUpdate/master/apk/xupdate_demo_1.0.2.apk"
        updateEntity.versionCode=2
        updateEntity.versionName="1.2.3"
        updateEntity.size=2048
        updateEntity.md5=""
        updateEntity.apkCacheDir="/test"
        val promptEntity = PromptEntity()
        UpdateDialogFragment.show(supportFragmentManager, updateEntity, object : IPrompterProxy {
            override fun getUrl(): String {
                return ""
            }

            override fun startDownload(
                updateEntity: UpdateEntity,
                downloadListener: OnFileDownloadListener?
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
        XUpdate.newBuild(this)
            .updateUrl("")
            .isAutoMode(true)
            .supportBackgroundUpdate(true)
            //.promptThemeColor(ResUtils.getColor(R.color.update_theme_color))
            .promptButtonTextColor(Color.WHITE)
            //.promptTopResId(R.mipmap.bg_update_top)
            .promptWidthRatio(0.7F)
            .updateParser(CustomUpdateParser())
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
            })
            .updatePrompter(CustomUpdatePrompter(this))
            .apkCacheDir("")
            .build()
            //.update()
            .download("", object : OnFileDownloadListener {
                override fun onStart() {
                    //HProgressDialogUtils.showHorizontalProgressDialog(getContext(), "下载进度", false);
                }

                override fun onProgress(progress: Float, total: Long) {
                    //HProgressDialogUtils.setProgress(Math.round(progress * 100));
                }

                override fun onCompleted(file: File?): Boolean {
                    /* HProgressDialogUtils.cancel();
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

}