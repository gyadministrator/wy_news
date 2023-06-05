package com.android.wy.news.sql

import android.content.Context

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/3 15:08
  * @Version:        1.0
  * @Description:    
 */
class RecordMusicRepository(context: Context) {
    private var recordMusicController: RecordMusicController

    init {
        recordMusicController = NewsDataBase.getInstance(context).getRecordMusicDao()
    }

    fun getRecordMusicList(): ArrayList<RecordMusicEntity> {
        return recordMusicController.getRecordMusic() as ArrayList<RecordMusicEntity>
    }

    fun getRecordMusicByMid(mid: String): RecordMusicEntity? {
        return recordMusicController.findRecordMusic(mid)
    }

    fun deleteRecordMusic(recordMusicEntity: RecordMusicEntity) {
        recordMusicController.deleteRecordMusic(recordMusicEntity)
    }

    fun deleteAllRecordMusic(recordMusicEntityList: ArrayList<RecordMusicEntity>) {
        recordMusicController.deleteAllRecordMusic(recordMusicEntityList)
    }

    fun addRecordMusic(recordMusicEntity: RecordMusicEntity) {
        recordMusicController.addRecordMusic(recordMusicEntity)
    }
}