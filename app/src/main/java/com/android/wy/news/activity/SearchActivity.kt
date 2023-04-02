package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.SearchAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.databinding.ActivitySearchBinding
import com.android.wy.news.entity.SearchResult
import com.android.wy.news.view.CustomLoadingView
import com.android.wy.news.viewmodel.SearchViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlin.math.roundToInt


class SearchActivity : BaseActivity<ActivitySearchBinding, SearchViewModel>(), OnRefreshListener,
    OnLoadMoreListener, BaseNewsAdapter.OnItemAdapterListener<SearchResult> {
    private var query: String = ""
    private var page = 0
    private lateinit var rlBack: RelativeLayout
    private lateinit var etSearch: EditText
    private lateinit var tvSearch: TextView
    private lateinit var rvContent: RecyclerView
    private var isRefresh = false
    private var isLoading = false
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var loadingView: CustomLoadingView
    private lateinit var searchAdapter: SearchAdapter

    companion object {
        private const val QUERY = "news_query"
        fun startSearch(context: Context, query: String) {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(QUERY, query)
            context.startActivity(intent)
        }
    }

    override fun setDefaultImmersionBar(): Boolean {
        return false
    }

    override fun hideStatusBar(): Boolean {
        return false
    }

    override fun hideNavigationBar(): Boolean {
        return false
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        rlBack = mBinding.rlBack
        etSearch = mBinding.etSearch
        tvSearch = mBinding.tvSearch
        loadingView = mBinding.loadingView
        rvContent = mBinding.rvContent
        refreshLayout = mBinding.refreshLayout

        val density = resources.displayMetrics.density
        val drawable = resources.getDrawable(R.mipmap.search)
        val width = (20 * density).roundToInt()
        val height = (20 * density).roundToInt()
        drawable.setBounds(0, 0, width, height)
        etSearch.setCompoundDrawables(drawable, null, null, null)

        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)
        rlBack.setOnClickListener {
            finish()
        }
        tvSearch.setOnClickListener {
            val s = etSearch.text.toString()
            if (!TextUtils.isEmpty(s)) {
                query = s
            }
            searchAdapter.clearData()
            goSearch()
        }
    }

    private fun goSearch() {
        if (!isRefresh && !isLoading && page == 0) {
            loadingView.visibility = View.VISIBLE
        }
        if (page == 0) {
            //首次进来
            getRefreshData()
        } else {
            getPageData()
        }
    }

    override fun initData() {
        val intent = intent
        query = intent.getStringExtra(QUERY).toString()
        setSearchHint(query)

        searchAdapter = SearchAdapter(this)
        rvContent.layoutManager = LinearLayoutManager(mActivity)
        rvContent.adapter = searchAdapter

        goSearch()
    }

    private fun setSearchHint(hint: String) {
        etSearch.hint = hint
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            query = intent.getStringExtra(QUERY).toString()
            setSearchHint(query)
        }
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivitySearchBinding {
        return ActivitySearchBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): SearchViewModel {
        return CommonTools.getViewModel(this, SearchViewModel::class.java)
    }

    override fun onClear() {

    }

    private fun getRefreshData() {
        mViewModel.getRefreshSearchList(query)
    }

    private fun getPageData() {
        mViewModel.getSearchPageList(query, page)
    }

    override fun onNotifyDataChanged() {
        mViewModel.dataList.observe(this) {
            if (it != null) {
                CommonTools.closeKeyboard(this, etSearch)
                if (isRefresh) {
                    refreshLayout.setNoMoreData(false)
                    refreshLayout.finishRefresh()
                    refreshLayout.setEnableLoadMore(true)
                }
                if (isLoading) {
                    refreshLayout.finishLoadMore()
                }
                if (it.size == 0) {
                    if (isLoading) {
                        refreshLayout.setNoMoreData(true)
                    }
                } else {
                    rvContent.visibility = View.VISIBLE
                    loadingView.visibility = View.GONE
                    if (isRefresh) {
                        searchAdapter.refreshData(it)
                    } else {
                        searchAdapter.loadMoreData(it)
                    }
                }
                isRefresh = false
                isLoading = false
            }
        }

        mViewModel.msg.observe(this) {
            CommonTools.closeKeyboard(this, etSearch)
            Toast.makeText(mActivity, it, Toast.LENGTH_SHORT).show()
            refreshLayout.setEnableLoadMore(false)
            if (isRefresh) {
                refreshLayout.setNoMoreData(false)
                refreshLayout.finishRefresh()
            }
            if (isLoading) {
                refreshLayout.finishLoadMore()
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        isRefresh = true
        page = 0
        goSearch()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        isLoading = true
        page++
        goSearch()
    }

    override fun onItemClickListener(view: View, data: SearchResult) {
        val url = Constants.WEB_URL + data.docid + ".html"
        WebActivity.startActivity(mActivity, url)
    }
}