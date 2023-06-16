package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutSingerMvItemBinding
import com.android.wy.news.databinding.LayoutTopNormalItemBinding
import com.android.wy.news.entity.music.Mvlist


/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/6/16 16:49
  * @Version:        1.0
  * @Description:    
 */
class SingerMvAdapter(itemAdapterListener: OnItemAdapterListener<Mvlist>) :
    BaseNewsAdapter<SingerMvAdapter.SingerMvHolder, Mvlist>(
        itemAdapterListener
    ) {

    class SingerMvHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutSingerMvItemBinding.bind(itemView)
        var ivCover = mBinding.ivCover
        var llPlayCount = mBinding.llPlayCount
        var tvPlayCount = mBinding.tvPlayCount
        var tvTime = mBinding.tvTime
        var tvDesc = mBinding.tvDesc
        var tvTitle = mBinding.tvTitle
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): SingerMvHolder {
        val view = getView(parent, R.layout.layout_singer_mv_item)
        return SingerMvHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindData(holder: SingerMvHolder, position: Int, data: Mvlist) {
        CommonTools.loadImage(data.pic, holder.ivCover)
        val duration = data.duration
        val time = CommonTools.second2Time(duration.toLong())
        holder.tvTime.text = time
        val mvPlayCnt = data.mvPlayCnt
        if (mvPlayCnt > 0) {
            if (mvPlayCnt > 10000) {
                val fl = mvPlayCnt / 10000f
                holder.tvPlayCount.text = "%.1f".format(fl) + "w播放"
            } else {
                holder.tvPlayCount.text = mvPlayCnt.toString() + "播放"
            }
        } else {
            holder.llPlayCount.visibility = View.GONE
        }
        holder.tvTitle.text = data.name
        holder.tvDesc.text = data.artist
    }
}