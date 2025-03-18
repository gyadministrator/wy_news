package com.android.wy.news.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/23 13:09
  * @Version:        1.0
  * @Description:    
 */
class BottomPagerAdapter(fm: FragmentManager, private val fragmentList: MutableList<Fragment>) :
    FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }
}