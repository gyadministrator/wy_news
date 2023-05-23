package com.android.wy.news.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/23 13:09
  * @Version:        1.0
  * @Description:    
 */
class BottomPagerAdapter(fm: FragmentManager, fragmentList: ArrayList<Fragment>) :
    FragmentPagerAdapter(fm) {
    private var fragmentList: ArrayList<Fragment>? = null

    init {
        this.fragmentList = fragmentList
    }

    override fun getCount(): Int {
        return if (fragmentList == null) {
            0
        } else {
            fragmentList?.size!!
        }
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList?.get(position)!!
    }
}