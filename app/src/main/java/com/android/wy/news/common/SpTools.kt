package com.android.wy.news.common

import android.content.Context
import com.android.wy.news.app.App

class SpTools {
    companion object {
        private const val NEWS_MODE = "news_mode"

        fun putString(key: String, value: String) {
            val preferences =
                App.app.applicationContext.getSharedPreferences(NEWS_MODE, Context.MODE_PRIVATE)
            val edit = preferences?.edit()
            edit?.putString(key, value)
            edit?.apply()
        }

        fun getString(key: String): String? {
            val preferences =
                App.app.applicationContext.getSharedPreferences(NEWS_MODE, Context.MODE_PRIVATE)
            return preferences?.getString(key, "")
        }

        fun putInt(key: String, value: Int) {
            val preferences =
                App.app.applicationContext.getSharedPreferences(NEWS_MODE, Context.MODE_PRIVATE)
            val edit = preferences?.edit()
            edit?.putInt(key, value)
            edit?.apply()
        }

        fun getInt(key: String): Int? {
            val preferences =
                App.app.applicationContext.getSharedPreferences(NEWS_MODE, Context.MODE_PRIVATE)
            return preferences?.getInt(key, -1)
        }

        fun putBoolean(key: String, value: Boolean) {
            val preferences =
                App.app.applicationContext.getSharedPreferences(NEWS_MODE, Context.MODE_PRIVATE)
            val edit = preferences?.edit()
            edit?.putBoolean(key, value)
            edit?.apply()
        }

        fun getBoolean(key: String): Boolean? {
            val preferences =
                App.app.applicationContext.getSharedPreferences(NEWS_MODE, Context.MODE_PRIVATE)
            return preferences?.getBoolean(key, false)
        }
    }
}