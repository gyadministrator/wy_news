package com.android.wy.news.common

import android.content.Context
import java.lang.ref.WeakReference

class SpTools {
    companion object {
        private const val NEWS_MODE = "news_mode"
        private var mContext: WeakReference<Context>? = null

        fun init(context: Context) {
            mContext = WeakReference<Context>(context.applicationContext)
        }

        fun put(key: String, value: String) {
            val preferences = mContext?.get()?.getSharedPreferences(NEWS_MODE, Context.MODE_PRIVATE)
            val edit = preferences?.edit()
            edit?.putString(key, value)
            edit?.apply()
        }

        fun get(key: String): String? {
            val preferences = mContext?.get()?.getSharedPreferences(NEWS_MODE, Context.MODE_PRIVATE)
            return preferences?.getString(key, "")
        }
    }
}