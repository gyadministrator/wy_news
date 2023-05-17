package com.android.wy.news.activity

import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import cn.jzvd.Jzvd
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.amap.api.maps.MapsInitializer
import com.android.wy.news.R
import com.android.wy.news.bottombar.BottomBarManager
import com.android.wy.news.bottombar.model.BarItem
import com.android.wy.news.bottombar.view.BottomBar
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SpTools
import com.android.wy.news.databinding.ActivityMainBinding
import com.android.wy.news.entity.music.MusicInfo
import com.android.wy.news.event.NoticeEvent
import com.android.wy.news.fragment.ClassifyTabFragment
import com.android.wy.news.fragment.LiveTabFragment
import com.android.wy.news.fragment.MusicTabFragment
import com.android.wy.news.fragment.TopTabFragment
import com.android.wy.news.fragment.VideoTabFragment
import com.android.wy.news.location.LocationHelper
import com.android.wy.news.location.OnLocationListener
import com.android.wy.news.locationselect.CityPicker
import com.android.wy.news.locationselect.adapter.OnPickListener
import com.android.wy.news.locationselect.model.City
import com.android.wy.news.locationselect.model.HotCity
import com.android.wy.news.locationselect.model.LocateState
import com.android.wy.news.locationselect.model.LocatedCity
import com.android.wy.news.manager.RouteManager
import com.android.wy.news.notification.NotificationUtil
import com.android.wy.news.skin.UiModeManager
import com.android.wy.news.util.AppUtil
import com.android.wy.news.util.BatteryManageUtil
import com.android.wy.news.util.PermissionCheckUtil
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.util.ToastUtil
import com.android.wy.news.view.MarqueeTextView
import com.android.wy.news.view.PlayBarView
import com.android.wy.news.viewmodel.MainViewModel
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/17 11:18
  * @Version:        1.0
  * @Description:    
 */

