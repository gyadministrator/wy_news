package com.android.wy.news.dialog

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.MusicListDialogBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.listener.IMusicItemChangeListener
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.util.JsonUtil
import com.android.wy.news.view.MusicRecyclerView


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/5 15:19
  * @Version:        1.0
  * @Description:    
 */
@SuppressLint("InflateParams")
class MusicListDialog : BaseBottomSheetFragment<MusicListDialogBinding>(),
    IMusicItemChangeListener {

    private var tvTitle: TextView? = null
    private var rvContent: MusicRecyclerView? = null
    private var rlClose: RelativeLayout? = null
    private var musicAdapter: MusicAdapter? = null
    private var dataList = ArrayList<MusicInfo>()

    companion object {
        const val MUSIC_LIST_KEY = "music_list_key"
    }

    override fun getLayoutHeight(): Int {
        return (CommonTools.getScreenHeight() * 0.75).toInt()
    }

    override fun getLayoutWidth(): Int {
        return CommonTools.getScreenWidth()
    }

    override fun getGravityLocation(): Int {
        return Gravity.BOTTOM
    }

    override fun initView() {
        tvTitle = mBinding.tvTitle
        rvContent = mBinding.rvContent
        rlClose = mBinding.rlClose
        rvContent?.seItemListener(this)
        rlClose?.setOnClickListener {
            dismiss()
        }
    }

    override fun getViewBinding(): MusicListDialogBinding {
        return MusicListDialogBinding.inflate(layoutInflater)
    }

    override fun onClear() {

    }

    override fun initIntent() {

    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        val arguments = arguments
        if (arguments != null) {
            val s = arguments.getString(MUSIC_LIST_KEY)
            dataList = JsonUtil.parseJsonToList(s)
        }
        musicAdapter = rvContent?.getMusicAdapter()
        rvContent?.refreshData(dataList)
        val list = musicAdapter?.getDataList()
        if (list != null) {
            tvTitle?.text = "当前播放列表(" + list.size + ")"
        }
        activity?.let {
            rvContent?.let { it1 ->
                musicAdapter?.let { it2 ->
                    PlayMusicManager.initMusicInfo(
                        it,
                        it1, null, this, it2
                    )
                }
            }
        }
        rvContent?.updatePosition(PlayMusicManager.getPlayPosition())
    }

    override fun initEvent() {

    }

    override fun onItemClick(view: View, data: MusicInfo) {
        val tag = view.tag
        if (tag is Int) {
            PlayMusicManager.prepareMusic(tag)
        }
    }

    override fun onItemLongClick(view: View, data: MusicInfo) {

    }
}