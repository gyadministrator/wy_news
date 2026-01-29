package com.android.wy.news.listener

import com.android.wy.news.entity.music.MusicInfo

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/5/29 14:40
  * @Version:        1.0
  * @Description:    
 */
interface IDownloadListener {
    fun goDownload(musicInfo: MusicInfo)
}