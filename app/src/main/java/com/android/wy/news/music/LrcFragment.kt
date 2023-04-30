package com.android.wy.news.music

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
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
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.listener.CustomAnimatorUpdateListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import jp.wasabeef.glide.transformations.BlurTransformation


class LrcFragment : DialogFragment() {
    private var mContentView: View? = null
    private var currentPosition = -1
    private var currentMusicInfo: MusicInfo? = null
    private var height = 0
    private var width = 0
    private var ivCover: ImageView? = null
    private var ivBg: ImageView? = null
    private var tvTitle: TextView? = null
    private var tvDesc: TextView? = null
    private var rlDown: RelativeLayout? = null
    private var mAnimStyle: Int =
        com.android.wy.news.locationselect.R.style.DefaultCityPickerAnimation

    companion object {
        private const val POSITION_KEY = "position_key"
        private const val MUSIC_INFO_KEY = "music_info_key"
        const val TAG = "LrcFragment"

        fun newInstance(position: Int, musicInfoJson: String): LrcFragment {
            val fragment = LrcFragment()
            val args = Bundle()
            args.putInt(POSITION_KEY, position)
            args.putString(MUSIC_INFO_KEY, musicInfoJson)
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
        mContentView = inflater.inflate(R.layout.lrc_fragment, container, false)
        return mContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initData()
    }

    private fun initViews() {
        ivBg = mContentView?.findViewById(R.id.iv_bg)
        ivCover = mContentView?.findViewById(R.id.iv_cover)
        tvTitle = mContentView?.findViewById(R.id.tv_title)
        tvDesc = mContentView?.findViewById(R.id.tv_desc)
        rlDown = mContentView?.findViewById(R.id.rl_down)
        rlDown?.setOnClickListener {
            dismiss()
        }
    }

    private fun initData() {
        val args = arguments
        if (args != null) {
            currentPosition = args.getInt(POSITION_KEY)
            val s = args.getString(MUSIC_INFO_KEY)
            if (!TextUtils.isEmpty(s)) {
                val gson = Gson()
                currentMusicInfo = gson.fromJson(s, MusicInfo::class.java)
            }
        }
        setMusic()
    }

    private fun setMusic() {
        ivBg?.let {
            Glide.with(this).load(this.currentMusicInfo?.pic)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 10)))
                .into(it)
        }
        this.currentMusicInfo?.pic?.let { ivCover?.let { it1 -> CommonTools.loadImage(it, it1) } }
        tvTitle?.text = this.currentMusicInfo?.artist
        tvDesc?.text = this.currentMusicInfo?.album
        initAnim()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun initAnim() {
        val lin = LinearInterpolator() //声明为线性变化
        val anim = ObjectAnimator.ofFloat(ivCover, "rotation", 0f, 360f) //设置动画为旋转动画，角度是0-360
        anim.duration = 15000 //时间15秒，这个可以自己酌情修改
        anim.interpolator = lin
        anim.repeatMode = ValueAnimator.RESTART //设置重复模式为重新开始
        anim.repeatCount = -1 //重复次数为-1，就是无限循环
        val listener =
            CustomAnimatorUpdateListener(anim) //将定义好的ObjectAnimator传给MyAnimatorUpdateListener监听
        anim.addUpdateListener(listener) //给动画加监听
        listener.play()
    }

    override fun onStart() {
        super.onStart()
        val mImmersionBar = ImmersionBar.with(this)
        mImmersionBar.hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
        mImmersionBar.fullScreen(true)
        mImmersionBar.init()
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