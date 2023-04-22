package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.RelativeLayout
import cn.jzvd.JzvdStd
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityIntroduceBinding
import com.android.wy.news.viewmodel.IntroduceViewModel
import com.gyf.immersionbar.ImmersionBar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class IntroduceActivity : BaseActivity<ActivityIntroduceBinding, IntroduceViewModel>() {
    private lateinit var jzVideo: JzvdStd
    private lateinit var rlBack: RelativeLayout
    private var localVideoPath: String? = null

    companion object {
        fun startIntroduceActivity(context: Context) {
            val intent = Intent(context, IntroduceActivity::class.java)
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
        return false
    }

    override fun initView() {
        ImmersionBar.with(this).statusBarColor(R.color.black).navigationBarColor(R.color.black)
            .statusBarDarkFont(false).init()
        jzVideo = mBinding.jzVideo
        rlBack = mBinding.rlBack
    }

    override fun initData() {
        localVideoPath =
            Environment.getExternalStorageDirectory().absolutePath + "/DCIM/Camera/wy_news_introduce.mp4"
        //cp video 防止视频被意外删除
        cpAssertVideoToLocalPath()
        jzVideo.setUp(localVideoPath, "")
        /*jzvdAssertPath.setUp(
            "local_video.mp4", "Play Assert Video", JzvdStd.SCREEN_NORMAL,
            JZMediaSystemAssertFolder::class.java
        )*/
    }

    private fun cpAssertVideoToLocalPath() {
        if (localVideoPath?.let { File(it).exists() } == true) return
        try {
            val myOutput: OutputStream = FileOutputStream(localVideoPath)
            val myInput: InputStream = this.assets.open("wy_news_introduce.mp4")
            val buffer = ByteArray(1024)
            var length = myInput.read(buffer)
            while (length > 0) {
                myOutput.write(buffer, 0, length)
                length = myInput.read(buffer)
            }
            myOutput.flush()
            myInput.close()
            myOutput.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun initEvent() {
        rlBack.setOnClickListener {
            finish()
        }
    }

    override fun getViewBinding(): ActivityIntroduceBinding {
        return ActivityIntroduceBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): IntroduceViewModel {
        return CommonTools.getViewModel(this, IntroduceViewModel::class.java)
    }

    override fun onClear() {
    }

    override fun onNotifyDataChanged() {
    }

}