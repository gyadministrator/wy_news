package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.wy.news.R
import com.android.wy.news.activity.VideoFullActivity
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutTopNormalItemBinding
import com.android.wy.news.databinding.LayoutTopVideoItemBinding
import com.android.wy.news.entity.ScreenVideoEntity
import com.android.wy.news.entity.TopEntity


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class TopAdapter(
    itemAdapterListener: OnItemAdapterListener<TopEntity>
) : BaseNewsAdapter<TopEntity>(itemAdapterListener) {
    private val videoList = ArrayList<ScreenVideoEntity>()

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
        var ivVideoCover = mBinding.ivVideoCover
        var tvPlay = mBinding.tvPlay
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
        var tvDuration = mBinding.tvDuration
        var tvCategory = mBinding.tvCategory
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
    override fun onBindData(holder: ViewHolder, position: Int, data: TopEntity) {
        val title = data.title
        val source = data.source
        val time = CommonTools.getTimeDiff(data.ptime)
        val replyCount = data.replyCount

        if (holder is NormalViewHolder) {
            if (!TextUtils.isEmpty(title)) {
                holder.tvTitle.visibility = View.VISIBLE
                holder.tvTitle.text = title
            }
            if (!TextUtils.isEmpty(source)) {
                holder.tvSource.visibility = View.VISIBLE
                holder.tvSource.text = source
            }

            if (!TextUtils.isEmpty(time)) {
                holder.tvTime.visibility = View.VISIBLE
                holder.tvTime.text = time
            } else {
                holder.tvTime.visibility = View.VISIBLE
                holder.tvTime.text = data.ptime
            }

            if (replyCount > 0) {
                if (replyCount > 10000) {
                    val fl = replyCount / 10000f
                    holder.tvRead.text = "%.1f".format(fl) + "w评论"
                } else {
                    holder.tvRead.text = replyCount.toString() + "评论"
                }
                holder.tvRead.visibility = View.VISIBLE
            }
            CommonTools.loadImage(data.imgsrc, holder.ivCover)
        } else if (holder is VideoViewHolder) {
            if (!TextUtils.isEmpty(title)) {
                holder.tvTitle.visibility = View.VISIBLE
                holder.tvTitle.text = title
            }
            if (!TextUtils.isEmpty(source)) {
                holder.tvSource.visibility = View.VISIBLE
                holder.tvSource.text = source
            }

            if (!TextUtils.isEmpty(time)) {
                holder.tvTime.visibility = View.VISIBLE
                holder.tvTime.text = time
            } else {
                holder.tvTime.visibility = View.VISIBLE
                holder.tvTime.text = data.ptime
            }

            val category = data.category
            if (!TextUtils.isEmpty(category)) {
                holder.tvCategory.visibility = View.VISIBLE
                holder.tvCategory.text = category
            }

            if (replyCount > 0) {
                if (replyCount > 10000) {
                    val fl = replyCount / 10000f
                    holder.tvPlay.text = "%.1f".format(fl) + "w次播放"
                } else {
                    holder.tvPlay.text = replyCount.toString() + "次播放"
                }
                holder.tvPlay.visibility = View.VISIBLE
            }
            val videoInfo = data.videoinfo
            if (videoInfo != null) {
                val cover = videoInfo.cover
                CommonTools.loadImage(cover, holder.ivVideoCover)
                holder.tvDuration.text =
                    CommonTools.second2Time(videoInfo.video_data.duration.toLong())
                holder.ivVideoCover.tag = holder.adapterPosition
                holder.ivVideoCover.setOnClickListener(coverClickListener)
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

    private val coverClickListener = View.OnClickListener { p0 ->
        if (p0 != null) {
            videoList.clear()
            val position = p0.tag as Int
            val list = CommonTools.topEntity2ScreenVideoEntity(position, mDataList)
            videoList.addAll(list)
            VideoFullActivity.startFullScreen(videoList, p0.context)
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
}