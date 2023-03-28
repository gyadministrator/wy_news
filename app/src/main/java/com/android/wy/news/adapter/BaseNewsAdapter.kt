package com.android.wy.news.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseNewsAdapter<H : RecyclerView.ViewHolder>(
    private var dataList: ArrayList<*>,
    private var itemAdapterListener: OnItemAdapterListener
) :
    RecyclerView.Adapter<H>(), View.OnClickListener {

    abstract fun onViewHolderCreate(parent: ViewGroup, viewType: Int): H

    abstract fun onBindData(holder: H, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H {
        return onViewHolderCreate(parent, viewType)
    }

    override fun getItemCount(): Int {
        return if (dataList.size > 0) {
            dataList.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: H, position: Int) {
        onBindData(holder, position)
        holder.itemView.tag = dataList[position]
        holder.itemView.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let { itemAdapterListener.onItemClickListener(it, it.tag) }
    }

    interface OnItemAdapterListener {
        fun onItemClickListener(view: View, data: Any)
    }

}