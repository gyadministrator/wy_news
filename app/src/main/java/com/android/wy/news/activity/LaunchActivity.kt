package com.android.wy.news.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.wy.news.R
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_STATUS_BAR).init()
    }
}