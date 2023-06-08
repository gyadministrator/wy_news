package com.android.wy.news.activity

import android.database.Cursor
import android.provider.MediaStore
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.wy.news.adapter.BaseNewsAdapter
import com.android.wy.news.adapter.LocalMusicAdapter
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.ActivityLocalMusicBinding
import com.android.wy.news.entity.music.LocalMusic
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.music.MediaPlayerHelper
import com.android.wy.news.music.MusicState
import com.android.wy.news.viewmodel.MusicLocalViewModel


@Route(path = RouteManager.PATH_ACTIVITY_MUSIC_LOCAL)
class MusicLocalActivity : BaseActivity<ActivityLocalMusicBinding, MusicLocalViewModel>(),
    BaseNewsAdapter.OnItemAdapterListener<LocalMusic> {
    private val dataList = ArrayList<LocalMusic>()
    private var rvContent: RecyclerView? = null
    private var localMusicAdapter: LocalMusicAdapter? = null
    private var mediaPlayer: MediaPlayerHelper? = null

    override fun setDefaultImmersionBar(): Boolean {
        return true
    }

    override fun hideStatusBar(): Boolean {
        return false
    }

    override fun hideNavigationBar(): Boolean {
        return false
    }

    override fun isFollowNightMode(): Boolean {
        return true
    }

    override fun initView() {
        rvContent = mBinding.rvContent
        localMusicAdapter = LocalMusicAdapter(this)
        rvContent?.layoutManager = LinearLayoutManager(this)
        rvContent?.adapter = localMusicAdapter
    }

    override fun initData() {
        mediaPlayer = MediaPlayerHelper.getInstance(this)
        getLocalMusic()
    }

    private fun getLocalMusic() {
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Audio.AudioColumns.IS_MUSIC
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val song =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val singer =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val path =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val duration =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val size =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val albumId =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                val localMusic =
                    LocalMusic(
                        song,
                        singer,
                        size,
                        duration,
                        path,
                        albumId,
                        id,
                        MusicState.STATE_PREPARE
                    )
                //把歌曲名字和歌手切割开
                if (localMusic.size > 1000 * 800) {
                    if (localMusic.song.contains("-")) {
                        val str: List<String> = localMusic.song.split("-")
                        localMusic.singer = str[0]
                        localMusic.song = str[1]
                    }
                    dataList.add(localMusic)
                }
            }
        }
        cursor?.close()
        Logger.i("getLocalMusic---$dataList")
        localMusicAdapter?.refreshData(dataList)
    }

    override fun initEvent() {

    }

    override fun getViewBinding(): ActivityLocalMusicBinding {
        return ActivityLocalMusicBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): MusicLocalViewModel {
        return CommonTools.getViewModel(this, MusicLocalViewModel::class.java)
    }

    override fun onClear() {

    }

    override fun onNotifyDataChanged() {

    }

    override fun onItemClickListener(view: View, data: LocalMusic) {
        mediaPlayer?.setPath(data.path)
        val tag = view.tag
        if (tag is Int) {
            localMusicAdapter?.setSelectedIndex(tag)
        }
    }

    override fun onItemLongClickListener(view: View, data: LocalMusic) {

    }

}