package com.android.wy.news.locationselect.adapter

import com.android.wy.news.locationselect.model.City


/*
  * @Author:         gao_yun
  * @CreateDate:     2023/4/15 13:38
  * @Version:        1.0
  * @Description:    
 */
interface InnerListener {
    fun dismiss(position: Int, data: City?)
    fun locate()
}