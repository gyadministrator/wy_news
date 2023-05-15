package com.android.wy.news.fragment

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.tablib.adapter.FragmentPageAdapter
import com.android.tablib.view.CustomTabLayout
import com.android.wy.news.R
import com.android.wy.news.databinding.FragmentPlayMusicBinding
import com.android.wy.news.entity.music.MusicInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import jp.wasabeef.glide.transformations.BlurTransformation


class PlayMusicFragment : DialogFragment() {
    private var mContentView: View? = null
    private var currentPosition = -1
    private var currentMusicInfo: MusicInfo? = null
    private var height = 0
    private var width = 0
    private var ivBg: ImageView? = null
    private var rlDown: RelativeLayout? = null
    private var mAnimStyle: Int =
        com.android.wy.news.locationselect.R.style.DefaultCityPickerAnimation
    private var currentPlayUrl: String? = null
    private var tabLayout: CustomTabLayout? = null
    private var viewPager: ViewPager? = null

    companion object {
        const val POSITION_KEY = "position_key"
        const val MUSIC_INFO_KEY = "music_info_key"
        const val MUSIC_URL_KEY = "music_url_key"
        const val TAG = "PlayMusicFragment"

        fun newInstance(position: Int, musicInfoJson: String, url: String?): PlayMusicFragment {
            val fragment = PlayMusicFragment()
            val args = Bundle()
            args.putInt(POSITION_KEY, position)
            args.putString(MUSIC_INFO_KEY, musicInfoJson)
            args.putString(MUSIC_URL_KEY, url)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.android.wy.news.locationselect.R.style.CityPickerStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mContentView = inflater.inflate(R.layout.fragment_play_music, container, false)
        return mContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = mContentView?.let { FragmentPlayMusicBinding.bind(it) }
        initView(binding)
        initData()
    }

    private fun initView(binding: FragmentPlayMusicBinding?) {
        ivBg = binding?.ivBg
        rlDown = binding?.rlDown
        viewPager = binding?.viewPager
        tabLayout = binding?.tabLayout
        rlDown?.setOnClickListener {
            dismiss()
        }
    }

    private fun initData() {
        val args = arguments
        if (args != null) {
            currentPosition = args.getInt(POSITION_KEY)
            currentPlayUrl = args.getString(MUSIC_URL_KEY)
            val s = args.getString(MUSIC_INFO_KEY)
            if (!TextUtils.isEmpty(s)) {
                val gson = Gson()
                currentMusicInfo = gson.fromJson(s, MusicInfo::class.java)
            }
        }
        initTab()
        ivBg?.let {
            Glide.with(this).load(this.currentMusicInfo?.pic)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 10))).into(it)
        }
    }

    private fun initTab() {
        val fragments = ArrayList<Fragment>()
        val mTitles = arrayListOf("歌曲", "歌词")
        val gson = Gson()
        val s = gson.toJson(this.currentMusicInfo)
        val playMusicSongFragment =
            PlayMusicSongFragment.newInstance(currentPosition, s, currentPlayUrl)
        val playMusicLrcFragment =
            PlayMusicLrcFragment.newInstance(currentPosition, s, currentPlayUrl)
        fragments.add(playMusicSongFragment)
        fragments.add(playMusicLrcFragment)
        viewPager?.offscreenPageLimit = mTitles.size
        viewPager?.adapter =
            FragmentPageAdapter(childFragmentManager, fragments, mTitles.toTypedArray())
        tabLayout?.setupWithViewPager(viewPager)
        tabLayout?.initLayout()
        viewPager?.isSaveEnabled = false
        tabLayout?.setSelectedTabIndicatorHeight(0)
    }

    override fun onStart() {
        super.onStart()
        hideNavigationBar()
        val dialog = dialog
        dialog?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss()
            }
            false
        }
        measure()
        val window = dialog?.window
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            window.setGravity(Gravity.BOTTOM)
            window.setLayout(width, height /*- ScreenUtil.getStatusBarHeight(requireActivity())*/)
            window.setWindowAnimations(mAnimStyle)
        }
    }

    private fun hideNavigationBar() {
        val mImmersionBar = ImmersionBar.with(this)
        mImmersionBar.hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
        mImmersionBar.init()
    }

    //测量宽高
    private fun measure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val dm = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getRealMetrics(dm)
            height = dm.heightPixels
            width = dm.widthPixels
        } else {
            val dm = resources.displayMetrics
            height = dm.heightPixels
            width = dm.widthPixels
        }
    }
}