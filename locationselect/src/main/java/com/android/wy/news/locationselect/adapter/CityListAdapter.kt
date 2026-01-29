package com.android.wy.news.locationselect.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.locationselect.R
import com.android.wy.news.locationselect.adapter.decoration.GridItemDecoration
import com.android.wy.news.locationselect.model.City
import com.android.wy.news.locationselect.model.HotCity
import com.android.wy.news.locationselect.model.LocateState
import com.android.wy.news.locationselect.model.LocatedCity


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/4/15 13:30
  * @Version:        1.0
  * @Description:    
 */
class CityListAdapter(
    context: Context?,
    data: ArrayList<City>?,
    hotData: ArrayList<HotCity>?,
    state: Int
) :
    RecyclerView.Adapter<CityListAdapter.BaseViewHolder>() {
    private val viewTypeLocation = 10
    private val viewTypeHot = 11

    private var mContext: Context? = context
    private var mData: ArrayList<City>? = data
    private var mHotData: ArrayList<HotCity>? = hotData
    private var locateState = state
    private var mInnerListener: InnerListener? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var stateChanged = false
    private var autoLocate = false

    fun autoLocate(auto: Boolean) {
        autoLocate = auto
    }

    fun setLayoutManager(manager: LinearLayoutManager?) {
        mLayoutManager = manager
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(data: ArrayList<City>) {
        mData = data
        notifyDataSetChanged()
    }

    fun updateLocateState(location: LocatedCity, state: Int) {
        mData?.removeAt(0)
        mData?.add(0, location)
        stateChanged = locateState != state
        locateState = state
        refreshLocationItem()
    }

    fun refreshLocationItem() {
        //如果定位城市的item可见则进行刷新
        if (stateChanged && mLayoutManager!!.findFirstVisibleItemPosition() == 0) {
            stateChanged = false
            notifyItemChanged(0)
        }
    }

    /**
     * 滚动RecyclerView到索引位置
     * @param index
     */
    fun scrollToSection(index: String) {
        if (mData == null || mData!!.isEmpty()) return
        if (TextUtils.isEmpty(index)) return
        val size = mData!!.size
        for (i in 0 until size) {
            if (TextUtils.equals(index.substring(0, 1), mData!![i].getSection().substring(0, 1))) {
                if (mLayoutManager != null) {
                    mLayoutManager!!.scrollToPositionWithOffset(i, 0)
                    if (TextUtils.equals(index.substring(0, 1), "定")) {
                        //防止滚动时进行刷新
                        Handler(Looper.getMainLooper()).postDelayed(
                            { if (stateChanged) notifyItemChanged(0) },
                            1000
                        )
                    }
                    return
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view: View
        return when (viewType) {
            viewTypeLocation -> {
                view =
                    LayoutInflater.from(mContext)
                        .inflate(R.layout.cp_list_item_location_layout, parent, false)
                LocationViewHolder(view)
            }

            viewTypeHot -> {
                view = LayoutInflater.from(mContext)
                    .inflate(R.layout.cp_list_item_hot_layout, parent, false)
                HotViewHolder(view)
            }

            else -> {
                view =
                    LayoutInflater.from(mContext)
                        .inflate(R.layout.cp_list_item_default_layout, parent, false)
                DefaultViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is DefaultViewHolder) {
            val pos = holder.getAdapterPosition()
            val data = mData!![pos]
            holder.name.text = data.name
            holder.name.setOnClickListener {
                if (mInnerListener != null) {
                    mInnerListener?.dismiss(pos, data)
                }
            }
        }
        //定位城市
        if (holder is LocationViewHolder) {
            val pos = holder.getAdapterPosition()
            val data = mData!![pos]
            //设置宽高
            val dm = mContext!!.resources.displayMetrics
            val screenWidth = dm.widthPixels
            val typedValue = TypedValue()
            mContext!!.theme.resolveAttribute(R.attr.cpGridItemSpace, typedValue, true)
            val space = mContext!!.resources.getDimensionPixelSize(R.dimen.cp_grid_item_space)
            val padding = mContext!!.resources.getDimensionPixelSize(R.dimen.cp_default_padding)
            val indexBarWidth =
                mContext!!.resources.getDimensionPixelSize(R.dimen.cp_index_bar_width)
            val itemWidth: Int =
                (screenWidth - padding - space * (GridListAdapter.spanCount - 1) - indexBarWidth) / GridListAdapter.spanCount
            val lp = holder.container.layoutParams
            lp.width = itemWidth
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            holder.container.layoutParams = lp
            when (locateState) {
                LocateState.LOCATING -> holder.current.setText(R.string.cp_locating)
                LocateState.SUCCESS -> holder.current.text = data.name
                LocateState.FAILURE -> holder.current.setText(R.string.cp_locate_failed)
            }
            holder.container.setOnClickListener {
                if (locateState == LocateState.SUCCESS) {
                    if (mInnerListener != null) {
                        mInnerListener?.dismiss(pos, data)
                    }
                } else if (locateState == LocateState.FAILURE) {
                    locateState = LocateState.LOCATING
                    notifyItemChanged(0)
                    if (mInnerListener != null) {
                        mInnerListener?.locate()
                    }
                }
            }
            //第一次弹窗，如果未定位则自动定位
            if (autoLocate && locateState == LocateState.LOCATING && mInnerListener != null) {
                mInnerListener?.locate()
                autoLocate = false
            }
        }
        //热门城市
        if (holder is HotViewHolder) {
            holder.getAdapterPosition()
            val mAdapter = GridListAdapter(mContext, mHotData)
            mAdapter.setInnerListener(mInnerListener)
            holder.mRecyclerView.adapter = mAdapter
        }
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 && TextUtils.equals(
                "定",
                mData!![position].getSection().substring(0, 1)
            )
        ) return viewTypeLocation
        return if (position == 1 && TextUtils.equals(
                "热",
                mData!![position].getSection().substring(0, 1)
            )
        ) viewTypeHot else super.getItemViewType(position)
    }

    fun setInnerListener(listener: InnerListener?) {
        mInnerListener = listener
    }

    open class BaseViewHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!
    )

    class DefaultViewHolder internal constructor(itemView: View) : BaseViewHolder(itemView) {
        var name: TextView

        init {
            name = itemView.findViewById(R.id.cp_list_item_name)
        }
    }

    class HotViewHolder internal constructor(itemView: View) : BaseViewHolder(itemView) {
        var mRecyclerView: RecyclerView

        init {
            mRecyclerView = itemView.findViewById(R.id.cp_hot_list)
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = GridLayoutManager(
                itemView.context,
                GridListAdapter.spanCount, LinearLayoutManager.VERTICAL, false
            )
            val space = itemView.context.resources.getDimensionPixelSize(R.dimen.cp_grid_item_space)
            mRecyclerView.addItemDecoration(
                GridItemDecoration(
                    GridListAdapter.spanCount,
                    space
                )
            )
        }
    }

    class LocationViewHolder internal constructor(itemView: View) :
        BaseViewHolder(itemView) {
        var container: FrameLayout
        var current: TextView

        init {
            container = itemView.findViewById(R.id.cp_list_item_location_layout)
            current = itemView.findViewById(R.id.cp_list_item_location)
        }
    }
}