package com.android.wy.news.listener

import android.view.View
import com.android.wy.news.entity.music.MusicInfo

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/5/30 13:24
  * @Version:        1.0
  * @Description:    
 */
interface IMusicItemChangeListener {
    fun onItemClick(view: View, data: MusicInfo)

    fun onItemLongClick(view: View, data: MusicInfo)
}