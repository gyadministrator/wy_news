package com.android.wy.news.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.FragmentPlayRecommendBinding
import com.android.wy.news.entity.music.MusicRecommendEntity
import com.android.wy.news.http.repository.MusicRepository
import com.android.wy.news.view.CustomLoadingView
import com.android.wy.news.view.MusicRecyclerView
import com.android.wy.news.viewmodel.PlayRecommendViewModel

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/25 9:10
  * @Version:        1.0
  * @Description:    
 */
class PlayRecommendFragment : BaseFragment<FragmentPlayRecommendBinding, PlayRecommendViewModel>() {
    private var ivCover: ImageView? = null
    private var tvTitle: TextView? = null
    private var loadingView: CustomLoadingView? = null
    private var musicRecyclerView: MusicRecyclerView? = null

    companion object {
        private const val POSITION_KEY = "position_key"
        private const val MUSIC_INFO_KEY = "music_info_key"
        private const val MUSIC_URL_KEY = "music_url_key"

        fun newInstance(position: Int, musicInfoJson: String, url: String?): PlayRecommendFragment {
            val fragment = PlayRecommendFragment()
            val args = Bundle()
            args.putInt(POSITION_KEY, position)
            args.putString(MUSIC_INFO_KEY, musicInfoJson)
            args.putString(MUSIC_URL_KEY, url)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView() {
        ivCover = mBinding.ivCover
        tvTitle = mBinding.tvTitle
        musicRecyclerView = mBinding.musicRecycler
        loadingView = mBinding.loadingView
    }

    override fun initData() {
        MusicRepository.getRecommendMusic().observe(this) {
            Logger.i("getRecommendMusic--->>>$it")
            setContent(it)
        }
    }

    private fun setContent(it: Result<MusicRecommendEntity>?) {
        loadingView?.visibility = View.GONE
        val musicRecommendEntity = it?.getOrNull()
        if (musicRecommendEntity != null) {
            val data = musicRecommendEntity.data
            ivCover?.let { it1 -> CommonTools.loadImage(data.img500, it1) }
            tvTitle?.text = data.name

            musicRecyclerView?.refreshData(data.musicList)
        }
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): FragmentPlayRecommendBinding {
        return FragmentPlayRecommendBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): PlayRecommendViewModel {
        return CommonTools.getViewModel(this, PlayRecommendViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }
}