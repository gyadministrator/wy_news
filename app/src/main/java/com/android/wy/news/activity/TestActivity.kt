package com.android.wy.news.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.view.TestVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer

class TestActivity : AppCompatActivity() {
    private lateinit var videoPlayer: TestVideoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        initData()
    }

    private fun initData() {
        videoPlayer = findViewById(R.id.video_player)
        val url =
            "http://flv0.bn.netease.com/2ed47162e5c0ccd548abfa749720cc2ef00be08a28ccf3c4975b4a103a02d12a5b1c9ec5c6ae2dbfb92a0157efcc918d6899b1a1957b8a3431eb44f9f2dd262bf21fbb8b7e73eed48da339bf79d7ef743b1707a149de35fd02559a45a9e13b25da51e07db0578a7d42062db0d354538617ea636ca868d131.mp4"
        val title = "【三】上海大哥聊聊加拿大的生活，在外国人眼里华人个个是富豪"
        val cover = "http://videoimg.ws.126.net/cover/20230307/Y6bXVE45I_cover.jpg"
        val setUp = videoPlayer.setUp(url, JCVideoPlayer.SCREEN_LAYOUT_NORMAL, "")
        if (setUp) {
            CommonTools.loadImage(cover, videoPlayer.thumbImageView)
        }
    }
}