package com.android.wy.news.common

import android.content.Context
import com.android.wy.news.app.App

class SpTools {
    companion object {
        private const val NEWS_MODE = "news_mode"

        fun put(key: String, value: String) {
            val preferences = App.app.applicationContext.getSharedPreferences(NEWS_MODE, Context.MODE_PRIVATE)
            val edit = preferences?.edit()
            edit?.putString(key, value)
            edit?.apply()
        }

        fun get(key: String): String? {
            val preferences = App.app.applicationContext.getSharedPreferences(NEWS_MODE, Context.MODE_PRIVATE)
            return preferences?.getString(key, "")
        }
    }
}