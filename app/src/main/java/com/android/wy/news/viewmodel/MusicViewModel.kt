package com.android.wy.news.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.wy.news.app.App
import com.android.wy.news.common.Constants
import com.android.wy.news.common.Logger
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.entity.music.MusicUrlEntity
import com.android.wy.news.event.MusicUrlEvent
import com.android.wy.news.http.HttpManager
import com.android.wy.news.http.IApiService
import com.android.wy.news.manager.JsoupManager
import com.android.wy.news.manager.ThreadExecutorManager
import com.android.wy.news.util.AppUtil
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/24 16:59
  * @Version:        1.0
  * @Description:    
 */
class MusicViewModel : BaseViewModel() {
    val isSuccess = MutableLiveData<Boolean>()
    val musicUrl = MutableLiveData<String?>()

    fun getCookie() {
        ThreadExecutorManager.mInstance.startExecute {
            val success = JsoupManager.getCookie()
            isSuccess.postValue(success)
        }
    }

    fun requestMusicUrl(musicInfo: MusicInfo) {
        val musicId = musicInfo.musicrid
        if (musicId.contains("_")) {
            val mid = musicId.substring(musicId.indexOf("_") + 1, musicId.length)
            val apiService =
                HttpManager.mInstance.getMusicApiService(
                    Constants.MUSIC_BASE_URL,
                    IApiService::class.java
                )
            val observable = apiService.getMusicUrl(mid)
            observable.enqueue(object : Callback<MusicUrlEntity> {
                override fun onResponse(
                    call: Call<MusicUrlEntity>,
                    response: Response<MusicUrlEntity>
                ) {
                    val musicUrlEntity = response.body()
                    Logger.i("mid:$mid---->>>musicUrlEntity:$musicUrlEntity")
                    if (musicUrlEntity != null) {
                        val musicUrlData = musicUrlEntity.data
                        if (musicUrlEntity.code == -1) {
                            //msg.postValue(musicUrlEntity.msg)
                            msg.postValue("该歌曲为付费歌曲,暂时不能免费播放")
                        }
                        val url = musicUrlData?.url
                        Logger.i("mid:$mid---->>>url:$url")
                        if (AppUtil.isBackground(App.app)) {
                            Logger.i("requestMusicUrl--->>>app onBack")
                            val musicUrlEvent = MusicUrlEvent(url)
                            EventBus.getDefault().postSticky(musicUrlEvent)
                        } else {
                            musicUrl.postValue(url)
                        }
                    }
                }

                override fun onFailure(call: Call<MusicUrlEntity>, t: Throwable) {
                    t.message?.let { msg.postValue(it) }
                }
            })
        }
    }


    override fun clear() {

    }
}