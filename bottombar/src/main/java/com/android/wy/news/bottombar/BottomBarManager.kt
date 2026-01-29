package com.android.wy.news.bottombar

import android.util.Log
import com.android.wy.news.bottombar.model.BarItem
import com.android.wy.news.bottombar.view.BottomBar

/*
  * @Author:         gao_yun
  * @CreateDate:     2023/5/17 10:49
  * @Version:        1.0
  * @Description:
 */
object BottomBarManager {
    private const val TAG = "BottomBarManager"

    fun initBottomBar(
        bottomBar: BottomBar,
        barItemList: MutableList<BarItem>,
        container: Int,
        normalColor: Int,
        selectColor: Int,
        onBottomBarSelectListener: BottomBar.OnBottomBarSelectListener
    ) {
        bottomBar.setContainer(container)
        bottomBar.setTitleBeforeAndAfterColor(normalColor, selectColor)
        if (barItemList.size > 0) {
            for (i in 0 until barItemList.size) {
                val barItem = barItemList[i]
                bottomBar.addItem(
                    barItem.fragment,
                    barItem.title,
                    barItem.normalIcon,
                    barItem.selectIcon
                )
            }
            bottomBar.setFirstChecked(0)
            bottomBar.setIconHeight(25)
            bottomBar.setIconWidth(25)
            bottomBar.setTitleIconMargin(6)
            bottomBar.setTitleSize(12)
            bottomBar.setSelectListener(onBottomBarSelectListener)
            bottomBar.build()
        } else {
            Log.e(TAG, "initBottomBar barItemList is empty")
        }
    }
}
