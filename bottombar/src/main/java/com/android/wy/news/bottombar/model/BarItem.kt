package com.android.wy.news.bottombar.modelgao_yun

import androidx.fragment.app.Fragment

/*
  * @Author:         gao_yun
  * @CreateDate:     2023/5/17 10:52
  * @Version:        1.0
  * @Description:
 */
data class BarItem(
    var fragment: Fragment,
    var title: String,
    var normalIcon: Int,
    var selectIcon: Int
)