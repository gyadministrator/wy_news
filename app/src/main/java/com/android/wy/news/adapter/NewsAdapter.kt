package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutItemImageBinding
import com.android.wy.news.databinding.LayoutNewsItemImageAdapterBinding
import com.android.wy.news.databinding.LayoutNewsItemNormalAdapterBinding
import com.android.wy.news.entity.NewsEntity
import com.android.wy.news.util.TaskUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class NewsAdapter(
    itemAdapterListener: OnItemAdapterListener<NewsEntity>
) : BaseNewsAdapter<NewsEntity>(itemAdapterListener) {
    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ITEM_TYPE_IMAGE -> {
                val view = getView(parent, R.layout.layout_news_item_image_adapter)
                ImageViewHolder(view)
            }

            else -> {
                val view = getView(parent, R.layout.layout_news_item_normal_adapter)
                NormalViewHolder(view)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindData(holder: ViewHolder, position: Int, data: NewsEntity) {
        when (holder) {
            is ImageViewHolder -> {
                setNewsContent(
                    holder.tvTitle, holder.tvComment, holder.tvSource, holder.tvTime, data
                )
                loadImage(holder, data)
            }

            is NormalViewHolder -> {
                holder.tvTitle.text = data.title
                if (TextUtils.isEmpty(data.digest)) {
                    holder.tvDesc.visibility = View.GONE
                } else {
                    holder.tvDesc.text = data.digest
                }
                val commentCount = data.commentCount
                if (commentCount > 0) {
                    if (commentCount > 10000) {
                        val fl = commentCount / 10000f
                        holder.tvComment.text = "%.1f".format(fl) + "w评论"
                    } else {
                        holder.tvComment.text = commentCount.toString() + "评论"
                    }
                    holder.tvComment.visibility = View.VISIBLE
                }
                val source = data.source
                if (!TextUtils.isEmpty(source)) {
                    holder.tvSource.visibility = View.VISIBLE
                    holder.tvSource.text = source
                }

                val time = CommonTools.getTimeDiff(data.ptime)
                if (!TextUtils.isEmpty(time)) {
                    holder.tvTime.visibility = View.VISIBLE
                    holder.tvTime.text = time
                } else {
                    holder.tvTime.visibility = View.VISIBLE
                    holder.tvTime.text = data.ptime
                }
            }
        }
    }

    private var imgCount: Int = 1

    companion object {
        const val ITEM_TYPE_NORMAL = 0
        const val ITEM_TYPE_IMAGE = 1
    }

    class NormalViewHolder(itemView: View) : ViewHolder(itemView) {
        private val mBinding = LayoutNewsItemNormalAdapterBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvComment = mBinding.tvComment
        var tvDesc = mBinding.tvDesc
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
    }

    class ImageViewHolder(itemView: View) : ViewHolder(itemView) {
        private val mBinding = LayoutNewsItemImageAdapterBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvComment = mBinding.tvComment
        var llContent = mBinding.llContent
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
    }

    @SuppressLint("SetTextI18n")
    private fun setNewsContent(
        tvTitle: TextView,
        tvComment: TextView,
        tvSource: TextView,
        tvTime: TextView,
        newsEntity: NewsEntity
    ) {
        tvTitle.text = newsEntity.title
        val commentCount = newsEntity.commentCount
        if (commentCount > 0) {
            if (commentCount > 10000) {
                val fl = commentCount / 10000f
                tvComment.text = "%.1f".format(fl) + "w评论"
            } else {
                tvComment.text = commentCount.toString() + "评论"
            }
            tvComment.visibility = View.VISIBLE
        }
        val source = newsEntity.source
        if (!TextUtils.isEmpty(source)) {
            tvSource.visibility = View.VISIBLE
            tvSource.text = source
        }

        val time = CommonTools.getTimeDiff(newsEntity.ptime)
        if (!TextUtils.isEmpty(time)) {
            tvTime.visibility = View.VISIBLE
            tvTime.text = time
        } else {
            tvTime.visibility = View.VISIBLE
            tvTime.text = newsEntity.ptime
        }
    }

    private fun loadImage(holder: ImageViewHolder, newsEntity: NewsEntity) {
        if (newsEntity.hasImg == 1) {
            loadOneImage(newsEntity.imgsrc, holder, true)
        } else {
            val imgExtra = newsEntity.imgextra
            if (!imgExtra.isNullOrEmpty()) {
                imgCount = if (imgExtra.size > 3) {
                    3
                } else {
                    imgExtra.size
                }
                for (i in 0 until imgCount) {
                    TaskUtil.runOnThread {
                        val imageExtra = imgExtra[i]
                        if (imgCount == 1) {
                            loadOneImage(imageExtra.imgsrc, holder, true)
                        } else {
                            loadOneImage(imageExtra.imgsrc, holder, false)
                        }
                    }
                }
            }
        }
    }

    private fun loadOneImage(imgSrc: String, holder: ImageViewHolder, isHasOne: Boolean) {
        holder.llContent.removeAllViews()
        Glide.with(holder.llContent.context).asBitmap().load(imgSrc)
            //.apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .diskCacheStrategy(DiskCacheStrategy.ALL).override(
                //关键代码，加载原始大小
                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
            )
            //设置为这种格式去掉透明度通道，可以减少内存占有
            .format(DecodeFormat.PREFER_RGB_565)
            //.placeholder(R.mipmap.img_default)
            //.error(R.mipmap.img_error)
            .into(object : CustomTarget<Bitmap>(
                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
            ) {
                override fun onResourceReady(
                    resource: Bitmap, transition: Transition<in Bitmap?>?
                ) {
                    val mBinding =
                        LayoutItemImageBinding.inflate(LayoutInflater.from(holder.llContent.context))
                    val layoutParams = mBinding.ivCover.layoutParams as RelativeLayout.LayoutParams
                    val height = resource.height
                    val screenHeight = CommonTools.getScreenHeight()
                    val screenWidth = CommonTools.getScreenWidth()
                    val i = screenHeight / 5
                    if (isHasOne) {
                        layoutParams.width = screenWidth
                        if (height > i) {
                            layoutParams.height = height
                        } else {
                            layoutParams.height = i
                        }
                    } else {
                        layoutParams.width = screenWidth / imgCount
                        if (height > i) {
                            layoutParams.height = height
                        } else {
                            layoutParams.height = i
                        }
                    }
                    mBinding.ivCover.layoutParams = layoutParams
                    mBinding.ivCover.setImageBitmap(resource)
                    val root = mBinding.root
                    if (root.parent != null) {
                        val viewGroup = root.parent as ViewGroup
                        viewGroup.removeView(root)
                    }
                    holder.llContent.addView(root)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    override fun getItemViewType(position: Int): Int {
        val newsEntity = mDataList[position]
        val hasImg = newsEntity.hasImg
        val imgExtra = newsEntity.imgextra
        if (hasImg == 1 || !imgExtra.isNullOrEmpty()) {
            return ITEM_TYPE_IMAGE
        }
        return ITEM_TYPE_NORMAL
    }
}