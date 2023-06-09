package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityWebBinding
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.skin.UiModeManager
import com.android.wy.news.view.CustomLoadingView
import com.android.wy.news.view.TitleBarView
import com.android.wy.news.viewmodel.WebViewModel
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient

@Route(path = RouteManager.PATH_ACTIVITY_WEB)
class WebActivity : BaseActivity<ActivityWebBinding, WebViewModel>() {
    private var url: String? = null
    private lateinit var llContent: LinearLayout
    private lateinit var titleBar: TitleBarView
    private lateinit var agentWeb: AgentWeb
    private lateinit var loadingView: CustomLoadingView

    companion object {
        const val WEB_URL = "web_url"
        fun startActivity(context: Context, url: String) {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(WEB_URL, url)
            context.startActivity(intent)
        }
    }

    override fun initView() {
        llContent = mBinding.llContent
        titleBar = mBinding.titleBar
        loadingView = mBinding.loadingView
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        initData()
    }

    override fun initData() {
        val intent = intent
        if (intent.hasExtra(WEB_URL)) {
            url = intent.getStringExtra(WEB_URL).toString()
        }
        llContent.visibility = View.GONE
        agentWeb =
            AgentWeb.with(this).setAgentWebParent(llContent, LinearLayout.LayoutParams(-1, -1))
                .closeIndicator().createAgentWeb().ready().go(url)
        val webCreator = agentWeb.webCreator
        webCreator.webView.isVerticalScrollBarEnabled = false
        webCreator.webView.isHorizontalScrollBarEnabled = false
        webCreator.webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                title?.let { titleBar.setTitle(it) }
            }
        }
        webCreator.webView.webViewClient = object : WebViewClient() {
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
        }
    }

    private fun hideDocument(view: WebView?) {
        val headJs =
            "javascript:function hideHeadJS() {" + "document.getElementsByClassName('header')[0].style.display='none';" + "}"
        view?.loadUrl(headJs)
        view?.loadUrl("javascript:hideHeadJS();")

        val mainOpenAppJs =
            "javascript:function hideMainOpenAppJS() {" + "document.getElementsByClassName('main-openApp js-open-app')[0].style.display='none';" + "}"
        view?.loadUrl(mainOpenAppJs)
        view?.loadUrl("javascript:hideMainOpenAppJS();")

        val floatingOpenAppJs =
            "javascript:function hideFloatingOpenAppJS() {" + "document.getElementsByClassName('backflow-floating js-open-app')[0].style.display='none';" + "}"
        view?.loadUrl(floatingOpenAppJs)
        view?.loadUrl("javascript:hideFloatingOpenAppJS();")

        val imageOpenAppJs =
            "javascript:function hideImageOpenAppJS() {" + "document.getElementsByClassName('s-tip js-open-app')[0].style.display='none';" + "}"
        view?.loadUrl(imageOpenAppJs)
        view?.loadUrl("javascript:hideImageOpenAppJS();")

        val commentJs =
            "javascript:function hideCommentJS() {" + "document.getElementsByClassName('comment-link')[0].style.display='none';" + "}"
        view?.loadUrl(commentJs)
        view?.loadUrl("javascript:hideCommentJS();")

        val sOpenAppJs =
            "javascript:function hideSOpenAppJS() {" + "document.getElementsByClassName('s-openApp js-open-app')[0].style.display='none';" + "}"
        view?.loadUrl(sOpenAppJs)
        view?.loadUrl("javascript:hideSOpenAppJS();")

        //直播底栏悬浮
        val liveOpenAppJs =
            "javascript:function hideLiveOpenAppJs() {" + "document.getElementsByClassName('widget-slider-article-simple js-open-newsapp')[0].style.display='none';" + "}"
        view?.loadUrl(liveOpenAppJs)
        view?.loadUrl("javascript:hideLiveOpenAppJs();")

        if (UiModeManager.isNightMode(this)) {
            //深色模式
            val darkJs =
                "javascript:function showDarkJs() {" + "document.body.style.backgroundColor='#111111';" +
                        "document.body.style.color='#BEBEBE';" + "}"
            view?.loadUrl(darkJs)
            view?.loadUrl("javascript:showDarkJs();")
        }

        loadingView.visibility = View.GONE
        val text = titleBar.getTitle()
        if (!TextUtils.isEmpty(text)) {
            if (text.contains("404")) {
                llContent.visibility = View.GONE
            } else {
                llContent.visibility = View.VISIBLE
            }
        }
    }

    override fun initEvent() {
    }

    override fun getViewBinding(): ActivityWebBinding {
        return ActivityWebBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): WebViewModel {
        return CommonTools.getViewModel(this, WebViewModel::class.java)
    }

    override fun onClear() {
        agentWeb.clearWebCache()
    }

    override fun onNotifyDataChanged() {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (agentWeb.webCreator.webView.canGoBack()) {
            finish()
            return true
        }
        if (agentWeb.handleKeyEvent(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
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

}