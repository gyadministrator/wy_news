package com.android.wy.news.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.wy.news.R
import com.android.wy.news.databinding.LayoutVideoFullListBinding
import com.android.wy.news.entity.ScreenVideoEntity
import com.android.wy.news.view.ScreenVideoView

class ScreenVideoAdapter(
    itemAdapterListener: OnItemAdapterListener<ScreenVideoEntity>,
    private var screenVideoListener: OnScreenVideoListener
) :
    BaseNewsAdapter<ScreenVideoEntity>(
        itemAdapterListener
    ), ScreenVideoView.OnScreenVideoListener {

    class ScreenViewHolder(itemView: View) : ViewHolder(itemView) {
        private val mBinding = LayoutVideoFullListBinding.bind(itemView)
        var playVideo = mBinding.playVideo
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = getView(parent, R.layout.layout_video_full_list)
        return ScreenViewHolder(view)
    }

    override fun onBindData(holder: ViewHolder, position: Int, data: ScreenVideoEntity) {
        if (holder is ScreenViewHolder) {
            holder.playVideo.setPlayState(data.isPlaying)
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

    }

    override fun onVideoFinish() {
        screenVideoListener.onPlayFinish()
    }

    interface OnScreenVideoListener {
        fun onPlayFinish()
    }
}