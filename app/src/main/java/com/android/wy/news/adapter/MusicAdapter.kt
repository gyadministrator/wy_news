package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.activity.MusicMvActivity
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.LayoutMusicItemBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.music.MusicState
import java.util.*


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class MusicAdapter(itemAdapterListener: OnItemAdapterListener<MusicInfo>) :
    BaseNewsAdapter<MusicAdapter.ViewHolder, MusicInfo>(itemAdapterListener) {
    private var selectedPosition = -5

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutMusicItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvDesc = mBinding.tvDesc
        var ivCover = mBinding.ivCover
        var viewLine = mBinding.viewLine
        var tvLossless = mBinding.tvLossless
        var tvMv = mBinding.tvMv
        var tvVip = mBinding.tvVip
        var tvPath = mBinding.tvPath
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
        holder.tvDesc.text = data.name
        val hasMv = data.hasmv
        if (hasMv == 1) {
            holder.tvMv.visibility = View.VISIBLE
        } else {
            holder.tvMv.visibility = View.GONE
        }
        if (data.hasLossless) {
            holder.tvLossless.visibility = View.VISIBLE
        } else {
            holder.tvLossless.visibility = View.GONE
        }
        if (data.isListenFee) {
            holder.tvVip.visibility = View.VISIBLE
        } else {
            holder.tvVip.visibility = View.GONE
        }
        /* val localPath = data.localPath
         if (!TextUtils.isEmpty(localPath)) {
             holder.tvPath.visibility = View.VISIBLE
             holder.tvPath.text = localPath
         } else {
             holder.tvPath.visibility = View.GONE
         }*/
        holder.tvMv.tag = data
        holder.tvMv.setOnClickListener(onMvClickListener)
        CommonTools.loadImage(data.pic, holder.ivCover)
        checkState(holder, position)
    }

    private val onMvClickListener = View.OnClickListener { p0 ->
        val tag = p0?.tag
        if (tag is MusicInfo) {
            val stringBuilder = StringBuilder()
            val name = tag.name
            val artist = tag.artist
            if (!TextUtils.isEmpty(name)) {
                stringBuilder.append(name)
            }
            if (!TextUtils.isEmpty(artist)) {
                stringBuilder.append("-")
                stringBuilder.append(artist)
            }
            p0.context?.let {
                MusicMvActivity.startMv(
                    it,
                    stringBuilder.toString(),
                    tag.pic,
                    tag.musicrid
                )
            }
        }
    }

    private fun checkState(holder: ViewHolder, position: Int) {
        val result = mDataList[position]
        val playMusicInfo = PlayMusicManager.getPlayMusicInfo()
        if (selectedPosition == position && playMusicInfo != null && playMusicInfo.musicrid == result.musicrid) {
            when (result.state) {
                MusicState.STATE_PREPARE -> {
                    holder.viewLine.visibility = View.INVISIBLE
                    holder.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            holder.tvTitle.context,
                            R.color.main_title
                        )
                    )
                    holder.tvDesc.setTextColor(
                        ContextCompat.getColor(
                            holder.tvDesc.context,
                            R.color.second_title
                        )
                    )
                }

                MusicState.STATE_PLAY -> {
                    holder.viewLine.visibility = View.VISIBLE
                    holder.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            holder.tvTitle.context,
                            R.color.text_select_color
                        )
                    )
                    holder.tvDesc.setTextColor(
                        ContextCompat.getColor(
                            holder.tvDesc.context,
                            R.color.text_select_color
                        )
                    )
                }

                MusicState.STATE_PAUSE -> {
                    holder.viewLine.visibility = View.INVISIBLE
                    holder.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            holder.tvTitle.context,
                            R.color.main_title
                        )
                    )
                    holder.tvDesc.setTextColor(
                        ContextCompat.getColor(
                            holder.tvDesc.context,
                            R.color.second_title
                        )
                    )
                }

                MusicState.STATE_ERROR -> {
                    holder.viewLine.visibility = View.INVISIBLE
                    holder.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            holder.tvTitle.context,
                            R.color.main_title
                        )
                    )
                    holder.tvDesc.setTextColor(
                        ContextCompat.getColor(
                            holder.tvDesc.context,
                            R.color.second_title
                        )
                    )
                }

                else -> {
                    holder.viewLine.visibility = View.INVISIBLE
                    holder.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            holder.tvTitle.context,
                            R.color.main_title
                        )
                    )
                    holder.tvDesc.setTextColor(
                        ContextCompat.getColor(
                            holder.tvDesc.context,
                            R.color.second_title
                        )
                    )
                }
            }
        } else {
            holder.viewLine.visibility = View.INVISIBLE
            holder.tvTitle.setTextColor(
                ContextCompat.getColor(
                    holder.tvTitle.context,
                    R.color.main_title
                )
            )
            holder.tvDesc.setTextColor(
                ContextCompat.getColor(
                    holder.tvDesc.context,
                    R.color.second_title
                )
            )
        }
    }
}