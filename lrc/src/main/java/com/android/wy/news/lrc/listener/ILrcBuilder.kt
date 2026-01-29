package com.android.wy.news.lrc.listener

import com.android.wy.news.lrc.impl.LrcRow

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/5/22 9:52
  * @Version:        1.0
  * @Description:    
 */
interface ILrcBuilder {
    /**
     * 解析歌词，得到LrcRow的集合
     *
     * @param rawLrc lrc内容
     * @return LrcRow的集合
     */
    fun getLrcRows(rawLrc: String?): ArrayList<LrcRow>?
}