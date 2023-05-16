package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.SearchAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalConstant
import com.android.wy.news.databinding.ActivitySearchBinding
import com.android.wy.news.databinding.LayoutHistoryItemBinding
import com.android.wy.news.databinding.LayoutHotItemBinding
import com.android.wy.news.entity.HotWord
import com.android.wy.news.entity.SearchResult
import com.android.wy.news.http.repository.HotRepository
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.sql.SearchHistoryEntity
import com.android.wy.news.sql.SearchHistoryRepository
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.util.ToastUtil
import com.android.wy.news.view.ClearEditText
import com.android.wy.news.view.CustomLoadingView
import com.android.wy.news.viewmodel.SearchViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

@Route(path = RouteManager.PATH_ACTIVITY_SEARCH)
class SearchActivity : BaseActivity<ActivitySearchBinding, SearchViewModel>(), OnRefreshListener,
    OnLoadMoreListener, BaseNewsAdapter.OnItemAdapterListener<SearchResult>,
    ClearEditText.OnEditTextListener {
    private var query: String? = ""
    private var page = 0
    private lateinit var rlBack: RelativeLayout
    private lateinit var etSearch: ClearEditText
    private lateinit var tvSearch: TextView
    private lateinit var rvContent: RecyclerView
    private var isRefresh = false
    private var isLoading = false
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var loadingView: CustomLoadingView
    private lateinit var llHot: LinearLayout
    private lateinit var llContent: LinearLayout
    private lateinit var llHistoryList: LinearLayout
    private lateinit var llHistory: LinearLayout
    private lateinit var rlClear: RelativeLayout
    private lateinit var scrollView: NestedScrollView
    private lateinit var searchAdapter: SearchAdapter
    private var searchHistoryRepository: SearchHistoryRepository? = null
    private var historyList: ArrayList<SearchHistoryEntity>? = null

    companion object {
        private const val QUERY = "news_query"
        fun startSearch(context: Context, query: String) {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(QUERY, query)
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

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        rlBack = mBinding.rlBack
        etSearch = mBinding.etSearch
        tvSearch = mBinding.tvSearch
        loadingView = mBinding.loadingView
        rvContent = mBinding.rvContent
        refreshLayout = mBinding.refreshLayout
        llHot = mBinding.llHot
        llContent = mBinding.llContent
        llHistoryList = mBinding.llHistoryList
        llHistory = mBinding.llHistory
        rlClear = mBinding.rlClear
        scrollView = mBinding.scrollContent

        etSearch.addListener(this)

        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)
        refreshLayout.setEnableFooterFollowWhenNoMoreData(true)

        rlBack.setOnClickListener {
            finish()
        }
        tvSearch.setOnClickListener {
            val s = etSearch.text.toString()
            if (!TextUtils.isEmpty(s)) {
                query = s
            }
            goSearch()
        }
    }

    private fun goSearch() {
        //添加关键词到数据库
        TaskUtil.runOnThread {
            addHistory()
        }
        llContent.visibility = View.GONE
        if (!isLoading) {
            searchAdapter.clearData()
            loadingView.visibility = View.VISIBLE
        }
        if (page == 0) {
            //首次进来
            getRefreshData()
        } else {
            getPageData()
        }
    }

    private fun getHistoryList() {
        historyList = searchHistoryRepository?.getSearchHistoryList()
        if (historyList != null && historyList!!.size > 0) {
            llHistory.visibility = View.VISIBLE
            rlClear.setOnClickListener(clearClickListener)
            llHistoryList.removeAllViews()
            for (i in 0 until historyList!!.size step 2) {
                val historyItemBinding = LayoutHistoryItemBinding.inflate(layoutInflater)
                val tvOne = historyItemBinding.tvOne
                val tvTwo = historyItemBinding.tvTwo
                val viewLine = historyItemBinding.viewLine

                val searchOneHistoryEntity = historyList!![i]
                tvOne.text = searchOneHistoryEntity.title
                tvOne.setOnClickListener(oneClickListener)

                if (i + 1 < historyList!!.size) {
                    val searchTwoHistoryEntity = historyList!![i + 1]
                    tvTwo.text = searchTwoHistoryEntity.title
                    tvTwo.setOnClickListener(twoClickListener)
                } else {
                    viewLine.visibility = View.GONE
                }

                val root = historyItemBinding.root
                val parent = root.parent
                if (parent != null && parent is ViewGroup) {
                    parent.removeView(root)
                }
                runOnUiThread {
                    llHistoryList.addView(root)
                }
            }
        }
    }

    private val oneClickListener = View.OnClickListener { p0 ->
        if (p0 != null) {
            val textView = p0 as TextView
            query = textView.text.toString()
            page = 0
            setEditTextContent()
            goSearch()
        }
    }

    private val twoClickListener = View.OnClickListener { p0 ->
        if (p0 != null) {
            val textView = p0 as TextView
            query = textView.text.toString()
            page = 0
            setEditTextContent()
            goSearch()
        }
    }

    private val clearClickListener = View.OnClickListener { p0 ->
        if (p0 != null) {
            TaskUtil.runOnThread {
                historyList?.let { searchHistoryRepository?.deleteAllSearchHistory(it) }
            }
            llHistory.visibility = View.GONE
        }
    }

    private fun addHistory() {
        val searchHistoryEntity =
            query?.let { searchHistoryRepository?.getSearchHistoryByTitle(it) }
        if (searchHistoryEntity == null || TextUtils.isEmpty(searchHistoryEntity.title)) {
            query?.let { SearchHistoryEntity(0, it) }
                ?.let { searchHistoryRepository?.addSearchHistory(it) }
        }
    }

    override fun initData() {
        setEditFocusAndShowSoft()
        searchHistoryRepository = SearchHistoryRepository(this.applicationContext)
        val intent = intent
        query = intent.getStringExtra(QUERY)
        query?.let { setSearchHint(it) }

        searchAdapter = SearchAdapter(this)
        rvContent.layoutManager = LinearLayoutManager(mActivity)
        rvContent.adapter = searchAdapter

        TaskUtil.runOnThread {
            getHistoryList()
        }
        getHot()
    }

    /**
     * EditText 获取焦点并弹出软键盘
     */
    private fun setEditFocusAndShowSoft() {
        etSearch.isFocusable = true
        etSearch.isFocusableInTouchMode = true
        etSearch.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun getHot() {
        //mViewModel.getHot()
        HotRepository.getHotData().observe(this) {
            val hotEntity = it.getOrNull()
            if (hotEntity != null) {
                val data = hotEntity.data
                if (data != null) {
                    val hotWordList = data.hotWordList
                    addHot(hotWordList)
                }
            }
        }
    }

    private fun setSearchHint(hint: String) {
        if (!TextUtils.isEmpty(hint)) {
            etSearch.hint = hint
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            query = intent.getStringExtra(QUERY)
            query?.let { setSearchHint(it) }
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
        query?.let { mViewModel.getRefreshSearchList(it) }
    }

    private fun getPageData() {
        query?.let { mViewModel.getSearchPageList(it, page) }
    }

    override fun onNotifyDataChanged() {
        mViewModel.dataList.observe(this) {
            if (it != null) {
                refreshLayout.visibility = View.VISIBLE
                scrollView.visibility = View.GONE
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
            ToastUtil.show(it)
            if (isRefresh) {
                refreshLayout.setNoMoreData(false)
                refreshLayout.finishRefresh(false)
            }
            if (isLoading) {
                refreshLayout.finishLoadMore(false)
                refreshLayout.setNoMoreData(true)
            }
        }

        mViewModel.hotList.observe(this) {
            addHot(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addHot(it: ArrayList<HotWord>?) {
        if (it != null && it.size > 0) {
            llHot.removeAllViews()
            for (i in 0 until it.size) {
                val hotBinding = LayoutHotItemBinding.inflate(layoutInflater)
                val tvTitle = hotBinding.tvTitle
                val tvIndex = hotBinding.tvIndex
                val tvNum = hotBinding.tvNum

                val hotWord = it[i]
                tvTitle.text = hotWord.hotWord
                tvIndex.text = (i + 1).toString()

                val exp = hotWord.exp
                var num: String = exp
                val l = exp.toLong()
                if (l > 10000) {
                    val fl = l / 10000f
                    num = "%.1f".format(fl) + "w"
                }
                tvNum.text = num

                val root = hotBinding.root
                val parent = root.parent
                if (parent != null && parent is ViewGroup) {
                    parent.removeView(root)
                }
                root.tag = hotWord
                root.setOnClickListener(hotClickListener)
                llHot.addView(root)
            }
        }
    }

    private val hotClickListener = View.OnClickListener { p0 ->
        if (p0 != null) {
            val hotWord = p0.tag as HotWord
            query = hotWord.hotWord
            setEditTextContent()
            page = 0
            goSearch()
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

    private fun setEditTextContent() {
        if (!TextUtils.isEmpty(query)) {
            etSearch.setText(query)
            query?.length?.let { etSearch.setSelection(it) }
        }
    }

    override fun onItemClickListener(view: View, data: SearchResult) {
        val url = GlobalConstant.WEB_URL + data.docid + ".html"
        WebActivity.startActivity(mActivity, url)
    }

    override fun onEditTextClear() {
        refreshLayout.visibility = View.GONE
        scrollView.visibility = View.VISIBLE
        llContent.visibility = View.VISIBLE
    }
}