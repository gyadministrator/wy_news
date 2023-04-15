package com.android.wy.news.locationselect.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.locationselect.R
import com.android.wy.news.locationselect.model.HotCity


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/15 13:37
  * @Version:        1.0
  * @Description:    
 */
class GridListAdapter(context: Context?, data: ArrayList<HotCity>?) :
    RecyclerView.Adapter<GridListAdapter.GridViewHolder>() {
    companion object{
       const val spanCount = 3
    }

    private var mContext: Context? = context
    private var mData: List<HotCity>? = data
    private var mInnerListener: InnerListener? = null

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val pos = holder.adapterPosition
        val data = mData!![pos]
        //设置item宽高
        val dm = mContext!!.resources.displayMetrics
        val screenWidth = dm.widthPixels
        val typedValue = TypedValue()
        mContext!!.theme.resolveAttribute(R.attr.cpGridItemSpace, typedValue, true)
        val space = mContext!!.resources.getDimensionPixelSize(typedValue.resourceId)
        val padding = mContext!!.resources.getDimensionPixelSize(R.dimen.cp_default_padding)
        val indexBarWidth = mContext!!.resources.getDimensionPixelSize(R.dimen.cp_index_bar_width)
        val itemWidth =
            (screenWidth - padding - space * (spanCount - 1) - indexBarWidth) / spanCount
        val lp = holder.container.layoutParams
        lp.width = itemWidth
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        holder.container.layoutParams = lp
        holder.name.text = data.name
        holder.container.setOnClickListener {
            if (mInnerListener != null) {
                mInnerListener?.dismiss(pos, data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.cp_grid_item_layout, parent, false)
        return GridViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var container: FrameLayout
        var name: TextView

        init {
            container = itemView.findViewById(R.id.cp_grid_item_layout)
            name = itemView.findViewById(R.id.cp_gird_item_name)
        }
    }

    fun setInnerListener(listener: InnerListener?) {
        mInnerListener = listener
    }
}