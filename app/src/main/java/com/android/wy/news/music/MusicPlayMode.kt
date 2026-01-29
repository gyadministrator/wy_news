package com.android.wy.news.music

import com.android.wy.news.common.SpTools
import kotlin.random.Random

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/5/5 13:13
  * @Version:        1.0
  * @Description:    
 */
object MusicPlayMode {
    const val STATE_TYPE_NORMAL = 0
    const val STATE_TYPE_RANDOM = 1
    const val STATE_TYPE_ONE = 2
    private const val STATE_TYPE_KEY = "state_type_key"
    val map = hashMapOf(
        STATE_TYPE_NORMAL to "顺序播放", STATE_TYPE_RANDOM to "随机播放",
        STATE_TYPE_ONE to "单曲循环"
    )

    fun setMode(mode: Int) {
        SpTools.putInt(STATE_TYPE_KEY, mode)
    }

    fun getMode(): Int? {
        return SpTools.getInt(STATE_TYPE_KEY)
    }

    fun getRandomPosition(size: Int): Int {
        val random = Random(size)
        return random.nextInt()
    }
}