package com.android.wy.news.locationselect.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/4/15 13:48
  * @Version:        1.0
  * @Description:    
 */
class GridItemDecoration(spanCount: Int, space: Int) : RecyclerView.ItemDecoration() {
    private var mSpanCount = spanCount
    private var mSpace = space

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % mSpanCount
        outRect.left = column * mSpace / mSpanCount
        outRect.right = mSpace - (column + 1) * mSpace / mSpanCount
        if (position >= mSpanCount) {
            outRect.top = mSpace
        }
    }
}