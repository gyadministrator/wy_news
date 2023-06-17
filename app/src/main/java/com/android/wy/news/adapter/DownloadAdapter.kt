package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.cache.DataCleanManager
import com.android.wy.news.databinding.LayoutDownloadItemBinding
import java.io.File
import java.util.*


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class DownloadAdapter(itemAdapterListener: OnItemAdapterListener<File>) :
    BaseNewsAdapter<File>(itemAdapterListener) {

    class DownloadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutDownloadItemBinding.bind(itemView)
        var tvName = mBinding.tvName
        var tvSize = mBinding.tvSize
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = getView(parent, R.layout.layout_download_item)
        return DownloadViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindData(holder: RecyclerView.ViewHolder, position: Int, data: File) {
        if (holder is DownloadViewHolder) {
            holder.tvName.text = data.name
            holder.tvSize.text = DataCleanManager.getFormatSize(data.length().toDouble())
        }
    }
}