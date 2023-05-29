package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.entity.music.MusicInfo

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/27 15:06
  * @Version:        1.0
  * @Description:    
 */
class MusicRecyclerView : CustomRecyclerView, BaseNewsAdapter.OnItemAdapterListener<MusicInfo> {
    private var musicAdapter: MusicAdapter? = null

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
    }

    fun loadData(dataList: ArrayList<MusicInfo>) {
        musicAdapter?.loadMoreData(dataList)
    }

    fun getDataList(): ArrayList<MusicInfo>? {
        return musicAdapter?.getDataList()
    }

    override fun onItemClickListener(view: View, data: MusicInfo) {

    }

    override fun onItemLongClickListener(view: View, data: MusicInfo) {

    }
}