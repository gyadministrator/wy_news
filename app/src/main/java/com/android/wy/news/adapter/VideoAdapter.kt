package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutVideoItemBinding
import com.android.wy.news.entity.VideoEntity


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class VideoAdapter(var context: Context, var newsListener: OnNewsListener) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>(), View.OnClickListener{
    private var mDataList = ArrayList<VideoEntity>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutVideoItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvRead = mBinding.tvRead
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
        var ivCover = mBinding.ivCover
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_video_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoEntity = mDataList[position]
        holder.tvTitle.text = videoEntity.title
        val playCount = videoEntity.playCount
        if (playCount > 0) {
            if (playCount > 10000) {
                val fl = playCount / 10000f
                holder.tvRead.text = "%.1f".format(fl) + "w次播放"
            } else {
                holder.tvRead.text = playCount.toString() + "次播放"
            }
        }
        holder.tvSource.text = videoEntity.topicName
        holder.tvTime.text = videoEntity.ptime

        CommonTools.loadImage(context,videoEntity.cover,holder.ivCover)

        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(dataList: ArrayList<VideoEntity>) {
        mDataList.clear()
        mDataList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun loadMoreData(dataList: ArrayList<VideoEntity>) {
        val originSize = mDataList.size
        mDataList.addAll(dataList)
        notifyItemRangeInserted(originSize + 1, dataList.size)
    }

    interface OnNewsListener {
        fun onNewsItemClickListener(view: View, videoEntity: VideoEntity)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            val tag = p0.tag as Int
            val videoEntity = mDataList[tag]
            newsListener.onNewsItemClickListener(p0, videoEntity)
        }
    }
}