package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutLiveItemBinding
import com.android.wy.news.entity.LiveReview
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class LiveAdapter(var context: Context, var newsListener: OnNewsListener) :
    RecyclerView.Adapter<LiveAdapter.ViewHolder>(), View.OnClickListener {
    private var mDataList = ArrayList<LiveReview>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutLiveItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvRead = mBinding.tvRead
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
        var ivCover = mBinding.ivCover
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_live_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val liveReview = mDataList[position]
        holder.tvTitle.text = liveReview.roomName
        val playCount = liveReview.userCount
        if (playCount > 0) {
            if (playCount > 10000) {
                val fl = playCount / 10000f
                holder.tvRead.text = "%.1f".format(fl) + "w人已看"
            } else {
                holder.tvRead.text = playCount.toString() + "人已看"
            }
        }
        holder.tvSource.text = liveReview.source

        val time = CommonTools.parseTime(liveReview.startTime)
        if (!TextUtils.isEmpty(time)) {
            holder.tvTime.text = "直播时间: $time"
        } else {
            holder.tvTime.text = "直播时间: " + liveReview.startTime
        }

        val pcImage = liveReview.pcImage
        var url: String = pcImage
        if (!TextUtils.isEmpty(pcImage)) {
            if (pcImage.contains("\"")) {
                url = pcImage.replace("\"", "")
            }
        }
        if (!TextUtils.isEmpty(url)) {
            CommonTools.loadImage(context, url, holder.ivCover)
        }

        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(dataList: ArrayList<LiveReview>) {
        mDataList.clear()
        mDataList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun loadMoreData(dataList: ArrayList<LiveReview>) {
        val originSize = mDataList.size
        mDataList.addAll(dataList)
        notifyItemRangeInserted(originSize + 1, dataList.size)
    }

    interface OnNewsListener {
        fun onNewsItemClickListener(view: View, liveReview: LiveReview)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            val tag = p0.tag as Int
            val liveReview = mDataList[tag]
            newsListener.onNewsItemClickListener(p0, liveReview)
        }
    }
}