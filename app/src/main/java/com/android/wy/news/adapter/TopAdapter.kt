package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.cache.VideoCacheManager
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutTopNormalItemBinding
import com.android.wy.news.databinding.LayoutTopVideoItemBinding
import com.android.wy.news.entity.TopEntity
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class TopAdapter(var context: Context, private var topListener: OnTopListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
    private var mDataList = ArrayList<TopEntity>()

    companion object {
        const val ITEM_TYPE_NORMAL = 0
        const val ITEM_TYPE_VIDEO = 1
    }

    class NormalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutTopNormalItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvRead = mBinding.tvRead
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
        var ivCover = mBinding.ivCover
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutTopVideoItemBinding.bind(itemView)
        var ivUser = mBinding.ivUser
        var tvUser = mBinding.tvUser
        var tvUserSource = mBinding.tvUserSource
        var tvTitle = mBinding.tvTitle
        var playVideo = mBinding.playVideo
        var tvComment = mBinding.tvComment
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return if (viewType == ITEM_TYPE_VIDEO) {
            view =
                LayoutInflater.from(context).inflate(R.layout.layout_top_video_item, parent, false)
            VideoViewHolder(view)
        } else {
            view =
                LayoutInflater.from(context).inflate(R.layout.layout_top_normal_item, parent, false)
            NormalViewHolder(view)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val topEntity = mDataList[position]
        if (holder is NormalViewHolder) {
            holder.tvTitle.text = topEntity.title
            holder.tvSource.text = topEntity.source

            val time = CommonTools.parseTime(topEntity.ptime)
            if (!TextUtils.isEmpty(time)) {
                holder.tvTime.text = time
            } else {
                holder.tvTime.text = topEntity.ptime
            }

            val replyCount = topEntity.replyCount
            if (replyCount > 0) {
                if (replyCount > 10000) {
                    val fl = replyCount / 10000f
                    holder.tvRead.text = "%.1f".format(fl) + "w评论"
                } else {
                    holder.tvRead.text = replyCount.toString() + "评论"
                }
            }
            CommonTools.loadImage(context, topEntity.imgsrc, holder.ivCover)
        } else if (holder is VideoViewHolder) {
            holder.tvTitle.text = topEntity.title
            holder.tvSource.text = topEntity.source

            val time = CommonTools.parseTime(topEntity.ptime)
            if (!TextUtils.isEmpty(time)) {
                holder.tvTime.text = time
            } else {
                holder.tvTime.text = topEntity.ptime
            }

            val replyCount = topEntity.replyCount
            if (replyCount > 0) {
                if (replyCount > 10000) {
                    val fl = replyCount / 10000f
                    holder.tvComment.text = "%.1f".format(fl) + "w评论"
                } else {
                    holder.tvComment.text = replyCount.toString() + "评论"
                }
            }
            val videoInfo = topEntity.videoinfo
            if (videoInfo != null) {
                val cover = videoInfo.cover

                val mp4Url = videoInfo.mp4_url
                val proxyUrl = VideoCacheManager.getProxyUrl(context, mp4Url)

                val setUp = holder.playVideo.setUp(proxyUrl, JCVideoPlayer.SCREEN_LAYOUT_LIST, "")
                if (setUp) {
                    val thumbImageView = holder.playVideo.thumbImageView
                    thumbImageView.adjustViewBounds = true
                    thumbImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    CommonTools.loadImage(context, cover, thumbImageView)
                }
            }
            val videoTopic = topEntity.videoTopic
            if (videoTopic != null) {
                holder.tvUser.text = videoTopic.ename
                val certificationText = videoTopic.certificationText
                if (TextUtils.isEmpty(certificationText)) {
                    holder.tvUserSource.text = videoTopic.alias
                } else {
                    holder.tvUserSource.text = certificationText
                }
                CommonTools.loadImage(context, videoTopic.topic_icons, holder.ivUser)
            }
        }
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(dataList: ArrayList<TopEntity>) {
        mDataList.clear()
        mDataList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun loadMoreData(dataList: ArrayList<TopEntity>) {
        val originSize = mDataList.size
        mDataList.addAll(dataList)
        notifyItemRangeInserted(originSize + 1, dataList.size)
    }

    interface OnTopListener {
        fun onTopItemClickListener(view: View, topEntity: TopEntity)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            val tag = p0.tag as Int
            val topEntity = mDataList[tag]
            topListener.onTopItemClickListener(p0, topEntity)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (mDataList.size > 0) {
            val topEntity = mDataList[position]
            val videoInfo = topEntity.videoinfo
            return if (videoInfo != null) {
                ITEM_TYPE_VIDEO
            } else {
                ITEM_TYPE_NORMAL
            }
        }
        return super.getItemViewType(position)
    }

    fun onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return
        }
    }

    fun onPause() {
        JCVideoPlayer.releaseAllVideos()
    }
}