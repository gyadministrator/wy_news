package com.android.wy.news.common

import android.util.Log

object Logger {
    private var isDebug: Boolean = false
    private const val TAG = "gy_news"

    fun setDebug(debug: Boolean) {
        isDebug = debug
    }

    fun e(str: String) {
        if (isDebug) {
            Log.e(TAG, "e: $str")
        }
    }

    fun i(str: String) {
        if (isDebug) {
            Log.i(TAG, "i: $str")
        }
    }

    fun d(str: String) {
        if (isDebug) {
            Log.d(TAG, "d: $str")
        }
    }
}