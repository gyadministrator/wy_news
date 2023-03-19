package com.android.wy.news.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.android.wy.news.R

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/2/3 13:27
  * @Version:        1.0
  * @Description:    
 */
class LoadingView : AppCompatImageView {
    private var loadObjectAnimator: ObjectAnimator? = null
    private var mContext: Context? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        this.mContext = context
        setImageResource(R.mipmap.loading)
    }

    fun startLoadingAnim() {
        visibility = VISIBLE
        val loadObjectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f)
        loadObjectAnimator.duration = 1000
        loadObjectAnimator.repeatMode = ValueAnimator.RESTART
        loadObjectAnimator.repeatCount = ValueAnimator.INFINITE
        loadObjectAnimator.interpolator = LinearInterpolator()
        loadObjectAnimator.start()
    }

    fun stopLoadingAnim() {
        visibility = GONE
        if (loadObjectAnimator != null) {
            loadObjectAnimator?.cancel()
            loadObjectAnimator = null
        }
    }
}