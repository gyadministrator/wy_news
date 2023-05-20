package com.android.wy.news.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.databinding.MusicListDialogBinding
import com.android.wy.news.entity.music.MusicInfo
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gyf.immersionbar.ImmersionBar


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/5 15:19
  * @Version:        1.0
  * @Description:    
 */
@SuppressLint("InflateParams")
class MusicListDialog(context: Context, theme: Int) : BottomSheetDialog(context, theme),
    BaseNewsAdapter.OnItemAdapterListener<MusicInfo> {


    private var tvTitle: TextView? = null
    private var rvContent: RecyclerView? = null
    private var rlClose: RelativeLayout? = null
    private lateinit var musicAdapter: MusicAdapter

    init {
        val view = layoutInflater.inflate(R.layout.music_list_dialog, null)
        val binding = MusicListDialogBinding.bind(view)
        initView(binding)
        this.setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }

    private fun initView(binding: MusicListDialogBinding) {
        tvTitle = binding.tvTitle
        rvContent = binding.rvContent
        rlClose = binding.rlClose
        musicAdapter = MusicAdapter(this)
        rvContent?.layoutManager = LinearLayoutManager(this.context)
        rvContent?.adapter = musicAdapter
        rlClose?.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        setBarColor()
    }

    private fun setBarColor() {
        val mImmersionBar = ownerActivity?.let { ImmersionBar.with(it) }
        mImmersionBar?.navigationBarColor(R.color.default_status_bar_color)
        mImmersionBar?.init()
    }

    @SuppressLint("SetTextI18n")
    fun setData(dataList: ArrayList<MusicInfo>) {
        musicAdapter.loadMoreData(dataList)
        val list = musicAdapter.getDataList()
        tvTitle?.text = "当前播放列表(" + list.size + ")"
    }

    override fun onItemClickListener(view: View, data: MusicInfo) {

    }
}