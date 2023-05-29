package com.android.wy.news.dialog

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.databinding.MusicListDialogBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.util.JsonUtil


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/5 15:19
  * @Version:        1.0
  * @Description:    
 */
@SuppressLint("InflateParams")
class MusicListDialog : BaseBottomSheetFragment<MusicListDialogBinding>(),
    BaseNewsAdapter.OnItemAdapterListener<MusicInfo> {

    private var tvTitle: TextView? = null
    private var rvContent: RecyclerView? = null
    private var rlClose: RelativeLayout? = null
    private lateinit var musicAdapter: MusicAdapter
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
        musicAdapter = MusicAdapter(this)
        rvContent?.layoutManager = LinearLayoutManager(this.context)
        rvContent?.adapter = musicAdapter

        musicAdapter.refreshData(dataList)
        val list = musicAdapter.getDataList()
        tvTitle?.text = "当前播放列表(" + list.size + ")"
    }

    override fun initEvent() {

    }

    override fun onItemClickListener(view: View, data: MusicInfo) {

    }

    override fun onItemLongClickListener(view: View, data: MusicInfo) {

    }
}