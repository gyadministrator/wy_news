package com.android.wy.news.music

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.gyf.immersionbar.BarHide
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
        setCanceledOnTouchOutside(false)
        setCancelable(false)
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
        //hideBar()
        setBarColor()
    }

    private fun hideBar() {
        //去除对话框内边距与背景透明
        val layoutParams = window?.attributes
        val width: Float = context.resources.displayMetrics.widthPixels * 1f
        val height: Float = context.resources.displayMetrics.heightPixels * 1f
        layoutParams?.width = width.toInt()
        layoutParams?.height = height.toInt()
        window?.attributes = layoutParams
        window?.decorView?.background = ColorDrawable(Color.TRANSPARENT)
        window?.decorView?.setPadding(0, 0, 0, 0)
        //隐藏导航栏与状态栏
        val params = window?.attributes
        params?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        window?.attributes = params
        setBarColor()
    }

    override fun onStop() {
        super.onStop()
        //hideBar()
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