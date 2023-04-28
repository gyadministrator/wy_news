package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseNewsAdapter<H : RecyclerView.ViewHolder, V>(
    private var itemAdapterListener: OnItemAdapterListener<V>
) : RecyclerView.Adapter<H>(), View.OnClickListener {
    private var currentPage = 0

    protected var mDataList = ArrayList<V>()

    abstract fun onViewHolderCreate(parent: ViewGroup, viewType: Int): H

    abstract fun onBindData(holder: H, position: Int, data: V)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H {
        return onViewHolderCreate(parent, viewType)
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

    override fun onBindViewHolder(holder: H, position: Int) {
        val data = mDataList[position]
        onBindData(holder, position, data)
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let { onItemClickListener(it) }
    }

    private fun onItemClickListener(view: View) {
        val tag = view.tag
        if (tag is Int) {
            val data = mDataList[tag]
            itemAdapterListener.onItemClickListener(view, data)
        }
    }

    interface OnItemAdapterListener<V> {
        fun onItemClickListener(view: View, data: V)
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

    fun getView(parent: ViewGroup, resId: Int): View {
        return LayoutInflater.from(parent.context).inflate(resId, parent, false)
    }

}