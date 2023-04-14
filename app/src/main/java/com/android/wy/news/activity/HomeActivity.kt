package com.android.wy.news.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.bottombar.activity.GYBottomActivity
import com.android.bottombar.model.GYBarItem
import com.android.bottombar.view.GYBottomBarView
import com.android.custom.pickview.entity.PickerData
import com.android.custom.pickview.util.JsonUtil
import com.android.custom.pickview.view.CustomPickerView
import com.android.wy.news.R
import com.android.wy.news.common.CommonTools
import com.android.wy.news.common.Constants
import com.android.wy.news.entity.CityInfo
import com.android.wy.news.fragment.ClassifyTabFragment
import com.android.wy.news.fragment.LiveTabFragment
import com.android.wy.news.fragment.TopTabFragment
import com.android.wy.news.fragment.VideoTabFragment
import com.android.wy.news.manager.ThreadExecutorManager
import com.android.wy.news.service.NewsService
import com.android.wy.news.skin.UiModeManager
import com.android.wy.news.view.MarqueeTextView
import com.android.wy.news.viewmodel.NewsMainViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import java.lang.ref.WeakReference
import java.util.*
import kotlin.system.exitProcess


class HomeActivity : GYBottomActivity(), GYBottomBarView.IGYBottomBarChangeListener {
    private lateinit var bottomView: GYBottomBarView
    private lateinit var tvCity: TextView
    private var firstTime: Long = 0
    private lateinit var mViewModel: NewsMainViewModel
    private lateinit var marqueeTextView: MarqueeTextView
    private lateinit var ivSetting: ImageView
    private lateinit var rlSearch: RelativeLayout
    private val list = ArrayList<String>()

    companion object {
        var mInstance: WeakReference<HomeActivity>? = null
    }

    override fun initBarItems() {
        barItems.add(GYBarItem("头条", R.mipmap.top))
        barItems.add(GYBarItem("频道", R.mipmap.classify))
        barItems.add(GYBarItem("视频", R.mipmap.video))
        barItems.add(GYBarItem("直播", R.mipmap.live))
    }

    override fun onDestroy() {
        super.onDestroy()
        marqueeTextView.stopScroll()
        mInstance = null
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val uiMode = newConfig.uiMode
        /*UiModeManager.onUiModeChange(this)
        val i = SpTools.getInt(SkinType.SKIN_TYPE)
        i?.let { UiModeManager.setCurrentUiMode(it) }*/
    }

    /*  override fun onRestart() {
          super.onRestart()
          UiModeManager.onUiModeChange(this)
      }*/

    override fun initFragment() {
        fragments.add(TopTabFragment.newInstance())
        fragments.add(ClassifyTabFragment.newInstance())
        fragments.add(VideoTabFragment.newInstance())
        fragments.add(LiveTabFragment.newInstance())
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
        ThreadExecutorManager.mInstance.startExecute {
            mViewModel.getHotWord()
        }
        //testNotify()
    }

