package com.android.wy.news.music.lrc

import com.android.lyric.ILrcBuilder
import com.android.lyric.impl.LrcRow
import com.android.wy.news.music.MediaPlayerHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/19 14:48
  * @Version:        1.0
  * @Description:    
 */
class LrcBuilder(mediaPlayerHelper: MediaPlayerHelper) : ILrcBuilder {
    private var mediaPlayer: MediaPlayerHelper? = mediaPlayerHelper

    override fun getLrcRows(rawLrc: String?): MutableList<LrcRow> {
        val lrcRowList = ArrayList<LrcRow>()
        try {
            val gson = Gson()
            val dataList = gson.fromJson<ArrayList<Lrc>>(
                rawLrc,
                object : TypeToken<ArrayList<Lrc>>() {}.type
            )
            if (dataList != null && dataList.size > 0) {
                for (i in 0 until dataList.size) {
                    val lrc = dataList[i]
                    val lrcRow = LrcRow()
                    lrcRow.setContent(lrc.text)
                    lrcRow.setStartTime((lrc.time * 1000).toLong())
                    lrcRow.setStartTimeString(LrcHelper.formatTime(lrc.time * 1000))
                    val endTime: Long = if (i < dataList.size - 1) {
                        (dataList[i + 1].time * 1000).toLong()
                    } else {
                        mediaPlayer?.getDuration()?.toLong()!!
                    }
                    lrcRow.setEndTime(endTime)
                    lrcRowList.add(lrcRow)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return lrcRowList
    }
}