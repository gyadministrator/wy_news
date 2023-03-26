package com.android.wy.news.fragment

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.activity.WebActivity
import com.android.wy.news.adapter.TopAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.databinding.FragmentTabTopBinding
import com.android.wy.news.databinding.LayoutTopCityItemBinding
import com.android.wy.news.entity.Ad
import com.android.wy.news.entity.House
import com.android.wy.news.entity.TopEntity
import com.android.wy.news.view.CustomLoadingView
import com.android.wy.news.viewmodel.TopViewModel
import com.bumptech.glide.Glide
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.config.IndicatorConfig.Direction
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer

class TopTabFragment : BaseFragment<FragmentTabTopBinding, TopViewModel>(), OnRefreshListener,
    OnLoadMoreListener, TopAdapter.OnTopListener {
    private var pageStart = 0
    private lateinit var rvContent: RecyclerView
    private var isRefresh = false
    private var isLoading = false
    private lateinit var topAdapter: TopAdapter
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var llContent: LinearLayout
    private lateinit var loadingView: CustomLoadingView

    companion object {
        fun newInstance() = TopTabFragment()
    }

    override fun initView() {
        loadingView = mBinding.loadingView
        rvContent = mBinding.rvContent
        llContent = mBinding.llContent
        refreshLayout = mBinding.refreshLayout
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)
    }

    override fun onPause() {
        super.onPause()
        topAdapter.onPause()
    }

    override fun initData() {
        topAdapter = TopAdapter(mActivity, this)
        rvContent.layoutManager = LinearLayoutManager(mActivity)
        rvContent.adapter = topAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    topAdapter.onBackPressed()
                }
            })
    }

    override fun handleBackPressed(): Boolean {
        topAdapter.onBackPressed()
        return true
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            JCVideoPlayer.releaseAllVideos()
        }
    }

    override fun initEvent() {
        getTopData()
        getCityData()
    }

    private fun getCityData() {
        mViewModel.getCityNews(Constants.currentCity)
    }

    override fun getViewBinding(): FragmentTabTopBinding {
        return FragmentTabTopBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): TopViewModel {
        return CommonTools.getViewModel(this, TopViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {
        mViewModel.topNewsList.observe(this) {
            if (isRefresh) {
                refreshLayout.setNoMoreData(false)
                refreshLayout.finishRefresh()
            }
            if (isLoading) {
                refreshLayout.finishLoadMore()
            }
            if (it.size == 0) {
                if (isLoading) {
                    refreshLayout.setNoMoreData(true)
                }
            } else {
                loadingView.visibility = View.GONE
                if (isRefresh) {
                    topAdapter.refreshData(it)
                } else {
                    topAdapter.loadMoreData(it)
                }
                addBannerHeader(it[0])
            }
            isRefresh = false
            isLoading = false
        }

        mViewModel.msg.observe(this) {
            Toast.makeText(mActivity, it, Toast.LENGTH_SHORT).show()
            if (isRefresh) {
                refreshLayout.setNoMoreData(false)
                refreshLayout.finishRefresh()
            }
            if (isLoading) {
                refreshLayout.finishLoadMore()
            }
        }

        mViewModel.cityNewsList.observe(this) {
            loadingView.visibility = View.GONE
            addCityNewsHeader(it)
        }
    }

    private fun addCityNewsHeader(it: ArrayList<House>?) {
        if (it != null && it.size > 0) {
            llContent.removeAllViews()
            for (i in 0 until it.size) {
                val cityItemBinding = LayoutTopCityItemBinding.inflate(layoutInflater)
                val tvTitle = cityItemBinding.tvTitle
                val tvSource = cityItemBinding.tvSource
                val tvTime = cityItemBinding.tvTime

                val house = it[i]
                tvTitle.text = house.title
                val time = CommonTools.parseTime(house.ptime)
                if (TextUtils.isEmpty(time)) {
                    tvTime.text = house.ptime
                } else {
                    tvTime.text = time
                }
                tvSource.text = house.source

                val root = cityItemBinding.root
                val parent = root.parent
                if (parent != null && parent is ViewGroup) {
                    parent.removeView(root)
                }
                llContent.tag = house
                llContent.setOnClickListener(CityNewsClickListener())
                llContent.addView(root)
            }
        }
    }

    private class CityNewsClickListener : View.OnClickListener {
        override fun onClick(p0: View?) {
            if (p0 != null) {
                val house = p0.tag as House
                val url = Constants.WEB_URL + house.docid + ".html"
                WebActivity.startActivity(p0.context, url)
            }
        }

    }

    private fun addBannerHeader(topEntity: TopEntity) {
        val banner = mBinding.banner
        val titleList = ArrayList<String>()
        val ads = topEntity.ads
        if (ads != null && ads.isNotEmpty()) {
            for (i in ads.indices) {
                val ad = ads[i]
                titleList.add(ad.title)
            }
            banner.setAdapter(object : BannerImageAdapter<Ad?>(ads) {

                override fun onBindView(
                    holder: BannerImageHolder?, data: Ad?, position: Int, size: Int
                ) {
                    if (holder != null && data != null) {
                        //图片加载自己实现
                        Glide.with(holder.itemView).load(data.imgsrc).into(holder.imageView)
                    }
                }
            }).addBannerLifecycleObserver(this) //添加生命周期观察者
                .setIndicator(CircleIndicator(mActivity)).setBannerGalleryEffect(10, 10)
                .setIndicatorHeight(20).setIndicatorHeight(20).setBannerRound(10f)
                .setBannerRound2(10f).setIndicatorNormalColorRes(R.color.text_normal_color)
                .setIndicatorSelectedColorRes(R.color.text_select_color).setIndicatorSpace(15)
                .setIndicatorGravity(Direction.CENTER)
        }
    }


    private fun getTopData() {
        mViewModel.getTopNews(pageStart)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        isRefresh = true
        pageStart = 0
        getTopData()
        getCityData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        isLoading = true
        pageStart += 10
        getTopData()
    }

    override fun onTopItemClickListener(view: View, topEntity: TopEntity) {
        val url = Constants.WEB_URL + topEntity.docid + ".html"
        WebActivity.startActivity(mActivity, url)
    }

}