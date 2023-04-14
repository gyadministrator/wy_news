package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.android.wy.news.R
import com.android.wy.news.common.IBaseCommon
import com.android.wy.news.listener.IBackPressedListener
import com.android.wy.news.skin.UiModeManager
import com.android.wy.news.viewmodel.BaseViewModel
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 14:45
  * @Version:        1.0
  * @Description:    
 */
abstract class BaseActivity<V : ViewBinding, M : BaseViewModel> : AppCompatActivity(),
    IBaseCommon<V, M> {
    protected lateinit var mBinding: V
    protected lateinit var mViewModel: M
    protected lateinit var mActivity: AppCompatActivity

    abstract fun setDefaultImmersionBar(): Boolean

    abstract fun hideStatusBar(): Boolean
    abstract fun hideNavigationBar(): Boolean
    abstract fun isFollowNightMode(): Boolean

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//竖屏
        setBarState()
        if (isFollowNightMode()) {
            UiModeManager.onUiModeChange(this)
        }
        mActivity = this
        mBinding = getViewBinding()
        setContentView(mBinding.root)
        initView()
        initEvent()
        mViewModel = getViewModel()
        initData()
        onNotifyDataChanged()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val uiMode = newConfig.uiMode
        if (isFollowNightMode()) {
            UiModeManager.onUiModeChange(this)
        }
    }

    private fun setBarState() {
        val mImmersionBar = ImmersionBar.with(this)
        if (hideStatusBar()) {
            mImmersionBar.hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
        } else {
            if (setDefaultImmersionBar()) {
                mImmersionBar.statusBarColor(R.color.default_status_bar_color)
                mImmersionBar.statusBarDarkFont(true)
            } else {
                mImmersionBar.statusBarColor(R.color.status_bar_color)
                mImmersionBar.statusBarDarkFont(false)
            }
        }
        if (hideNavigationBar()) {
            mImmersionBar.hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
        } else {
            mImmersionBar.navigationBarColor(R.color.default_status_bar_color)
        }
        mImmersionBar.init()
    }

    override fun onDestroy() {
        super.onDestroy()
        JCVideoPlayer.releaseAllVideos()
        onClear()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return
        }
        if (!interceptBackPressed()) {
            super.onBackPressed()
        }
    }

    /**
     * 拦截事件
     */
    private fun interceptBackPressed(): Boolean {
        supportFragmentManager.fragments.forEach {
            if (it is IBackPressedListener) {
                if (it.handleBackPressed()) {
                    return true
                }
            }
        }
        return false
    }

    override fun onPause() {
        super.onPause()
        JCVideoPlayer.releaseAllVideos()
    }
}