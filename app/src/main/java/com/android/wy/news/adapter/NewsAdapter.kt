package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutNewsItemBinding
import com.android.wy.news.entity.NewsEntity


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class NewsAdapter(var context: Context, var newsListener: OnNewsListener) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>(), View.OnClickListener{
    private var mDataList = ArrayList<NewsEntity>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutNewsItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvRead = mBinding.tvRead
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
        var ivCover = mBinding.ivCover
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_news_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newsEntity = mDataList[position]
        holder.tvTitle.text = newsEntity.title
        val commentCount = newsEntity.commentCount
        if (commentCount > 0) {
            if (commentCount > 10000) {
                val fl = commentCount / 10000f
                holder.tvRead.text = "%.1f".format(fl) + "w评论"
            } else {
                holder.tvRead.text = commentCount.toString() + "评论"
            }
        }
        holder.tvSource.text = newsEntity.source
        holder.tvTime.text = newsEntity.ptime

        CommonTools.loadImage(context,newsEntity.imgsrc,holder.ivCover)

        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(dataList: ArrayList<NewsEntity>) {
        mDataList.clear()
        mDataList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun loadMoreData(dataList: ArrayList<NewsEntity>) {
        val originSize = mDataList.size
        mDataList.addAll(dataList)
        notifyItemRangeInserted(originSize + 1, dataList.size)
    }

    interface OnNewsListener {
        fun onNewsItemClickListener(view: View, newsEntity: NewsEntity)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            val tag = p0.tag as Int
            val newsEntity = mDataList[tag]
            newsListener.onNewsItemClickListener(p0, newsEntity)
        }
    }
}