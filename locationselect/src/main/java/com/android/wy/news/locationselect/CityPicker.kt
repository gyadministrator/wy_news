package com.android.wy.news.locationselect

import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.android.wy.news.locationselect.adapter.OnPickListener
import com.android.wy.news.locationselect.model.HotCity
import com.android.wy.news.locationselect.model.LocateState
import com.android.wy.news.locationselect.model.LocatedCity
import java.lang.ref.WeakReference


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/15 14:09
  * @Version:        1.0
  * @Description:    
 */
class CityPicker {
    companion object {
        private const val TAG = "CityPicker"
        fun from(fragment: Fragment): CityPicker {
            return CityPicker(fragment)
        }

        fun from(activity: FragmentActivity): CityPicker {
            return CityPicker(activity)
        }
    }

    private var mContext: WeakReference<FragmentActivity>? = null
    private var mFragment: WeakReference<Fragment>? = null
    private var mFragmentManager: WeakReference<FragmentManager>? = null

    private var enableAnim = false
    private var mAnimStyle = 0
    private var mLocation: LocatedCity? = null
    private var mHotCities: ArrayList<HotCity>? = null
    private var mOnPickListener: OnPickListener? = null

    constructor() {}

    constructor(fragment: Fragment) : this(fragment.activity, fragment) {
        mFragmentManager = WeakReference(fragment.childFragmentManager)
    }

    constructor(activity: FragmentActivity) : this(activity, null) {
        mFragmentManager = WeakReference(activity.supportFragmentManager)
    }

    constructor(activity: FragmentActivity?, fragment: Fragment?) {
        mContext = WeakReference(activity)
        mFragment = WeakReference<Fragment>(fragment)
    }

    /**
     * 设置动画效果
     * @param animStyle
     * @return
     */
    fun setAnimationStyle(@StyleRes animStyle: Int): CityPicker {
        mAnimStyle = animStyle
        return this
    }

    /**
     * 设置当前已经定位的城市
     * @param location
     * @return
     */
    fun setLocatedCity(location: LocatedCity?): CityPicker {
        mLocation = location
        return this
    }

    fun setHotCities(data: ArrayList<HotCity>): CityPicker {
        mHotCities = data
        return this
    }

    /**
     * 启用动画效果，默认为false
     * @param enable
     * @return
     */
    fun enableAnimation(enable: Boolean): CityPicker {
        enableAnim = enable
        return this
    }

    /**
     * 设置选择结果的监听器
     * @param listener
     * @return
     */
    fun setOnPickListener(listener: OnPickListener?): CityPicker {
        mOnPickListener = listener
        return this
    }

    fun show() {
        var ft: FragmentTransaction? = mFragmentManager?.get()?.beginTransaction()
        val prev: Fragment? = mFragmentManager?.get()?.findFragmentByTag(TAG)
        if (prev != null) {
            ft?.remove(prev)?.commit()
            ft = mFragmentManager?.get()?.beginTransaction()
        }
        ft?.addToBackStack(null)
        val cityPickerFragment: CityPickerDialogFragment =
            CityPickerDialogFragment.newInstance(enableAnim)
        cityPickerFragment.setLocatedCity(mLocation)
        cityPickerFragment.setHotCities(mHotCities)
        cityPickerFragment.setAnimationStyle(mAnimStyle)
        cityPickerFragment.setOnPickListener(mOnPickListener)
        ft?.let { cityPickerFragment.show(it, TAG) }
    }

    /**
     * 定位完成
     * @param location
     * @param state
     */
    fun locateComplete(location: LocatedCity?, @LocateState.State state: Int) {
        val fragment = mFragmentManager?.get()?.findFragmentByTag(TAG) as CityPickerDialogFragment
        fragment.locationChanged(location, state)
    }
}