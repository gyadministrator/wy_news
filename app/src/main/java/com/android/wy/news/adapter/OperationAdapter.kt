package com.android.wy.news.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.databinding.LayoutOperationItemBinding
import com.android.wy.news.entity.OperationItemEntity


/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/6/15 14:58
  * @Version:        1.0
  * @Description:    
 */
class OperationAdapter(itemAdapterListener: OnItemAdapterListener<OperationItemEntity>) :
    BaseNewsAdapter<OperationAdapter.OperationViewHolder, OperationItemEntity>(itemAdapterListener) {

    class OperationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutOperationItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var ivIcon = mBinding.ivIcon
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): OperationViewHolder {
        val view = getView(parent, R.layout.layout_operation_item)
        return OperationViewHolder(view)
    }

    override fun onBindData(holder: OperationViewHolder, position: Int, data: OperationItemEntity) {
        holder.ivIcon.setImageResource(data.icon)
        holder.tvTitle.text = data.title
    }
}