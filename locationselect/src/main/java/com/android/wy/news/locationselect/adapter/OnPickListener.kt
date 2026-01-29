package com.android.wy.news.locationselect.adapter

import com.android.wy.news.locationselect.model.City




/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/4/15 13:45
  * @Version:        1.0
  * @Description:    
 */
interface OnPickListener {
 fun onPick(position: Int, data: City?)
 fun onLocate()
 fun onCancel()
}