package com.android.wy.news.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.wy.news.R
import com.android.wy.news.databinding.LayoutOperationItemBinding
import com.android.wy.news.entity.OperationItemEntity


/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/6/15 14:58
  * @Version:        1.0
  * @Description:    
 */
class OperationAdapter(itemAdapterListener: OnItemAdapterListener<OperationItemEntity>) :
    BaseNewsAdapter<OperationItemEntity>(itemAdapterListener) {

    class OperationViewHolder(itemView: View) : ViewHolder(itemView) {
        private val mBinding = LayoutOperationItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var ivIcon = mBinding.ivIcon
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = getView(parent, R.layout.layout_operation_item)
        return OperationViewHolder(view)
    }

    override fun onBindData(holder: ViewHolder, position: Int, data: OperationItemEntity) {
        if (holder is OperationViewHolder) {
            holder.ivIcon.setImageResource(data.icon)
            holder.tvTitle.text = data.title
        }
    }
}