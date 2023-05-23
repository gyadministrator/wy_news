package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import me.majiajie.pagerbottomtabstrip.item.MaterialItemView

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/23 15:20
  * @Version:        1.0
  * @Description:    
 */
class BottomBarItem : MaterialItemView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}