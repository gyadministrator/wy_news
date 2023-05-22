package com.android.wy.news.lrc.impl

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/22 9:45
  * @Version:        1.0
  * @Description:
 * 歌词行
 * 包括该行歌词的时间，歌词内容
 */
data class LrcRow(

    /* 该行歌词要开始播放的时间，格式如下：[02:34.14] */
    var startTimeString: String,

    /* 该行歌词要开始播放的时间，由[02:34.14]格式转换为long型，
     * 即将2分34秒14毫秒都转为毫秒后 得到的long型值：startTime=02*60*1000+34*1000+14
     */
    var startTime: Long,

    /* 该行歌词要结束播放的时间，由[02:34.14]格式转换为long型，
     * 即将2分34秒14毫秒都转为毫秒后 得到的long型值：startTime=02*60*1000+34*1000+14
     */
    var endTime: Long,

    /* 该行歌词的内容 */
    var content: String,

    ) : Comparable<LrcRow> {

    /**
     * 排序的时候，根据歌词的时间来排序
     */
    override fun compareTo(other: LrcRow): Int {
        return (startTime - other.startTime).toInt()
    }

    override fun toString(): String {
        return "LrcRow{" +
                "startTimeString='" + startTimeString + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", content='" + content + '\'' +
                '}'
    }
}