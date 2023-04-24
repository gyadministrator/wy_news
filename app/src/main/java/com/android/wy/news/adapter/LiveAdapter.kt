package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutLiveItemBinding
import com.android.wy.news.entity.LiveReview
import java.util.*


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class LiveAdapter(
    itemAdapterListener: OnItemAdapterListener<LiveReview>
) : BaseNewsAdapter<LiveAdapter.ViewHolder, LiveReview>(itemAdapterListener) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutLiveItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvRead = mBinding.tvRead
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
        var ivCover = mBinding.ivCover
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = getView(parent, R.layout.layout_live_item)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindData(holder: ViewHolder, data: LiveReview) {
        holder.tvTitle.text = data.roomName
        val playCount = data.userCount
        if (playCount > 0) {
            if (playCount > 10000) {
                val fl = playCount / 10000f
                holder.tvRead.text = "%.1f".format(fl) + "w人已看"
            } else {
                holder.tvRead.text = playCount.toString() + "人已看"
            }
        }
        holder.tvSource.text = data.source

        val time = CommonTools.getTimeDiff(data.startTime)
        if (!TextUtils.isEmpty(time)) {
            holder.tvTime.text = "直播时间: $time"
        } else {
            holder.tvTime.text = "直播时间: " + data.startTime
        }

        val pcImage = data.pcImage
        var url: String = pcImage
        if (!TextUtils.isEmpty(pcImage)) {
            if (pcImage.contains("\"")) {
                url = pcImage.replace("\"", "")
            }
        }
        if (!TextUtils.isEmpty(url)) {
            CommonTools.loadImage(url, holder.ivCover)
        }
    }
}