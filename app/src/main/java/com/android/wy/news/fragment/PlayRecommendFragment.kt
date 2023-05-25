package com.android.wy.news.fragment

import android.os.Bundle
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.FragmentPlayRecommendBinding
import com.android.wy.news.viewmodel.PlayRecommendViewModel

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/25 9:10
  * @Version:        1.0
  * @Description:    
 */
class PlayRecommendFragment : BaseFragment<FragmentPlayRecommendBinding, PlayRecommendViewModel>() {

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

    }

    override fun initData() {

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