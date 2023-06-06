package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.common.GlobalData
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.listener.IMusicItemChangeListener
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.music.MusicState

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/27 15:06
  * @Version:        1.0
  * @Description:    
 */
class MusicRecyclerView : CustomRecyclerView, BaseNewsAdapter.OnItemAdapterListener<MusicInfo> {
    private var musicAdapter: MusicAdapter? = null
    private var musicItemListener: IMusicItemChangeListener? = null

    init {
        musicAdapter = MusicAdapter(this)
        this.layoutManager = LinearLayoutManager(context)
        this.adapter = musicAdapter
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    fun refreshData(dataList: ArrayList<MusicInfo>) {
        musicAdapter?.refreshData(dataList)
        val playPosition = PlayMusicManager.getPlayPosition()
        updatePosition(playPosition)
    }

    fun loadData(dataList: ArrayList<MusicInfo>) {
        musicAdapter?.loadMoreData(dataList)
        val playPosition = PlayMusicManager.getPlayPosition()
        updatePosition(playPosition)
    }

    private fun getDataList(): ArrayList<MusicInfo>? {
        return musicAdapter?.getDataList()
    }

    fun updatePosition(position: Int) {
        val dataList = getDataList()
        if (dataList != null && dataList.size > 0) {
            if (position >= 0 && position < dataList.size) {
                val musicInfo = dataList[position]
                if (GlobalData.isPlaying) {
                    musicInfo.state = MusicState.STATE_PLAY
                    musicAdapter?.setSelectedIndex(position)
                }
            }
        }
    }

    fun getMusicAdapter(): MusicAdapter? {
        return musicAdapter
    }

    fun seItemListener(musicItemListener: IMusicItemChangeListener) {
        this.musicItemListener = musicItemListener
    }

    override fun onItemClickListener(view: View, data: MusicInfo) {
        musicItemListener?.onItemClick(view, data)
    }

    override fun onItemLongClickListener(view: View, data: MusicInfo) {
        musicItemListener?.onItemLongClick(view, data)
    }
}