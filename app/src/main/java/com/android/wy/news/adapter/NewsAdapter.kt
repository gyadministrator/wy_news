package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.LayoutItemImageBinding
import com.android.wy.news.databinding.LayoutNewsItemImageAdapterBinding
import com.android.wy.news.databinding.LayoutNewsItemNormalAdapterBinding
import com.android.wy.news.entity.NewsEntity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class NewsAdapter(var context: Context, private var newsListener: OnNewsListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
    private var mDataList = ArrayList<NewsEntity>()
    private var imgCount: Int = 1

    companion object {
        const val ITEM_TYPE_NORMAL = 0
        const val ITEM_TYPE_IMAGE = 1
    }

    class NormalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutNewsItemNormalAdapterBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvComment = mBinding.tvComment
        var tvDesc = mBinding.tvDesc
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutNewsItemImageAdapterBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvComment = mBinding.tvComment
        var llContent = mBinding.llContent
        var tvTime = mBinding.tvTime
        var tvSource = mBinding.tvSource
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_IMAGE -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_news_item_image_adapter, parent, false)
                ImageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_news_item_normal_adapter, parent, false)
                NormalViewHolder(view)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val newsEntity = mDataList[position]
        when (holder) {
            is ImageViewHolder -> {
                setNewsContent(
                    holder.tvTitle, holder.tvComment, holder.tvSource, holder.tvTime, newsEntity
                )
                loadImage(holder, newsEntity)
            }
            is NormalViewHolder -> {
                holder.tvTitle.text = newsEntity.title
                if (TextUtils.isEmpty(newsEntity.digest)) {
                    holder.tvDesc.visibility = View.GONE
                } else {
                    holder.tvDesc.text = newsEntity.digest
                }
                val commentCount = newsEntity.commentCount
                if (commentCount > 0) {
                    if (commentCount > 10000) {
                        val fl = commentCount / 10000f
                        holder.tvComment.text = "%.1f".format(fl) + "w评论"
                    } else {
                        holder.tvComment.text = commentCount.toString() + "评论"
                    }
                } else {
                    holder.tvComment.visibility = View.GONE
                }
                holder.tvSource.text = newsEntity.source

                val time = CommonTools.parseTime(newsEntity.ptime)
                if (!TextUtils.isEmpty(time)) {
                    holder.tvTime.text = time
                } else {
                    holder.tvTime.text = newsEntity.ptime
                }
            }
        }

        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
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
        }
        tvSource.text = newsEntity.source

        val time = CommonTools.parseTime(newsEntity.ptime)
        if (!TextUtils.isEmpty(time)) {
            tvTime.text = time
        } else {
            tvTime.text = newsEntity.ptime
        }
    }

    private fun loadImage(holder: ImageViewHolder, newsEntity: NewsEntity) {
        if (newsEntity.hasImg == 1) {
            loadOneImage(newsEntity.imgsrc, holder, true)
        } else {
            val imgExtra = newsEntity.imgextra
            if (imgExtra != null && imgExtra.isNotEmpty()) {
                imgCount = if (imgExtra.size > 3) {
                    3
                } else {
                    imgExtra.size
                }
                for (i in 0 until imgCount) {
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

    private fun loadOneImage(imgSrc: String, holder: ImageViewHolder, isHasOne: Boolean) {
        holder.llContent.removeAllViews()
        Glide.with(context).asBitmap().load(imgSrc)
            //.apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .diskCacheStrategy(DiskCacheStrategy.ALL).override(
                //关键代码，加载原始大小
                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
            )
            //设置为这种格式去掉透明度通道，可以减少内存占有
            .format(DecodeFormat.PREFER_RGB_565).placeholder(R.mipmap.img_default)
            .error(R.mipmap.img_error).into(object : SimpleTarget<Bitmap>(
                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
            ) {
                override fun onResourceReady(
                    resource: Bitmap, transition: Transition<in Bitmap?>?
                ) {
                    val mBinding = LayoutItemImageBinding.inflate(LayoutInflater.from(context))
                    val layoutParams = mBinding.ivCover.layoutParams as RelativeLayout.LayoutParams
                    val screenHeight = CommonTools.getScreenHeight()
                    val screenWidth = CommonTools.getScreenWidth()
                    if (isHasOne) {
                        layoutParams.width = screenWidth
                        layoutParams.height = screenHeight / 5
                    } else {
                        layoutParams.width = screenWidth / imgCount
                        layoutParams.height = screenHeight / 5
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
            })
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(dataList: ArrayList<NewsEntity>) {
        mDataList.clear()
        mDataList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun loadMoreData(dataList: ArrayList<NewsEntity>) {
        val originSize = mDataList.size
        mDataList.addAll(dataList)
        notifyItemRangeInserted(originSize + 1, dataList.size)
    }

    interface OnNewsListener {
        fun onNewsItemClickListener(view: View, newsEntity: NewsEntity)
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            val tag = p0.tag as Int
            val newsEntity = mDataList[tag]
            newsListener.onNewsItemClickListener(p0, newsEntity)
        }
    }


    override fun getItemViewType(position: Int): Int {
        val newsEntity = mDataList[position]
        val hasImg = newsEntity.hasImg
        val imgExtra = newsEntity.imgextra
        if (hasImg == 1 || (imgExtra != null && imgExtra.isNotEmpty())) {
            return ITEM_TYPE_IMAGE
        }
        return ITEM_TYPE_NORMAL
    }
}