package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.wy.news.R

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/20 19:50
  * @Version:        1.0
  * @Description:    
 */
class EmptyView : ConstraintLayout {
    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        val view=LayoutInflater.from(context).inflate(R.layout.layout_empty_data,null)
        addView(view)
    }
}