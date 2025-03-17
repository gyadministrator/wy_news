package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.android.tablib.adapter.FragmentPageAdapter
import com.android.tablib.view.CustomTabLayout
import com.android.wy.news.R
import com.android.wy.news.databinding.LayoutTabViewpagerBinding


/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/8/31 13:55
  * @Version:        1.0
  * @Description:    
 */
class TabViewPager : LinearLayout {
    private lateinit var tabLayout: CustomTabLayout
    private lateinit var viewPager: ViewPager

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_tab_viewpager, this)
        val binding = LayoutTabViewpagerBinding.bind(view)
        initView(binding)
    }

    private fun initView(binding: LayoutTabViewpagerBinding) {
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
    }

    fun initData(
        fragmentManager: FragmentManager,
        fragments: ArrayList<Fragment>,
        titles: ArrayList<String>
    ) {
        //一个ViewPager+ABCDE五个Fragment，默认一开始在C Fragment。
        //a) 如果设置setOffscreenPageLimit(1)，则会提前加载BCD三个
        //b）如果设置setOffscreenPageLimit(2),则会提前加载ABCDE五个
        //c) 如果设置setOffscreenPageLimit(1),而从C滑动到D时，E也会被预加载，而B可能会被destroy，也可能只是被remove，具体看adapter实现
        //viewPager.offscreenPageLimit = titles.size
        viewPager.adapter =
            FragmentPageAdapter(fragmentManager, fragments, titles.toTypedArray())
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.initLayout()
        //使用viewpager嵌套fragment的时候出现Expected the adapter to be ‘fresh‘ while restoring state.
        viewPager.isSaveEnabled = false
        //tabLayout.setSelectedTabIndicatorHeight(0)
    }
}