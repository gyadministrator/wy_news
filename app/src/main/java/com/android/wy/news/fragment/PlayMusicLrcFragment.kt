package com.android.wy.news.fragment

import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.FragmentPlayMusicLrcBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.event.MusicEvent
import com.android.wy.news.event.MusicInfoEvent
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.manager.LrcDesktopManager
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.lrc.Lrc
import com.android.wy.news.music.lrc.LrcView
import com.android.wy.news.viewmodel.PlayMusicLrcViewModel
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PlayMusicLrcFragment : BaseFragment<FragmentPlayMusicLrcBinding, PlayMusicLrcViewModel>() {
    private var tvTitle: TextView? = null
    private var tvDesc: TextView? = null
    private var lrcView: LrcView? = null
    private var currentMusicInfo: MusicInfo? = null
    private var mediaHelper: MediaPlayerHelper? = null
    private var currentLrcList = ArrayList<Lrc>()

    companion object {
        private const val POSITION_KEY = "position_key"
        private const val MUSIC_INFO_KEY = "music_info_key"
        private const val MUSIC_URL_KEY = "music_url_key"

        fun newInstance(position: Int, musicInfoJson: String, url: String?): PlayMusicLrcFragment {
            val fragment = PlayMusicLrcFragment()
            val args = Bundle()
            args.putInt(POSITION_KEY, position)
            args.putString(MUSIC_INFO_KEY, musicInfoJson)
            args.putString(MUSIC_URL_KEY, url)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView() {
        tvTitle = mBinding.tvTitle
        tvDesc = mBinding.tvDesc
        lrcView = mBinding.lrcView
    }

    override fun initData() {
        mediaHelper = context?.let { MediaPlayerHelper.getInstance(it) }
        val args = arguments
        if (args != null) {
            val s = args.getString(MUSIC_INFO_KEY)
            if (!TextUtils.isEmpty(s)) {
                val gson = Gson()
                currentMusicInfo = gson.fromJson(s, MusicInfo::class.java)
            }
        }
        setMusic()
    }

    private fun getLrc() {
        val musicId = this.currentMusicInfo?.musicrid
        if (musicId!!.contains("_")) {
            val mid = musicId.substring(musicId.indexOf("_") + 1, musicId.length)
            MusicRepository.getMusicLrc(mid).observe(this) {
                val musicLrcEntity = it.getOrNull()
                if (musicLrcEntity != null) {
                    val musicLrcData = musicLrcEntity.data
                    if (musicLrcData != null) {
                        val lrcList = musicLrcData.lrclist
                        if (lrcList.isNotEmpty()) {
                            val realLrcList = CommonTools.parseLrc(lrcList)
                            currentLrcList.clear()
                            currentLrcList.addAll(realLrcList)
                            lrcView?.setLrcData(currentLrcList)
                        }
                    }
                }
            }
        }
    }

    override fun initEvent() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(o: Any) {
        Logger.i("LrcFragment--->>>onEvent--->>>o:$o")
        if (o is MusicEvent) {
            Logger.i("onEvent--->>>time:${o.time}")
            if (currentLrcList.size == 0) {
                setMusic()
            }
            activity?.let { LrcDesktopManager.showDesktopLrc(it, o.time.toLong()) }
            lrcView?.updateTime(o.time.toLong())
        } else if (o is MusicInfoEvent) {
            val gson = Gson()
            currentMusicInfo = gson.fromJson(o.musicJson, MusicInfo::class.java)
            setMusic()
        }
    }

    private fun setMusic() {
        tvTitle?.text = this.currentMusicInfo?.artist
        tvDesc?.text = this.currentMusicInfo?.name
        getLrc()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
            setMusic()
        }
    }

    override fun getViewBinding(): FragmentPlayMusicLrcBinding {
        return FragmentPlayMusicLrcBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): PlayMusicLrcViewModel {
        return CommonTools.getViewModel(this, PlayMusicLrcViewModel::class.java)
    }

    override fun onClear() {
    }

    override fun onNotifyDataChanged() {
    }
}