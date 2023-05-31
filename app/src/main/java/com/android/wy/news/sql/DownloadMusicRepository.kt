package com.android.wy.news.sql

import android.content.Context

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/3 15:08
  * @Version:        1.0
  * @Description:    
 */
class DownloadMusicRepository(context: Context) {
    private var downloadMusicController: DownloadMusicController

    init {
        downloadMusicController = NewsDataBase.getInstance(context).getDownloadMusicDao()
    }

    fun getDownloadMusicList(): ArrayList<DownloadMusicEntity> {
        return downloadMusicController.getSearchDownloadMusic() as ArrayList<DownloadMusicEntity>
    }

    fun getDownloadMusicByPath(localPath: String): DownloadMusicEntity? {
        return downloadMusicController.findDownloadMusic(localPath)
    }

    fun deleteDownloadMusic(downloadMusicEntity: DownloadMusicEntity) {
        downloadMusicController.deleteDownloadMusic(downloadMusicEntity)
    }

    fun deleteAllDownloadMusic(downloadMusicEntityList: ArrayList<DownloadMusicEntity>) {
        downloadMusicController.deleteAllDownloadMusic(downloadMusicEntityList)
    }

    fun addDownloadMusic(downloadMusicEntity: DownloadMusicEntity) {
        downloadMusicController.addDownloadMusic(downloadMusicEntity)
    }
}