    private fun testNotify() {
        val intent = Intent(this, NewsService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    override fun initSelectIcons() {
        icons.add(R.mipmap.top_p)
        icons.add(R.mipmap.classify_p)
        icons.add(R.mipmap.video_p)
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
        mInstance = WeakReference(this)
        UiModeManager.onUiModeChange(this)
        //竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        bottomView = findViewById(R.id.bottomView)

        tvCity = findViewById(R.id.tv_city)
        tvCity.text = Constants.currentCity

        marqueeTextView = findViewById(R.id.marqueeTextView)
        list.add("热词加载中...")
        marqueeTextView.setList(list)
        marqueeTextView.startScroll()

        ivSetting = findViewById(R.id.iv_setting)
        ivSetting.setOnClickListener {
            SettingActivity.startSettingActivity(this)
        }

        rlSearch = findViewById(R.id.rl_search_top)
        rlSearch.setOnClickListener {
            SearchActivity.startSearch(this, marqueeTextView.getShowText())
        }
        initCityData()
        jumpUrl()
    }

    private fun initCityData() {
        val pickerData: PickerData = readJson()
        val pickerView = CustomPickerView(this, pickerData)
        pickerView.setScreenH(3)
            .setDiscolourHook(true)
            .setRadius(0)
            .setContentLine(true)
            .setContentText(16, Color.RED)
            .setListText(16, Color.RED)
            .setTitle("请选择地址")
            .setBtnText("确定")
            .setBtnColor(Color.RED)
            .setRadius(0)
            .build()

        tvCity.setOnClickListener {
            //显示选择器
            pickerView.show(tvCity)
        }

        //选择器点击事件
        pickerView.setOnPickerClickListener { pickerData ->
            Toast.makeText(
                this,
                pickerData.firstText + "," + pickerData.secondText + "," + pickerData.thirdText,
                Toast.LENGTH_SHORT
            ).show()
            pickerView.dismiss() //关闭选择器
        }
    }

    private fun readJson(): PickerData {
        val mCityData: MutableList<String> = ArrayList()
        val mDistrictMap: MutableMap<String, List<String>> = HashMap()
        val mVillageMap: MutableMap<String, List<String>> = HashMap()
        val json = JsonUtil.getJson("city.json", this)
        val gson = Gson()
        val dataList = gson.fromJson<ArrayList<CityInfo>>(
            json, object : TypeToken<ArrayList<CityInfo>>() {}.type
        )
        if (dataList.size > 0) {
            for (i in 0 until dataList.size) {
                val cityInfo = dataList[i]
                mCityData.add(cityInfo.name)
                val cityList = cityInfo.cityList
                if (cityList.size > 0) {
                    val districtNameList = ArrayList<String>()
                    for (j in 0 until cityList.size) {
                        val city = cityList[j]
                        districtNameList.add(city.name)
                        val areaList = city.areaList
                        if (areaList.size > 0) {
                            val areaNameList = ArrayList<String>()
                            for (m in 0 until areaList.size) {
                                val area = areaList[m]
                                areaNameList.add(area.name)
                            }
                            mVillageMap[city.name] = areaNameList
                        }
                    }
                    mDistrictMap[cityInfo.name] = districtNameList
                }
            }
        }
        val pickerData = PickerData()
        pickerData.firstData = mCityData
        pickerData.secondData = mDistrictMap
        pickerData.thirdData = mVillageMap
        pickerData.setInitSelectText("请选择")
        return pickerData
    }

    private fun jumpUrl() {
        Handler(Looper.getMainLooper()).postDelayed({
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
        if (position == 2) {
            hideSearch()
            ImmersionBar.with(this).statusBarColor(R.color.black).navigationBarColor(R.color.black)
                .statusBarDarkFont(false).init()
            bottomView.setBackgroundResource(R.color.black)
        } else {
            showSearch()
            UiModeManager.onUiModeChange(this)
            bottomView.setBackgroundResource(R.color.default_status_bar_color)
        }
    }

    @SuppressLint("RestrictedApi")
    fun getShowFragment(): Fragment? {
        val fragments: List<Fragment> = supportFragmentManager.fragments
        var fragment: Fragment? = null
        for (i in fragments.indices) {
            fragment = fragments[i]
            if (fragment.isAdded && fragment.isMenuVisible) {
                break
            }
        }
        return fragment
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return
        }
        val secondTime = System.currentTimeMillis()
        if (secondTime - firstTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
            firstTime = secondTime
        } else {
            finish()
            exitProcess(0)
        }
        //super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        JCVideoPlayer.releaseAllVideos()
    }

    private fun hideSearch() {
        val rlSearch = findViewById<LinearLayout>(R.id.rl_search)
        rlSearch.visibility = View.GONE
    }

    private fun showSearch() {
        val rlSearch = findViewById<LinearLayout>(R.id.rl_search)
        rlSearch.visibility = View.VISIBLE
    }
}