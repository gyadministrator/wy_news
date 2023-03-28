package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class VideoAdapter(var context: Context, private var videoListener: OnVideoListener) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>(), View.OnClickListener,
    SeekBar.OnSeekBarChangeListener {
    private var mDataList = ArrayList<VideoEntity>()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_video_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoEntity = mDataList[position]
        holder.tvTitle.text = videoEntity.title
        val playCount = videoEntity.playCount
        if (playCount > 0) {
            if (playCount > 10000) {
                val fl = playCount / 10000f
                holder.tvPlay.text = "%.1f".format(fl) + "w次播放"
            } else {
                holder.tvPlay.text = playCount.toString() + "次播放"
            }
        }
        holder.tvSource.text = videoEntity.topicName

        val time = CommonTools.parseTime(videoEntity.ptime)
        if (TextUtils.isEmpty(time)) {
            holder.tvTime.text = videoEntity.ptime
        } else {
            holder.tvTime.text = time
        }

        val mp4Url = videoEntity.mp4_url
        val proxyUrl = VideoCacheManager.getProxyUrl(context, mp4Url)
        val setUp =
            holder.playVideo.setUp(proxyUrl, JCVideoPlayer.SCREEN_LAYOUT_LIST, "")

        holder.playVideo.progressBar.setOnSeekBarChangeListener(this)
        if (setUp) {
            val thumbImageView = holder.playVideo.thumbImageView
            CommonTools.loadImage(context, videoEntity.fullSizeImg, thumbImageView)
        }

        val videoTopic = videoEntity.videoTopic
        if (videoTopic != null) {
            holder.tvUser.text = videoTopic.tname
            holder.tvUserSource.text = videoTopic.alias
            CommonTools.loadImage(context, videoTopic.topic_icons, holder.ivUser)
        }

        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(dataList: ArrayList<VideoEntity>) {
        mDataList.clear()
        mDataList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun loadMoreData(dataList: ArrayList<VideoEntity>) {
        val originSize = mDataList.size
        mDataList.addAll(dataList)
        notifyItemRangeInserted(originSize + 1, dataList.size)
    }

    interface OnVideoListener {
        fun onVideoItemClickListener(view: View, videoEntity: VideoEntity)
        fun onVideoFinish()
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            val tag = p0.tag as Int
            val videoEntity = mDataList[tag]
            videoListener.onVideoItemClickListener(p0, videoEntity)
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
}