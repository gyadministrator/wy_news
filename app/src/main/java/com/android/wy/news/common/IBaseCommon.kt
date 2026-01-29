package com.android.wy.news.common

import androidx.viewbinding.ViewBinding
import com.android.wy.news.viewmodel.BaseViewModel

/*     
  * @Author:         gao_yun
  * @CreateDate:     2023/3/16 14:57
  * @Version:        1.0
  * @Description:    
 */
interface IBaseCommon<V : ViewBinding, M : BaseViewModel> {
    fun initView()
    fun initData()
    fun initEvent()
    fun getViewBinding(): V
    fun getViewModel(): M
    fun onClear()
    fun onNotifyDataChanged()
}