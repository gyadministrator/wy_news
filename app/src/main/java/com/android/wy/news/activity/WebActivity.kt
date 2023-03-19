package com.android.wy.news.activity

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.ActivityWebBinding
import com.android.wy.news.viewmodel.WebViewModel
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient

class WebActivity : BaseActivity<ActivityWebBinding, WebViewModel>() {
    private lateinit var url: String
    private lateinit var llContent: LinearLayout
    private lateinit var tvTitle: TextView
    private lateinit var rlBack: RelativeLayout
    private lateinit var agentWeb: AgentWeb

    companion object {
        private const val URL = "web_url"
        fun startActivity(context: Context, url: String) {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(URL, url)
            context.startActivity(intent)
        }
    }

    override fun initView() {
        llContent = mBinding.llContent
        tvTitle = mBinding.tvTitle
        rlBack = mBinding.rlBack
    }

    override fun initData() {
        val intent = intent
        if (intent.hasExtra(URL)) {
            url = intent.getStringExtra(URL).toString()
        }
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

    }

    override fun onNotifyDataChanged() {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (agentWeb.handleKeyEvent(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}