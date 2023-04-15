package com.android.wy.news.locationselect

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.wy.news.locationselect.adapter.CityListAdapter
import com.android.wy.news.locationselect.adapter.InnerListener
import com.android.wy.news.locationselect.adapter.OnPickListener
import com.android.wy.news.locationselect.adapter.decoration.DividerItemDecoration
import com.android.wy.news.locationselect.adapter.decoration.SectionItemDecoration
import com.android.wy.news.locationselect.db.DBManager
import com.android.wy.news.locationselect.model.City
import com.android.wy.news.locationselect.model.HotCity
import com.android.wy.news.locationselect.model.LocateState
import com.android.wy.news.locationselect.model.LocatedCity
import com.android.wy.news.locationselect.util.ScreenUtil
import com.android.wy.news.locationselect.view.SideIndexBar


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/15 13:59
  * @Version:        1.0
  * @Description:    
 */
class CityPickerDialogFragment : DialogFragment(), TextWatcher, View.OnClickListener,
    SideIndexBar.OnIndexTouchedChangedListener, InnerListener {
    private var mContentView: View? = null
    private var mRecyclerView: RecyclerView? = null
    private var mEmptyView: View? = null
    private var mOverlayTextView: TextView? = null
    private var mIndexBar: SideIndexBar? = null
    private var mSearchBox: EditText? = null
    private var mCancelBtn: TextView? = null
    private var mClearAllBtn: ImageView? = null

    private var mLayoutManager: LinearLayoutManager? = null
    private var mAdapter: CityListAdapter? = null
    private var mAllCities: ArrayList<City>? = null
    private var mHotCities: ArrayList<HotCity>? = null
    private var mResults: ArrayList<City>? = null

    private var dbManager: DBManager? = null

    private var height = 0
    private var width = 0

    private var enableAnim = false
    private var mAnimStyle: Int = R.style.DefaultCityPickerAnimation
    private var mLocatedCity: LocatedCity? = null
    private var locateState = 0
    private var mOnPickListener: OnPickListener? = null

    companion object {
        /**
         * 获取实例
         * @param enable 是否启用动画效果
         */
        fun newInstance(enable: Boolean): CityPickerDialogFragment {
            val fragment = CityPickerDialogFragment()
            val args = Bundle()
            args.putBoolean("cp_enable_anim", enable)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CityPickerStyle)
    }

    fun setLocatedCity(location: LocatedCity?) {
        mLocatedCity = location
    }

    fun setHotCities(data: ArrayList<HotCity>?) {
        if (!data.isNullOrEmpty()) {
            mHotCities = data
        }
    }

    @SuppressLint("ResourceType")
    fun setAnimationStyle(@StyleRes resId: Int) {
        mAnimStyle = if (resId <= 0) mAnimStyle else resId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        mContentView = inflater.inflate(R.layout.cp_dialog_city_picker, container, false)
        return mContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initViews()
    }

    private fun initViews() {
        mRecyclerView = mContentView?.findViewById(R.id.cp_city_recyclerview)
        mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mRecyclerView?.layoutManager = mLayoutManager
        mRecyclerView?.setHasFixedSize(true)
        mRecyclerView?.addItemDecoration(SectionItemDecoration(requireActivity(), mAllCities), 0)
        mRecyclerView?.addItemDecoration(DividerItemDecoration(requireActivity()), 1)
        mAdapter = CityListAdapter(activity, mAllCities, mHotCities, locateState)
        mAdapter?.autoLocate(true)
        mAdapter?.setInnerListener(this)
        mAdapter?.setLayoutManager(mLayoutManager)
        mRecyclerView?.adapter = mAdapter
        mRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                //确保定位城市能正常刷新
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAdapter?.refreshLocationItem()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {}
        })
        mEmptyView = mContentView?.findViewById(R.id.cp_empty_view)
        mOverlayTextView = mContentView?.findViewById(R.id.cp_overlay)
        mIndexBar = mContentView?.findViewById(R.id.cp_side_index_bar)
        mIndexBar?.setNavigationBarHeight(ScreenUtil.getNavigationBarHeight(requireActivity()))
        mIndexBar?.setOverlayTextView(mOverlayTextView)?.setOnIndexChangedListener(this)
        mSearchBox = mContentView?.findViewById(R.id.cp_search_box)
        mSearchBox?.addTextChangedListener(this)
        mCancelBtn = mContentView?.findViewById(R.id.cp_cancel)
        mClearAllBtn = mContentView?.findViewById(R.id.cp_clear_all)
        mCancelBtn?.setOnClickListener(this)
        mClearAllBtn?.setOnClickListener(this)
    }

    private fun initData() {
        val args = arguments
        if (args != null) {
            enableAnim = args.getBoolean("cp_enable_anim")
        }
        //初始化热门城市
        if (mHotCities == null || mHotCities!!.isEmpty()) {
            mHotCities = ArrayList()
            mHotCities?.add(HotCity("北京", "北京", "101010100"))
            mHotCities?.add(HotCity("上海", "上海", "101020100"))
            mHotCities?.add(HotCity("广州", "广东", "101280101"))
            mHotCities?.add(HotCity("深圳", "广东", "101280601"))
            mHotCities?.add(HotCity("天津", "天津", "101030100"))
            mHotCities?.add(HotCity("杭州", "浙江", "101210101"))
            mHotCities?.add(HotCity("南京", "江苏", "101190101"))
            mHotCities?.add(HotCity("成都", "四川", "101270101"))
            mHotCities?.add(HotCity("武汉", "湖北", "101200101"))
        }
        //初始化定位城市，默认为空时会自动回调定位
        if (mLocatedCity == null) {
            mLocatedCity = LocatedCity(getString(R.string.cp_locating), "未知", "0")
            locateState = LocateState.LOCATING
        } else {
            locateState = LocateState.SUCCESS
        }
        dbManager = DBManager(requireActivity())
        mAllCities = dbManager?.getAllCities()
        mAllCities?.add(0, mLocatedCity!!)
        mAllCities?.add(1, HotCity("热门城市", "未知", "0"))
        mResults = mAllCities
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (mOnPickListener != null) {
                    mOnPickListener!!.onCancel()
                }
            }
            false
        }
        measure()
        val window = dialog?.window
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            window.setGravity(Gravity.BOTTOM)
            window.setLayout(width, height - ScreenUtil.getStatusBarHeight(requireActivity()))
            if (enableAnim) {
                window.setWindowAnimations(mAnimStyle)
            }
        }
    }

    //测量宽高
    private fun measure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val dm = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getRealMetrics(dm)
            height = dm.heightPixels
            width = dm.widthPixels
        } else {
            val dm = resources.displayMetrics
            height = dm.heightPixels
            width = dm.widthPixels
        }
    }

    /** 搜索框监听  */
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        val keyword = s.toString()
        if (TextUtils.isEmpty(keyword)) {
            mClearAllBtn!!.visibility = View.GONE
            mEmptyView!!.visibility = View.GONE
            mResults = mAllCities
            (mRecyclerView!!.getItemDecorationAt(0) as SectionItemDecoration).setData(mResults)
            mResults?.let { mAdapter?.updateData(it) }
        } else {
            mClearAllBtn!!.visibility = View.VISIBLE
            //开始数据库查找
            mResults = dbManager?.searchCity(keyword)
            (mRecyclerView!!.getItemDecorationAt(0) as SectionItemDecoration).setData(mResults)
            if (mResults == null || mResults!!.isEmpty()) {
                mEmptyView!!.visibility = View.VISIBLE
            } else {
                mEmptyView!!.visibility = View.GONE
                mAdapter?.updateData(mResults!!)
            }
        }
        mRecyclerView!!.scrollToPosition(0)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.cp_cancel) {
            dismiss()
            if (mOnPickListener != null) {
                mOnPickListener!!.onCancel()
            }
        } else if (id == R.id.cp_clear_all) {
            mSearchBox!!.setText("")
        }
    }


    override fun onIndexChanged(index: String?, position: Int) {
        //滚动RecyclerView到索引位置
        mAdapter!!.scrollToSection(index!!)
    }

    fun locationChanged(location: LocatedCity?, state: Int) {
        mAdapter!!.updateLocateState(location!!, state)
    }

    override fun dismiss(position: Int, data: City?) {
        dismiss()
        if (mOnPickListener != null) {
            mOnPickListener!!.onPick(position, data)
        }
    }

    override fun locate() {
        if (mOnPickListener != null) {
            mOnPickListener!!.onLocate()
        }
    }

    fun setOnPickListener(listener: OnPickListener?) {
        mOnPickListener = listener
    }
}