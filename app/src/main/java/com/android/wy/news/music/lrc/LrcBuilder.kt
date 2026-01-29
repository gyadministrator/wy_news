package com.android.wy.news.music.lrc

import com.android.wy.news.lrc.impl.LrcRow
import com.android.wy.news.lrc.listener.ILrcBuilder
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.util.JsonUtil

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/5/19 14:48
  * @Version:        1.0
  * @Description:    
 */
class LrcBuilder(mediaPlayerHelper: MediaPlayerHelper) : ILrcBuilder {
    private var mediaPlayer: MediaPlayerHelper? = mediaPlayerHelper

    override fun getLrcRows(rawLrc: String?): ArrayList<LrcRow> {
        val lrcRowList = ArrayList<LrcRow>()
        try {
            val dataList = JsonUtil.parseJsonToList<Lrc>(rawLrc)
            if (dataList.size > 0) {
                for (i in 0 until dataList.size) {
                    val lrc = dataList[i]
                    val endTime: Long = if (i < dataList.size - 1) {
                        (dataList[i + 1].time * 1000).toLong()
                    } else {
                        mediaPlayer?.getDuration()?.toLong()!!
                    }
                    val lrcRow = LrcRow(
                        LrcHelper.formatTime(lrc.time * 1000),
                        (lrc.time * 1000).toLong(),
                        endTime,
                        lrc.text
                    )
                    lrcRowList.add(lrcRow)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return lrcRowList
    }
}