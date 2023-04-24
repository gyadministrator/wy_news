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
                        Logger.i("当前定位城市: ${mapLocation.city}")
                        stopLocation()
                    } else {
                        onLocationListener.error(getErrorCodeMsg(errorCode = mapLocation.errorCode))
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Logger.i(getErrorCodeMsg(errorCode = mapLocation.errorCode))
                    }
                }
            }
        }

        private fun getErrorCodeMsg(errorCode: Int): String {
            val msg: String
            when (errorCode) {
                0 -> {
                    msg = "[定位成功。],可以在定位回调里判断定位返回成功后再进行业务逻辑运算。"
                }

                1 -> {
                    msg = "[一些重要参数为空，如context；],请对定位传递的参数进行非空判断。"
                }

                2 -> {
                    msg = "[定位失败，由于仅扫描到单个wifi，且没有基站信息。],请重新尝试。"
                }

                3 -> {
                    msg =
                        "[获取到的请求参数为空，可能获取过程中出现异常。],请对所连接网络进行全面检查，请求可能被篡改。"
                }

                4 -> {
                    msg =
                        "[请求服务器过程中的异常，多为网络情况差，链路不通导致],请检查设备网络是否通畅，检查通过接口设置的网络访问超时时间，建议采用默认的30秒。"
                }

                5 -> {
                    msg =
                        "[请求被恶意劫持，定位结果解析失败。],您可以稍后再试，或检查网络链路是否存在异常。"
                }

                6 -> {
                    msg =
                        "[定位服务返回定位失败。],请获取errorDetail（通过getLocationDetail()方法获取）信息并参考定位常见问题进行解决。"
                }

                7 -> {
                    msg =
                        "[KEY鉴权失败。],请仔细检查key绑定的sha1值与apk签名sha1值是否对应，或通过高频问题查找相关解决办法。"
                }

                8 -> {
                    msg =
                        "[Android exception常规错误],请将errordetail（通过getLocationDetail()方法获取）信息通过工单系统反馈给我们。"
                }

                9 -> {
                    msg = "[定位初始化时出现异常。],请重新启动定位。"
                }

                10 -> {
                    msg =
                        "[定位客户端启动失败。],请检查AndroidManifest.xml文件是否配置了APSService定位服务"
                }

                11 -> {
                    msg =
                        "[定位时的基站信息错误。],请检查是否安装SIM卡，设备很有可能连入了伪基站网络。"
                }

                12 -> {
                    msg = "[缺少定位权限。],请在设备的设置中开启app的定位权限。"
                }

                13 -> {
                    msg =
                        "[定位失败，由于未获得WIFI列表和基站信息，且GPS当前不可用。],建议开启设备的WIFI模块，并将设备中插入一张可以正常工作的SIM卡，或者检查GPS是否开启；如果以上都内容都确认无误，请您检查App是否被授予定位权限。"
                }

                14 -> {
                    msg =
                        "[GPS 定位失败，由于设备当前 GPS 状态差。],建议持设备到相对开阔的露天场所再次尝试。"
                }

                15 -> {
                    msg =
                        "[定位结果被模拟导致定位失败],如果您希望位置被模拟，请通过setMockEnable(true);方法开启允许位置模拟"
                }

                16 -> {
                    msg =
                        "[当前POI检索条件、行政区划检索条件下，无可用地理围栏],建议调整检索条件后重新尝试，例如调整POI关键字，调整POI类型，调整周边搜区域，调整行政区关键字等。"
                }

                18 -> {
                    msg =
                        "[定位失败，由于手机WIFI功能被关闭同时设置为飞行模式],建议手机关闭飞行模式，并打开WIFI开关"
                }

                19 -> {
                    msg =
                        "[定位失败，由于手机没插sim卡且WIFI功能被关闭],建议手机插上sim卡，打开WIFI开关"
                }

                20 -> {
                    msg =
                        "[模糊定位异常，用户设置应用位置权限为“大致位置”时定位异常],可以通过 getErrorInfo(); 获取详细错误信息"
                }

                else -> {
                    msg = "定位发生未知错误"
                }
            }
            return msg
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