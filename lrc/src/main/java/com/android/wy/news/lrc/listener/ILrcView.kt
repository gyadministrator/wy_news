package com.android.wy.news.lrc.listener

import com.android.wy.news.lrc.impl.LrcRow

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/22 10:09
  * @Version:        1.0
  * @Description:    展示歌词的接口
 */
interface ILrcView {
    /**
     * 设置要展示的歌词行集合
     */
    fun setLrc(lrcRows: ArrayList<LrcRow>?)

    /**
     * 音乐播放的时候调用该方法滚动歌词，高亮正在播放的那句歌词
     */
    fun seekLrcToTime(time: Long)

    /**
     * 设置歌词拖动时候的监听类
     */
    fun setLrcViewListener(listener: ILrcViewListener?)
}