package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutSingerMvItemBinding
import com.android.wy.news.entity.music.Album


/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/6/16 16:49
  * @Version:        1.0
  * @Description:    
 */
class SingerAlbumAdapter(itemAdapterListener: OnItemAdapterListener<Album>) :
    BaseNewsAdapter<Album>(
        itemAdapterListener
    ) {

    class SingerAlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutSingerMvItemBinding.bind(itemView)
        var ivCover = mBinding.ivCover
        var llPlayCount = mBinding.llPlayCount
        var tvPlayCount = mBinding.tvPlayCount
        var tvTime = mBinding.tvTime
        var tvDesc = mBinding.tvDesc
        var tvTitle = mBinding.tvTitle
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = getView(parent, R.layout.layout_singer_mv_item)
        return SingerAlbumHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindData(holder: ViewHolder, position: Int, data: Album) {
        if (holder is SingerAlbumHolder) {
            CommonTools.loadImage(data.pic, holder.ivCover)
            holder.llPlayCount.visibility = View.GONE
            holder.tvTitle.text = data.album
            holder.tvDesc.text = data.albuminfo
        }
    }
}