package com.android.wy.news.adapter

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.activity.MusicMvActivity
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Logger
import com.android.wy.news.databinding.LayoutMusicItemBinding
import com.android.wy.news.entity.music.LocalMusic
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.manager.PlayMusicManager
import com.android.wy.news.music.MusicState
import com.android.wy.news.util.JsonUtil
import java.io.FileDescriptor
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.math.max


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/17 13:45
  * @Version:        1.0
  * @Description:    
 */
class LocalMusicAdapter(itemAdapterListener: OnItemAdapterListener<LocalMusic>) :
    BaseNewsAdapter<LocalMusicAdapter.ViewHolder, LocalMusic>(itemAdapterListener) {
    private var selectedPosition = -5

    //获取专辑封面的Uri
    private val albumArtUri = Uri.parse("content://media/external/audio/albumart")

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mBinding = LayoutMusicItemBinding.bind(itemView)
        var tvTitle = mBinding.tvTitle
        var tvDesc = mBinding.tvDesc
        var ivCover = mBinding.ivCover
        var viewLine = mBinding.viewLine
        var tvLossless = mBinding.tvLossless
        var tvMv = mBinding.tvMv
        var tvVip = mBinding.tvVip
        var tvPath = mBinding.tvPath
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedIndex(position: Int) {
        Logger.i("setSelectedIndex: $position")
        selectedPosition = position
        notifyItemChanged(position)
        notifyDataSetChanged()
    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = getView(parent, R.layout.layout_music_item)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindData(holder: ViewHolder, position: Int, data: LocalMusic) {
        holder.tvTitle.text = data.song
        holder.tvDesc.text = data.singer
        /*val localPath = data.path
        if (!TextUtils.isEmpty(localPath)) {
            holder.tvPath.visibility = View.VISIBLE
            holder.tvPath.text = localPath
        } else {
            holder.tvPath.visibility = View.GONE
        }*/
        holder.tvMv.tag = data
        holder.tvMv.setOnClickListener(onMvClickListener)
        //CommonTools.loadImage(data.pic, holder.ivCover)
        val bitmap = getArtwork(holder.ivCover.context, data.id, data.albumId, true, true)
        if (bitmap != null) {
            holder.ivCover.setImageBitmap(bitmap)
        }
        checkState(holder, position)
    }

    private val onMvClickListener = View.OnClickListener { p0 ->
        val tag = p0?.tag
        if (tag is MusicInfo) {
            val s = JsonUtil.parseObjectToJson(tag)
            p0.context?.let { MusicMvActivity.startMv(it, s) }
        }
    }

    private fun checkState(holder: ViewHolder, position: Int) {
        val result = mDataList[position]
        if (selectedPosition == position) {
            when (result.state) {
                MusicState.STATE_PREPARE -> {
                    holder.viewLine.visibility = View.INVISIBLE
                    holder.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            holder.tvTitle.context,
                            R.color.main_title
                        )
                    )
                    holder.tvDesc.setTextColor(
                        ContextCompat.getColor(
                            holder.tvDesc.context,
                            R.color.second_title
                        )
                    )
                }

                MusicState.STATE_PLAY -> {
                    holder.viewLine.visibility = View.VISIBLE
                    holder.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            holder.tvTitle.context,
                            R.color.text_select_color
                        )
                    )
                    holder.tvDesc.setTextColor(
                        ContextCompat.getColor(
                            holder.tvDesc.context,
                            R.color.text_select_color
                        )
                    )
                }

                MusicState.STATE_PAUSE -> {
                    holder.viewLine.visibility = View.INVISIBLE
                    holder.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            holder.tvTitle.context,
                            R.color.main_title
                        )
                    )
                    holder.tvDesc.setTextColor(
                        ContextCompat.getColor(
                            holder.tvDesc.context,
                            R.color.second_title
                        )
                    )
                }

                MusicState.STATE_ERROR -> {
                    holder.viewLine.visibility = View.INVISIBLE
                    holder.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            holder.tvTitle.context,
                            R.color.main_title
                        )
                    )
                    holder.tvDesc.setTextColor(
                        ContextCompat.getColor(
                            holder.tvDesc.context,
                            R.color.second_title
                        )
                    )
                }

                else -> {
                    holder.viewLine.visibility = View.INVISIBLE
                    holder.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            holder.tvTitle.context,
                            R.color.main_title
                        )
                    )
                    holder.tvDesc.setTextColor(
                        ContextCompat.getColor(
                            holder.tvDesc.context,
                            R.color.second_title
                        )
                    )
                }
            }
        } else {
            holder.viewLine.visibility = View.INVISIBLE
            holder.tvTitle.setTextColor(
                ContextCompat.getColor(
                    holder.tvTitle.context,
                    R.color.main_title
                )
            )
            holder.tvDesc.setTextColor(
                ContextCompat.getColor(
                    holder.tvDesc.context,
                    R.color.second_title
                )
            )
        }
    }

    /**
     * 获取专辑封面位图对象
     * @param context
     * @param songId songId
     * @param albumId albumId
     * @param allowDefault allowDefault
     * @param small small
     * @return
     */
    fun getArtwork(
        context: Context,
        songId: Long,
        albumId: Long,
        allowDefault: Boolean,
        small: Boolean
    ): Bitmap? {
        if (albumId < 0) {
            if (songId < 0) {
                val bm: Bitmap? = getArtworkFromFile(context, songId, -1)
                if (bm != null) {
                    return bm
                }
            }
            return if (allowDefault) {
                getDefaultArtwork(context, small)
            } else null
        }
        val res = context.contentResolver
        val uri: Uri = ContentUris.withAppendedId(albumArtUri, albumId)

        var `in`: InputStream? = null
        return try {
            `in` = res.openInputStream(uri)
            val options = BitmapFactory.Options()
            //先制定原始大小
            options.inSampleSize = 1
            //只进行大小判断
            options.inJustDecodeBounds = true
            //调用此方法得到options得到图片的大小
            BitmapFactory.decodeStream(`in`, null, options)
            /** 我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例  */
            /** 这里的target为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合  */
            if (small) {
                options.inSampleSize = computeSampleSize(options, 40)
            } else {
                options.inSampleSize = computeSampleSize(options, 600)
            }
            // 我们得到了缩放比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false
            options.inDither = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            `in` = res.openInputStream(uri)
            BitmapFactory.decodeStream(`in`, null, options)
        } catch (e: FileNotFoundException) {
            var bm: Bitmap? = getArtworkFromFile(context, songId, albumId)
            if (bm != null) {
                if (bm.config == null) {
                    bm = bm.copy(Bitmap.Config.RGB_565, false)
                    if (bm == null && allowDefault) {
                        return getDefaultArtwork(context, small)
                    }
                }
            } else if (allowDefault) {
                bm = getDefaultArtwork(context, small)
            }
            bm
        } finally {
            try {
                `in`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 从文件当中获取专辑封面位图
     * @param context
     * @param songid
     * @param albumid
     * @return
     */
    @SuppressLint("Recycle")
    private fun getArtworkFromFile(context: Context, songid: Long, albumid: Long): Bitmap? {
        var bm: Bitmap? = null
        require(!(albumid < 0 && songid < 0)) { "Must specify an album or a song id" }
        try {
            val options = BitmapFactory.Options()
            var fd: FileDescriptor? = null
            if (albumid < 0) {
                val uri = Uri.parse(
                    "content://media/external/audio/media/"
                            + songid + "/albumart"
                )
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd != null) {
                    fd = pfd.fileDescriptor
                }
            } else {
                val uri = ContentUris.withAppendedId(albumArtUri, albumid)
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd != null) {
                    fd = pfd.fileDescriptor
                }
            }
            options.inSampleSize = 1
            // 只进行大小判断
            options.inJustDecodeBounds = true
            // 调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options)
            // 我们的目标是在800pixel的画面上显示
            // 所以需要调用computeSampleSize得到图片缩放的比例
            options.inSampleSize = 100
            // 我们得到了缩放的比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false
            options.inDither = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            //根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return bm
    }

    /**
     * 获取默认专辑图片
     * @param context
     * @return
     */
    private fun getDefaultArtwork(context: Context, small: Boolean): Bitmap? {
        val opts = BitmapFactory.Options()
        opts.inPreferredConfig = Bitmap.Config.RGB_565
        if (small) {    //返回小图片
            return BitmapFactory.decodeResource(context.resources, R.mipmap.music_default, opts)
        }
        return null
    }

    /**
     * 对图片进行合适的缩放
     * @param options
     * @param target
     * @return
     */
    private fun computeSampleSize(options: BitmapFactory.Options, target: Int): Int {
        val w = options.outWidth
        val h = options.outHeight
        val candidateW = w / target
        val candidateH = h / target
        var candidate = max(candidateW, candidateH)
        if (candidate == 0) {
            return 1
        }
        if (candidate > 1) {
            if (w > target && w / candidate < target) {
                candidate -= 1
            }
        }
        if (candidate > 1) {
            if (h > target && h / candidate < target) {
                candidate -= 1
            }
        }
        return candidate
    }

    /**
     * 根据专辑ID获取专辑封面图
     * @param albumId 专辑ID
     * @return
     */
    fun getAlbumArt(context: Context, albumId: Long): String? {
        val mUriAlbums = "content://media/external/audio/albums"
        val projection = arrayOf("album_art")
        val cur = context.contentResolver.query(
            Uri.parse(
                "$mUriAlbums/$albumId"
            ), projection, null, null, null
        )
        var albumArt: String? = null
        if (cur!!.count > 0 && cur.columnCount > 0) {
            cur.moveToNext()
            albumArt = cur.getString(0)
        }
        cur.close()
        var path: String? = null
        if (albumArt != null) {
            path = albumArt
        } else {
            //path = "drawable/music_no_icon.png";
            //bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_cover);
        }
        return path
    }
}