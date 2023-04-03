package com.android.wy.news.sql

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/4/3 14:39
  * @Version:        1.0
  * @Description:    
 */
class DBOpenHelper:SQLiteOpenHelper {
 constructor(
  context: Context?,
  name: String?,
  factory: SQLiteDatabase.CursorFactory?,
  version: Int
 ) : super(context, name, factory, version)

 constructor(
  context: Context?,
  name: String?,
  factory: SQLiteDatabase.CursorFactory?,
  version: Int,
  errorHandler: DatabaseErrorHandler?
 ) : super(context, name, factory, version, errorHandler)

 override fun onCreate(p0: SQLiteDatabase?) {

 }

 override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

 }
}