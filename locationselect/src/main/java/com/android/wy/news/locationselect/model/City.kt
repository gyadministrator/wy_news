package com.android.wy.news.locationselect.model

import android.text.TextUtils
import java.util.Locale

import java.util.regex.Matcher
import java.util.regex.Pattern


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/4/15 13:10
  * @Version:        1.0
  * @Description:    
 */
open class City(
    var name: String,
    var province: String,
    var pinyin: String,
    var code: String
) {
    /***
     * 获取悬浮栏文本，（#、定位、热门 需要特殊处理）
     */
    fun getSection(): String {
        return if (TextUtils.isEmpty(pinyin)) {
            "#"
        } else {
            val c = pinyin.substring(0, 1)
            val p: Pattern = Pattern.compile("[a-zA-Z]")
            val m: Matcher = p.matcher(c)
            if (m.matches()) {
                c.uppercase(Locale.getDefault())
            } else if (TextUtils.equals(c, "定") || TextUtils.equals(c, "热")) pinyin else "#"
        }
    }
}