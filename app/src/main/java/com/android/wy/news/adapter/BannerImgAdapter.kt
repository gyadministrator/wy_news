package com.android.wy.news.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.entity.House
import com.youth.banner.adapter.BannerAdapter

/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/28 15:06
  * @Version:        1.0
  * @Description:
 */
class BannerImgAdapter(dataList: ArrayList<House>) :
    BannerAdapter<House, BannerImgAdapter.BannerImageViewHolder>(dataList) {


    class BannerImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivCover: ImageView
        var tvTitle: TextView

        init {
            ivCover = itemView.findViewById(R.id.iv_cover)
            tvTitle = itemView.findViewById(R.id.tv_title)
        }
    }

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerImageViewHolder {
        val view =
            LayoutInflater.from(parent!!.context)
                .inflate(R.layout.layout_banner_item, parent, false)
        return BannerImageViewHolder(view)
    }

    override fun onBindView(
        holder: BannerImageViewHolder?,
        data: House?,
        position: Int,
        size: Int
    ) {
        if (holder != null) {
            if (data != null) {
                val picInfo = data.picInfo
                if (picInfo != null && picInfo.isNotEmpty()) {
                    val info = picInfo[0]
                    info?.let { CommonTools.loadImage(it.url, holder.ivCover) }
                }
            }
            if (data != null) {
                holder.tvTitle.text = data.title
            }
        }
    }
}