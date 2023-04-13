package com.android.wy.news.compose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.android.wy.news.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/13 15:01
  * @Version:        1.0
  * @Description:    
 */
class AlbumActivity : ComponentActivity() {
    companion object {
        fun startAlbumActivity(context: Context) {
            val intent = Intent(context, AlbumActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setSystemBarsColor(Color.White, true)
            ShowAlbum()
        }
    }

    @Composable
    fun ShowAlbum() {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White)
        ) {
            Image(painter = painterResource(id = R.drawable.bg_launch), contentDescription = null)
        }
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun ShowAlbumPreview() {
        ShowAlbum()
    }
}