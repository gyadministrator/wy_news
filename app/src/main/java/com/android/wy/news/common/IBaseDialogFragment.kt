package com.android.wy.news.common

import androidx.viewbinding.ViewBinding

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/5/29 13:08
  * @Version:        1.0
  * @Description:    
 */
interface IBaseDialogFragment<V : ViewBinding> {
    fun initView()
    fun initData()
    fun initEvent()
    fun getViewBinding(): V
    fun onClear()
    fun initIntent()
    fun getLayoutHeight(): Int
    fun getLayoutWidth(): Int
    fun setFragmentStyle()
    fun getGravityLocation(): Int
}