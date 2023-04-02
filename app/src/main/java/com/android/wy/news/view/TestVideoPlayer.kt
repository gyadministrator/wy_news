package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.android.wy.news.R
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard

class TestVideoPlayer : JCVideoPlayerStandard {
    private lateinit var rlRoot: RelativeLayout

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun getLayoutId(): Int {
        return R.layout.layout_video_player
    }

    override fun init(context: Context?) {
        super.init(context)
        rlRoot = findViewById(R.id.rl_root)
        rlRoot.setOnClickListener(this)
    }

    override fun updateStartImage() {
        if (currentState == CURRENT_STATE_PLAYING) {
            startButton.visibility = View.INVISIBLE
            //startButton.setImageResource(R.drawable.jc_click_pause_selector)
        } else if (currentState == CURRENT_STATE_ERROR) {
            //startButton.setImageResource(R.drawable.jc_click_error_selector)
        } else {
            startButton.setImageResource(R.mipmap.play)
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        if (v != null) {
            val id = v.id
            if (id == R.id.rl_root) {
                startButton.performClick()
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        //拦截触摸事件
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //拦截触摸事件
        return true
    }
}