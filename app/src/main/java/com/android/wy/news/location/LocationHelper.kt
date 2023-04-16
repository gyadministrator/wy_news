package com.android.wy.news.location

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.*
import com.amap.api.location.AMapLocationQualityReport
import com.amap.api.maps.MapsInitializer
import com.android.wy.news.common.Constants
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SpTools
import com.android.wy.news.dialog.PrivacyDialogFragment
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/10 15:38
  * @Version:        1.0
  * @Description:    
 */
class LocationHelper {
    companion object {
        private var client: WeakReference<AMapLocationClient>? = null
        private var clientOption: AMapLocationClientOption? = null

        @SuppressLint("SimpleDateFormat")
        fun startLocation(context: Context, onLocationListener: OnLocationListener) {
            val locationClient = AMapLocationClient(context)
            client = WeakReference(locationClient)
            clientOption = getDefaultOption()
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            clientOption?.locationMode = AMapLocationMode.Hight_Accuracy
            //设置定位间隔,单位毫秒,默认为2000ms
            clientOption?.interval = 2000
            //设置定位参数
            locationClient.setLocationOption(clientOption)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            //启动定位
            locationClient.startLocation()
            locationClient.setLocationListener { mapLocation ->
                if (mapLocation != null) {
                    //定位成功回调信息，设置相关消息
                    if (mapLocation.errorCode == 0) {
                        //获取当前定位结果来源，如网络定位结果，详见定位类型表
                        mapLocation.locationType
                        //获取纬度
                        mapLocation.latitude
                        //获取经度
                        mapLocation.longitude
                        //获取精度信息
                        mapLocation.accuracy
                        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val date = Date(mapLocation.time)
                        //定位时间
                        df.format(date)
                        //地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        mapLocation.address
                        //国家信息
                        mapLocation.country
                        //省信息
                        mapLocation.province
                        //城市信息
                        mapLocation.city
                        //城区信息
                        mapLocation.district
                        //街道信息
                        mapLocation.street
                        //街道门牌号信息
                        mapLocation.streetNum
                        //城市编码
                        mapLocation.cityCode
                        //地区编码
                        mapLocation.adCode
                        onLocationListener.success(mapLocation)
                        Logger.e("当前定位城市: ${mapLocation.city}")
                        stopLocation()
                    } else {
                        onLocationListener.error("location Error, ErrCode:" + mapLocation.errorCode + ", errInfo:" + mapLocation.errorInfo)
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Logger.e("location Error, ErrCode:" + mapLocation.errorCode + ", errInfo:" + mapLocation.errorInfo)
                    }
                }
            }
        }


        /**
         * 默认的定位参数
         */
        private fun getDefaultOption(): AMapLocationClientOption {
            val mOption = AMapLocationClientOption()
            mOption.locationMode =
                AMapLocationMode.Hight_Accuracy //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
            mOption.isGpsFirst = false //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
            mOption.httpTimeOut = 30000 //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
            mOption.interval = 2000 //可选，设置定位间隔。默认为2秒
            mOption.isNeedAddress = true //可选，设置是否返回逆地理地址信息。默认是true
            mOption.isOnceLocation = false //可选，设置是否单次定位。默认是false
            mOption.isOnceLocationLatest =
                false //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
            setLocationProtocol(AMapLocationProtocol.HTTP) //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
            mOption.isSensorEnable = false //可选，设置是否使用传感器。默认是false
            mOption.isWifiScan =
                true //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
            mOption.isLocationCacheEnable = true //可选，设置是否使用缓存定位，默认为true
            mOption.geoLanguage = GeoLanguage.DEFAULT //可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
            return mOption
        }

        fun privacyCompliance(activity: AppCompatActivity, privacyListener: OnPrivacyListener) {
            MapsInitializer.updatePrivacyShow(activity, true, true)

            val fragment = PrivacyDialogFragment.newInstance()
            fragment.addListener(object : PrivacyDialogFragment.OnDialogFragmentListener {
                override fun onClickSure() {
                    MapsInitializer.updatePrivacyAgree(activity, true)
                    SpTools.putInt(Constants.PRIVACY_STATUS, Constants.PRIVACY_STATUS_AGREE)
                    privacyListener.onClickAgree()
                }

                override fun onClickCancel() {
                    MapsInitializer.updatePrivacyAgree(activity, false)
                    SpTools.putInt(Constants.PRIVACY_STATUS, Constants.PRIVACY_STATUS_CANCEL)
                    activity.finish()
                    exitProcess(0)
                }
            })
            fragment.show(activity.supportFragmentManager, "news_dialog")
        }


        /**
         * 销毁定位
         */
        fun destroyLocation() {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            val locationClient: AMapLocationClient? = client?.get()
            locationClient?.onDestroy()
            clientOption = null
        }

        /**
         * 停止定位
         */
        private fun stopLocation() {
            try {
                // 停止定位
                val locationClient: AMapLocationClient? = client?.get()
                locationClient?.stopLocation()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface OnPrivacyListener {
        fun onClickAgree()
    }

    interface OnLocationListener {
        fun success(aMapLocation: AMapLocation)
        fun error(msg: String)
    }
}