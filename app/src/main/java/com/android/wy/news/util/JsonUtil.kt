package com.android.wy.news.util

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type


/*
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/25 15:30
  * @Version:        1.0
  * @Description:    
 */
object JsonUtil {
    private val gson = Gson()

    /**
     * 把一个map变成json字符串
     * @param map map
     * @return String
     */
    fun parseMapToJson(map: Map<*, *>?): String? {
        try {
            return gson.toJson(map)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 把一个json字符串变成对象
     * @param json json
     * @param cls cls
     * @return T
     */
    fun <T> parseJsonToObject(json: String?, cls: Class<T>?): T? {
        var t: T? = null
        try {
            t = gson.fromJson(json, cls)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return t
    }

    /**
     * 把json字符串变成map
     * @param json json
     * @return HashMap
     */
    fun parseJsonToMap(json: String?): HashMap<String, String>? {
        val type: Type = object : TypeToken<HashMap<String, String>?>() {}.type
        var map: HashMap<String, String>? = null
        try {
            map = gson.fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return map
    }

    /**
     * 把json字符串变成集合
     * params: new TypeToken<List></List><yourbean>>(){}.getType(),
     *
     * @param json
     * @return ArrayList
     * */
    inline fun <reified T> parseJsonToList(
        json: String?,
    ): ArrayList<T> {
        //val type = object : TypeToken<ArrayList<T>>() {}.type
        //return gson.fromJson<ArrayList<T>>(json, type)
        return parseJsonToArray(json, T::class.java)
    }

    @Throws(java.lang.Exception::class)
    fun <T> parseJsonToArray(json: String?, clazz: Class<T>?): ArrayList<T> {
        val dataList: ArrayList<T> = ArrayList()
        val array = JsonParser().parse(json).asJsonArray
        for (elem in array) {
            dataList.add(Gson().fromJson(elem, clazz))
        }
        return dataList
    }

    /**
     *
     * 获取json串中某个字段的值，注意，只能获取同一层级的value
     *
     * @param json json
     * @param key key
     * @return
     */
    fun getFieldValue(json: String, key: String?): String? {
        if (TextUtils.isEmpty(json)) return null
        if (!json.contains(key!!)) return ""
        val jsonObject: JSONObject?
        var value: String? = null
        try {
            jsonObject = JSONObject(json)
            value = jsonObject.getString(key)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return value
    }

    /**
     * 格式化json
     * @param uglyJSONString
     * @return String
     */
    fun jsonFormatter(uglyJSONString: String?): String? {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jp = JsonParser()
        val je = jp.parse(uglyJSONString)
        return gson.toJson(je)
    }

    /**
     * @param any any
     * @return String
     */
    fun parseObjectToJson(any: Any): String {
        return gson.toJson(any)
    }
}