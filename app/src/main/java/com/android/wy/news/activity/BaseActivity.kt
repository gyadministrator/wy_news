package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.android.wy.news.R
import com.android.wy.news.common.IBaseCommon
import com.android.wy.news.viewmodel.BaseViewModel
import com.gyf.immersionbar.ImmersionBar

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
    private var firstTime: Long = 0

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;//竖屏
        ImmersionBar.with(this).statusBarColor(R.color.main_bg_color)
            .navigationBarColor(R.color.main_bg_color).statusBarDarkFont(true).init()
        mActivity = this
        mBinding = getViewBinding()
        setContentView(mBinding.root)
        initView()
        initEvent()
        mViewModel = getViewModel()
        initData()
        onNotifyDataChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        onClear()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN && this.javaClass.name.equals(
                    "MainActivity"
                )
            ) {
                val secondTime = System.currentTimeMillis()
                if (secondTime - firstTime > 2000) {
                    Toast.makeText(mActivity, "再按一次退出程序", Toast.LENGTH_SHORT).show()
                    firstTime = secondTime
                    return true
                } else {
                    finish()
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}