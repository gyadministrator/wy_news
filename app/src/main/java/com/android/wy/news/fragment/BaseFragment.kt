package com.android.wy.news.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.android.wy.news.common.IBaseCommon
import com.android.wy.news.viewmodel.BaseViewModel

/*     
  * @Author:         gao_yun@leapmotor.com
  * @CreateDate:     2023/3/16 15:07
  * @Version:        1.0
  * @Description:    
 */
abstract class BaseFragment<V : ViewBinding, M : BaseViewModel> : Fragment(), IBaseCommon<V, M> {
    protected lateinit var mBinding: V
    protected lateinit var mViewModel: M
    protected lateinit var mActivity: Activity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mActivity = requireActivity()
        mBinding = getViewBinding()
        initView()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = getViewModel()
        initData()
        initEvent()
        onNotifyDataChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        onClear()
    }
}