package com.android.wy.news.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.LayoutMusicPlayBarBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.event.MusicEvent
import com.android.wy.news.event.MusicUrlEvent
import com.android.wy.news.fragment.PlayMusicFragment
import com.android.wy.news.manager.LrcDesktopManager
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.MusicState
import com.android.wy.news.util.JsonUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/25 16:52
  * @Version:        1.0
  * @Description:    
 */
class PlayBarView : LinearLayout, View.OnClickListener {
    private lateinit var tvTitle: TextView
    private lateinit var ivCover: ImageView
    private lateinit var ivPlay: ImageView
    private lateinit var rlPlay: RelativeLayout
    private lateinit var roundProgressBar: RoundProgressBar
    private var activity: AppCompatActivity? = null
    private var currentMusicInfo: MusicInfo? = null
    private var mediaHelper: MediaPlayerHelper? = null
    private var isShowAnim = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_music_play_bar, this)
        val binding = LayoutMusicPlayBarBinding.bind(view)
        initView(binding)
    }

    private fun initPlayBar() {
        val s = SpTools.getString(GlobalData.SpKey.LAST_PLAY_MUSIC_KEY)
        this.currentMusicInfo = JsonUtil.parseJsonToObject(s, MusicInfo::class.java)
        if (this.currentMusicInfo != null) {
            showPlayBar()
        }
    }

    private fun showPlayBar() {
        val stringBuilder = StringBuilder()
        val album = currentMusicInfo?.name
        val artist = currentMusicInfo?.artist
        stringBuilder.append(artist)
        if (!TextUtils.isEmpty(album)) {
            stringBuilder.append("-$album")
        }
        visibility = View.VISIBLE
        currentMusicInfo?.pic?.let {
            currentMusicInfo?.duration?.let { it1 ->
                setCover(it).setTitle(stringBuilder.toString())
                    .setDuration(duration = it1 * 1000)
            }
        }

        startAnim()
    }

    private fun startAnim() {
        if (isShowAnim) return
        val animatorSet = AnimatorSet()
        animatorSet.duration = 1000
        animatorSet.interpolator = LinearInterpolator()
        val alpha = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
        val translationY = ObjectAnimator.ofFloat(this, "translationY", 50f, 0f)
        animatorSet.playTogether(alpha, translationY)
        animatorSet.start()
        isShowAnim = true
    }

    private fun stopAnim() {
        val animatorSet = AnimatorSet()
        animatorSet.duration = 1000
        animatorSet.interpolator = LinearInterpolator()
        val alpha = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
        val translationY = ObjectAnimator.ofFloat(this, "translationY", 50f, 0f)
        animatorSet.playTogether(alpha, translationY)
        animatorSet.start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
    }

    fun register(appCompatActivity: AppCompatActivity) {
        this.activity = appCompatActivity
        mediaHelper = MediaPlayerHelper.getInstance(appCompatActivity)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initPlayBar()
        GlobalData.isPlaying.observe(appCompatActivity) {
            setPlay(it)
        }
        PlayMusicManager.getPlayMusicInfo().observe(appCompatActivity) {
            setCover(it.pic)
        }
    }

    fun unRegister() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(o: Any) {
        if (o is MusicEvent) {
            Logger.i("onEvent--->>>time:${o.time}")
            if (GlobalData.currentLrcData.size == 0) {
                PlayMusicManager.getLrc()
            }
            updateProgress(o.time)
            activity?.let { LrcDesktopManager.showDesktopLrc(it, o.time.toLong()) }
            val position =
                CommonTools.lrcTime2Position(GlobalData.currentLrcData, o.time.toLong())
            val lrc = GlobalData.currentLrcData[position]
            setTitle(lrc.text)
        } else if (o is MusicUrlEvent) {
            PlayMusicManager.playMusic(o.url)
        }
    }

    private fun initView(binding: LayoutMusicPlayBarBinding) {
        tvTitle = binding.tvTitle
        ivCover = binding.ivCover
        ivPlay = binding.ivPlay
        rlPlay = binding.rlPlay
        roundProgressBar = binding.roundProgressBar
        rlPlay.setOnClickListener(this)
        ivCover.setOnClickListener(this)
        tvTitle.setOnClickListener(this)
    }

    private fun updateProgress(progress: Int) {
        roundProgressBar.setProgress(progress)
    }

    fun getPlayContainer(): RelativeLayout {
        return rlPlay
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.rl_play -> {
                    onClickPlay()
                }

                R.id.iv_cover -> {
                    showLrcPage()
                }

                R.id.tv_title -> {
                    showLrcPage()
                }

                else -> {

                }
            }
        }
    }

    private fun onClickPlay() {
        Logger.i("onClickPlay--->>>")
        if (mediaHelper != null) {
            if (mediaHelper!!.isPlaying()) {
                mediaHelper?.pause()
                this.currentMusicInfo?.state = MusicState.STATE_PAUSE
            } else {
                if (!TextUtils.isEmpty(PlayMusicManager.getPlayUrl())) {
                    mediaHelper?.start()
                    this.currentMusicInfo?.state = MusicState.STATE_PLAY
                } else {
                    currentMusicInfo?.let { PlayMusicManager.requestMusicInfo(it) }
                }
            }
        }
    }

    private fun showLrcPage() {
        val fragmentManager = activity?.supportFragmentManager
        var ft: FragmentTransaction? = fragmentManager?.beginTransaction()
        val prev: Fragment? = fragmentManager?.findFragmentByTag(PlayMusicFragment.TAG)
        if (prev != null) {
            ft?.remove(prev)?.commit()
            ft = fragmentManager.beginTransaction()
        }
        ft?.addToBackStack(null)
        val s = this.currentMusicInfo?.let { JsonUtil.parseObjectToJson(it) }
        val playMusicFragment =
            s?.let {
                PlayMusicFragment.newInstance(
                    PlayMusicManager.getPlayPosition(),
                    it,
                    PlayMusicManager.getPlayUrl()
                )
            }
        if (playMusicFragment != null) {
            ft?.let { playMusicFragment.show(ft, PlayMusicFragment.TAG) }
        }
    }

    private fun setDuration(duration: Int): PlayBarView {
        roundProgressBar.setMax(duration)
        return this
    }

    private fun setCover(cover: String): PlayBarView {
        CommonTools.loadImage(cover, ivCover)
        return this
    }

    private fun setTitle(title: String): PlayBarView {
        tvTitle.text = title
        return this
    }

    private fun setPlay(isPlaying: Boolean): PlayBarView {
        if (isPlaying) {
            ivPlay.setImageResource(R.mipmap.music_play)
        } else {
            ivPlay.setImageResource(R.mipmap.music_pause)
        }
        return this
    }

    fun checkState(musicInfo: MusicInfo): Boolean {
        if (currentMusicInfo != null && currentMusicInfo!!.rid == musicInfo.rid) return false
        this.currentMusicInfo = musicInfo
        return true
    }
}