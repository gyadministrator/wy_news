package com.android.wy.news.fragment

import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.common.LrcType
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.FragmentPlayMusicLrcBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.event.LrcChangeEvent
import com.android.wy.news.event.MusicEvent
import com.android.wy.news.event.MusicInfoEvent
import com.android.wy.news.event.PlayFinishEvent
import com.android.wy.news.lrc.impl.LrcRow
import com.android.wy.news.lrc.impl.LrcView
import com.android.wy.news.lrc.listener.ILrcViewListener
import com.android.wy.news.manager.LrcDesktopManager
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.lrc.LrcBuilder
import com.android.wy.news.util.AppUtil
import com.android.wy.news.viewmodel.PlayMusicLrcViewModel
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PlayMusicLrcFragment : BaseFragment<FragmentPlayMusicLrcBinding, PlayMusicLrcViewModel>(),
    ILrcViewListener {
    private var tvTitle: TextView? = null
    private var tvDesc: TextView? = null
    private var lrcView: LrcView? = null
    private var currentMusicInfo: MusicInfo? = null
    private var mediaHelper: MediaPlayerHelper? = null

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
        setMusicInfo()
        setLrcInfo()
    }

    private fun playFinish() {
        Logger.i("PlayMusicLrcFragment--->>>playFinish")
        lrcView?.setLrc(null)
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
        when (o) {
            is MusicEvent -> {
                Logger.i("onEvent--->>>time:${o.time}")
                activity?.let { LrcDesktopManager.showDesktopLrc(it, o.time.toLong()) }
                lrcView?.seekLrcToTime(o.time.toLong())
            }

            is MusicInfoEvent -> {
                val gson = Gson()
                currentMusicInfo = gson.fromJson(o.musicJson, MusicInfo::class.java)
                setMusicInfo()
            }

            is LrcChangeEvent -> {
                setLrcInfo()
            }

            is PlayFinishEvent -> {
                playFinish()
            }
        }
    }

    private fun setMusicInfo() {
        tvTitle?.text = this.currentMusicInfo?.artist
        tvDesc?.text = this.currentMusicInfo?.name
    }

    private fun setLrcInfo() {
        val lrcBuilder = mediaHelper?.let { LrcBuilder(it) }
        val gson = Gson()
        val s = gson.toJson(GlobalData.currentLrcData)
        val lrcRows = lrcBuilder?.getLrcRows(s)
        lrcView?.setNormalLrcColor(AppUtil.getColor(mActivity, R.color.main_title))
            ?.setSelectLrcColor(AppUtil.getColor(mActivity, R.color.text_select_color))
            ?.setSeekLineColor(AppUtil.getColor(mActivity, R.color.select_indicator_color))
            ?.setSeekLineLrcColor(AppUtil.getColor(mActivity, R.color.select_indicator_color))
            ?.setLineSpace(30)
            ?.setSeekLineSize(17)
            ?.setLrcSize(17)
            ?.setLrcSelectSize(20)
            ?.setLrc(lrcRows)
        setLrcType()
        lrcView?.setLrcViewListener(this)
    }

    private fun setLrcType() {
        val lrcType = SpTools.getInt(GlobalData.SpKey.LRC_TYPE)
        if (lrcType == LrcType.LRC_TYPE_KALAOK) {
            lrcView?.setMode(LrcView.MODE_HIGH_LIGHT_KARAOKE)
        } else {
            lrcView?.setMode(LrcView.MODE_HIGH_LIGHT_NORMAL)
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
        GlobalData.lrcTypeChange.observe(this) {
            setLrcType()
            lrcView?.invalidate()
        }
    }

    override fun onLrcDrag(newPosition: Int, row: LrcRow) {
        mediaHelper?.seekTo(row.startTime.toInt())
    }
}