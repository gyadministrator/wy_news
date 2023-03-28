package com.android.wy.news.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.wy.news.R
import com.android.wy.news.common.ViewTools
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_launch)
        //ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_STATUS_BAR).init()
        //jump()
        /*val clContent=findViewById<ConstraintLayout>(R.id.cl_content)
        clContent.post {
            ViewTools.view2File(clContent)
        }*/
    }

    private fun jump() {
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }
}