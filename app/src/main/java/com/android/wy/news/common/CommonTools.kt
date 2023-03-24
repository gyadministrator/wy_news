package com.android.wy.news.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.android.wy.news.R
import com.android.wy.news.entity.NewsEntity
import com.android.wy.news.entity.TopEntity
import com.android.wy.news.entity.VideoEntity
import com.android.wy.news.viewmodel.BaseViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 15:01
  * @Version:        1.0
  * @Description:    
 */
class CommonTools {
    companion object {
        fun <T : View> fd(@IdRes id: Int, view: View): T {
            return view.findViewById(id)
        }

        fun <T : BaseViewModel> getViewModel(owner: ViewModelStoreOwner, clazz: Class<T>): T {
            return ViewModelProvider(owner).get(clazz)
        }

        fun getScreenWidth(): Int {
            return Resources.getSystem().displayMetrics.widthPixels//屏幕宽度
        }

        fun getScreenHeight(): Int {
            return Resources.getSystem().displayMetrics.heightPixels//屏幕高度
        }

        // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
        fun dip2px(context: Context, dpValue: Float): Int {
            // 获取当前手机的像素密度（1个dp对应几个px）
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt() // 四舍五入取整
        }

        // 根据手机的分辨率从 px(像素) 的单位 转成为 dp
        fun px2dip(context: Context, pxValue: Float): Int {
            // 获取当前手机的像素密度（1个dp对应几个px）
            val scale = context.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt() // 四舍五入取整
        }

        fun parseNewsData(data: String?): ArrayList<NewsEntity> {
            var dataList = ArrayList<NewsEntity>()
            if (data != null && !TextUtils.isEmpty(data)) {
                if (data.contains("(") && data.endsWith(")")) {
                    val content = data.substring(data.indexOf("(") + 1, data.length - 1)
                    if (content.contains("[") && content.endsWith("]}")) {
                        val realContent =
                            content.substring(content.indexOf("["), content.length - 1)
                        val gson = Gson()
                        dataList = gson.fromJson(
                            realContent, object : TypeToken<ArrayList<NewsEntity>>() {}.type
                        )
                    }
                }
            }
            return dataList
        }

        fun parseVideoData(data: String?): ArrayList<VideoEntity> {
            var dataList = ArrayList<VideoEntity>()
            if (data != null && !TextUtils.isEmpty(data)) {
                if (data.contains("(") && data.endsWith(")")) {
                    val content = data.substring(data.indexOf("(") + 1, data.length - 1)
                    if (content.contains("[") && content.endsWith("]}")) {
                        val realContent =
                            content.substring(content.indexOf("["), content.length - 1)
                        val gson = Gson()
                        dataList = gson.fromJson(
                            realContent, object : TypeToken<ArrayList<VideoEntity>>() {}.type
                        )
                    }
                }
            }
            return dataList
        }

        fun parseTopData(data: String?): ArrayList<TopEntity> {
            var dataList = ArrayList<TopEntity>()
            if (data != null && !TextUtils.isEmpty(data)) {
                if (data.contains("[") && data.endsWith("]}")) {
                    val realContent = data.substring(data.indexOf("["), data.length - 1)
                    val gson = Gson()
                    dataList = gson.fromJson(
                        realContent, object : TypeToken<ArrayList<TopEntity>>() {}.type
                    )
                }
            }
            return dataList
        }

        fun getAssertContent(context: Context, json: String): String {
            val stringBuilder = StringBuilder()
            val inputStream: InputStream
            try {
                inputStream = context.resources.assets.open(json)
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var jsonLine = bufferedReader.readLine()
                while (jsonLine != null) {
                    stringBuilder.append(jsonLine)
                    jsonLine = bufferedReader.readLine()
                }
                bufferedReader.close()
                inputStreamReader.close()
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return stringBuilder.toString()
        }

        fun loadImage(context: Context, imgSrc: String, ivCover: ImageView) {
            Glide.with(context)
                .asBitmap()
                .load(imgSrc)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(
                    //关键代码，加载原始大小
                    com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                    com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
                )
                //设置为这种格式去掉透明度通道，可以减少内存占有
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.mipmap.img_default)
                .error(R.mipmap.img_error)
                .into(object : SimpleTarget<Bitmap>(
                    com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                    com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
                ) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        ivCover.setImageBitmap(resource)
                    }
                })
        }

        @SuppressLint("SimpleDateFormat")
        fun parseTime(time: String): String {
            var format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = format.parse(time)
            if (date != null) {
                format = SimpleDateFormat("yyyy-MM-dd HH:mm")
                return format.format(date)
            }
            return ""
        }
    }
}