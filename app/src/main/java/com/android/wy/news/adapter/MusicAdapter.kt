package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.LayoutMusicItemBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.music.MusicState
import java.util.*


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class MusicAdapter(itemAdapterListener: OnItemAdapterListener<MusicInfo>) : BaseNewsAdapter<MusicAdapter.ViewHolder, MusicInfo>(itemAdapterListener) {
    private var selectedPosition = -5

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutMusicItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvDesc = mBinding.tvDesc
        var ivCover = mBinding.ivCover
        var ivPlay = mBinding.ivPlay
        var ivStateLoading = mBinding.ivStateLoading
        var ivStatePlay = mBinding.ivStatePlay
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedIndex(position: Int) {
        Logger.i("setSelectedIndex: $position")
        selectedPosition = position
        notifyItemChanged(position)
        notifyDataSetChanged()
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = getView(parent, R.layout.layout_music_item)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindData(holder: ViewHolder, position: Int, data: MusicInfo) {
        holder.tvTitle.text = data.artist
        holder.tvDesc.text = data.album
        CommonTools.loadImage(data.pic, holder.ivCover)
        checkState(holder, position)
    }

    private fun checkState(holder: ViewHolder, position: Int) {
        val result = mDataList[position]
        if (selectedPosition == position) {
            when (result.state) {
                MusicState.STATE_PREPARE -> {
                    holder.ivPlay.setImageResource(R.mipmap.music_pause)
                    holder.ivStateLoading.visibility = View.VISIBLE
                    holder.ivStateLoading.show()

                    holder.ivStatePlay.visibility = View.GONE
                    holder.ivStatePlay.hide()
                }

                MusicState.STATE_PLAY -> {
                    holder.ivPlay.setImageResource(R.mipmap.music_play)
                    holder.ivStatePlay.visibility = View.VISIBLE
                    holder.ivStatePlay.show()

                    holder.ivStateLoading.visibility = View.GONE
                    holder.ivStateLoading.hide()
                }

                MusicState.STATE_PAUSE -> {
                    holder.ivPlay.setImageResource(R.mipmap.music_pause)
                    holder.ivStateLoading.visibility = View.GONE
                    holder.ivStateLoading.hide()
                    holder.ivStatePlay.visibility = View.GONE
                    holder.ivStatePlay.hide()
                }

                MusicState.STATE_ERROR -> {
                    holder.ivPlay.setImageResource(R.mipmap.music_pause)
                    holder.ivStateLoading.visibility = View.GONE
                    holder.ivStateLoading.hide()
                    holder.ivStatePlay.visibility = View.GONE
                    holder.ivStatePlay.hide()
                }

                else -> {
                    holder.ivPlay.setImageResource(R.mipmap.music_pause)
                    holder.ivStateLoading.visibility = View.GONE
                    holder.ivStateLoading.hide()
                    holder.ivStatePlay.visibility = View.GONE
                    holder.ivStatePlay.hide()
                }
            }
        } else {
            holder.ivPlay.setImageResource(R.mipmap.music_pause)
            holder.ivStateLoading.visibility = View.GONE
            holder.ivStateLoading.hide()
            holder.ivStatePlay.visibility = View.GONE
            holder.ivStatePlay.hide()
        }
    }
}