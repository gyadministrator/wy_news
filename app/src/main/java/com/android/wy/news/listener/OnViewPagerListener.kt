package com.android.wy.news.listener

interface OnViewPagerListener {
    fun onInitComplete()

    fun onPageRelease(isNext: Boolean, position: Int)

    fun onPageSelected(position: Int, isBottom: Boolean)
}