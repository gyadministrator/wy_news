package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.LayoutMusicItemBinding
import com.android.wy.news.entity.music.MusicResult
import com.android.wy.news.receiver.MusicBroadCastReceiver
import com.android.wy.news.service.MusicService
import java.util.*


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class MusicAdapter(
    context: Context, itemAdapterListener: OnItemAdapterListener<MusicResult>
) : BaseNewsAdapter<MusicAdapter.ViewHolder, MusicResult>(itemAdapterListener) {
    private var musicBroadCastReceiver: MusicBroadCastReceiver? = null
    private var isBind = false
    private var mContext: Context? = null
    private var selectedPosition = -5
    private var mResID = -5
    private var musicBinder: MusicService.MusicBinder? = null
    private var musicService: MusicService? = null
    private var musicResult: MusicResult? = null
    private var mServiceIntent: Intent? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutMusicItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvPlay = mBinding.tvPlay
        var ivCover = mBinding.ivCover
    }

    init {
        mContext = context
        musicBroadCastReceiver = MusicBroadCastReceiver.instance
        isBind = false
    }

    private fun setMusicBroadCastListener() {
        /**
         * 此处仍然来实现单例对象的接口对象
         * 即实现歌曲的切换，实现广播接收器的外放接口
         */
        musicBroadCastReceiver?.setMusicBroadListener(object :
            MusicBroadCastReceiver.OnMusicBroadListener {
            override fun playPre() {
                playMusicPre()
            }

            override fun playNext() {
                playMusicNext()
            }
        })
    }

    private fun playMusicPre() {
        if (selectedPosition > 0) {
            setSelectedIndex(selectedPosition - 1)
        }
    }

    private fun playMusicNext() {
        if (selectedPosition < itemCount - 1) {
            setSelectedIndex(selectedPosition + 1)
        }
    }

    private fun stateChange(holder: ViewHolder, position: Int) {
        if (selectedPosition == position) {
            setMusicBroadCastListener()

            holder.itemView.setBackgroundResource(R.drawable.bg_select_item)
            holder.tvTitle.setTextColor(ContextCompat.getColor(mContext!!, R.color.white))

            val result = mDataList[position]
            if (result.id == mResID && mResID != -5) {
                //相同音乐id或者且不是第一次播放，就直接返回
                return
            }
            mResID = result.id
            //每次切歌需要重新绑定服务
            destroy()
            if (!isBind) {
                mContext?.bindService(mServiceIntent, connection, Context.BIND_AUTO_CREATE)
                isBind = true
            }
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_item)
            holder.tvTitle.setTextColor(ContextCompat.getColor(mContext!!, R.color.main_title))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setSelectedIndex(position: Int) {
        Logger.i("setSelectedIndex: $position")
        selectedPosition = position
        notifyItemChanged(position)
        notifyDataSetChanged()
    }


    private fun destroy() {
        if (isBind) {
            mContext?.unbindService(connection)
            isBind = false
        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            musicBinder = service as (MusicService.MusicBinder)
            musicService = musicBinder?.getService();
            musicResult?.let { musicBinder?.setMusic(musicResult = it) }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {

        }
    };


    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = getView(parent, R.layout.layout_music_item)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindData(holder: ViewHolder, position: Int, data: MusicResult) {
        holder.tvTitle.text = data.title
        val trackCount = data.trackCount
        if (trackCount is Int) {
            if (trackCount > 0) {
                if (trackCount > 10000) {
                    val fl = trackCount / 10000f
                    holder.tvPlay.text = "%.1f".format(fl) + "w次播放"
                } else {
                    holder.tvPlay.text = trackCount.toString() + "次播放"
                }
            }
        }
        setClick(holder, position)
        CommonTools.loadImage(data.pic, holder.ivCover)
        stateChange(holder, position)
    }

    private fun setClick(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            setSelectedIndex(position)
            showNotify(position)
        }
    }

    private fun showNotify(position: Int) {
        this.musicResult = mDataList[position]
        //启动服务来播放
        if (mServiceIntent == null) {
            mServiceIntent = Intent(mContext, MusicService::class.java)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext?.startForegroundService(mServiceIntent)
        } else {
            mContext?.startService(mServiceIntent)
        }
    }
}