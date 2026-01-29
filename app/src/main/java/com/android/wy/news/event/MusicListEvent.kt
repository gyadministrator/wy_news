package com.android.wy.news.event

import com.android.wy.news.entity.music.MusicInfo

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/5/5 16:33
  * @Version:        1.0
  * @Description:    
 */
data class MusicListEvent(
    var dataList: ArrayList<MusicInfo>
)