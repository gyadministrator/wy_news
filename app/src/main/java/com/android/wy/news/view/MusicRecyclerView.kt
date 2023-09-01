package com.android.wy.news.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.wy.news.R
import com.android.wy.news.activity.SingerAlbumActivity
import com.android.wy.news.activity.SingerMusicActivity
import com.android.wy.news.activity.SingerMvActivity
import com.android.wy.news.activity.WebFragmentActivity
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.MusicAdapter
import com.android.wy.news.app.App
import com.android.wy.news.common.GlobalData
import com.android.wy.news.dialog.CommonOperationDialog
import com.android.wy.news.entity.OperationItemEntity
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.listener.IMusicItemChangeListener
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.music.MusicState
import com.android.wy.news.util.AppUtil

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
        val stringBuilder = StringBuilder()
        val musicInfo = data
        val album = data.name
        val artist = data.artist
        stringBuilder.append(artist)
        if (!TextUtils.isEmpty(album)) {
            stringBuilder.append("-$album")
        }
        val activity = context as AppCompatActivity
        val list = arrayListOf(
            OperationItemEntity(R.mipmap.album, AppUtil.getString(App.app, R.string.album)),
            OperationItemEntity(R.mipmap.state_one, AppUtil.getString(App.app, R.string.single)),
            OperationItemEntity(R.mipmap.video, AppUtil.getString(App.app, R.string.mv)),
        )
        CommonOperationDialog.show(
            activity,
            stringBuilder.toString(),
            list,
            object : BaseNewsAdapter.OnItemAdapterListener<OperationItemEntity> {
                override fun onItemClickListener(view: View, data: OperationItemEntity) {
                    val tag = view.tag
                    val artistId = musicInfo.artistid
                    if (tag is Int) {
                        when (tag) {
                            0 -> {
                                SingerAlbumActivity.startActivity(context, artistId.toString())
                            }

                            1 -> {
                                SingerMusicActivity.startActivity(context, artistId.toString(), 0)
                            }

                            2 -> {
                                SingerMvActivity.startActivity(context, artistId.toString())
                            }

                            else -> {

                            }
                        }
                    }
                }

                override fun onItemLongClickListener(view: View, data: OperationItemEntity) {
                }
            })
        //musicItemListener?.onItemLongClick(view, data)
    }
}