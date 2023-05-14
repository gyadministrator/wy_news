package com.android.wy.news.fragment

import android.os.Bundle
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.FragmentPlayMusicSongBinding
import com.android.wy.news.viewmodel.PlayMusicSongViewModel

class PlayMusicSongFragment : BaseFragment<FragmentPlayMusicSongBinding, PlayMusicSongViewModel>() {
    companion object {
        private const val POSITION_KEY = "position_key"
        private const val MUSIC_INFO_KEY = "music_info_key"
        private const val MUSIC_URL_KEY = "music_url_key"

        fun newInstance(position: Int, musicInfoJson: String, url: String?): PlayMusicSongFragment {
            val fragment = PlayMusicSongFragment()
            val args = Bundle()
            args.putInt(POSITION_KEY, position)
            args.putString(MUSIC_INFO_KEY, musicInfoJson)
            args.putString(MUSIC_URL_KEY, url)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView() {
    }

    override fun initData() {
    }

    override fun initEvent() {
    }

    override fun getViewBinding(): FragmentPlayMusicSongBinding {
        return FragmentPlayMusicSongBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): PlayMusicSongViewModel {
        return CommonTools.getViewModel(this, PlayMusicSongViewModel::class.java)
    }

    override fun onClear() {
    }

    override fun onNotifyDataChanged() {
    }
}