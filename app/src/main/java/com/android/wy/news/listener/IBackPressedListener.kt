package com.android.wy.news.listener

interface IBackPressedListener {
    /**
     * @return true代表响应back键点击，false代表不响应
     */
    fun handleBackPressed(): Boolean
}