package com.android.wy.news.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.tablib.adapter.FragmentPageAdapter
import com.android.tablib.view.CustomTabLayout
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.FragmentPlayMusicBinding
import com.android.wy.news.dialog.BaseDialogFragment
import com.android.wy.news.dialog.LrcTypeDialog
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.listener.IPageChangeListener
import com.android.wy.news.util.JsonUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import jp.wasabeef.glide.transformations.BlurTransformation


class PlayMusicFragment : BaseDialogFragment<FragmentPlayMusicBinding>(), IPageChangeListener {
    private var currentPosition = -1
    private var currentMusicInfo: MusicInfo? = null
    private var height = 0
    private var width = 0
    private var ivBg: ImageView? = null
    private var rlDown: RelativeLayout? = null
    private var rlMore: RelativeLayout? = null
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

    override fun initView() {
        ivBg = mBinding.ivBg
        rlDown = mBinding.rlDown
        rlMore = mBinding.rlMore
        viewPager = mBinding.viewPager
        tabLayout = mBinding.tabLayout
        rlDown?.setOnClickListener {
            dismiss()
        }
        rlMore?.setOnClickListener {
            showMore()
        }
    }

    private fun showMore() {
        val dialog = LrcTypeDialog()
        dialog.show((context as AppCompatActivity).supportFragmentManager, "lrc_type_dialog")
    }

    override fun initData() {
        val args = arguments
        if (args != null) {
            currentPosition = args.getInt(POSITION_KEY)
            currentPlayUrl = args.getString(MUSIC_URL_KEY)
            val s = args.getString(MUSIC_INFO_KEY)
            if (!TextUtils.isEmpty(s)) {
                currentMusicInfo = JsonUtil.parseJsonToObject(s, MusicInfo::class.java)
            }
        }
        initTab()
        ivBg?.let {
            Glide.with(this).load(this.currentMusicInfo?.pic)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 10))).into(it)
        }
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): FragmentPlayMusicBinding {
        return FragmentPlayMusicBinding.inflate(layoutInflater)
    }

    override fun onClear() {

    }

    override fun initIntent() {

    }

    override fun getLayoutHeight(): Int {
        return CommonTools.getScreenHeight()
    }

    override fun getLayoutWidth(): Int {
        return CommonTools.getScreenWidth()
    }

    override fun setFragmentStyle() {
        setStyle(STYLE_NORMAL, com.android.wy.news.locationselect.R.style.CityPickerStyle)
    }

    override fun getGravityLocation(): Int {
        return Gravity.BOTTOM
    }

    override fun isTouchDismiss(): Boolean {
        return true
    }

    private fun initTab() {
        val fragments = ArrayList<Fragment>()
        val mTitles = arrayListOf(/*"推荐", */"歌曲", "歌词")
        val s = this.currentMusicInfo?.let { JsonUtil.parseObjectToJson(it) }
        /*val playRecommendFragment =
            s?.let { PlayRecommendFragment.newInstance(currentPosition, it, currentPlayUrl) }*/
        val playMusicSongFragment =
            s?.let { PlayMusicSongFragment.newInstance(currentPosition, it, currentPlayUrl, this) }
        val playMusicLrcFragment =
            s?.let { PlayMusicLrcFragment.newInstance(currentPosition, it, currentPlayUrl) }
        //playRecommendFragment?.let { fragments.add(it) }
        playMusicSongFragment?.let { fragments.add(it) }
        playMusicLrcFragment?.let { fragments.add(it) }
        viewPager?.offscreenPageLimit = mTitles.size
        viewPager?.adapter =
            FragmentPageAdapter(childFragmentManager, fragments, mTitles.toTypedArray())
        tabLayout?.setupWithViewPager(viewPager)
        tabLayout?.initLayout()
        viewPager?.isSaveEnabled = false
        tabLayout?.setSelectedTabIndicatorHeight(0)
        viewPager?.currentItem = 0
    }

    override fun onResume() {
        super.onResume()
        hideNavigationBar()
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
            window.setLayout(width, height)
            window.setWindowAnimations(mAnimStyle)
        }
    }

    private fun hideNavigationBar() {
        val mImmersionBar = ImmersionBar.with(this)
        mImmersionBar.hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
        mImmersionBar.init()
    }

    //测量宽高
    @SuppressLint("ObsoleteSdkInt")
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

    override fun changePage(page: Int) {
        if (page < 3) {
            viewPager?.currentItem = page
        }
    }
}