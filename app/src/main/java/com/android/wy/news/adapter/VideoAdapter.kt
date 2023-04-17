package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.databinding.LayoutVideoFullListBinding
import com.android.wy.news.entity.VideoEntity
import com.android.wy.news.view.ScreenVideoView


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class VideoAdapter(
    itemAdapterListener: OnItemAdapterListener<VideoEntity>,
    private var videoListener: OnVideoListener
) :
    BaseNewsAdapter<VideoAdapter.VideoViewHolder, VideoEntity>(itemAdapterListener),
    ScreenVideoView.OnScreenVideoListener {

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutVideoFullListBinding.bind(itemView)
        var playVideo = mBinding.playVideo
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = getView(parent, R.layout.layout_video_full_list)
        return VideoViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindData(holder: VideoViewHolder, data: VideoEntity) {
        data.videoTopic?.let {
            holder.playVideo
                .setTitle(data.title)
                .setPlayCount(data.playCount.toLong())
                .setSource(data.topicName)
                .setTime(data.ptime)
                .setUser(it.ename)
                .setUserSource(data.videoTopic.alias)
                .setUserCover(data.videoTopic.topic_icons)
                .setUp(data.mp4_url, data.fullSizeImg, true)
                .addOnScreenVideoListener(this)
        }
    }

    interface OnVideoListener {
        fun onVideoFinish()
    }

    override fun onVideoFinish() {
        videoListener.onVideoFinish()
    }
}