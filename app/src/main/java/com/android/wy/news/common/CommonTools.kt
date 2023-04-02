package com.android.wy.news.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.android.wy.news.R
import com.android.wy.news.app.App
import com.android.wy.news.entity.AdEntity
import com.android.wy.news.entity.NewsEntity
import com.android.wy.news.entity.ScreenVideoEntity
import com.android.wy.news.entity.TopEntity
import com.android.wy.news.entity.VideoEntity
import com.android.wy.news.viewmodel.BaseViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


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
            return ViewModelProvider(owner)[clazz]
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

        fun parseAdData(data: String?): AdEntity? {
            var adEntity: AdEntity? = null
            if (data != null && !TextUtils.isEmpty(data)) {
                if (data.contains("(") && data.endsWith(")")) {
                    val content = data.substring(data.indexOf("(") + 1, data.length - 1)
                    val gson = Gson()
                    adEntity = gson.fromJson(content, AdEntity::class.java)
                }
            }
            return adEntity
        }

        fun parseVideoData(data: String?): ArrayList<VideoEntity> {
            var dataList = ArrayList<VideoEntity>()
            if (data != null && !TextUtils.isEmpty(data)) {
                if (data.startsWith("{") && data.endsWith("}")) {
                    if (data.contains("[{")) {
                        val realContent = data.substring(data.indexOf("[{"), data.length - 1)
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

        fun loadImage(imgSrc: String, ivCover: ImageView) {
            Glide.with(ivCover.context).asBitmap().load(imgSrc)
                //.apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(
                    //关键代码，加载原始大小
                    com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                    com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
                )
                //设置为这种格式去掉透明度通道，可以减少内存占有
                .format(DecodeFormat.PREFER_RGB_565).placeholder(R.mipmap.img_default)
                .error(R.mipmap.img_error).into(object : SimpleTarget<Bitmap>(
                    com.bumptech.glide.request.target.Target.SIZE_ORIGINAL,
                    com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
                ) {
                    override fun onResourceReady(
                        resource: Bitmap, transition: Transition<in Bitmap?>?
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
                format = SimpleDateFormat("yyyy.MM.dd HH:mm")
                return format.format(date)
            }
            return ""
        }

        fun getAddress(latitude: Double, longitude: Double) {
            var addressList: List<Address?>? = null
            val geocoder = Geocoder(App.app.applicationContext)
            try {
                addressList = geocoder.getFromLocation(latitude, longitude, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (addressList != null) {
                for (address in addressList) {
                }
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun getTimeDiff(time: String): String {
            var result = ""
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = df.parse(time)
            val nowDate = Date()
            if (date != null) {
                val diff = nowDate.time - date.time
                val day = diff / (1000 * 60 * 60 * 24)
                val hour = diff / (60 * 60 * 1000) - day * 24
                val minute = diff / (60 * 1000) - day * 24 * 60 - hour * 60
                val second = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60
                val y = day / 365
                if (y > 0) {
                    result = y.toString() + "年前"
                } else {
                    val m = day / 30
                    if (m > 0) {
                        result = m.toString() + "月前"
                    } else {
                        val w = day / 7
                        if (w > 0) {
                            result = w.toString() + "周前"
                        } else {
                            if (day > 0) {
                                result = day.toString() + "天前"
                            } else {
                                if (hour > 0) {
                                    result = hour.toString() + "小时前"
                                } else {
                                    if (minute > 0) {
                                        result = minute.toString() + "分钟前"
                                    } else {
                                        if (second > 0) {
                                            result = second.toString() + "秒前"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return result
        }

        fun second2Time(second: Long?): String {
            if (second == null || second < 0) {
                return "00:00"
            }
            val h = second / 3600
            val m = second % 3600 / 60
            val s = second % 60
            var str = ""
            if (h > 0) {
                str = (if (h < 10) "0$h" else h).toString() + ":"
            }
            str += (if (m < 10) "0$m" else m).toString() + ":"
            str += if (s < 10) "0$s" else s
            return str
        }

        fun topEntity2ScreenVideoEntity(
            position: Int,
            mDataList: ArrayList<TopEntity>
        ): ArrayList<ScreenVideoEntity> {
            val videoList = ArrayList<ScreenVideoEntity>()
            for (i in position until mDataList.size) {
                val topEntity = mDataList[i]
                val videoInfo = topEntity.videoinfo
                val videoTopic = topEntity.videoTopic
                if (videoInfo != null) {
                    var userSource = ""
                    if (videoTopic != null) {
                        val certificationText = videoTopic.certificationText
                        userSource = if (TextUtils.isEmpty(certificationText)) {
                            videoTopic.alias
                        } else {
                            certificationText
                        }
                    }
                    videoTopic?.let {
                        val screenVideoEntity = ScreenVideoEntity(
                            topEntity.title,
                            topEntity.replyCount.toLong(),
                            topEntity.source,
                            videoInfo.ptime,
                            videoInfo.mp4_url,
                            videoInfo.cover,
                            videoTopic.ename,
                            userSource,
                            it.topic_icons
                        )
                        videoList.add(screenVideoEntity)
                    }
                }
            }
            return videoList
        }

    }
}