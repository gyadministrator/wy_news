package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import cn.jzvd.Jzvd
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.amap.api.maps.MapsInitializer
import com.android.bottombar.activity.GYBottomActivity
import com.android.bottombar.model.GYBarItem
import com.android.bottombar.view.GYBottomBarView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.GlobalData
import com.android.wy.news.common.Logger
import com.android.wy.news.common.SpTools
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
import com.android.wy.news.util.BatteryManageUtil
import com.android.wy.news.util.PermissionCheckUtil
import com.android.wy.news.util.TaskUtil
import com.android.wy.news.util.ToastUtil
import com.android.wy.news.view.MarqueeTextView
import com.android.wy.news.view.PlayBarView
import com.android.wy.news.viewmodel.NewsMainViewModel
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

@Route(path = RouteManager.PATH_ACTIVITY_HOME)
class HomeActivity : GYBottomActivity(), GYBottomBarView.IGYBottomBarChangeListener {
    private lateinit var bottomView: GYBottomBarView
    private lateinit var tvCity: TextView
    private var firstTime: Long = 0
    private lateinit var mViewModel: NewsMainViewModel
    private lateinit var marqueeTextView: MarqueeTextView
    private lateinit var rlSetting: RelativeLayout
    private lateinit var rlSearch: RelativeLayout
    private lateinit var playBarView: PlayBarView
    private val list = ArrayList<String>()
    private var currentSelectPosition = 0

    override fun initBarItems() {
        barItems.add(GYBarItem("头条", R.mipmap.top))
        barItems.add(GYBarItem("精选", R.mipmap.classify))
        barItems.add(GYBarItem("视频", R.mipmap.video))
        barItems.add(GYBarItem("聆听", R.mipmap.music))
        barItems.add(GYBarItem("直播", R.mipmap.live))
    }

    override fun onDestroy() {
        super.onDestroy()
        marqueeTextView.stopScroll()
        EventBus.getDefault().unregister(this)
        SpTools.putBoolean(GlobalData.SpKey.NOTICE_STATUS, false)
    }

    override fun initFragment() {
        fragments.add(TopTabFragment.newInstance())
        fragments.add(ClassifyTabFragment.newInstance())
        fragments.add(VideoTabFragment.newInstance())
        fragments.add(MusicTabFragment.newInstance())
        fragments.add(LiveTabFragment.newInstance())
        setBottomBarState(0)

        mViewModel = CommonTools.getViewModel(this, NewsMainViewModel::class.java)

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
        TaskUtil.runOnThread {
            mViewModel.getHotWord()
        }
    }

    override fun initSelectIcons() {
        icons.add(R.mipmap.top_p)
        icons.add(R.mipmap.classify_p)
        icons.add(R.mipmap.video_p)
        icons.add(R.mipmap.music_p)
        icons.add(R.mipmap.live_p)
    }

    override fun initContentView(): Int {
        return R.layout.activity_home
    }

    override fun getBottomBarView(): GYBottomBarView {
        return bottomView
    }

    override fun initContainerId(): Int {
        return R.id.fl_container
    }

    override fun initChangeListener(): GYBottomBarView.IGYBottomBarChangeListener {
        return this
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun initView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        UiModeManager.onUiModeChange(this)
        //竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        bottomView = findViewById(R.id.bottomView)

        tvCity = findViewById(R.id.tv_city)

        marqueeTextView = findViewById(R.id.marqueeTextView)
        list.add("热词加载中...")
        marqueeTextView.setList(list)
        marqueeTextView.startScroll()

        rlSetting = findViewById(R.id.rl_setting)
        rlSetting.setOnClickListener {
            SettingActivity.startSettingActivity(this)
        }

        rlSearch = findViewById(R.id.rl_search_top)
        playBarView = findViewById(R.id.play_bar_view)
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
        val hotCities: ArrayList<HotCity> = ArrayList<HotCity>()
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
                    MapsInitializer.updatePrivacyShow(this@HomeActivity, true, true)
                    MapsInitializer.updatePrivacyAgree(this@HomeActivity, true)
                    TaskUtil.runOnUiThread({
                        LocationHelper.startLocation(this@HomeActivity,
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

    override fun onSelected(position: Int) {
        currentSelectPosition = position
        if (position == 2) {
            hideSearch()
            ImmersionBar.with(this).statusBarColor(R.color.black).navigationBarColor(R.color.black)
                .statusBarDarkFont(false).keyboardEnable(false).init()
            bottomView.setBackgroundResource(R.color.black)
        } else {
            showSearch()
            UiModeManager.onUiModeChange(this)
            bottomView.setBackgroundResource(R.color.default_status_bar_color)
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
            for (i in 0 until 5) {
                val imageView = bottomView.getBottomViewPositionImageView(i) as ImageView
                val textView = bottomView.getBottomViewPositionTextView(i) as TextView
                val drawable = imageView.drawable
                val wrap = DrawableCompat.wrap(drawable)
                if (i == position) {
                    DrawableCompat.setTint(
                        wrap,
                        ContextCompat.getColor(this, R.color.text_select_color)
                    )
                    textView.setTextColor(ContextCompat.getColor(this, R.color.text_select_color))
                } else {
                    DrawableCompat.setTint(wrap, ContextCompat.getColor(this, R.color.white))
                    textView.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
                imageView.setImageDrawable(wrap)
            }
        } else {
            for (i in 0 until 5) {
                val imageView = bottomView.getBottomViewPositionImageView(i) as ImageView
                val textView = bottomView.getBottomViewPositionTextView(i) as TextView
                val drawable = imageView.drawable
                val wrap = DrawableCompat.wrap(drawable)
                if (i == position) {
                    DrawableCompat.setTint(
                        wrap,
                        ContextCompat.getColor(this, R.color.text_select_color)
                    )
                    textView.setTextColor(ContextCompat.getColor(this, R.color.text_select_color))
                } else {
                    DrawableCompat.setTint(wrap, ContextCompat.getColor(this, R.color.main_title))
                    textView.setTextColor(ContextCompat.getColor(this, R.color.main_title))
                }
                imageView.setImageDrawable(wrap)
            }
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