package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
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
class TopAdapter(
    itemAdapterListener: OnItemAdapterListener<TopEntity>
) : BaseNewsAdapter<ViewHolder, TopEntity>(itemAdapterListener) {

    companion object {
        const val ITEM_TYPE_NORMAL = 0
        const val ITEM_TYPE_VIDEO = 1
    }

    class NormalViewHolder(itemView: View) : ViewHolder(itemView) {
        private val mBinding = LayoutTopNormalItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvRead = mBinding.tvRead
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
        var ivCover = mBinding.ivCover
    }

    class VideoViewHolder(itemView: View) : ViewHolder(itemView) {
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

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View
        return if (viewType == ITEM_TYPE_VIDEO) {
            view = getView(parent, R.layout.layout_top_video_item)
            VideoViewHolder(view)
        } else {
            view = getView(parent, R.layout.layout_top_normal_item)
            NormalViewHolder(view)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindData(holder: ViewHolder, data: TopEntity) {
        if (holder is NormalViewHolder) {
            holder.tvTitle.text = data.title
            holder.tvSource.text = data.source

            val time = CommonTools.getTimeDiff(data.ptime)
            if (!TextUtils.isEmpty(time)) {
                holder.tvTime.text = time
            } else {
                holder.tvTime.text = data.ptime
            }

            val replyCount = data.replyCount
            if (replyCount > 0) {
                if (replyCount > 10000) {
                    val fl = replyCount / 10000f
                    holder.tvRead.text = "%.1f".format(fl) + "w评论"
                } else {
                    holder.tvRead.text = replyCount.toString() + "评论"
                }
            }
            CommonTools.loadImage(data.imgsrc, holder.ivCover)
        } else if (holder is VideoViewHolder) {
            holder.tvTitle.text = data.title
            holder.tvSource.text = data.source

            val time = CommonTools.getTimeDiff(data.ptime)
            if (!TextUtils.isEmpty(time)) {
                holder.tvTime.text = time
            } else {
                holder.tvTime.text = data.ptime
            }

            val replyCount = data.replyCount
            if (replyCount > 0) {
                if (replyCount > 10000) {
                    val fl = replyCount / 10000f
                    holder.tvComment.text = "%.1f".format(fl) + "w评论"
                } else {
                    holder.tvComment.text = replyCount.toString() + "评论"
                }
            }
            val videoInfo = data.videoinfo
            if (videoInfo != null) {
                val cover = videoInfo.cover

                val mp4Url = videoInfo.mp4_url
                val proxyUrl = VideoCacheManager.getProxyUrl(holder.playVideo.context, mp4Url)

                val setUp = holder.playVideo.setUp(proxyUrl, JCVideoPlayer.SCREEN_LAYOUT_LIST, "")
                if (setUp) {
                    val thumbImageView = holder.playVideo.thumbImageView
                    thumbImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    CommonTools.loadImage(cover, thumbImageView)
                }
            }
            val videoTopic = data.videoTopic
            if (videoTopic != null) {
                holder.tvUser.text = videoTopic.ename
                val certificationText = videoTopic.certificationText
                if (TextUtils.isEmpty(certificationText)) {
                    holder.tvUserSource.text = videoTopic.alias
                } else {
                    holder.tvUserSource.text = certificationText
                }
                CommonTools.loadImage(videoTopic.topic_icons, holder.ivUser)
            }
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