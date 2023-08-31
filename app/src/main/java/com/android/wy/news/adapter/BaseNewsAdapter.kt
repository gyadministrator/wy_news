package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R

abstract class BaseNewsAdapter<V>(
    private var itemAdapterListener: OnItemAdapterListener<V>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener,
    View.OnLongClickListener {
    private var currentPage = 0
    protected var mDataList = ArrayList<V>()

    /*init {
        //当数据有唯一ID时，设置为true来提高RecyclerView的性能，
        //这样数据变化时，RecyclerView可以更准确判断数据项的变化，避免重复绑定数据
        setHasStableIds(true)
    }*/

    abstract fun onViewHolderCreate(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    abstract fun onBindData(holder: RecyclerView.ViewHolder, position: Int, data: V)

    companion object {
        const val ITEM_TYPE_EMPTY = -99
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_EMPTY) {
            // 创建空布局item
            onEmptyViewHolderCreate(parent)
        } else {
            onViewHolderCreate(parent, viewType)
        }
    }

    private fun onEmptyViewHolderCreate(parent: ViewGroup): RecyclerView.ViewHolder {
        return EmptyViewHolder(getEmptyView(parent))
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    private fun getEmptyView(parent: ViewGroup): View {
        return getView(parent, R.layout.layout_empty)
    }

    override fun getItemCount(): Int {
        return if (mDataList.size > 0) {
            mDataList.size
        } else {
            0
        }
    }

    fun getDataList(): ArrayList<V> {
        return mDataList
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EmptyViewHolder) {
            return
        }
        val data = mDataList[position]
        onBindData(holder, position, data)
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
        holder.itemView.setOnLongClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let { onItemClickListener(it) }
    }

    override fun onLongClick(p0: View?): Boolean {
        p0?.let { onItemLongClickListener(it) }
        return true
    }

    private fun onItemClickListener(view: View) {
        val tag = view.tag
        if (tag is Int) {
            val data = mDataList[tag]
            itemAdapterListener.onItemClickListener(view, data)
        }
    }

    private fun onItemLongClickListener(view: View) {
        val tag = view.tag
        if (tag is Int) {
            val data = mDataList[tag]
            itemAdapterListener.onItemLongClickListener(view, data)
        }
    }

    interface OnItemAdapterListener<V> {
        fun onItemClickListener(view: View, data: V)

        fun onItemLongClickListener(view: View, data: V)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(dataList: ArrayList<V>) {
        currentPage = 1
        mDataList.clear()
        mDataList.addAll(dataList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        mDataList.clear()
        notifyDataSetChanged()
    }

    fun loadMoreData(dataList: ArrayList<V>): Int {
        if (dataList.size > 0) {
            currentPage++
        }
        val originSize = mDataList.size
        mDataList.addAll(dataList)
        notifyItemRangeInserted(originSize + 1, dataList.size)
        return originSize
    }

    override fun getItemViewType(position: Int): Int {
        return if (mDataList.size == 0) {
            // 空布局
            ITEM_TYPE_EMPTY
        } else {
            super.getItemViewType(position)
        }
    }

    fun getView(parent: ViewGroup, resId: Int): View {
        return LayoutInflater.from(parent.context).inflate(resId, parent, false)
    }
}