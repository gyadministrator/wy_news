package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.cache.VideoCacheManager
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutVideoItemBinding
import com.android.wy.news.entity.VideoEntity
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer


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
    BaseNewsAdapter<VideoAdapter.ViewHolder, VideoEntity>(itemAdapterListener),
    SeekBar.OnSeekBarChangeListener {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutVideoItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvPlay = mBinding.tvPlay
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
        var playVideo = mBinding.playVideo
        var ivUser = mBinding.ivUser
        var tvUser = mBinding.tvUser
        var tvUserSource = mBinding.tvUserSource
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = getView(parent, R.layout.layout_video_item)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindData(holder: ViewHolder, data: VideoEntity) {
        holder.tvTitle.text = data.title
        val playCount = data.playCount
        if (playCount > 0) {
            if (playCount > 10000) {
                val fl = playCount / 10000f
                holder.tvPlay.text = "%.1f".format(fl) + "w次播放"
            } else {
                holder.tvPlay.text = playCount.toString() + "次播放"
            }
        }
        holder.tvSource.text = data.topicName

        val time = CommonTools.getTimeDiff(data.ptime)
        if (TextUtils.isEmpty(time)) {
            holder.tvTime.text = data.ptime
        } else {
            holder.tvTime.text = time
        }

        val mp4Url = data.mp4_url
        val proxyUrl = VideoCacheManager.getProxyUrl(holder.playVideo.context, mp4Url)
        val setUp = holder.playVideo.setUp(proxyUrl, JCVideoPlayer.SCREEN_LAYOUT_LIST, "")

        holder.playVideo.progressBar.setOnSeekBarChangeListener(this)
        if (setUp) {
            val thumbImageView = holder.playVideo.thumbImageView
            thumbImageView.scaleType=ImageView.ScaleType.CENTER_CROP
            CommonTools.loadImage(data.fullSizeImg, thumbImageView)
        }

        val videoTopic = data.videoTopic
        if (videoTopic != null) {
            holder.tvUser.text = videoTopic.tname
            holder.tvUserSource.text = videoTopic.alias
            CommonTools.loadImage(videoTopic.topic_icons, holder.ivUser)
        }
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (p1 == 100) {
            videoListener.onVideoFinish()
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

    interface OnVideoListener {
        fun onVideoFinish()
    }
}