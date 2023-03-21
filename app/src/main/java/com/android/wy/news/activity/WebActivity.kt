package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.databinding.ActivityWebBinding
import com.android.wy.news.view.LoadingView
import com.android.wy.news.viewmodel.WebViewModel
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient

class WebActivity : BaseActivity<ActivityWebBinding, WebViewModel>() {
    private lateinit var url: String
    private lateinit var llContent: LinearLayout
    private lateinit var tvTitle: TextView
    private lateinit var rlBack: RelativeLayout
    private lateinit var agentWeb: AgentWeb
    private lateinit var loadingView: LoadingView

    companion object {
        private const val DOC_ID = "doc_id"
        fun startActivity(context: Context, docID: String) {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(DOC_ID, docID)
            context.startActivity(intent)
        }
    }

    override fun initView() {
        llContent = mBinding.llContent
        tvTitle = mBinding.tvTitle
        rlBack = mBinding.rlBack
        loadingView = mBinding.loadingView
        loadingView.startLoadingAnim()
    }

    override fun initData() {
        val intent = intent
        if (intent.hasExtra(DOC_ID)) {
            val docID = intent.getStringExtra(DOC_ID).toString()
            url = Constants.WEB_URL + docID + ".html"
        }
        llContent.visibility = View.GONE
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(llContent, LinearLayout.LayoutParams(-1, -1))
            .closeIndicator()
            .createAgentWeb()
            .ready()
            .go(url)
        val webCreator = agentWeb.webCreator
        webCreator.webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                tvTitle.text = title
            }
        }
        webCreator.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                hideDocument(view)
                super.onPageFinished(view, url)
            }
        }
    }

    private fun hideDocument(view: WebView?) {
        val headJs = "javascript:function hideHeadJS() {" +
                "document.getElementsByClassName('header')[0].style.display='none';" +
                "}"
        view?.loadUrl(headJs)
        view?.loadUrl("javascript:hideHeadJS();")

        val mainOpenAppJs = "javascript:function hideMainOpenAppJS() {" +
                "document.getElementsByClassName('main-openApp js-open-app')[0].style.display='none';" +
                "}"
        view?.loadUrl(mainOpenAppJs)
        view?.loadUrl("javascript:hideMainOpenAppJS();")

        val floatingOpenAppJs = "javascript:function hideFloatingOpenAppJS() {" +
                "document.getElementsByClassName('backflow-floating js-open-app')[0].style.display='none';" +
                "}"
        view?.loadUrl(floatingOpenAppJs)
        view?.loadUrl("javascript:hideFloatingOpenAppJS();")

        val imageOpenAppJs = "javascript:function hideImageOpenAppJS() {" +
                "document.getElementsByClassName('s-tip js-open-app')[0].style.display='none';" +
                "}"
        view?.loadUrl(imageOpenAppJs)
        view?.loadUrl("javascript:hideImageOpenAppJS();")

        val commentJs = "javascript:function hideCommentJS() {" +
                "document.getElementsByClassName('comment-link')[0].style.display='none';" +
                "}"
        view?.loadUrl(commentJs)
        view?.loadUrl("javascript:hideCommentJS();")

        val sOpenAppJs = "javascript:function hideSOpenAppJS() {" +
                "document.getElementsByClassName('s-openApp js-open-app')[0].style.display='none';" +
                "}"
        view?.loadUrl(sOpenAppJs)
        view?.loadUrl("javascript:hideSOpenAppJS();")

        loadingView.stopLoadingAnim()
        llContent.visibility = View.VISIBLE
    }

    override fun initEvent() {
        rlBack.setOnClickListener {
            finish()
        }
    }

    override fun getViewBinding(): ActivityWebBinding {
        return ActivityWebBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): WebViewModel {
        return CommonTools.getViewModel(this, WebViewModel::class.java)
    }

    override fun onClear() {
        loadingView.stopLoadingAnim()
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

}