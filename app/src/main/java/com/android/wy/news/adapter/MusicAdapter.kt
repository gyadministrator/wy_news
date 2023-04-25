package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutMusicItemBinding
import com.android.wy.news.entity.music.MusicResult
import java.util.*


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class MusicAdapter(
    itemAdapterListener: OnItemAdapterListener<MusicResult>
) : BaseNewsAdapter<MusicAdapter.ViewHolder, MusicResult>(itemAdapterListener) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutMusicItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvPlay = mBinding.tvPlay
        var ivCover = mBinding.ivCover
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = getView(parent, R.layout.layout_music_item)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindData(holder: ViewHolder, data: MusicResult) {
        holder.tvTitle.text = data.title
        val trackCount = data.trackCount
        if (trackCount is Int) {
            if (trackCount > 0) {
                if (trackCount > 10000) {
                    val fl = trackCount / 10000f
                    holder.tvPlay.text = "%.1f".format(fl) + "w次播放"
                } else {
                    holder.tvPlay.text = trackCount.toString() + "次播放"
                }
            }
        }
        CommonTools.loadImage(data.pic, holder.ivCover)
    }
}