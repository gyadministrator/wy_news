package com.android.wy.news.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.R
import com.android.wy.news.entity.music.Album
import com.android.wy.news.entity.music.Mvlist


/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/6/16 16:49
  * @Version:        1.0
  * @Description:    
 */
class SingerAlbumAdapter(itemAdapterListener: OnItemAdapterListener<Album>) :
    BaseNewsAdapter<SingerAlbumAdapter.SingerMvHolder, Album>(
        itemAdapterListener
    ) {

    class SingerMvHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onViewHolderCreate(parent: ViewGroup, viewType: Int): SingerMvHolder {
        val view = getView(parent, R.layout.layout_singer_album_item)
        return SingerMvHolder(view)
    }

    override fun onBindData(holder: SingerMvHolder, position: Int, data: Album) {

    }
}