@Route(path = RouteManager.PATH_ACTIVITY_MAIN)
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(),
    BottomBar.OnBottomBarSelectListener {
    private var bottomBar: BottomBar? = null
    private lateinit var tvCity: TextView
    private var firstTime: Long = 0
    private lateinit var marqueeTextView: MarqueeTextView
    private lateinit var rlSetting: RelativeLayout
    private lateinit var rlSearch: RelativeLayout
    private lateinit var playBarView: PlayBarView
    private val list = ArrayList<String>()
    private var currentSelectPosition = 0

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
        bottomBar = mBinding.bottomBar
        tvCity = mBinding.tvCity
        marqueeTextView = mBinding.marqueeTextView
        rlSetting = mBinding.rlSetting
        rlSearch = mBinding.rlSearchTop
        playBarView = mBinding.playBarView
    }

    override fun initData() {
        val barItemList = arrayListOf(
            BarItem(TopTabFragment::class.java, "头条", R.mipmap.top, R.mipmap.top_p),
            BarItem(
                ClassifyTabFragment::class.java,
                "精选",
                R.mipmap.classify,
                R.mipmap.classify_p
            ),
            BarItem(VideoTabFragment::class.java, "视频", R.mipmap.video, R.mipmap.video_p),
            BarItem(MusicTabFragment::class.java, "聆听", R.mipmap.music, R.mipmap.music_p),
            BarItem(LiveTabFragment::class.java, "直播", R.mipmap.live, R.mipmap.live_p)
        )
        bottomBar?.let {
            BottomBarManager.initBottomBar(
                it,
                barItemList,
                R.id.fl_container,
                AppUtil.getColor(this, R.color.main_title),
                AppUtil.getColor(this, R.color.text_select_color),
                this
            )
        }
        TaskUtil.runOnThread {
            mViewModel.getHotWord()
        }
    }

    override fun initEvent() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        UiModeManager.onUiModeChange(this)

        list.add("热词加载中...")
        marqueeTextView.setList(list)
        marqueeTextView.startScroll()

        rlSetting.setOnClickListener {
            SettingActivity.startSettingActivity(this)
        }
        rlSearch.setOnClickListener {
            SearchActivity.startSearch(this, marqueeTextView.getShowText())
        }
        tvCity.setOnClickListener {
            goLocationPage()
        }
        jumpUrl()
        TaskUtil.runOnUiThread({
            checkNotification()
        }, 1000)
    }

    /**
     * 引导之后，部分机型可以直接设置。当然，部分机型还有如下问题：
     *1.小米需要更改通知过滤规则，避免消息被过滤掉；
     *2.华为需要手动在电池管理里进行操作，才能进行自启动和后台运行。
     * 没办法，提供操作说明和解决方案吧。提供解决方案以后，目前还正常。
     */
    private fun guideNotification() {
        val checkFloatPermission = PermissionCheckUtil.checkFloatPermission(this)
        if (!checkFloatPermission) {
            //打开悬浮窗
            PermissionCheckUtil.requestSettingCanDrawOverlays(this)
        }
        //打开自启动窗口
        //AutoStartUtil.getAutostartSettingIntent(this)
        //打开电池优化
        BatteryManageUtil.ignoreBatteryOptimization(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(o: Any) {
        if (o is NoticeEvent) {
            val url = o.url
            val noticeStatus = SpTools.getBoolean(GlobalData.SpKey.NOTICE_STATUS)
            if (noticeStatus == true) {
                TaskUtil.runOnUiThread({
                    WebActivity.startActivity(this, url = url)
                }, 500)
            } else {
                WebActivity.startActivity(this, url = url)
            }
        }
    }

    fun updateCity(city: String) {
        tvCity.text = city
    }

    private fun goLocationPage() {
        val hotCities: ArrayList<HotCity> = ArrayList()
        //code为城市代码
        hotCities.add(HotCity("北京", "北京", "101010100"))
        hotCities.add(HotCity("上海", "上海", "101020100"))
        hotCities.add(HotCity("广州", "广东", "101280101"))
        hotCities.add(HotCity("深圳", "广东", "101280601"))
        hotCities.add(HotCity("杭州", "浙江", "101210101"))
        hotCities.add(HotCity("贵阳", "贵州", "101210101"))
        hotCities.add(HotCity("六盘水", "贵州", "101210101"))
        //activity或者fragment
        val cityPicker = CityPicker.from(this)
        //启用动画效果，默认无
        cityPicker.enableAnimation(true)
            //自定义动画
            //.setAnimationStyle(anim)
            //APP自身已定位的城市，传null会自动定位（默认）
            .setLocatedCity(null)
            //指定热门城市
            .setHotCities(hotCities).setOnPickListener(object : OnPickListener {

                override fun onPick(position: Int, data: City?) {
                    tvCity.text = data?.name
                    EventBus.getDefault().post(data?.name)
                    Logger.i("选中的城市: " + data?.name)
                }

                /**
                 * 定位接口
                 */
                override fun onLocate() {
                    MapsInitializer.updatePrivacyShow(this@MainActivity, true, true)
                    MapsInitializer.updatePrivacyAgree(this@MainActivity, true)
                    TaskUtil.runOnUiThread({
                        LocationHelper.startLocation(this@MainActivity,
                            object : OnLocationListener {
                                override fun success(aMapLocation: AMapLocation) {
                                    //定位完成之后更新数据
                                    cityPicker.locateComplete(
                                        LocatedCity(
                                            aMapLocation.city,
                                            aMapLocation.province,
                                            aMapLocation.cityCode
                                        ), LocateState.SUCCESS
                                    )
                                }

                                override fun error(msg: String) {
                                    ToastUtil.show(msg)
                                }

                            })
                    }, 500)
                }

                override fun onCancel() {
                    Logger.i("用户取消城市选择")
                }
            }).show()
    }

    private fun checkNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //判断是否需要开启通知栏功能
            NotificationUtil.openNotificationSetting(
                this,
                object : NotificationUtil.OnNextListener {
                    override fun onNext() {
                        Logger.i(resources.getString(R.string.app_name) + "已开启通知权限")
                        guideNotification()
                    }
                })
        }
    }

    private fun jumpUrl() {
        TaskUtil.runOnUiThread({
            val intent = intent
            if (intent.hasExtra(WebActivity.WEB_URL)) {
                val url = intent.getStringExtra(WebActivity.WEB_URL).toString()
                if (!TextUtils.isEmpty(url)) {
                    WebActivity.startActivity(this, url = url)
                }
            }
        }, 500)
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun getViewModel(): MainViewModel {
        return CommonTools.getViewModel(this, MainViewModel::class.java)
    }

    override fun onClear() {
        marqueeTextView.stopScroll()
        EventBus.getDefault().unregister(this)
        SpTools.putBoolean(GlobalData.SpKey.NOTICE_STATUS, false)
    }

    override fun onNotifyDataChanged() {
        mViewModel.dataList.observe(this) {
            if (it != null && it.size > 0) {
                list.clear()
                marqueeTextView.stopScroll()
                for (i in 0 until it.size) {
                    val rollHotWord = it[i]
                    val hotWord = rollHotWord.hotWord
                    list.add(hotWord)
                }
            }
            marqueeTextView.setList(list)
            marqueeTextView.startScroll()
        }
    }

    override fun onItemSelect(position: Int) {
        Logger.i("onItemSelect--->>>position:$position")
        currentSelectPosition = position
        if (position == 2) {
            hideSearch()
            ImmersionBar.with(this).statusBarColor(R.color.black).navigationBarColor(R.color.black)
                .statusBarDarkFont(false).keyboardEnable(false).init()
            bottomBar?.setBackgroundResource(R.color.black)
        } else {
            showSearch()
            UiModeManager.onUiModeChange(this)
            bottomBar?.setBackgroundResource(R.color.default_status_bar_color)
        }
        setBottomBarState(position)
        if (position != 3) {
            playBarView.visibility = View.GONE
        } else {
            val gson = Gson()
            val s = SpTools.getString(GlobalData.SpKey.LAST_PLAY_MUSIC_KEY)
            val musicInfo = gson.fromJson(s, MusicInfo::class.java)
            if (musicInfo != null) {
                playBarView.visibility = View.VISIBLE
            }
        }
    }

    fun getSelectPosition(): Int {
        return currentSelectPosition
    }

    private fun setBottomBarState(position: Int) {
        if (position == 2) {
            bottomBar?.setTitleBeforeAndAfterColor(
                AppUtil.getColor(this, R.color.white),
                AppUtil.getColor(this, R.color.text_select_color)
            )
            bottomBar?.invalidate()
        } else {
            bottomBar?.setTitleBeforeAndAfterColor(
                AppUtil.getColor(this, R.color.main_title),
                AppUtil.getColor(this, R.color.text_select_color)
            )
            bottomBar?.invalidate()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        val secondTime = System.currentTimeMillis()
        if (secondTime - firstTime > 2000) {
            ToastUtil.show("再按一次退出程序")
            firstTime = secondTime
        } else {
            LocationHelper.destroyLocation()
            //暂时消失当前activity，移动到后台
            moveTaskToBack(true)
            //finish()
            //exitProcess(0)
        }
        //super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }

    private fun hideSearch() {
        val rlSearch = findViewById<LinearLayout>(R.id.rl_search)
        rlSearch.visibility = View.GONE
    }

    private fun showSearch() {
        val rlSearch = findViewById<LinearLayout>(R.id.rl_search)
        rlSearch.visibility = View.VISIBLE
    }

    fun getPlayBarView(): PlayBarView {
        return playBarView
    }
}