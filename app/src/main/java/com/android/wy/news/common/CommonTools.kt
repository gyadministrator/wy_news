package com.android.wy.news.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.android.wy.news.app.App
import com.android.wy.news.entity.*
import com.android.wy.news.entity.music.Lrclist
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.music.lrc.Lrc
import com.android.wy.news.music.lrc.LrcHelper
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
import kotlin.collections.ArrayList


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

        fun parseRecommendVideoData(data: String?): ArrayList<RecommendVideoEntity> {
            var dataList = ArrayList<RecommendVideoEntity>()
            if (data != null && !TextUtils.isEmpty(data)) {
                if (data.contains("(") && data.endsWith(")")) {
                    val content = data.substring(data.indexOf("(") + 1, data.length - 1)
                    if (content.contains("[") && content.endsWith("]}")) {
                        val realContent =
                            content.substring(content.indexOf("["), content.length - 1)
                        val gson = Gson()
                        dataList = gson.fromJson(
                            realContent,
                            object : TypeToken<ArrayList<RecommendVideoEntity>>() {}.type
                        )
                    }
                }
            }
            return dataList
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
                .format(DecodeFormat.PREFER_RGB_565)
                //.placeholder(R.mipmap.img_default)
                //.error(R.mipmap.img_error)
                .into(object : SimpleTarget<Bitmap>(
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
            position: Int, mDataList: ArrayList<TopEntity>
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
                            it.topic_icons,
                            false
                        )
                        videoList.add(screenVideoEntity)
                    }
                }
            }
            return videoList
        }

        /** 改变键盘输入法的状态，如果已经弹出就关闭，如果关闭了就强制弹出  */
        fun changeInputState(context: Context) {
            val imm: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        /** 强制关闭软键盘  */
        fun closeKeyboard(context: Context, view: View) {
            val imm: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun getVersionName(context: Context): String {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName
        }

        fun getVersionCode(context: Context): Long {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
        }

        fun processData(
            stringName: List<String>, stringCode: List<String>
        ): List<CityInfo> {
            val provinceList: MutableList<CityInfo> = ArrayList()


            //获取省
            for (i in stringCode.indices) {
                val provinceName = stringName[i]
                val provinceCode = stringCode[i]
                if (provinceCode.endsWith("0000")) {
                    val cities = ArrayList<City>()
                    val cityInfo = CityInfo(cities, provinceCode, provinceName)
                    provinceList.add(cityInfo)
                }
            }


            //获取市
            for (i in provinceList.indices) {
                val provinceName: String = provinceList[i].name
                val provinceCode: String = provinceList[i].code
                //直辖市 城市和省份名称一样
                if (provinceName.contains("北京") || provinceName.contains("上海") || provinceName.contains(
                        "天津"
                    ) || provinceName.contains("重庆")
                ) {
                    val areas = ArrayList<Area>()
                    val city = City(areas, provinceCode, provinceName)
                    provinceList[i].cityList.add(city)
                } else {
                    for (j in stringCode.indices) {
                        val cityName = stringName[j]
                        val cityCode = stringCode[j]
                        if (cityCode != provinceCode) {
                            if (cityCode.startsWith(provinceCode.substring(0, 2))) {
                                if (cityCode.endsWith("00")) {
                                    val areas = ArrayList<Area>()
                                    val city = City(areas, cityCode, cityName)
                                    provinceList[i].cityList.add(city)
                                }
                            }
                        }
                    }
                }
            }


            //获取区县
            for (province in provinceList) {
                val cities: List<City> = province.cityList
                for (city in cities) {
                    //遍历获取县区
                    val cityCode: String = city.code
                    val cityName: String = city.name
                    for (k in stringCode.indices) {
                        val areaName = stringName[k]
                        val areaCode = stringCode[k]
                        if (cityName.contains("北京") || cityName.contains("上海") || cityName.contains(
                                "天津"
                            ) || cityName.contains("重庆")
                        ) {
                            if (province.code != areaCode && areaCode.startsWith(
                                    province.code.substring(0, 2)
                                )
                            ) {
                                val area = Area(areaCode, areaName)
                                city.areaList.add(area)
                            }
                        } else {
                            if (areaCode != cityCode && areaCode.startsWith(
                                    cityCode.substring(
                                        0, 4
                                    )
                                )
                            ) {
                                val area = Area(areaCode, areaName)
                                city.areaList.add(area)
                            }
                        }
                    }
                }
            }


            //已经处理的数据移除
            val stringNameList: MutableList<String> = ArrayList(stringName)
            val stringCodeList: MutableList<String> = ArrayList(stringCode)
            for (province in provinceList) {
                stringNameList.remove(province.name)
                stringCodeList.remove(province.code)
                val cities: List<City> = province.cityList
                for (city in cities) {
                    stringNameList.remove(city.name)
                    stringCodeList.remove(city.code)
                    val listArea: List<Area> = city.areaList
                    for (area in listArea) {
                        stringNameList.remove(area.name)
                        stringCodeList.remove(area.code)
                    }
                }
            }

            //处理石河子 特殊 市，City Code 不以00结尾
            for (province in provinceList) {
                for (k in stringCodeList.indices) {
                    if (stringCodeList[k].startsWith(province.code.substring(0, 2))) {
                        val areas = ArrayList<Area>()
                        val city = City(areas, stringNameList[k], stringCodeList[k])
                        province.cityList.add(city)
                    }
                }
            }
            return provinceList
        }

        fun parseVideoEntityToScreenVideoEntity(it: ArrayList<VideoEntity>?): ArrayList<ScreenVideoEntity> {
            val videoList = ArrayList<ScreenVideoEntity>()
            if (it != null && it.size > 0) {
                for (i in 0 until it.size) {
                    val videoEntity = it[i]
                    val screenVideoEntity = videoEntity.videoTopic?.ename?.let { it1 ->
                        ScreenVideoEntity(
                            videoEntity.title,
                            videoEntity.playCount.toLong(),
                            videoEntity.topicName,
                            videoEntity.ptime,
                            videoEntity.mp4_url,
                            videoEntity.fullSizeImg,
                            it1,
                            videoEntity.videoTopic.alias,
                            videoEntity.videoTopic.topic_icons,
                            false
                        )
                    }
                    screenVideoEntity?.let { it1 -> videoList.add(it1) }
                }
            }
            return videoList
        }

        fun filterMusicList(musicList: ArrayList<MusicInfo>?): ArrayList<MusicInfo> {
            val dataList = ArrayList<MusicInfo>()
            if (musicList != null && musicList.size > 0) {
                for (i in 0 until musicList.size) {
                    val musicInfo = musicList[i]
                    val listenFee = musicInfo.isListenFee
                    //付费歌曲，获取不了url，不显示
                    if (!listenFee) {
                        dataList.add(musicInfo)
                    }
                }
            }
            return dataList
        }

        fun parseLrc(lrcList: List<Lrclist>): ArrayList<Lrc> {
            val dataList = ArrayList<Lrc>()
            if (lrcList.isNotEmpty()) {
                for (element in lrcList) {
                    val lrc=Lrc(element.time.toFloat(),element.lineLyric)
                    dataList.add(lrc)
                }
            }
            return dataList
        }
    }
}