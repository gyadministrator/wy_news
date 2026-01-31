package com.android.wy.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.android.wy.news.R
import com.android.wy.news.adapter.TabItemAdapter
import com.android.wy.news.databinding.LayoutTabViewpagerBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/8/31 13:55
  * @Version:        1.0
  * @Description:
 */
@Suppress("DEPRECATION")
class TabViewPager : LinearLayout {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

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

    fun showLine(show: Boolean, height: Int = 2) {
        if (show) {
            tabLayout.setSelectedTabIndicatorHeight(height)
        } else {
            tabLayout.setSelectedTabIndicatorHeight(0)
        }
    }

    fun initViewPager(
        lifecycle: Lifecycle,
        fragmentManager: FragmentManager,
        fragments: ArrayList<Fragment>,
        titles: ArrayList<String>
    ): TabViewPager {
        //一个ViewPager+ABCDE五个Fragment，默认一开始在C Fragment。
        //a) 如果设置setOffscreenPageLimit(1)，则会提前加载BCD三个
        //b）如果设置setOffscreenPageLimit(2),则会提前加载ABCDE五个
        //c) 如果设置setOffscreenPageLimit(1),而从C滑动到D时，E也会被预加载，而B可能会被destroy，也可能只是被remove，具体看adapter实现
        //viewPager.offscreenPageLimit = titles.size
        viewPager.adapter =
            TabItemAdapter(fragments, fragmentManager, lifecycle)
        TabLayoutMediator(
            tabLayout,
            viewPager
        ) { tab, position -> tab.text = titles[position] }
            .attach()
        //使用viewpager嵌套fragment的时候出现Expected the adapter to be ‘fresh‘ while restoring state.
        viewPager.isSaveEnabled = false
        return this
    }
}