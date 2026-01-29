package com.android.wy.news.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.entity.music.RecommendMusicType
import com.youth.banner.adapter.BannerAdapter

/*
  * @Author:         gao_yun
  * @CreateDate:     2023/3/28 15:06
  * @Version:        1.0
  * @Description:
 */
class MineBannerImgAdapter(dataList: List<RecommendMusicType>) :
    BannerAdapter<RecommendMusicType, MineBannerImgAdapter.BannerImageViewHolder>(dataList) {


    class BannerImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    }

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerImageViewHolder {
        val view =
            LayoutInflater.from(parent!!.context)
                .inflate(R.layout.layout_mine_banner_item, parent, false)
        return BannerImageViewHolder(view)
    }

    override fun onBindView(
        holder: BannerImageViewHolder?,
        data: RecommendMusicType?,
        position: Int,
        size: Int
    ) {
        if (holder != null) {
            if (data != null) {
                val img = data.img
                CommonTools.loadImage(img, holder.ivCover, 120, 120)
            }
            if (data != null) {
                holder.tvTitle.text = data.name
            }
        }
    }
}