package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.View
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.databinding.ActivityWebFragmentBinding
import com.android.wy.news.manager.JsoupManager
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.skin.UiModeManager
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.view.CustomLoadingView
import com.android.wy.news.viewmodel.WebFragmentViewModel

@Route(path = RouteManager.PATH_ACTIVITY_WEB_FRAGMENT)
class WebFragmentActivity : BaseActivity<ActivityWebFragmentBinding, WebFragmentViewModel>() {
    private var webView: WebView? = null
    private var loadingView: CustomLoadingView? = null
    private var tvContent: TextView? = null
    private var artistId = ""

    companion object {
        private const val ARTIST_ID = "artist_id"
        fun startActivity(context: Context, artistId: String) {
            val intent = Intent(context, WebFragmentActivity::class.java)
            intent.putExtra(ARTIST_ID, artistId)
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
        webView = mBinding.webView
        loadingView = mBinding.loadingView
        tvContent = mBinding.tvContent
    }

    override fun initData() {
        val intent = intent
        if (intent.hasExtra(ARTIST_ID)) {
            artistId = intent.getStringExtra(ARTIST_ID).toString()
            webView?.settings?.defaultTextEncodingName = "utf-8"
            /*webView?.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    hideDocument(view)
                    super.onPageFinished(view, url)
                }

                @Deprecated(
                    "Deprecated in Java", ReplaceWith(
                        "super.shouldInterceptRequest(view, url)", "android.webkit.WebViewClient"
                    )
                )
                override fun shouldInterceptRequest(
                    view: WebView?, url: String?
                ): WebResourceResponse? {
                    return super.shouldInterceptRequest(view, url)
                }
            }*/
            TaskUtil.runOnThread {
                val webContent =
                    JsoupManager.getSingerInfo(GlobalConstant.MUSIC_BASE_URL + "/singer_detail/$artistId/info")
                TaskUtil.runOnUiThread {
                    loadingView?.visibility = View.GONE
                    tvContent?.text = Html.fromHtml(webContent, Html.FROM_HTML_MODE_LEGACY)
                    //当我们要加载的html片段和上边代码中示例的一样，含有转义字符和css样式时，
                    // 我们要使用webView.loadDataWithBaseURL这个方法，不能用webView.loadData，
                    // 原因是loadData()中的html data中不能包含'#', '%', '\', '?'这四中特殊字符。
                    //webView?.loadDataWithBaseURL(null, webContent, "text/html", "utf-8", null)
                }
            }
        }
    }

    private fun hideDocument(view: WebView?) {
        if (UiModeManager.isNightMode(this)) {
            //深色模式
            val darkJs =
                "javascript:function showDarkJs() {" + "document.body.style.backgroundColor='#111111';" +
                        "document.body.style.color='#BEBEBE';" + "}"
            view?.loadUrl(darkJs)
            view?.loadUrl("javascript:showDarkJs();")
        }
        loadingView?.visibility = View.GONE
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivityWebFragmentBinding {
        return ActivityWebFragmentBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): WebFragmentViewModel {
        return CommonTools.getViewModel(this, WebFragmentViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}