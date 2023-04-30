package com.android.wy.news.listener

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.os.CountDownTimer


class CustomAnimatorUpdateListener: ValueAnimator.AnimatorUpdateListener {
    /**
     * 暂停状态
     */
    private var isPause = false

    /**
     * 是否已经暂停，如果一已经暂停，那么就不需要再次设置停止的一些事件和监听器了
     */
    private var isPaused = false

    private var isPlay = true

    /**
     * 当前的动画的播放位置
     */
    private var fraction = 0.0f

    /**
     * 当前动画的播放运行时间
     */
    private var mCurrentPlayTime = 0L

    /**
     * 是否是暂停状态
     *
     * @return
     */
    private var animator: ObjectAnimator? = null

    constructor(animator: ObjectAnimator){
        this.animator = animator
    }

    fun isPause(): Boolean {
        return isPause
    }

    fun isPlay(): Boolean {
        return isPlay
    }

    /**
     * 停止方法，只是设置标志位，剩余的工作会根据状态位置在onAnimationUpdate进行操作
     */
    fun pause() {
        isPause = true
        isPlay = false
    }

    fun play() {
        isPause = false
        isPaused = false
        isPlay = true
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        /**
         * 如果是暂停则将状态保持下来，并每个刷新动画的时间了；来设置当前时间，让动画
         * 在时间上处于暂停状态，同时要设置一个静止的时间加速器，来保证动画不会抖动
         */
        /**
         * 如果是暂停则将状态保持下来，并每个刷新动画的时间了；来设置当前时间，让动画
         * 在时间上处于暂停状态，同时要设置一个静止的时间加速器，来保证动画不会抖动
         */
        if (isPause) {
            if (!isPaused) {
                mCurrentPlayTime = animation.currentPlayTime
                fraction = animation.animatedFraction
                animation.interpolator = TimeInterpolator { fraction }
                isPaused = true
            }
            // 每隔动画播放的时间，我们都会将播放时间往回调整，以便重新播放的时候接着使用这个时间,同时也为了让整个动画不结束
            object : CountDownTimer(
                ValueAnimator.getFrameDelay(),
                ValueAnimator.getFrameDelay()
            ) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    animator!!.currentPlayTime = mCurrentPlayTime
                }
            }.start()
        } else {
            // 将时间拦截器恢复成线性的，如果您有自己的，也可以在这里进行恢复
            animation.interpolator = null
        }
    }
}