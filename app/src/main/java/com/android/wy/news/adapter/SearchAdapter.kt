package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.entity.SearchResult

class SearchAdapter(itemAdapterListener: OnItemAdapterListener<SearchResult>) :
    BaseNewsAdapter<ViewHolder, SearchResult>(
        itemAdapterListener
    ) {

    companion object {
        const val ITEM_TYPE_NORMAL = 0
        const val ITEM_TYPE_IMAGE = 1
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == ITEM_TYPE_NORMAL) {
            val view = getView(parent, R.layout.layout_news_item_normal_adapter)
            NewsAdapter.NormalViewHolder(view)
        } else {
            val view = getView(parent, R.layout.layout_top_normal_item)
            TopAdapter.NormalViewHolder(view)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindData(holder: ViewHolder, position: Int, data: SearchResult) {
        val title = data.title
        var titleText = title
        if (title.contains("<em>")) {
            val s = title.replace("<em>", "")
            titleText = s
        }
        if (titleText.contains("</em>")) {
            val s = titleText.replace("</em>", "")
            titleText = s
        }
        if (holder is NewsAdapter.NormalViewHolder) {
            holder.tvTitle.text = titleText
            if (TextUtils.isEmpty(data.dkeys)) {
                holder.tvDesc.visibility = View.GONE
            } else {
                holder.tvDesc.text = data.dkeys
            }
            val commentCount = data.score
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
            holder.tvSource.text = data.source

            val time = CommonTools.getTimeDiff(data.ptime)
            if (!TextUtils.isEmpty(time)) {
                holder.tvTime.text = time
            } else {
                holder.tvTime.text = data.ptime
            }
        } else if (holder is TopAdapter.NormalViewHolder) {
            holder.tvTitle.text = titleText
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
            val imgUrl = data.imgurl
            if (imgUrl.isNotEmpty()) {
                CommonTools.loadImage(imgSrc = imgUrl[0], holder.ivCover)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val searchResult = mDataList[position]
        val imgUrl = searchResult.imgurl
        return if (imgUrl.isNotEmpty()) {
            ITEM_TYPE_IMAGE
        } else {
            ITEM_TYPE_NORMAL
        }
    }
}