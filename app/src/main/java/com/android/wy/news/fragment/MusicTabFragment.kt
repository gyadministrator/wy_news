package com.android.wy.news.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.databinding.FragmentTabMusicBinding
import com.android.wy.news.view.PlayBarView
import com.android.wy.news.view.TabViewPager
import com.android.wy.news.viewmodel.MusicTabViewModel

class MusicTabFragment : BaseFragment<FragmentTabMusicBinding, MusicTabViewModel>() {
    private lateinit var tabViewPager: TabViewPager
    private lateinit var playBarView: PlayBarView
    private var isShowAnim = false

    companion object {
        fun newInstance() = MusicTabFragment()
    }

    override fun initView() {
        tabViewPager = mBinding.tabViewPager
        playBarView = mBinding.playBarView
    }

    fun getPlayBarView(): PlayBarView {
        return playBarView
    }

    fun startAnim() {
        if (isShowAnim) return
        val animatorSet = AnimatorSet()
        animatorSet.duration = 1000
        animatorSet.interpolator = LinearInterpolator()
        val alpha = ObjectAnimator.ofFloat(playBarView, "alpha", 0f, 1f)
        val translationY = ObjectAnimator.ofFloat(playBarView, "translationY", 50f, 0f)
        animatorSet.playTogether(alpha, translationY)
        animatorSet.start()
        isShowAnim = true
    }

    private fun stopAnim() {
        val animatorSet = AnimatorSet()
        animatorSet.duration = 1000
        animatorSet.interpolator = LinearInterpolator()
        val alpha = ObjectAnimator.ofFloat(playBarView, "alpha", 1f, 0f)
        val translationY = ObjectAnimator.ofFloat(playBarView, "translationY", 50f, 0f)
        animatorSet.playTogether(alpha, translationY)
        animatorSet.start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                playBarView.visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
    }

    override fun initData() {
        val titleList = GlobalData.mMusicTitleList
        val fragments = ArrayList<Fragment>()
        val mTitles = arrayListOf<String>()
        if (titleList.size > 0) {
            for (i in titleList.indices) {
                val titleEntity = titleList[i]
                val id = titleEntity.id
                mTitles.add(titleEntity.title)
                val fragment = MusicFragment.newInstance(id)
                fragments.add(fragment)
            }
            tabViewPager.initData(childFragmentManager, fragments, mTitles)
        }
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): FragmentTabMusicBinding {
        return FragmentTabMusicBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): MusicTabViewModel {
        return CommonTools.getViewModel(this, MusicTabViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

}