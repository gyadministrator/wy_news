package com.android.wy.news.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.databinding.LayoutVideoFullListBinding
import com.android.wy.news.entity.ScreenVideoEntity
import com.android.wy.news.view.ScreenVideoView

class ScreenVideoAdapter(itemAdapterListener: OnItemAdapterListener<ScreenVideoEntity>,var screenVideoListener: OnScreenVideoListener) :
    BaseNewsAdapter<ScreenVideoAdapter.ScreenViewHolder, ScreenVideoEntity>(
        itemAdapterListener
    ), ScreenVideoView.OnScreenVideoListener {

    class ScreenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutVideoFullListBinding.bind(itemView)
        var playVideo = mBinding.playVideo
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): ScreenViewHolder {
        val view = getView(parent, R.layout.layout_video_full_list)
        return ScreenViewHolder(view)
    }

    override fun onBindData(holder: ScreenViewHolder, data: ScreenVideoEntity) {
        holder.playVideo
            .setTitle(data.title)
            .setPlayCount(data.playCount)
            .setSource(data.source)
            .setTime(data.pTime)
            .setUser(data.user)
            .setUserSource(data.userSource)
            .setUserCover(data.userCover)
            .setUp(data.url, data.videoCover, true)
            .addOnScreenVideoListener(this)

    }

    override fun onVideoFinish() {
        screenVideoListener.onPlayFinish()
    }

    interface OnScreenVideoListener{
        fun onPlayFinish()
    }
}