package com.android.wy.news.music

data class MusicState(
    var state: Int
) {
    companion object {
        const val STATE_PREPARE = 1
        const val STATE_PLAY = 2
        const val STATE_PAUSE = 3
        const val STATE_ERROR = 4
    }
}