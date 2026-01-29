package com.android.wy.news.locationselect.db

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import com.android.wy.news.locationselect.db.DBConfig.Companion.COLUMN_C_CODE
import com.android.wy.news.locationselect.db.DBConfig.Companion.COLUMN_C_NAME
import com.android.wy.news.locationselect.db.DBConfig.Companion.COLUMN_C_PINYIN
import com.android.wy.news.locationselect.db.DBConfig.Companion.COLUMN_C_PROVINCE
import com.android.wy.news.locationselect.db.DBConfig.Companion.DB_NAME_V1
import com.android.wy.news.locationselect.db.DBConfig.Companion.LATEST_DB_NAME
import com.android.wy.news.locationselect.db.DBConfig.Companion.TABLE_NAME
import com.android.wy.news.locationselect.model.City
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Collections
import java.util.Comparator
import kotlin.collections.ArrayList


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/4/15 13:22
  * @Version:        1.0
  * @Description:    
 */
class DBManager(context: Context) {
    private val bufferSize = 1024

    private var dbPath: String? = null
    private var mContext: Context? = context

    init {
        dbPath = (File.separator + "data"
                + Environment.getDataDirectory().absolutePath + File.separator
                + context.packageName + File.separator + "databases" + File.separator)
        copyDBFile()
    }

    private fun copyDBFile() {
        val dir = dbPath?.let { File(it) }
        if (dir != null) {
            if (!dir.exists()) {
                dir.mkdirs()
            }
        }
        //如果旧版数据库存在，则删除
        val dbV1 = File(dbPath + DB_NAME_V1)
        if (dbV1.exists()) {
            dbV1.delete()
        }
        //创建新版本数据库
        val dbFile = File(dbPath + LATEST_DB_NAME)
        if (!dbFile.exists()) {
            val `is`: InputStream
            val os: OutputStream
            try {
                `is` = mContext!!.resources.assets.open(LATEST_DB_NAME)
                os = FileOutputStream(dbFile)
                val buffer = ByteArray(bufferSize)
                var length: Int
                while (`is`.read(buffer, 0, buffer.size).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
                os.close()
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("Range")
    fun getAllCities(): ArrayList<City> {
        val db = SQLiteDatabase.openOrCreateDatabase(dbPath + LATEST_DB_NAME, null)
        val cursor = db.rawQuery("select * from $TABLE_NAME", null)
        val result = ArrayList<City>()
        var city: City
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_C_NAME))
            val province = cursor.getString(cursor.getColumnIndex(COLUMN_C_PROVINCE))
            val pinyin = cursor.getString(cursor.getColumnIndex(COLUMN_C_PINYIN))
            val code = cursor.getString(cursor.getColumnIndex(COLUMN_C_CODE))
            city = City(name, province, pinyin, code)
            result.add(city)
        }
        cursor.close()
        db.close()
        Collections.sort(result, CityComparator())
        return result
    }

    @SuppressLint("Range")
    fun searchCity(keyword: String): ArrayList<City> {
        val sql = buildString {
            append(
                (("select * from $TABLE_NAME" + " where "
                        + COLUMN_C_NAME) + " like ? " + "or "
                        + COLUMN_C_PINYIN)
            )
            append(" like ? ")
        }
        val db = SQLiteDatabase.openOrCreateDatabase(dbPath + LATEST_DB_NAME, null)
        val cursor = db.rawQuery(
            sql, arrayOf(
                "%$keyword%",
                "$keyword%"
            )
        )
        val result = ArrayList<City>()
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_C_NAME))
            val province = cursor.getString(cursor.getColumnIndex(COLUMN_C_PROVINCE))
            val pinyin = cursor.getString(cursor.getColumnIndex(COLUMN_C_PINYIN))
            val code = cursor.getString(cursor.getColumnIndex(COLUMN_C_CODE))
            val city = City(name, province, pinyin, code)
            result.add(city)
        }
        cursor.close()
        db.close()
        val comparator = CityComparator()
        Collections.sort(result, comparator)
        return result
    }

    /**
     * sort by a-z
     */
    private class CityComparator : Comparator<City?> {
        override fun compare(p0: City?, p1: City?): Int {
            val a = p0?.pinyin?.substring(0, 1)
            val b = p1?.pinyin?.substring(0, 1)
            return a!!.compareTo(b!!)
        }
    }
}