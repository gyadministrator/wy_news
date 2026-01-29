package com.android.wy.news.locationselect.db

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/4/15 13:22
  * @Version:        1.0
  * @Description:    
 */
class DBConfig {
    companion object {
        const val DB_NAME_V1 = "china_cities.db"
        const val DB_NAME_V2 = "china_cities_v2.db"
        const val LATEST_DB_NAME = DB_NAME_V2

        const val TABLE_NAME = "cities"

        const val COLUMN_C_NAME = "c_name"
        const val COLUMN_C_PROVINCE = "c_province"
        const val COLUMN_C_PINYIN = "c_pinyin"
        const val COLUMN_C_CODE = "c_code"
    }
}