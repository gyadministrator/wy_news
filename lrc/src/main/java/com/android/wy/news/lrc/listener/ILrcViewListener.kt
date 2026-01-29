package com.android.wy.news.lrc.listener

import com.android.wy.news.lrc.impl.LrcRow

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/5/22 10:10
  * @Version:        1.0
  * @Description:    歌词拖动时候的监听类
 */
interface ILrcViewListener {
    /**
     * 当歌词被用户上下拖动的时候
     */
    fun onLrcDrag(newPosition: Int, row: LrcRow)
}