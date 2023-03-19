package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.databinding.LayoutNewsItemBinding
import com.android.wy.news.entity.NewsEntity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class NewsAdapter(var context: Context, var newsListener: OnNewsListener) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>(), View.OnClickListener, View.OnLongClickListener {
    private var mDataList = ArrayList<NewsEntity>()


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutNewsItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvDesc = mBinding.tvDesc
        var tvRead = mBinding.tvRead
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
        var ivCover = mBinding.ivCover
        var ivEye = mBinding.ivEye
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_news_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newsEntity = mDataList[position]
        holder.tvTitle.text = newsEntity.title
        holder.tvDesc.text = newsEntity.digest
        val layoutParams = holder.tvSource.layoutParams as ConstraintLayout.LayoutParams
        if (newsEntity.commentCount > 0) {
            holder.ivEye.visibility = View.VISIBLE
            layoutParams.marginStart = 15
            holder.tvRead.text = newsEntity.commentCount.toString()
        } else {
            holder.ivEye.visibility = View.GONE
            layoutParams.marginStart = 0
        }
        holder.tvSource.layoutParams = layoutParams
        holder.tvSource.text = newsEntity.source
        holder.tvTime.text = newsEntity.ptime
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).override(
            com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
            com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
        )//关键代码，加载原始大小
            .format(DecodeFormat.PREFER_RGB_565)//设置为这种格式去掉透明度通道，可以减少内存占有
            .placeholder(R.mipmap.img_default).error(R.mipmap.img_error)
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(newsEntity.imgsrc)
            .into(holder.ivCover)
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
        holder.itemView.setOnLongClickListener(this)
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
        fun onNewsItemLongClickListener(view: View, newsEntity: NewsEntity)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            val tag = p0.tag as Int
            val newsEntity = mDataList[tag]
            newsListener.onNewsItemClickListener(p0, newsEntity)
        }
    }

    override fun onLongClick(p0: View?): Boolean {
        if (p0 != null) {
            val tag = p0.tag as Int
            val newsEntity = mDataList[tag]
            newsListener.onNewsItemLongClickListener(p0, newsEntity)
        }
        return true
    }
}