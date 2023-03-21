package com.android.wy.news.adapter

import android.R.attr.banner
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutNewsHeaderBannerItemBinding
import com.android.wy.news.databinding.LayoutNewsHeaderItemBinding
import com.android.wy.news.entity.Ad
import com.android.wy.news.entity.NewsHeaderEntity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class HeaderAdapter(var context: Context, var newsListener: OnNewsListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener{
    private var mDataList = ArrayList<NewsHeaderEntity>()

    companion object {
        const val ITEM_TYPE_NORMAL = 0
        const val ITEM_TYPE_BANNER = 1
    }

    class NormalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutNewsHeaderItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvRead = mBinding.tvRead
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
        var ivCover = mBinding.ivCover
    }

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutNewsHeaderBannerItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvDesc = mBinding.tvDesc
        var tvRead = mBinding.tvRead
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
        var banner = mBinding.banner
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return if (viewType == ITEM_TYPE_NORMAL) {
            view = LayoutInflater.from(context)
                .inflate(R.layout.layout_news_header_item, parent, false)
            NormalViewHolder(view)
        } else {
            view = LayoutInflater.from(context)
                .inflate(R.layout.layout_news_header_banner_item, parent, false)
            BannerViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val newsEntity = mDataList[position]
        if (holder is NormalViewHolder) {
            holder.tvTitle.text = newsEntity.title
            holder.tvSource.text = newsEntity.source
            holder.tvTime.text = newsEntity.ptime
            val replyCount = newsEntity.replyCount
            if (replyCount > 0) {
                if (replyCount > 10000) {
                    val fl = replyCount / 10000f
                    holder.tvRead.text = "%.1f".format(fl) + "w评论"
                } else {
                    holder.tvRead.text = replyCount.toString() + "评论"
                }
            }
            CommonTools.loadImage(context, newsEntity.imgsrc, holder.ivCover)
        } else if (holder is BannerViewHolder) {
            holder.tvTitle.text = newsEntity.title
            holder.tvDesc.text = newsEntity.digest
            holder.tvSource.text = newsEntity.source
            holder.tvTime.text = newsEntity.ptime
            val replyCount = newsEntity.replyCount
            if (replyCount > 0) {
                if (replyCount > 10000) {
                    val fl = replyCount / 10000f
                    holder.tvRead.text = "%.1f".format(fl) + "w评论"
                } else {
                    holder.tvRead.text = replyCount.toString() + "评论"
                }
            }
            val ads = newsEntity.ads
            holder.banner.setAdapter(object : BannerImageAdapter<Ad?>(ads) {

                override fun onBindView(
                    holder: BannerImageHolder?,
                    data: Ad?,
                    position: Int,
                    size: Int
                ) {
                    if (holder != null && data != null) {
                        //图片加载自己实现
                        Glide.with(holder.itemView)
                            .load(data.imgsrc)
                            .apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
                            .into(holder.imageView)
                    }
                }
            })
            // .addBannerLifecycleObserver(this) //添加生命周期观察者
            //.setIndicator(CircleIndicator(this))
        }
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(dataList: ArrayList<NewsHeaderEntity>) {
        mDataList.clear()
        mDataList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun loadMoreData(dataList: ArrayList<NewsHeaderEntity>) {
        val originSize = mDataList.size
        mDataList.addAll(dataList)
        notifyItemRangeInserted(originSize + 1, dataList.size)
    }

    interface OnNewsListener {
        fun onNewsItemClickListener(view: View, newsEntity: NewsHeaderEntity)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            val tag = p0.tag as Int
            val newsEntity = mDataList[tag]
            newsListener.onNewsItemClickListener(p0, newsEntity)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (mDataList.size > 0) {
            val headerEntity = mDataList[position]
            val ads = headerEntity.ads
            return if (ads != null && ads.isNotEmpty()) {
                ITEM_TYPE_BANNER
            } else {
                ITEM_TYPE_NORMAL
            }
        }
        return super.getItemViewType(position)
    }
}